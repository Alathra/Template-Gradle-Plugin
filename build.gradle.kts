import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration
import org.jooq.meta.jaxb.Database
import org.jooq.meta.jaxb.Generator
import org.jooq.meta.jaxb.Jdbc
import java.time.Instant

plugins {
    `java-library`

    id("com.github.johnrengelman.shadow") version "8.1.1" // Shades and relocates dependencies, See https://imperceptiblethoughts.com/shadow/introduction/
    id("xyz.jpenilla.run-paper") version "2.2.2" // Adds runServer and runMojangMappedServer tasks for testing
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0" // Automatic plugin.yml generation
//    id("io.papermc.paperweight.userdev") version "1.5.9" // Used to develop internal plugins using Mojang mappings, See https://github.com/PaperMC/paperweight
    id("org.flywaydb.flyway") version "10.4.1" // Database migrations

    eclipse
    idea
}

group = "io.github.ExampleUser"
version = "1.0.5"
description = ""
val mainPackage = "${project.group}.${rootProject.name}"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_17.majorVersion)) // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
}

repositories {
    mavenCentral()

    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://mvn-repo.arim.space/lesser-gpl3/")

    maven("https://jitpack.io/") {
        content {
            includeGroup("com.github.milkdrinkers")
        }
    }
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.1.0")
    annotationProcessor("org.jetbrains:annotations:24.1.0")

    //paperweight.paperDevBundle("1.20.2-R0.1-SNAPSHOT") // Use instead of the `paper-api` entry if developing plugins using Mojang mappings
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    implementation("space.arim.morepaperlib:morepaperlib:latest.release")

    implementation("com.github.milkdrinkers:crate:1.1.0")
    implementation("com.github.milkdrinkers:colorparser:2.0.0") {
        exclude("net.kyori")
    }

    implementation("dev.jorel:commandapi-bukkit-shade:9.3.0")
//    compileOnly("dev.jorel:commandapi-annotations:9.3.0")
//    annotationProcessor("dev.jorel:commandapi-annotations:9.3.0")

    implementation("dev.triumphteam:triumph-gui:3.1.7") {
        exclude("net.kyori")
    }

    // Database Dependencies
    implementation("com.zaxxer:HikariCP:5.1.0")
    library("org.flywaydb:flyway-core:10.4.1")
    library("org.flywaydb:flyway-mysql:10.4.1")
    library("org.flywaydb:flyway-database-hsqldb:10.4.1")
    library("org.jooq:jooq:3.19.1")

    // JDBC Drivers
    library("org.hsqldb:hsqldb:2.7.2")
    library("com.h2database:h2:2.2.224")
    library("com.mysql:mysql-connector-j:8.2.0")
    library("org.mariadb.jdbc:mariadb-java-client:3.3.2")
}

