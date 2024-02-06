package io.github.ExampleUser.ExamplePlugin.command;

import io.github.ExampleUser.ExamplePlugin.ExamplePlugin;
import io.github.ExampleUser.ExamplePlugin.Reloadable;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;

/**
 * A class to handle registration of commands.
 */
public class CommandHandler implements Reloadable {
    private final ExamplePlugin plugin;

    /**
     * Instantiates the Command handler.
     *
     * @param plugin the plugin
     */
    public CommandHandler(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(plugin).shouldHookPaperReload(true).silentLogs(true));
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