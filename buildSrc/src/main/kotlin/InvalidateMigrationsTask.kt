import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File
import java.security.MessageDigest

/**
 * A task that generates a migration state file based on the current state of Flyway migrations, this provides cache support for Flyway preventing unnecessary migrations and jOOQ builds from being run.
 */
abstract class InvalidateMigrationsTask : DefaultTask() {
    companion object {
        private const val DEFAULT_CHECKSUM_FILE = "migration-state.txt"
        private const val DEFAULT_MIGRATION_PATH = "src/main/resources/db/migration"
    }

    // Flyway migration sources
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val trackedFilesystemMigrations: ConfigurableFileCollection

    // Flyway migration sources
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val trackedClasspathMigrations: ConfigurableFileCollection

    // Gradle build configuration
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val trackedBuildScripts: ConfigurableFileCollection

    // Gradle build logic
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val trackedBuildLogic: ConfigurableFileCollection

    @get:Input
    abstract val checksumFileName: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        // Set default values
        checksumFileName.convention(DEFAULT_CHECKSUM_FILE)
        outputFile.convention(
            project.layout.buildDirectory.file(
                checksumFileName.map { "tmp/invalidateMigrations/$it" }
            )
        )

        // Configure default file collections
        configureDefaultInputs()
    }

    private fun configureDefaultInputs() {
        // Flyway filesystem migrations
        trackedFilesystemMigrations.from(
            project.fileTree(project.layout.projectDirectory.dir(DEFAULT_MIGRATION_PATH)) {
                include("**/*.sql")
            }
        )

        // Flyway classpath migrations
        val mainPackage = project.findProperty("mainPackage") as? String
        if (mainPackage != null) {
            val classpathDir = "src/main/java/${mainPackage.replace('.', '/')}/database/migration/migrations"
            trackedClasspathMigrations.from(
                project.fileTree(project.layout.projectDirectory.dir(classpathDir)) {
                    include("**/*.java", "**/*.kt")
                }
            )
        }

        // Build scripts
        trackedBuildScripts.from(
            project.rootProject.allprojects.map { it.buildFile }
        )
        trackedBuildScripts.from(
            project.rootProject.files(
                "settings.gradle.kts",
                "settings.gradle",
                "gradle/libs.versions.toml"
            ).filter { it.exists() }
        )
        trackedBuildScripts.from(
            project.files(
                "settings.gradle.kts",
                "settings.gradle",
                "gradle/libs.versions.toml"
            ).filter { it.exists() }
        )

        // Build logic
        trackedBuildLogic.from(
            project.rootProject.fileTree("buildSrc/src/main/kotlin") {
                include("**/*.kt")
            }
        )
        trackedBuildLogic.from(
            project.rootProject.files("build.gradle.kts", "build.gradle").filter { it.exists() }
        )
    }

    @TaskAction
    fun generateMigrationState() {
        val outputFile = outputFile.get().asFile
        outputFile.parentFile.mkdirs()

        val migrationState = buildString {
            appendFileStates("filesystem", trackedFilesystemMigrations.files)
            appendFileStates("classpath", trackedClasspathMigrations.files)
            appendFileStates("buildscript", trackedBuildScripts.files)
            appendFileStates("buildlogic", trackedBuildLogic.files)
        }

        outputFile.writeText(migrationState)

        logger.info("Generated migration state file: ${outputFile.absolutePath}")
    }

    private fun StringBuilder.appendFileStates(category: String, files: Set<File>) {
        files.forEach { file ->
            try {
                val hash = calculateFileHash(file) // Try hashing file
                appendLine("$category:${file.canonicalPath}:$hash")
            } catch (e: Exception) { // Fallback to last modified timestamp if hashing fails
                logger.warn("Failed to calculate hash for ${file.absolutePath}: ${e.message}")
                appendLine("$category:${file.canonicalPath}:lastmod-${file.lastModified()}")
            }
        }
    }

    private fun calculateFileHash(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = file.readBytes()
        val hashBytes = digest.digest(bytes)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}