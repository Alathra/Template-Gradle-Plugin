package com.github.ExampleUser.ExamplePlugin.command;

import com.github.ExampleUser.ExamplePlugin.Main;
import com.github.ExampleUser.ExamplePlugin.Reloadable;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;

/**
 * A class to handle registration of commands.
 */
public class CommandHandler implements Reloadable {
    private final Main main;

    public CommandHandler(Main main) {
        this.main = main;
    }

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(main).shouldHookPaperReload(true).silentLogs(true));
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();

        // Register commands here
        new ExampleCommand();
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
    }
}