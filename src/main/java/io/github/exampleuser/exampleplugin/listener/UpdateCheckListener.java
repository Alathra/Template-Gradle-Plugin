package io.github.exampleuser.exampleplugin.listener;

import com.github.milkdrinkers.colorparser.ColorParser;
import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.exampleuser.exampleplugin.translation.Translation;
import io.github.exampleuser.exampleplugin.utility.Cfg;
import io.github.exampleuser.exampleplugin.updatechecker.SemanticVersion;
import io.github.exampleuser.exampleplugin.updatechecker.UpdateChecker;
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
            ColorParser.of(Translation.of("update-checker.update-found-player"))
                .parseMinimessagePlaceholder("plugin_name", pluginName)
                .parseMinimessagePlaceholder("version_current", currentVersion.getVersionFull())
                .parseMinimessagePlaceholder("version_latest", latestVersion.getVersionFull())
                .parseMinimessagePlaceholder("download_link", UpdateChecker.LATEST_RELEASE)
                .build()
        );
    }
}
