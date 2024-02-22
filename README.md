<h1 align="center">Paper Plugin Template (Gradle)</h1>
<p align="center">
    <img src="https://img.shields.io/github/license/Alathra/Template-Gradle-Plugin?color=blue&style=flat-square" alt="license"/>
    <img alt="GitHub release (latest by SemVer including pre-releases)" src="https://img.shields.io/github/downloads-pre/Alathra/Template-Gradle-Plugin/latest/total?style=flat-square">
    <img alt="GitHub release" src="https://img.shields.io/github/downloads-pre/Alathra/Template-Gradle-Plugin/latest?style=flat-square">
    <img alt="GitHub Workflow Status (with event)" src="https://img.shields.io/github/actions/workflow/status/Alathra/Template-Gradle-Plugin/release.yml?style=flat-square">
    <img alt="GitHub issues" src="https://img.shields.io/github/issues/Alathra/Template-Gradle-Plugin?style=flat-square">
</p>

---

## Description

This is a configured and ready to go template for making Minecraft plugins. It includes a plethora of useful
boilerplate, libraries and examples to quickly get you going when creating a new plugin.

All of the included libraries and tooling has been hand-picked for its stability and extendability. Its purpose is to
minimize duplicated code, while providing powerful and ergonomic ways of working with the Bukkit API, databases,
configuration and repository maintenance.

---

## Information

---

### Template Features

> [!NOTE]
> Is there some feature/library you don't need? Remove it!

**GitHub Setup**:

- Issue Templates - _Templates to streamline bug reports and feature requests._
- Funding -
  _[You can configure your sponsor button by editing a FUNDING.yml...](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/displaying-a-sponsor-button-in-your-repository)._
