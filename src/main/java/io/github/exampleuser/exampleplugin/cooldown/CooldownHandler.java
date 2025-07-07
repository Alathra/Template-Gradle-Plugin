package io.github.exampleuser.exampleplugin.cooldown;

import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.exampleuser.exampleplugin.Reloadable;
import io.github.exampleuser.exampleplugin.cooldown.listener.ListenerHandler;
import io.github.exampleuser.exampleplugin.database.Queries;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class CooldownHandler implements Reloadable {
    private ListenerHandler listenerHandler;
    private ScheduledTask autoSaveTask;

    @Override
    public void onLoad(ExamplePlugin plugin) {
        listenerHandler = new ListenerHandler(plugin);
        listenerHandler.onLoad(plugin);
    }

    @Override
    public void onEnable(ExamplePlugin plugin) {
        listenerHandler.onEnable(plugin);
        autoSaveTask = plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, autoSaveTask(plugin), 10L, 10L, TimeUnit.MINUTES);
    }

    @Override
    public void onDisable(ExamplePlugin plugin) {
        autoSaveTask.cancel();
        listenerHandler.onDisable(plugin);
        Cooldowns.reset();
    }

    private Consumer<ScheduledTask> autoSaveTask(JavaPlugin plugin) {
        return task -> {
            for (final Player p : plugin.getServer().getOnlinePlayers()) {
                if (!p.isOnline())
                    continue;

                Queries.Cooldown.save(p);
            }
        };
    }
}
