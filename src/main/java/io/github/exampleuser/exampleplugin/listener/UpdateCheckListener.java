package io.github.exampleuser.exampleplugin.listener;

import com.github.milkdrinkers.colorparser.ColorParser;
import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.exampleuser.exampleplugin.utility.Cfg;
import io.github.exampleuser.exampleplugin.utility.updatechecker.SemanticVersion;
import io.github.exampleuser.exampleplugin.utility.updatechecker.UpdateChecker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Sends an update message to operators if there's a plugin update available
 */
public class UpdateCheckListener implements Listener {
    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (ExamplePlugin.getInstance().getUpdateChecker().isLatest())
            return;

        if (!Cfg.get().getOrDefault("update-checker.enable", true) || !Cfg.get().getOrDefault("update-checker.op", true))
            return;

        if (!e.getPlayer().isOp())
            return;

        String pluginName = ExamplePlugin.getInstance().getUpdateChecker().getPluginName();
        SemanticVersion latestVersion = ExamplePlugin.getInstance().getUpdateChecker().getLatestVersion();
        SemanticVersion currentVersion = ExamplePlugin.getInstance().getUpdateChecker().getCurrentVersion();

        if (latestVersion == null || currentVersion == null)
            return;

        e.getPlayer().sendMessage(
            ColorParser.of(
                UpdateChecker.UPDATE_FOUND_PLAYER.formatted(
                    pluginName,
                    currentVersion.getVersionFull(),
                    UpdateChecker.LATEST_RELEASE,
                    latestVersion.getVersionFull()
                )
            ).build()
        );
    }
}
