package com.github.ExampleUser.ExamplePlugin.utility;

import com.github.ExampleUser.ExamplePlugin.Main;
import com.github.ExampleUser.ExamplePlugin.config.ConfigHandler;
import de.leonhard.storage.Config;
import org.jetbrains.annotations.NotNull;

/**
 * Convenience class for accessing {@link ConfigHandler#getConfig}
 */
public abstract class Cfg {
    /**
     * Convenience method for {@link ConfigHandler#getConfig} to get {@link Config}
     */
    @NotNull
    public static Config get() {
        return Main.getInstance().getConfigHandler().getConfig();
    }
}
