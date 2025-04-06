package io.github.exampleuser.exampleplugin.threadutil;

import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.exampleuser.exampleplugin.Reloadable;
import io.github.milkdrinkers.threadutil.PlatformBukkit;
import io.github.milkdrinkers.threadutil.Scheduler;

import java.time.Duration;

/**
 * A wrapper handler class for handling thread-util lifecycle.
 */
public class SchedulerHandler implements Reloadable {
    @Override
    public void onLoad(ExamplePlugin plugin) {
        Scheduler.init(new PlatformBukkit(plugin)); // Initialize thread-util
    }

    @Override
    public void onEnable(ExamplePlugin plugin) {

    }

    @Override
    public void onDisable(ExamplePlugin plugin) {
        Scheduler.shutdown(Duration.ofSeconds(60));
    }
}
