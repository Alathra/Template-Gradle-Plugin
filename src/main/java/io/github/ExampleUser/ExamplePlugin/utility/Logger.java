package io.github.ExampleUser.ExamplePlugin.utility;


import io.github.ExampleUser.ExamplePlugin.ExamplePlugin;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

/**
 * A class that provides shorthand access to {@link ExamplePlugin#getComponentLogger}.
 */
public class Logger {
    /**
     * Get component logger. Shorthand for:
     *
     * @return the component logger {@link ExamplePlugin#getComponentLogger}.
     */
    @NotNull
    public static ComponentLogger get() {
        return ExamplePlugin.getInstance().getComponentLogger();
    }
}
