package io.github.exampleuser.exampleplugin.hook.bstats;

import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.exampleuser.exampleplugin.hook.AbstractHook;
import org.bstats.bukkit.Metrics;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A hook to interface with <a href="https://github.com/Bastian/bstats-metrics">BStats</a>.
 */
public class BStatsHook extends AbstractHook {
    private final static int BSTATS_ID = 1234; // Signup to BStats and register your new plugin here: https://bstats.org/getting-started, replace the id with you new one!
    private @Nullable Metrics hook;

    /**
     * Instantiates a new BStats hook.
     *
     * @param plugin the plugin instance
     */
    public BStatsHook(ExamplePlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable(ExamplePlugin plugin) {
        // Catch startup errors for bstats
        try {
            setHook(new Metrics(getPlugin(), BSTATS_ID));
        } catch (Exception ignored) {
            setHook(null);
        }
    }

    @Override
    public void onDisable(ExamplePlugin plugin) {
        getHook().shutdown();
        setHook(null);
    }

    @Override
    public boolean isHookLoaded() {
        return hook != null;
    }

    /**
     * Gets BStats metrics instance. Should only be used following {@link #isHookLoaded()}.
     *
     * @return instance
     */
    public Metrics getHook() {
        if (!isHookLoaded())
            throw new IllegalStateException("Attempted to access BStats metrics instance hook when it is unavailable!");

        return hook;
    }

    /**
     * Sets the BStats metrics instance.
     *
     * @param hook The BStats metrics instance {@link Metrics}
     */
    @ApiStatus.Internal
    private void setHook(@Nullable Metrics hook) {
        this.hook = hook;
    }
}
