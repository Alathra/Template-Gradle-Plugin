import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Utility plugin to add common project extensions methods.
 */
abstract class ProjectExtensionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Add the extensions to the project
        project.extensions.add("mainPackage", getMainPackage(project))
        project.extensions.add("entryPointClass", getEntryPointClass(project))
        project.extensions.add("relocationPackage", getRelocationPackage(project))
    }

    private fun getMainPackage(project: Project): String {
        return "${project.rootProject.group}.${project.rootProject.name.lowercase()}"
    }

    private fun getEntryPointClass(project: Project): String {
        return "${getMainPackage(project)}.${project.rootProject.name}"
    }

    private fun getRelocationPackage(project: Project): String {
        return "${getMainPackage(project)}.lib"
    }
}