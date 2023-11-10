package com.github.ExampleUser.ExamplePlugin;

import com.github.ExampleUser.ExamplePlugin.command.CommandHandler;
import com.github.ExampleUser.ExamplePlugin.config.ConfigHandler;
import com.github.ExampleUser.ExamplePlugin.db.DatabaseHandler;
import com.github.ExampleUser.ExamplePlugin.listener.ListenerHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ExamplePlugin extends JavaPlugin {
    private static ExamplePlugin instance;
    private ConfigHandler configHandler;
    private DatabaseHandler DatabaseHandler;
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
