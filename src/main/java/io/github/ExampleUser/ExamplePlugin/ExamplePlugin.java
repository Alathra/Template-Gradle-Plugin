package io.github.ExampleUser.ExamplePlugin;

import io.github.ExampleUser.ExamplePlugin.command.CommandHandler;
import io.github.ExampleUser.ExamplePlugin.config.ConfigHandler;
import io.github.ExampleUser.ExamplePlugin.db.DatabaseHandler;
import io.github.ExampleUser.ExamplePlugin.listener.ListenerHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ExamplePlugin extends JavaPlugin {
    private static ExamplePlugin instance;
    private ConfigHandler configHandler;
    private DatabaseHandler databaseHandler;
    private CommandHandler commandHandler;
    private ListenerHandler listenerHandler;

    public static ExamplePlugin getInstance() {
        return instance;
    }

    public void onLoad() {
        instance = this;
        configHandler = new ConfigHandler(instance);
        databaseHandler = new DatabaseHandler(instance);
        commandHandler = new CommandHandler(instance);
        listenerHandler = new ListenerHandler(instance);

        configHandler.onLoad();
        databaseHandler.onLoad();
        commandHandler.onLoad();
        listenerHandler.onLoad();
    }

    public void onEnable() {
        configHandler.onEnable();
        databaseHandler.onEnable();
        commandHandler.onEnable();
        listenerHandler.onEnable();
    }

    public void onDisable() {
        configHandler.onDisable();
        databaseHandler.onDisable();
        commandHandler.onDisable();
        listenerHandler.onDisable();
    }

    @NotNull
    public DatabaseHandler getDataHandler() {
        return databaseHandler;
    }

    @NotNull
    public ConfigHandler getConfigHandler() {
        return configHandler;
    }
}
