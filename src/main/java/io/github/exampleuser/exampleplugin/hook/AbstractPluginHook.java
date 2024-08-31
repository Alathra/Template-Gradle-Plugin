package io.github.exampleuser.exampleplugin.hook;

import io.github.exampleuser.exampleplugin.Reloadable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public abstract class AbstractPluginHook<T> implements Reloadable {
    private final String pluginName;
    private T pluginHook;

    public AbstractPluginHook(String pluginName) {
        this.pluginName = pluginName;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        if (!Bukkit.getPluginManager().isPluginEnabled(pluginName))
            return;

        setHook(Bukkit.getPluginManager().getPlugin(pluginName));
    }

    @Override
    public void onDisable() {
        setHook(null);
    }

    /**
     * Check if this plugin hook is loaded and ready for use.
     * @return whether this plugin hook is loaded or not
     */
    public boolean isHookLoaded() {
        return pluginHook != null;
    }

    /**
     * Gets plugin. Should only be used following {@link #isHookLoaded()}.
     *
     * @return plugin instance
     */
    public T getHook() {
        if (!isHookLoaded())
            throw new IllegalStateException("Attempted to access a plugin hook for plugin (%s) when it is unavailable!".formatted(pluginName));

        return pluginHook;
    }

    @SuppressWarnings("unchecked")
    private void setHook(Plugin plugin) {
        this.pluginHook = (T) plugin;
    }
}
