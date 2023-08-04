package com.github.ExampleUser.ExamplePlugin.listener;

import com.github.ExampleUser.ExamplePlugin.Main;
import com.github.ExampleUser.ExamplePlugin.Reloadable;

/**
 * A class to handle registration of event listeners.
 */
public class ListenerHandler implements Reloadable {
    private final Main main;

    public ListenerHandler(Main main) {
        this.main = main;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        // Register listeners here
        //instance.getServer().getPluginManager().registerEvents(new PlayerJoinListener(instance), instance);
    }

    @Override
    public void onDisable() {
    }
}
