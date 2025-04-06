import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * A custom caching implementation for Flyway and jOOQ to work seamlessly together.
 */
abstract class InvalidateMigrationsTask : DefaultTask() {
    // Flyway migration sources
    @get:InputFiles
    var trackedFilesystemMigrations: Set<File> = project.fileTree(
        project.layout.projectDirectory.dir("src/main/resources/db/migration")
    ).files


    // Flyway migration sources
    @get:InputFiles
    var trackedClasspathMigrations: Set<File> = run {
        val mainPackage = project.findProperty("mainPackage") as String
        val classpathDir = "src/main/java/${mainPackage.replace('.', '/')}/database/migration/migrations"
        project.fileTree(project.layout.projectDirectory.dir(classpathDir)).files
    }

    // Gradle build configuration
    @get:InputFiles
    var trackedBuildScripts: Set<File> = project.rootProject.allprojects.map { subproject ->
        subproject.buildFile
    }.toSet() +
        setOfNotNull(
            project.rootProject.file("settings.gradle.kts").takeIf { it.exists() },
            project.rootProject.file("settings.gradle").takeIf { it.exists() },
            project.rootProject.file("gradle/libs.versions.toml").takeIf { it.exists() }
        ) +
        setOfNotNull(
            project.file("settings.gradle.kts").takeIf { it.exists() },
            project.file("settings.gradle").takeIf { it.exists() },
            project.file("gradle/libs.versions.toml").takeIf { it.exists() }
        )


    // Gradle build logic
    @get:InputFiles
    var trackedBuildLogic: Set<File> = project.rootProject.fileTree("buildSrc/src/main/kotlin").files.toSet() +
        project.rootProject.file("build.gradle.kts") +
        project.rootProject.file("build.gradle")

    @get:Input
    var checksumFileName = "migration-state.txt"

    // Declare checksum file as output
    @get:OutputFile
    val outputFile: Provider<RegularFile> = project.layout.buildDirectory.file(
        project.provider { "tmp/invalidateMigrations/${checksumFileName}" }
    )

    @TaskAction
    fun action() {
        // Output a marker file with hash information
            outputFile.get().asFile.parentFile.mkdirs()

            val migrationState = buildString {
                // Record state of filesystem migrations
                trackedFilesystemMigrations.forEach { file ->
                    appendLine("filesystem:${file.name}:${file.lastModified()}")
                }

                // Record state of classpath migrations
                trackedClasspathMigrations.forEach { file ->
                    appendLine("classpath:${file.name}:${file.lastModified()}")
                }

                // Record state of build scripts
                trackedBuildScripts.forEach { file ->
                    appendLine("buildscript:${file.name}:${file.lastModified()}")
                }

                // Record state of build logic (buildSrc)
                trackedBuildLogic.forEach { file ->
                    appendLine("buildlogic:${file.name}:${file.lastModified()}")
                }
            }

            outputFile.get().asFile.writeText(migrationState)
    }
}