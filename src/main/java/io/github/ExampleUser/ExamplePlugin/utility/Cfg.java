package io.github.ExampleUser.ExamplePlugin.utility;

import io.github.ExampleUser.ExamplePlugin.ExamplePlugin;
import io.github.ExampleUser.ExamplePlugin.config.ConfigHandler;
import com.github.milkdrinkers.Crate.Config;
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
