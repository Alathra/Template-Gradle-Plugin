plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("script-runtime"))
}

gradlePlugin {
    plugins {
        create("projectextensions") {
            id = "projectextensions"
            implementationClass = "ProjectExtensionsPlugin"
        }
    }
    plugins {
        create("versioner") {
            id = "versioner"
            implementationClass = "VersionerPlugin"
        }
    }
    plugins {
        create("flywaypatches") {
            id = "flywaypatches"
            implementationClass = "FlywayPatchesPlugin"
        }
    }
}