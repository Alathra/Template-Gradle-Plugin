package com.github.ExampleUser.ExamplePlugin;

import com.github.ExampleUser.ExamplePlugin.command.CommandHandler;
import com.github.ExampleUser.ExamplePlugin.config.ConfigHandler;
import com.github.ExampleUser.ExamplePlugin.db.DBHandler;
import com.github.ExampleUser.ExamplePlugin.db.DBQueries;
import com.github.ExampleUser.ExamplePlugin.listener.ListenerHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Main extends JavaPlugin {
    private static Main instance;
    private ConfigHandler configHandler;
    private DBHandler DBHandler;
    private CommandHandler commandHandler;
    private ListenerHandler listenerHandler;

    public static Main getInstance() {
        return instance;
    }

    public void onLoad() {
        instance = this;
        configHandler = new ConfigHandler(instance);
        DBHandler = new DBHandler(instance);
        commandHandler = new CommandHandler(instance);
        listenerHandler = new ListenerHandler(instance);

        configHandler.onLoad();
        DBHandler.onLoad();
        commandHandler.onLoad();
        listenerHandler.onLoad();
        DBQueries.init();
    }

    public void onEnable() {
        configHandler.onEnable();
        DBHandler.onEnable();
        commandHandler.onEnable();
        listenerHandler.onEnable();
    }

    public void onDisable() {
        configHandler.onDisable();
        DBHandler.onDisable();
        commandHandler.onDisable();
        listenerHandler.onDisable();
    }

    @NotNull
    public DBHandler getDataHandler() {
        return DBHandler;
    }

    @NotNull
    public ConfigHandler getConfigHandler() {
        return configHandler;
    }
}
