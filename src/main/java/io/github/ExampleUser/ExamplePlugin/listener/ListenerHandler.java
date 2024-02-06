package io.github.ExampleUser.ExamplePlugin.listener;

import io.github.ExampleUser.ExamplePlugin.ExamplePlugin;
import io.github.ExampleUser.ExamplePlugin.Reloadable;

/**
 * A class to handle registration of event listeners.
 */
public class ListenerHandler implements Reloadable {
    private final ExamplePlugin plugin;

    /**
     * Instantiates a the Listener handler.
     *
     * @param plugin the plugin instance
     */
    public ListenerHandler(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        // Register listeners here
        //plugin.getServer().getPluginManager().registerEvents(new PlayerJoinListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new VaultListener(), plugin);
    }

    @Override
    public void onDisable() {
    }
}
