<h1 align="center">Paper Plugin Template (Gradle)</h1>
<p align="center">
    <img src="https://img.shields.io/github/license/Alathra/Template-Gradle-Plugin?color=blue&style=flat-square" alt="license"/>
</p>

---

## Description

This is a configured and ready to go template for making Minecraft plugins. It includes a plethora of useful libraries and examples to quickly get you going quickly when creating a new plugin. 

All of the included libraries have been hand-picked for their stability and extendability. Their purpose is to minimize duplicated code, while providing powerful and ergonomic ways of working with the Bukkit API.

---

## Features/Libraries

* MiniMessage support from [Adventure](https://docs.advntr.dev/index.html) with utility library [ColorParser](https://github.com/milkdrinkers/ColorParser). 
* Commands using [CommandAPI](https://github.com/JorelAli/CommandAPI).
* GUIs using [Triumph GUI](https://github.com/TriumphTeam/triumph-gui).
* Configs using [SimplixStorage](https://github.com/milkdrinkers/SimplixStorage).
* SQL Storage using [HikariCP](https://github.com/brettwooldridge/HikariCP) with [MariaDB](https://mariadb.com/docs/skysql-previous-release/connect/programming-languages/java/) or [SQLite](https://www.sqlite.org/index.html).

---

## Useful Links

### Text/Formatting
* **ColorParser** - [Link](https://github.com/milkdrinkers/ColorParser)
* **MiniMessage Formatting** - [Link](https://docs.advntr.dev/minimessage/format.html)
* **MiniMessage Previewer** - [Link](https://webui.advntr.dev/)
* **Adventure Documentation** - [Link](https://docs.advntr.dev/index.html)

### Commands
* **Documentation** - [Link](https://commandapi.jorel.dev/latest.html)

### GUIs
* **Documentation** - [Link](https://triumphteam.dev/library/triumph-gui/introduction)

### Configs
* **Wiki** - [Link](https://github.com/milkdrinkers/SimplixStorage/wiki)

### Gradle & Plugin.yml
* **Plugin.yml Documentation** - [Link](https://github.com/Minecrell/plugin-yml#bukkit)

---

## Testing & Debugging

The template includes [jpenilla's run-task](https://github.com/jpenilla/run-task) gradle plugin. This allows you to easily setup and run a development server for your plugin.

By default IntelliJ IDEA has excellent integration for debugging, and enables running your test server with a debugger attached, in one click.

If using other IDEs you can connect a debugger to port `5005` which the development server listens on by default.

### Development Server

1. Run `gradlew runServer` to start a minecraft server on `localhost:25565` (_The first time you launch the server you will need to accept the Minecraft EULA, found in `/run/eula.txt`_).

### Debugging (_IntelliJ IDEA_)

The Development Server is configured to work with the IntelliJ Debugger by default.

Simply press the `Debug` button to launch your Development Server with a Debugger attached.
![IntelliJ Debugger](https://i.imgur.com/vr9VRTs.png)

### Debugging (External Debugger)
> The steps here are mirrored from [PaperMC's Guide](https://docs.papermc.io/paper/dev/debugging#using-a-remote-debugger) and are meant for IntelliJ IDEA. Other IDEs may be similar.

1. Open the `Run/Debug Configurations` page by clicking `Edit Configurations...`.
![Edit Configuration Image](https://i.imgur.com/rO4wXXN.png)

2. Click the `+` button in the _top left_ and select `Remote JVM Debug`.

3. Name the config whatever you want (_like Debug_), then hit `Apply`.
![Configuration Image](https://i.imgur.com/4M0LgZU.png)

> The Development Server listens for Debuggers on port 5005.
4. When your Development Server is running, connect your debugger by pressing the `Debug` button.
![Debug Button Image](https://i.imgur.com/5lEvNVT.png)