package com.github.ExampleUser.ExamplePlugin;

import com.github.ExampleUser.ExamplePlugin.command.CommandHandler;
import com.github.ExampleUser.ExamplePlugin.listener.ListenerHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main instance;
    private CommandHandler commandHandler;
    private ListenerHandler listenerHandler;

    public static Main getInstance() {
        return instance;
    }

    public void onLoad() {
        instance = this;
        commandHandler = new CommandHandler(instance);
        listenerHandler = new ListenerHandler(instance);

        commandHandler.onLoad();
        listenerHandler.onLoad();
    }

    public void onEnable() {
        commandHandler.onEnable();
        listenerHandler.onEnable();
    }

    public void onDisable() {
        commandHandler.onDisable();
        listenerHandler.onDisable();
    }
}
