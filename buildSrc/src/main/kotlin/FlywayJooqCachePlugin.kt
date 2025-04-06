import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

/**
 * A plugin providing cache support when using both jOOQ and Flyway.
 */
abstract class FlywayJooqCachePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // register task
        project.tasks.register<InvalidateMigrationsTask>("invalidateMigrations", InvalidateMigrationsTask::class.java) {
            group = "flywayjooq"
            description = "Invalidate Flyway cache"
        }

        project.tasks.named("flywayMigrate") {
            dependsOn("invalidateMigrations")

            // Only continue flyway migrate if input files for flyway have changes since last run
            // Running flywayMigrate would otherwise bust the jOOQ cache even with no changes were made
            // Since flywayMigrate modifies the flyway output files (database)
            onlyIf {
                project.tasks.named("invalidateMigrations").get().didWork ||
                    !project.file("${project.layout.buildDirectory.get()}/generated/flyway/database.mv.db").exists()
            }
        }

        project.tasks.named("jooqCodegen") {
            dependsOn("flywayMigrate")

            // Track migration-state.txt to invalidate cache
            inputs.files(
                project.tasks.withType(InvalidateMigrationsTask::class.java)["invalidateMigrations"].outputFile
            )

            // Declare generate outputs
            outputs.dirs(
                "${project.layout.buildDirectory.get()}/generated-sources/jooq"
            )
        }
    }
}