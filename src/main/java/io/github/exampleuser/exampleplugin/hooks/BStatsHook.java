package io.github.exampleuser.exampleplugin.hooks;

import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.exampleuser.exampleplugin.Reloadable;
import org.bstats.bukkit.Metrics;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A hook to interface with <a href="https://github.com/Bastian/bstats-metrics">BStats</a>.
 */
public class BStatsHook implements Reloadable {
    private final static int bStatsId = 1234; // Signup to BStats and register your new plugin here: https://bstats.org/getting-started, replace the id with you new one!
    private final ExamplePlugin plugin;
    private @Nullable Metrics metrics;

    /**
     * Instantiates a new BStats hook.
     *
     * @param plugin the plugin instance
     */
    public BStatsHook(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        setMetrics(new Metrics(plugin, bStatsId));
    }

    @Override
    public void onDisable() {
        getMetrics().shutdown();
        setMetrics(null);
    }

    /**
     * Gets BStats metrics instance.
     *
     * @return metrics instance
     */
    public Metrics getMetrics() {
        if (metrics == null)
            throw new NullPointerException("The plugin tried to use BStats without it being properly loaded.");

        return metrics;
    }

    /**
     * Sets the BStats metrics instance.
     *
     * @param metrics The BStats metrics instance {@link Metrics}
     */
    @ApiStatus.Internal
    public void setMetrics(@Nullable Metrics metrics) {
        this.metrics = metrics;
    }
}