tasks {
    // NOTE: Use when developing plugins using Mojang mappings
//    assemble {
//        dependsOn(reobfJar)
//    }

    build {
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
        options.compilerArgs.addAll(arrayListOf("-Xlint:all", "-Xlint:-processing", "-Xdiags:verbose"))
        
        // Generate jOOQ sources before compilation
        dependsOn(project.tasks.named("generateSources"))
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    shadowJar {
        archiveBaseName.set(project.name)
        archiveClassifier.set("")

        // Shadow classes
        fun reloc(originPkg: String, targetPkg: String) = relocate(originPkg, "${mainPackage}.lib.${targetPkg}")

        reloc("space.arim.morepaperlib", "morepaperlib")
        reloc("com.github.milkdrinkers.Crate", "crate")
        reloc("com.github.milkdrinkers.colorparser", "colorparser")
        reloc("dev.jorel.commandapi", "commandapi")
        reloc("dev.triumphteam.gui", "gui")
        reloc("com.zaxxer.hikari", "hikaricp")

        mergeServiceFiles {
            setPath("META-INF/services/org.flywaydb.core.extensibility.Plugin") // Fix Flyway overriding its own files
        }

        minimize()
    }

    runServer {
        // Configure the Minecraft version for our task.
        minecraftVersion("1.20.2")

        // IntelliJ IDEA debugger setup: https://docs.papermc.io/paper/dev/debugging#using-a-remote-debugger
        jvmArgs("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-DPaper.IgnoreJavaVersion=true", "-Dcom.mojang.eula.agree=true", "-DIReallyKnowWhatIAmDoingISwear")
        systemProperty("terminal.jline", false)
        systemProperty("terminal.ansi", true)

        // Automatically install dependencies
        downloadPlugins {
//            modrinth("carbon", "2.1.0-beta.21")
//            github("jpenilla", "MiniMOTD", "v2.0.13", "minimotd-bukkit-2.0.13.jar")
//            hangar("squaremap", "1.2.0")
//            url("https://download.luckperms.net/1515/bukkit/loader/LuckPerms-Bukkit-5.4.102.jar")
            github("MilkBowl", "Vault", "1.7.3", "Vault.jar")
        }
    }
}

bukkit { // Options: https://github.com/Minecrell/plugin-yml#bukkit
    // Plugin main class (required)
    main = "${mainPackage}.${rootProject.name}"

    // Plugin Information
    name = project.name
    prefix = project.name
    version = "${project.version}"
    description = "${project.description}"
    authors = listOf("GITHUB_USERNAME")
    contributors = listOf()
    apiVersion = "1.19"

    // Misc properties
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.POSTWORLD // STARTUP or POSTWORLD
    depend = listOf()
    softDepend = listOf()
}

flyway {
    url = "jdbc:h2:${project.layout.buildDirectory.get()}/generated/flyway/db;AUTO_SERVER=TRUE;MODE=MySQL;CASE_INSENSITIVE_IDENTIFIERS=TRUE;IGNORECASE=TRUE"
    user = "sa"
    password = ""
    placeholders = mapOf( // Substitute placeholders for flyway
        "tablePrefix" to "",
        "columnSuffix" to " VIRTUAL",
        "tableDefaults" to "",
        "uuidType" to "BINARY(16)",
        "inetType" to "VARBINARY(16)",
        "binaryType" to "BLOB",
        "alterViewStatement" to "ALTER VIEW",
    )
    validateMigrationNaming = true
    baselineOnMigrate = true
    cleanDisabled = false
    locations = arrayOf(
        "filesystem:src/main/resources/db/migration",
        "classpath:db/migration"
    )
}

task("generateSources") {
    this.group = "jooq"
    val dir = layout.buildDirectory.dir("generated-src/jooq").get()

    // Ensure database schema has been prepared by Flyway before generating the jOOQ sources
    dependsOn.add(tasks.flywayMigrate)

    // Declare Flyway migration scripts as inputs on the jOOQ task
    inputs.files(fileTree("src/main/resources/db/migration"), fileTree("src/main/java/${mainPackage}/db/flyway/migration"))
        .withPropertyName("migration files")
        .withPathSensitivity(PathSensitivity.RELATIVE)

    // jOOQ Generation Task
    doLast {
        GenerationTool.generate(Configuration()
            .withLogging(org.jooq.meta.jaxb.Logging.WARN)
            .withJdbc(Jdbc()
                .withDriver("org.h2.Driver")
                .withUrl(flyway.url)
                .withUser(flyway.user)
                .withPassword(flyway.password)
            )
            .withGenerator(Generator()
                .withName("org.jooq.codegen.DefaultGenerator")
                .withDatabase(Database()
                    .withName("org.jooq.meta.h2.H2Database")
                    .withIncludes(".*")
                    .withExcludes("(flyway_schema_history)|(?i:information_schema\\..*)|(?i:system_lobs\\..*)") // Exclude db specific files
                    .withInputSchema("PUBLIC")
                    .withSchemaVersionProvider("SELECT :schema_name || '_' || MAX(\"version\") FROM \"flyway_schema_history\"") // Grab version from Flyway
                )
                .withTarget(org.jooq.meta.jaxb.Target()
                    .withPackageName("${mainPackage}.db.schema")
                    .withDirectory(dir.toString())
                    .withClean(true)
                )
            )
        )
    }

    // Declare outputs
    outputs.dir(dir)
        .withPropertyName("jooq generated sources")
    sourceSets {
        get("main").java.srcDir(dir)
    }

    // Enable build caching
    outputs.cacheIf { true }
}

buildscript {
    dependencies {
        classpath("org.jooq:jooq:3.19.1")
        classpath("org.jooq:jooq-meta:3.19.1")
        classpath("org.jooq:jooq-codegen:3.19.1")
        classpath("com.h2database:h2:2.2.224")
    }
}

// Apply custom version arg
val versionArg = if (hasProperty("customVersion"))
    (properties["customVersion"] as String).uppercase() // Uppercase version string
else
    "${project.version}-SNAPSHOT-${Instant.now().epochSecond}" // Append snapshot to version

// Strip prefixed "v" from version tag
project.version = if (versionArg.first().equals('v', true))
    versionArg.substring(1)
else
    versionArg.uppercase()