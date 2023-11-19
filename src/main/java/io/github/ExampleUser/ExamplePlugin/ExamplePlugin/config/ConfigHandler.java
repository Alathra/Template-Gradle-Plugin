package io.github.ExampleUser.ExamplePlugin.ExamplePlugin.config;

import io.github.ExampleUser.ExamplePlugin.ExamplePlugin.ExamplePlugin;
import io.github.ExampleUser.ExamplePlugin.ExamplePlugin.Reloadable;
import com.github.milkdrinkers.Crate.Config;

import javax.inject.Singleton;

/**
 * A class that generates/loads & provides access to a configuration file.
 */
@Singleton
public class ConfigHandler implements Reloadable {
    private final ExamplePlugin examplePlugin;
    private Config cfg;

    /**
     * Instantiates a new Config handler.
     *
     * @param examplePlugin the plugin instance
     */
    public ConfigHandler(ExamplePlugin examplePlugin) {
        this.examplePlugin = examplePlugin;
    }

    @Override
    public void onLoad() {
        cfg = new Config("config", examplePlugin.getDataFolder().getPath(), examplePlugin.getResource("config.yml")); // Create a config file from the template in our resources folder
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    /**
     * Gets examplePlugin config object.
     *
     * @return the examplePlugin config object
     */
    public Config getConfig() {
        return cfg;
    }
}
