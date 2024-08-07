import org.jooq.meta.jaxb.Logging
import java.time.Instant

plugins {
    `java-library`

    id("io.github.goooler.shadow") version "8.1.8" // Shades and relocates dependencies, See https://imperceptiblethoughts.com/shadow/introduction/
    id("xyz.jpenilla.run-paper") version "2.3.0" // Adds runServer and runMojangMappedServer tasks for testing
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0" // Automatic plugin.yml generation
//    id("io.papermc.paperweight.userdev") version "1.7.1" // Used to develop internal plugins using Mojang mappings, See https://github.com/PaperMC/paperweight
    id("org.flywaydb.flyway") version "10.15.2" // Database migrations
    id("org.jooq.jooq-codegen-gradle") version "3.19.10"

    eclipse
    idea
}

val mainPackage = "${project.group}.${project.name.lowercase()}"
applyCustomVersion()

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21)) // Configure the java toolchain. This allows gradle to auto-provision JDK 21 on systems that only have JDK 8 installed for example.
    withJavadocJar() // Enable Javadoc generation
//    withSourcesJar()
}

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://mvn-repo.arim.space/lesser-gpl3/")

    maven("https://maven.athyrium.eu/releases")

    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // PlaceholderAPI
    maven("https://repo.dmulloy2.net/repository/public/") // ProtocolLib
    maven("https://jitpack.io/") {
        content {
            includeGroup("com.github.MilkBowl") // VaultAPI
        }
    }
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.1.0")
    annotationProcessor("org.jetbrains:annotations:24.1.0")

    //paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT") // Use instead of the `paper-api` entry if developing plugins using Mojang mappings
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    implementation("space.arim.morepaperlib:morepaperlib:latest.release")

    implementation("com.github.milkdrinkers:crate:1.2.1")
    implementation("com.github.milkdrinkers:colorparser:2.0.3") {
        exclude("net.kyori")
    }

    implementation("dev.jorel:commandapi-bukkit-shade:9.5.1")
//    compileOnly("dev.jorel:commandapi-annotations:9.4.2")
//    annotationProcessor("dev.jorel:commandapi-annotations:9.4.2")

    implementation("dev.triumphteam:triumph-gui:3.1.10") {
        exclude("net.kyori")
    }

    implementation("org.bstats:bstats-bukkit:3.0.2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("me.clip:placeholderapi:2.11.6")

    // Database Dependencies
    implementation("com.zaxxer:HikariCP:5.1.0")
    library("org.flywaydb:flyway-core:10.15.2")
    library("org.flywaydb:flyway-mysql:10.15.2")
    library("org.flywaydb:flyway-database-hsqldb:10.15.2")
    library("org.jooq:jooq:3.19.10")
    jooqCodegen("com.h2database:h2:2.2.224")

    // JDBC Drivers
    library("org.hsqldb:hsqldb:2.7.3")
    library("com.h2database:h2:2.2.224")
    library("com.mysql:mysql-connector-j:9.0.0")
    library("org.mariadb.jdbc:mariadb-java-client:3.4.0")
}

tasks {
    // NOTE: Use when developing plugins using Mojang mappings
//    assemble {
//        dependsOn(reobfJar)
//    }

    build {
        dependsOn(shadowJar)
    }

    jooqCodegen {
        dependsOn(flywayMigrate)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(21)
        options.compilerArgs.addAll(arrayListOf("-Xlint:all", "-Xlint:-processing", "-Xdiags:verbose"))

        dependsOn(jooqCodegen) // Generate jOOQ sources before compilation
    }

    javadoc {
        isFailOnError = false
        exclude("${mainPackage.replace(".", "/")}/db/schema/**") // Exclude generated jOOQ sources from javadocs
        val options = options as StandardJavadocDocletOptions
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        options.overview = "src/main/javadoc/overview.html"
        options.tags("apiNote:a:API Note:", "implNote:a:Implementation Note:", "implSpec:a:Implementation Requirements:")
        options.use()
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
        reloc("org.bstats", "bstats")

        mergeServiceFiles {
            setPath("META-INF/services/org.flywaydb.core.extensibility.Plugin") // Fix Flyway overriding its own files
        }

        minimize()
    }

    runServer {
        // Configure the Minecraft version for our task.
        minecraftVersion("1.20.6")

        // IntelliJ IDEA debugger setup: https://docs.papermc.io/paper/dev/debugging#using-a-remote-debugger
        jvmArgs("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-DPaper.IgnoreJavaVersion=true", "-Dcom.mojang.eula.agree=true", "-DIReallyKnowWhatIAmDoingISwear", "-Dpaper.playerconnection.keepalive=6000")
        systemProperty("terminal.jline", false)
        systemProperty("terminal.ansi", true)

        // Automatically install dependencies
        downloadPlugins {
//            modrinth("carbon", "2.1.0-beta.21")
//            github("jpenilla", "MiniMOTD", "v2.0.13", "minimotd-bukkit-2.0.13.jar")
//            hangar("squaremap", "1.2.0")
//            url("https://download.luckperms.net/1515/bukkit/loader/LuckPerms-Bukkit-5.4.102.jar")
            github("MilkBowl", "Vault", "1.7.3", "Vault.jar")
            github("dmulloy2", "ProtocolLib", "5.2.0", "ProtocolLib.jar")
        }
    }
}

bukkit { // Options: https://github.com/Minecrell/plugin-yml#bukkit
    // Plugin main class (required)
    main = "${mainPackage}.${project.name}"

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
    softDepend = listOf("Vault", "ProtocolLib", "PlaceholderAPI")
}

flyway {
    url = "jdbc:h2:${project.layout.buildDirectory.get()}/generated/flyway/db;AUTO_SERVER=TRUE;MODE=MySQL;CASE_INSENSITIVE_IDENTIFIERS=TRUE;IGNORECASE=TRUE"
    user = "sa"
    password = ""
    schemas = listOf("PUBLIC").toTypedArray()
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

jooq {
    configuration {
        logging = Logging.ERROR
        jdbc {
            driver = "org.h2.Driver"
            url = flyway.url
            user = flyway.user
            password = flyway.password
        }
        generator {
            database {
                name = "org.jooq.meta.h2.H2Database"
                includes = ".*"
                excludes = "(flyway_schema_history)|(?i:information_schema\\..*)|(?i:system_lobs\\..*)"  // Exclude db specific files
                inputSchema = "PUBLIC"
                schemaVersionProvider = "SELECT :schema_name || '_' || MAX(\"version\") FROM \"flyway_schema_history\"" // Grab version from Flyway
            }
            target {
                packageName = "${mainPackage}.db.schema"
                withClean(true)
            }
        }
    }
}

fun applyCustomVersion() {
    // Apply custom version arg or append snapshot version
    val ver = properties["altVer"]?.toString() ?: "${rootProject.version}-SNAPSHOT-${Instant.now().epochSecond}"

    // Strip prefixed "v" from version tag
    rootProject.version = (if (ver.first().equals('v', true)) ver.substring(1) else ver.uppercase()).uppercase()
}