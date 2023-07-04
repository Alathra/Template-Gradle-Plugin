package com.github.ExampleUser.ExamplePlugin.command;

import com.github.ExampleUser.ExamplePlugin.Main;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;

/**
 * A class to handle registration of commands.
 */
public class CommandHandler {
    private final Main instance;

    public CommandHandler(Main instance) {
        this.instance = instance;
    }

    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(instance).shouldHookPaperReload(true).silentLogs(true));
    }

    public void onEnable() {
        CommandAPI.onEnable();

        // Register commands here
        new ExampleCommand();
    }

    public void onDisable() {
        CommandAPI.onDisable();
    }
}