package com.github.ExampleUser.ExamplePlugin;

import com.github.ExampleUser.ExamplePlugin.command.CommandHandler;
import com.github.ExampleUser.ExamplePlugin.config.ConfigHandler;
import com.github.ExampleUser.ExamplePlugin.listener.ListenerHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Main extends JavaPlugin {
    private static Main instance;
    private ConfigHandler configHandler;
    private CommandHandler commandHandler;
    private ListenerHandler listenerHandler;

    public static Main getInstance() {
        return instance;
    }

    public void onLoad() {
        instance = this;
        configHandler = new ConfigHandler(instance);
        commandHandler = new CommandHandler(instance);
        listenerHandler = new ListenerHandler(instance);

        configHandler.onLoad();
        commandHandler.onLoad();
        listenerHandler.onLoad();
    }

    public void onEnable() {
        configHandler.onEnable();
        commandHandler.onEnable();
        listenerHandler.onEnable();
    }

    public void onDisable() {
        configHandler.onDisable();
        commandHandler.onDisable();
        listenerHandler.onDisable();
    }

    @NotNull
    public ConfigHandler getConfigHandler() {
        return configHandler;
    }
}