- Codeowners -
  _[Listed users are automatically requested for review when...](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/about-code-owners#about-code-owners)._
- Contributor Code of Conduct - _[A code of conduct for all contributors to follow.](./CODE_OF_CONDUCT.md)_
- Contributing Guidelines - _[Provides a guide and technical specifications regarding your project.](./CONTRIBUTING.md)_
- License -
  _[Change this to whatever license you wish to use](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/licensing-a-repository)._

**Automation**:

- Stale Workflow - _Issues and PRs are marked as stale after 60 days._
- Build Workflow - _Each commit builds a snapshot jar for testing._
- Release Workflow - _Automated releases and pre-releases using tags._
- Dependabot - _Dependabot will create PRs to keep your dependencies up-to date._

**Specifications**:

- Commit Messages: [Conventional Commits](https://conventionalcommits.org/)
- Versioning: [Semantic Versioning 2.0.0](https://semver.org/)
- Style Guide: `.editorconfig`

**Versioning Strategy**:

- Stable releases as: `ExamplePlugin-1.0.0.jar`
- Testing releases as: `ExamplePlugin-1.0.0-RC-X.jar`
- Development releases as: `ExamplePlugin-1.0.0-SNAPSHOT-X.jar`

| GitHub Event               | Version Format        | CI Action             | GitHub Release Draft? |
|----------------------------|-----------------------|-----------------------|-----------------------|
| Tag `X.Y.Z`                | `X.Y.Z`               | Build, test & release | Release               |
| Tag `X.Y.Z-RC-N`           | `X.Y.Z-RC-N`          | Build, test & release | Pre-release           |
| Schedule                   | `X.Y.Z-SNAPSHOT-TIME` | Build & test          | No                    |
| Push to `main` or `master` | `X.Y.Z-SNAPSHOT-TIME` | Build & test          | No                    |
| Pull Request               | `X.Y.Z-SNAPSHOT-TIME` | Build & test          | No                    |

---

### Template Libraries

* MiniMessage support using [Adventure](https://docs.advntr.dev/index.html) with utility
  library [ColorParser](https://github.com/milkdrinkers/ColorParser).
* Command creation and handling using [CommandAPI](https://github.com/JorelAli/CommandAPI).
* GUIs using [Triumph GUI](https://github.com/TriumphTeam/triumph-gui).
* YAML, JSON & TOML Configuration files using [Crate](https://github.com/milkdrinkers/Crate).
* Database Setup & Tooling:
    - Database versioning and migrations using [Flyway](https://flywaydb.org/).
    - [jOOQ](https://www.jooq.org/) to build and execute type safe SQL queries.
    - Uses [HikariCP](https://github.com/brettwooldridge/HikariCP) to manage the connection pool.
    - Supports the following database engines out of the box:
        - [HyperSQL](https://hsqldb.org/) (_Local_)
        - [H2](https://www.h2database.com/html/main.html) (_Local_)
        - [MySQL](https://www.mysql.com/) (_Remote_)
        - [MariaDB](https://mariadb.com/docs/skysql-previous-release/connect/programming-languages/java/) (_Remote_)

---

## Documentation Links

### Adventure Library

* **ColorParser** - [Link](https://github.com/milkdrinkers/ColorParser)
* **MiniMessage Formatting** - [Link](https://docs.advntr.dev/minimessage/format.html)
* **MiniMessage Previewer** - [Link](https://webui.advntr.dev/)
* **Adventure Documentation** - [Link](https://docs.advntr.dev/index.html)

### Minecraft APIs

* **CommandAPI** - [Link](https://commandapi.jorel.dev/latest.html)
* **Triumph GUI** - [Link](https://triumphteam.dev/library/triumph-gui/introduction)
* **Crate** - [Link](https://milkdrinkers.github.io/Crate/introduction)

### Database Tooling

* **Flyway** - [Link](https://documentation.red-gate.com/fd/quickstart-how-flyway-works-184127223.html)
* **jOOQ** - [Link](https://www.jooq.org/doc/latest/manual/getting-started/)

### Gradle Plugin.yml Generation

* **Plugin.yml Generator** - [Link](https://github.com/Minecrell/plugin-yml#bukkit)

---

## Setup

1. #### Change [.github/CODEOWNERS](./.github/CODEOWNERS)
   Replace `GITHUB_USERNAME` with your GitHub username.
    ```CODEOWNERS
    *       @darksaid98 @SOME_OTHER_USER
    ```
2. #### Change [.github/FUNDING.yml](./.github/FUNDING.yml)
   Replace `GITHUB_USERNAME` with your GitHub
   username. [You can configure your sponsor button by editing the FUNDING.yml](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/displaying-a-sponsor-button-in-your-repository).

   ```FUNDING.yml
   github: darksaid98
   github: SOME_OTHER_USER
   ```
3. #### Change [Code of Conduct](./CODE_OF_CONDUCT.md)
   If you choose to adopt the Code of Conduct in this template, please replace `GITHUB_CONTACT_EMAIL` at line 63 with
   your preferred method of contact. Otherwise replace or remove it.
4. #### Change [Project Name](./settings.gradle.kts)
   Replace all occurances of `ExamplePlugin` with your new plugin name. Don't forget to rename the main
   class [ExamplePlugin.java](./src/main/java/com/github/ExampleUser/ExamplePlugin/ExamplePlugin.java) to the same
   value.
   ```kotlin
   rootProject.name = "MyNamePlugin"
   ```
5. #### Change [build.gradle.kts](./build.gradle.kts)
    1. ##### Change Plugin Info
       > The final package path for your plugin will end up being something like `io.github.darksaid98.exampleplugin`. It's made up of these components `io.github.<USERNAME>.<PLUGINNAME>` where `<USERNAME>` is your github name in lowercase, and `<PLUGINNAME>` is added by `rootProject.name` lowercased.
       
       Don't forget to change package locations in `src/main/java/` when changing the group.
       ```kotlin
       group = "io.github.darksaid98"
       version = "0.9.8"
       description = "Some plugin description here..."
       ```
    2. ##### Change Plugin.yml
       > [!NOTE]
       > The plugin.yml is automatically generated by gradle.
       
       Update the authors list and any other required settings.
       ```kotlin
       authors = listOf("GITHUB_USERNAME") // Replace with your username
       contributors = listOf()
       apiVersion = "1.19"
 
       // Misc properties
       load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.POSTWORLD // STARTUP or POSTWORLD
       depend = listOf()
       softDepend = listOf()
       ```
    3. ##### Change Dependencies
       Lastly, remove/add any dependecies you don't need/want.

       > You can also make the development server install any plugin dependencies:
       ```kotlin
       downloadPlugins {
           //modrinth("carbon", "2.1.0-beta.21")
           //github("jpenilla", "MiniMOTD", "v2.0.13", "minimotd-bukkit-2.0.13.jar")
           //hangar("squaremap", "1.2.0")
           //url("https://download.luckperms.net/1515/bukkit/loader/LuckPerms-Bukkit-5.4.102.jar")
           github("MilkBowl", "Vault", "1.7.3", "Vault.jar")
       }
       ```

---

## Testing & Debugging

The template includes [jpenilla's run-task](https://github.com/jpenilla/run-task) gradle plugin. This allows you to
easily setup and run a development server for your plugin.

By default IntelliJ IDEA has excellent integration for debugging, and enables running your test server with a debugger
attached, in one click.

If using other IDEs you can connect a debugger to port `5005` which the development server listens on by default.

### Development Server

1. Run `gradlew runServer` to start a minecraft server on `localhost:25565`.

### Debugging (_IntelliJ IDEA_)

The Development Server is configured to work with the IntelliJ Debugger by default.

Simply press the `Debug` button to launch your Development Server with a Debugger attached.
![IntelliJ Debugger](https://i.imgur.com/vr9VRTs.png)

### Debugging (External Debugger)

> [!NOTE]
> The steps here are mirrored
> from [PaperMC's Guide](https://docs.papermc.io/paper/dev/debugging#using-a-remote-debugger) and are meant for IntelliJ
> IDEA. Other IDEs may be similar.

1. Open the `Run/Debug Configurations` page by clicking `Edit Configurations...`.
   ![Edit Configuration Image](https://i.imgur.com/rO4wXXN.png)

2. Click the `+` button in the _top left_ and select `Remote JVM Debug`.

3. Name the config whatever you want (_like Debug_), then hit `Apply`.
   ![Configuration Image](https://i.imgur.com/4M0LgZU.png)

> [!NOTE]
> The Development Server listens for Debuggers on port 5005.

4. When your Development Server is running, connect your debugger by pressing the `Debug` button.
   ![Debug Button Image](https://i.imgur.com/5lEvNVT.png)

---

## Credits

- **[leviem1:](https://github.com/leviem1)** _For their excellent [__Spigot plugin-template__](https://github.com/CrimsonWarpedcraft/plugin-template) which this was originally a fork of. I highly recommend their more minimalistic and much less opinionated template._
- **[A248:](https://github.com/A248)** _For exposing me to Flyway and jOOQ, inspiring me to include it in this template. I highly recommend you check out their projects [LibertyBans](https://github.com/A248/LibertyBans) & [MorePaperLib](https://github.com/A248/MorePaperLib) which are of exceptional quality._