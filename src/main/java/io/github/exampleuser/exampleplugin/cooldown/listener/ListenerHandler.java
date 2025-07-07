package io.github.exampleuser.exampleplugin.cooldown.listener;

import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.exampleuser.exampleplugin.Reloadable;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to handle registration of event listeners.
 */
@SuppressWarnings("FieldCanBeLocal")
public class ListenerHandler implements Reloadable {
    private final ExamplePlugin plugin;
    private final List<Listener> listeners = new ArrayList<>();

    public ListenerHandler(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(ExamplePlugin plugin) {
    }

    @Override
    public void onEnable(ExamplePlugin plugin) {
        listeners.clear();
        listeners.add(new CooldownListener(plugin));

        for (Listener listener : listeners) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    @Override
    public void onDisable(ExamplePlugin plugin) {
    }
}
