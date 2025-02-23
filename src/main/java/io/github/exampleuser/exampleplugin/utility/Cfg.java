package io.github.exampleuser.exampleplugin.utility;

import io.github.milkdrinkers.crate.Config;
import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.exampleuser.exampleplugin.config.ConfigHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Convenience class for accessing {@link ConfigHandler#getConfig}
 */
public abstract class Cfg {
    /**
     * Convenience method for {@link ConfigHandler#getConfig} to getConnection {@link Config}
     *
     * @return the config
     */
    @NotNull
    public static Config get() {
        return ExamplePlugin.getInstance().getConfigHandler().getConfig();
    }
}
