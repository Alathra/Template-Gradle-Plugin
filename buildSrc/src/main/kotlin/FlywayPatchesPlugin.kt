import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.language.jvm.tasks.ProcessResources
import java.io.File

/**
 * A Gradle plugin that provides all the necessary tricks to improve Flyway and jOOQ.
 *
 * Features:
 * 1. Flyway supports RDBMS-specific migrations while maintaining a common folder for shared migrations.
 * 2. Flyway cache to prevent unnecessary jOOQ builds.
 * 3. jOOQ code generation runs before Java compilation.
 * 4. Sources and Javadoc JARs are generated correctly with jOOQ.
 */
abstract class FlywayPatchesPlugin : Plugin<Project> {
    companion object {
        private const val PLUGIN_GROUP = "flywaypatches"
        private const val ASSIMILATE_MIGRATIONS_TASK = "assimilateMigrations"
        private const val MIGRATION_PATH = "db/migration"
        private const val TEMP_MIGRATION_PATH = "tmp/assimilateMigrations/"
        private const val INVALIDATE_MIGRATIONS_TASK = "invalidateMigrations"
        private const val PROCESS_RESOURCES_TASK = "processResources"
        private const val FLYWAY_MIGRATE_TASK = "flywayMigrate"
        private const val JOOQ_CODEGEN_TASK = "jooqCodegen"
        private const val SOURCES_TASK = "sourcesJar"
        private const val JAVADOC_TASK = "javadoc"
        private const val COMPILE_JAVA_TASK = "compileJava"
    }

    override fun apply(project: Project) {
        val hasProcessResources = project.tasks.names.contains(PROCESS_RESOURCES_TASK)
        val hasFlywayMigrate = project.tasks.names.contains(FLYWAY_MIGRATE_TASK)
        val hasJooqCodegen = project.tasks.names.contains(JOOQ_CODEGEN_TASK)

        if (hasProcessResources && hasFlywayMigrate && hasJooqCodegen) {
            val invalidateMigrationsTask = registerInvalidateMigrationsTask(project)
            configureFlywayMigrate(project, invalidateMigrationsTask)
            configureJooqCodegen(project, invalidateMigrationsTask)
        } else {
            project.logger.warn(
                "FlywayPatchesPlugin: Could not register $INVALIDATE_MIGRATIONS_TASK task because required tasks are missing: $PROCESS_RESOURCES_TASK, $FLYWAY_MIGRATE_TASK, $JOOQ_CODEGEN_TASK"
            )
        }

        if (hasProcessResources) {
            val assimilateMigrationsTask = registerAssimilateMigrationsTask(project)
            configureProcessResources(project, assimilateMigrationsTask)
        } else {
            project.logger.warn("FlywayPatchesPlugin: $PROCESS_RESOURCES_TASK task not found, skipping $ASSIMILATE_MIGRATIONS_TASK task configuration")
        }

        project.afterEvaluate {
            val hasSources = project.tasks.names.contains(SOURCES_TASK)
            val hasJavadoc = project.tasks.names.contains(JAVADOC_TASK)
            val hasCompileJava = project.tasks.names.contains(COMPILE_JAVA_TASK)

            // Required for sources jar generation with jOOQ
            if (hasSources && hasJooqCodegen) {
                project.tasks.named<Jar>(SOURCES_TASK) {
                    dependsOn(JOOQ_CODEGEN_TASK)
                    duplicatesStrategy = DuplicatesStrategy.INCLUDE
                }
            }

            // Required for javadoc jar generation with jOOQ
            if (hasJavadoc && hasJooqCodegen) {
                project.tasks.named<Javadoc>(JAVADOC_TASK) {
                    exclude("**/database/schema/**") // Exclude generated jOOQ sources from javadocs
                }
            }

            // Ensure jOOQ code generation runs before Java compilation
            if (hasCompileJava && hasJooqCodegen) {
                project.tasks.named<JavaCompile>(COMPILE_JAVA_TASK) {
                    dependsOn(JOOQ_CODEGEN_TASK) // Generate jOOQ sources before compilation
                }
            }
        }
    }

    private fun registerAssimilateMigrationsTask(project: Project): TaskProvider<AssimilateMigrationsTask> {
        return project.tasks.register<AssimilateMigrationsTask>(ASSIMILATE_MIGRATIONS_TASK) {
            group = PLUGIN_GROUP
            description = "Assimilate RDBMS-specific and common migrations into a unified structure that can be used by Flyway."
        }
    }

    private fun configureProcessResources(
        project: Project,
        assimilateMigrationsTask: TaskProvider<AssimilateMigrationsTask>
    ) {
        project.tasks.named<ProcessResources>(PROCESS_RESOURCES_TASK) {
            dependsOn(assimilateMigrationsTask)

            // Exclude original migration files from being processed directly
            exclude("$MIGRATION_PATH/**")

            doLast {
                copyAssimilatedMigrations(project)
            }
        }
    }

    private fun copyAssimilatedMigrations(project: Project) {
        val sourceDir = project.layout.buildDirectory
            .dir(TEMP_MIGRATION_PATH)
            .get()
            .asFile

        val destinationDir = project.tasks
            .named<ProcessResources>(PROCESS_RESOURCES_TASK)
            .get()
            .destinationDir

        val targetDir = File(destinationDir, MIGRATION_PATH)

        if (sourceDir.exists()) {
            sourceDir.copyRecursively(targetDir, overwrite = true)
            project.logger.info("FlywayBetterLocationsPlugin: Copied assimilated migrations from $sourceDir to $targetDir")
        } else {
            project.logger.warn("FlywayBetterLocationsPlugin: Source directory $sourceDir does not exist, skipping migration copy")
        }
    }

    private fun registerInvalidateMigrationsTask(project: Project): TaskProvider<InvalidateMigrationsTask> {
        return project.tasks.register<InvalidateMigrationsTask>(INVALIDATE_MIGRATIONS_TASK) {
            group = PLUGIN_GROUP
            description = "Invalidate Flyway cache"
            dependsOn(PROCESS_RESOURCES_TASK)
        }
    }

    private fun configureFlywayMigrate(
        project: Project,
        invalidateMigrationsTask: TaskProvider<InvalidateMigrationsTask>
    ) {
        project.tasks.named(FLYWAY_MIGRATE_TASK) {
            dependsOn(invalidateMigrationsTask)

            // Only continue flyway migrate if input files for flyway have changes since last run
            // Running flywayMigrate would otherwise bust the jOOQ cache even with no changes were made
            // Since flywayMigrate modifies the flyway output files (database)
            onlyIf {
                val databaseFile = project.layout.buildDirectory
                    .file("generated/flyway/database.mv.db")
                    .get()
                    .asFile

                invalidateMigrationsTask.get().didWork || !databaseFile.exists()
            }

            // Track migration-state.txt to invalidate cache
            inputs.files(invalidateMigrationsTask.flatMap { it.outputFile })

            // Declare generated outputs
            outputs.files(
                project.layout.buildDirectory.file("generated/flyway/database.mv.db")
            )
        }
    }

    private fun configureJooqCodegen(
        project: Project,
        invalidateMigrationsTask: TaskProvider<InvalidateMigrationsTask>
    ) {
        project.tasks.named(JOOQ_CODEGEN_TASK) {
            dependsOn(FLYWAY_MIGRATE_TASK)

            // Track migration-state.txt to invalidate cache
            inputs.files(invalidateMigrationsTask.flatMap { it.outputFile })

            // Declare generated outputs
            outputs.dirs(
                project.layout.buildDirectory.dir("generated-sources/jooq")
            )
        }
    }
}