import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * A task allowing Flyway to support different RDBMS specific migrations while having a common folder for shared migrations.
 *
 * This task will:
 * 1. Create temp dir for parsed flyway migrations
 * 2. Copy RDBMS specific migrations to the temp dir
 * 3. Copy non-clashing common migrations to the temp dir (Common migrations are overridden by RDBMS specific migrations)
 */
abstract class AssimilateMigrationsTask : DefaultTask() {
    companion object {
        private const val DEFAULT_MIGRATION_PATH = "src/main/resources/db/migration"
        private const val SQL_EXTENSION = "sql"
    }

    // Flyway migration sources
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val resourceMigrationDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    init {
        // Set default values using convention
        resourceMigrationDir.convention(
            project.layout.projectDirectory.dir(DEFAULT_MIGRATION_PATH)
        )
        outputDir.convention(
            project.layout.buildDirectory.dir("tmp/assimilateMigrations")
        )
    }

    @TaskAction
    fun assimilateMigrations() {
        val outputDir = outputDir.get().asFile
        val inputDir = resourceMigrationDir.get().asFile

        // Validate input directory exists
        if (!inputDir.exists()) {
            logger.warn("Migration directory does not exist: ${inputDir.absolutePath}")
            return
        }

        // Clean and prepare output directory
        outputDir.deleteRecursively()
        outputDir.mkdirs()

        // Get all common migrations from the resource directory
        val commonMigrations = inputDir.walkTopDown()
            .filter { it.isFile && it.extension == SQL_EXTENSION }
            .associateBy { it.name }

        logger.info("Found ${commonMigrations.size} common migrations")

        // Process each specific directory
        val rdbmsDirectories = inputDir.listFiles { file -> file.isDirectory } ?: emptyArray()

        rdbmsDirectories.forEach { rdbmsDir ->
            processRdbmsDirectory(rdbmsDir, commonMigrations, outputDir)
        }

        logger.info("Assimilated migrations for ${rdbmsDirectories.size} RDBMS types")
    }

    private fun processRdbmsDirectory(
        rdbmsDir: File,
        commonMigrations: Map<String, File>,
        outputDir: File
    ) {
        val rdbmsName = rdbmsDir.name
        val specificMigrations = rdbmsDir.walkTopDown()
            .filter { it.isFile && it.extension == SQL_EXTENSION }
            .associateBy { it.name }

        val allVersions = (commonMigrations.keys + specificMigrations.keys).toSet()
        val rdbmsOutputDir = outputDir.resolve(rdbmsName)
        rdbmsOutputDir.mkdirs()

        logger.info("Processing $rdbmsName: ${specificMigrations.size} specific, ${commonMigrations.size} common migrations")

        // Copy migration files with priority: specific > common
        allVersions.forEach { fileName ->
            val sourceFile = specificMigrations[fileName] ?: commonMigrations[fileName]
            sourceFile?.let { source ->
                val targetFile = rdbmsOutputDir.resolve(fileName)
                source.copyTo(targetFile, overwrite = true)

                val sourceType = if (specificMigrations.containsKey(fileName)) "specific" else "common"
                logger.debug("Copied $sourceType migration: $fileName to $rdbmsName")
            }
        }
    }
}