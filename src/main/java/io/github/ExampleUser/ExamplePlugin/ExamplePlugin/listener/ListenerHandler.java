package io.github.ExampleUser.ExamplePlugin.ExamplePlugin.listener;

import io.github.ExampleUser.ExamplePlugin.ExamplePlugin.ExamplePlugin;
import io.github.ExampleUser.ExamplePlugin.ExamplePlugin.Reloadable;

/**
 * A class to handle registration of event listeners.
 */
public class ListenerHandler implements Reloadable {
    private final ExamplePlugin examplePlugin;

    public ListenerHandler(ExamplePlugin examplePlugin) {
        this.examplePlugin = examplePlugin;
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
