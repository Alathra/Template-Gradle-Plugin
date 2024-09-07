package io.github.exampleuser.exampleplugin.hook;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.scheduler.ProtocolScheduler;
import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.exampleuser.exampleplugin.Reloadable;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A hook to interface with <a href="https://github.com/dmulloy2/ProtocolLib">ProtocolLib</a>.
 */
public class ProtocolLibHook implements Reloadable {
    private final ExamplePlugin plugin;
    private final static String pluginName = "ProtocolLib";
    private @Nullable ProtocolManager protocolManager;
    private @Nullable ProtocolScheduler protocolScheduler;

    /**
     * Instantiates a new ProtocolLib hook.
     *
     * @param plugin the plugin instance
     */
    public ProtocolLibHook(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        if (!Bukkit.getPluginManager().isPluginEnabled(pluginName))
            return;

        setProtocolManager(ProtocolLibrary.getProtocolManager());
        setProtocolScheduler(ProtocolLibrary.getScheduler());
    }

    @Override
    public void onDisable() {
        if (!Bukkit.getPluginManager().isPluginEnabled(pluginName))
            return;

        setProtocolScheduler(null);
        setProtocolManager(null);
    }

    /**
     * Check if the ProtocolLib hook is loaded and ready for use.
     * @return whether the ProtocolLib hook is loaded or not
     */
    public boolean isHookLoaded() {
        return protocolManager != null &&
            !protocolManager.isClosed() &&
            protocolScheduler != null;
    }

    /**
     * Gets ProtocolManager instance. Should only be used following {@link #isHookLoaded()}.
     *
     * @return instance
     */
    public ProtocolManager getProtocolManager() {
        if (!isHookLoaded())
            throw new IllegalStateException("Attempted to access ProtocolLib hook when it is unavailable!");

        return protocolManager;
    }

    /**
     * Sets the ProtocolManager instance.
     *
     * @param protocolManager The ProtocolLib ProtocolManager instance {@link ProtocolManager}
     */
    @ApiStatus.Internal
    private void setProtocolManager(@Nullable ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
    }

    /**
     * Gets ProtocolScheduler instance. Should only be used following {@link #isHookLoaded()}.
     *
     * @return instance
     */
    public ProtocolScheduler getProtocolScheduler() {
        if (!isHookLoaded())
            throw new IllegalStateException("Attempted to access ProtocolLib hook when it is unavailable!");

        return protocolScheduler;
    }

    /**
     * Sets the ProtocolScheduler instance.
     *
     * @param hook The ProtocolLib ProtocolScheduler instance {@link ProtocolScheduler}
     */
    @ApiStatus.Internal
    private void setProtocolScheduler(@Nullable ProtocolScheduler hook) {
        this.protocolScheduler = hook;
    }
}
