package io.github.ExampleUser.ExamplePlugin.ExamplePlugin;

import io.github.ExampleUser.ExamplePlugin.ExamplePlugin.command.CommandHandler;
import io.github.ExampleUser.ExamplePlugin.ExamplePlugin.config.ConfigHandler;
import io.github.ExampleUser.ExamplePlugin.ExamplePlugin.db.DatabaseHandler;
import io.github.ExampleUser.ExamplePlugin.ExamplePlugin.listener.ListenerHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ExamplePlugin extends JavaPlugin {
    private static ExamplePlugin instance;
    private ConfigHandler configHandler;
    private io.github.ExampleUser.ExamplePlugin.ExamplePlugin.db.DatabaseHandler DatabaseHandler;
    private CommandHandler commandHandler;
    private ListenerHandler listenerHandler;

    public static ExamplePlugin getInstance() {
        return instance;
    }

    public void onLoad() {
        instance = this;
        configHandler = new ConfigHandler(instance);
        DatabaseHandler = new DatabaseHandler(instance);
        commandHandler = new CommandHandler(instance);
        listenerHandler = new ListenerHandler(instance);

        configHandler.onLoad();
        DatabaseHandler.onLoad();
        commandHandler.onLoad();
        listenerHandler.onLoad();
    }

    public void onEnable() {
        configHandler.onEnable();
        DatabaseHandler.onEnable();
        commandHandler.onEnable();
        listenerHandler.onEnable();
    }

    public void onDisable() {
        configHandler.onDisable();
        DatabaseHandler.onDisable();
        commandHandler.onDisable();
        listenerHandler.onDisable();
    }

    @NotNull
    public DatabaseHandler getDataHandler() {
        return DatabaseHandler;
    }

    @NotNull
    public ConfigHandler getConfigHandler() {
        return configHandler;
    }
}
