package com.github.ExampleUser.ExamplePlugin.config;

import com.github.ExampleUser.ExamplePlugin.Main;
import com.github.ExampleUser.ExamplePlugin.Reloadable;
import de.leonhard.storage.Config;

import javax.inject.Singleton;

/**
 * A class that generates/loads & provides access to a configuration file.
 */
@Singleton
public class ConfigHandler implements Reloadable {
    private final Main main;
    private Config cfg;

    /**
     * Instantiates a new Config handler.
     *
     * @param main the plugin instance
     */
    public ConfigHandler(Main main) {
        this.main = main;
    }

    @Override
    public void onLoad() {
        cfg = new Config("config", main.getDataFolder().getPath(), main.getResource("config.yml")); // Create a config file from the template in our resources folder
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    /**
     * Gets main config object.
     *
     * @return the main config object
     */
    public Config getConfig() {
        return cfg;
    }
}
