package io.github.exampleuser.exampleplugin.hook;

import io.github.milkdrinkers.colorparser.ColorParser;
import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.exampleuser.exampleplugin.Reloadable;
import io.github.exampleuser.exampleplugin.utility.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.HashMap;

/**
 * Manages lifecycle of all hook objects and makes them accessible through the {@link Hook} enum.
 */
public class HookManager implements Reloadable {
    private final HashMap<Class<? extends AbstractHook>, AbstractHook> hooks = new HashMap<>();
    private final ExamplePlugin plugin;

    public HookManager(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * On plugin load.
     */
    @Override
    public void onLoad(ExamplePlugin plugin) {
        for (Hook hook : Hook.values()) {
            try {
                if (hook.getPluginName() != null && Bukkit.getPluginManager().getPlugin(hook.getPluginName()) == null) {
                    // Warn on missing dependency
                    Logger.get().warn(
                        ColorParser.of("<yellow><plugin> is not installed on this server. <plugin> support has been disabled.")
                            .parseMinimessagePlaceholder("plugin", hook.getPluginName())
                            .build()
                    );

                    // Shutdown plugin if required
                    if (!hook.isOptional())
                        Bukkit.getPluginManager().disablePlugin(plugin);
                    continue;
                }

                // Instantiate hook class
                final AbstractHook hookInstance = hook.getHookClass().getDeclaredConstructor(ExamplePlugin.class).newInstance(plugin);
                getHooks().put(hook.getHookClass(), hookInstance);
                hook.setHook(getHooks().get(hook.getHookClass()));

                // Run onLoad for hook
                hookInstance.onLoad(plugin);

                // Successfully loaded
                if (hook.getPluginName() != null) {
                    Logger.get().info(
                        ColorParser.of("<green><plugin> has been found on this server. <plugin> support enabled.")
                            .parseMinimessagePlaceholder("plugin", hook.getPluginName())
                            .build()
                    );
                }
            } catch (Exception e) {
                Logger.get().warn(
                    ColorParser.of("<yellow><hook> failed to load: <exception>")
                        .parseMinimessagePlaceholder("hook", hook.getHookClass().getName())
                        .parseMinimessagePlaceholder("exception", e.getMessage())
                        .build()
                );
            }
        }
    }

    /**
     * On plugin enable.
     */
    @Override
    public void onEnable(ExamplePlugin plugin) {
        for (AbstractHook hook : getHooks().values()) {
            hook.onEnable(plugin);

            // Register events in hook if applicable
            if (hook instanceof Listener listener)
                Bukkit.getPluginManager().registerEvents(listener, plugin);
        }
    }

    /**
     * On plugin disable.
     */
    @Override
    public void onDisable(ExamplePlugin plugin) {
        for (AbstractHook hook : getHooks().values()) {
            hook.onDisable(plugin);
        }
        Hook.clearHooks();
        getHooks().clear();
    }

    /**
     * Get a list of all hooks.
     * @return the hooks
     */
    public HashMap<Class<? extends AbstractHook>, AbstractHook> getHooks() {
        return hooks;
    }
}
