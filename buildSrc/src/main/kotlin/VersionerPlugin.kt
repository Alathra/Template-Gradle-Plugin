import org.gradle.api.Plugin
import org.gradle.api.Project
import java.time.Instant

/**
 * A plugin to help in dealing with versioning in Gradle projects.
 */
abstract class VersionerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Execute version parser code
        applyCustomVersion(project)
    }

    private fun applyCustomVersion(project: Project) {
        // Inherit root project version
        project.version = project.rootProject.version

        // Apply custom version arg or append snapshot version
        var version = project.properties["altVer"]?.toString() ?: "${project.version}-SNAPSHOT-${Instant.now().epochSecond}"

        // Strip leading "v" from version tag
        if (version.first().equals('v', true))
            version = version.substring(1)

        // Uppercase version and assign
        project.version = version.uppercase()
    }
}