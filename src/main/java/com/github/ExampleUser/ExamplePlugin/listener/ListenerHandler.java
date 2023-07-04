package com.github.ExampleUser.ExamplePlugin.listener;

import com.github.ExampleUser.ExamplePlugin.Main;

/**
 * A class to handle registration of event listeners.
 */
public class ListenerHandler {
    private final Main instance;

    public ListenerHandler(Main instance) {
        this.instance = instance;
    }

    public void onLoad() {

    }

    public void onEnable() {
        // Register listeners here
        //instance.getServer().getPluginManager().registerEvents(new PlayerJoinListener(instance), instance);
    }

    public void onDisable() {

    }
}
