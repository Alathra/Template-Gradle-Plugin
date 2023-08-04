package com.github.ExampleUser.ExamplePlugin.utility;


import com.github.ExampleUser.ExamplePlugin.Main;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

/**
 * A class that provides shorthand access to {@link Main#getComponentLogger}.
 */
public class Logger {
    /**
     * Get component logger. Shorthand for:
     *
     * @return the component logger {@link Main#getComponentLogger}.
     */
    @NotNull
    public static ComponentLogger get() {
        return Main.getInstance().getComponentLogger();
    }
}
