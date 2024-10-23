package io.github.exampleuser.exampleplugin.hook;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;

/**
 * A hook that enables API for PacketEvents.
 */
public class PacketEventsHook implements Hook {
    private final ExamplePlugin plugin;
    private final static String pluginName = "PacketEvents";

    /**
     * Instantiates a new PacketEvents hook.
     *
     * @param plugin the plugin instance
     */
    public PacketEventsHook(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad() {
        if (!Bukkit.getPluginManager().isPluginEnabled(pluginName))
            return;

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        if (!Bukkit.getPluginManager().isPluginEnabled(pluginName))
            return;

        PacketEvents.getAPI().init();
    }

    @Override
    public void onDisable() {
        if (!Bukkit.getPluginManager().isPluginEnabled(pluginName))
            return;

        PacketEvents.getAPI().terminate();
    }

    /**
     * Check if the PacketEvents hook is loaded and ready for use.
     * @return whether the PacketEvents hook is loaded or not
     */
    public boolean isHookLoaded() {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName) && PacketEvents.getAPI().isLoaded();
    }
}
