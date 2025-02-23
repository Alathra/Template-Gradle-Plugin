package io.github.exampleuser.exampleplugin.updatechecker;

import io.github.milkdrinkers.colorparser.ColorParser;
import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.exampleuser.exampleplugin.Reloadable;
import io.github.exampleuser.exampleplugin.translation.Translation;
import io.github.exampleuser.exampleplugin.utility.Cfg;
import io.github.exampleuser.exampleplugin.utility.Logger;
import io.github.milkdrinkers.versionwatch.platform.exception.VersionWatchException;
import io.github.milkdrinkers.versionwatch.platform.github.ConfigGithub;
import io.github.milkdrinkers.versionwatch.platform.github.ConfigGithubBuilder;
import io.github.milkdrinkers.versionwatch.platform.github.GithubCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Run update checks against your release platform.
 */
public class UpdateHandler implements Reloadable {
    private final static String GITHUB_USER = "Alathra"; // The GitHub user/organization name
    private final static String GITHUB_REPO = "Template-Gradle-Plugin"; // The GitHub repository

    private final static ConfigGithub config = new ConfigGithubBuilder()
        .withUserAgent("ExamplePlugin") // Put your plugin name here
        .withOwner(GITHUB_USER)
        .withRepo(GITHUB_REPO)
        .build();

    private final ExamplePlugin plugin;
    private final VersionHolder versionHandler;

    public UpdateHandler(ExamplePlugin plugin) {
        this.plugin = plugin;
        this.versionHandler = new VersionHolder(plugin);
    }

    /**
     * On plugin load.
     */
    @Override
    public void onLoad(ExamplePlugin plugin) {
    }

    /**
     * On plugin enable.
     */
    @Override
    public void onEnable(ExamplePlugin plugin) {
        try {
            final boolean shouldLog = Cfg.get().getOrDefault("update-checker.enable", true) && Cfg.get().getOrDefault("update-checker.console", true);

            new GithubCheck(config)
                .fetchLatestVersionAsync()
                .whenCompleteAsync((version, throwable) -> {
                    if (throwable != null) {
                        if (shouldLog)
                            Logger.get().warn(ColorParser.of(Translation.of("update-checker.update-failed")).parseMinimessagePlaceholder("error", throwable.getMessage()).build());
                        return;
                    }

                    versionHandler.setLatestVersion(version);
                    checkVersion();
                });
        } catch (VersionWatchException e) {
            Logger.get().warn(ColorParser.of(Translation.of("update-checker.update-failed")).parseMinimessagePlaceholder("error", e.getMessage()).build());
        }

        // Register version check listener for player joins
        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            @SuppressWarnings("unused")
            public void onPlayerJoin(PlayerJoinEvent e) {
                checkVersion(e.getPlayer());
            }
        }, plugin);
    }

    /**
     * On plugin disable.
     */
    @Override
    public void onDisable(ExamplePlugin plugin) {
    }

    /**
     * Compare the current and latest versions, logs to console.
     */
    public void checkVersion() {
        if (versionHandler.getPluginName() == null || versionHandler.getLatestVersion() == null || versionHandler.getCurrentVersion() == null)
            return;

        final boolean shouldLog = Cfg.get().getOrDefault("update-checker.enable", true) && Cfg.get().getOrDefault("update-checker.console", true);

        if (!shouldLog)
            return;

        if (versionHandler.isLatest()) {
            Logger.get().info(
                ColorParser.of(Translation.of("update-checker.running-latest"))
                    .parseMinimessagePlaceholder("plugin_name", versionHandler.getPluginName())
                    .build()
            );
        } else {
            Logger.get().info(
                ColorParser.of(Translation.of("update-checker.update-found-console"))
                    .parseMinimessagePlaceholder("plugin_name", versionHandler.getPluginName())
                    .parseMinimessagePlaceholder("version_current", versionHandler.getCurrentVersion().getVersionFull())
                    .parseMinimessagePlaceholder("version_latest", versionHandler.getLatestVersion().getVersionFull())
                    .parseMinimessagePlaceholder("download_link", config.getLatestReleaseLink())
                    .build()
            );
        }
    }

    /**
     * Compare the current and latest versions, logs to player.
     */
    public void checkVersion(Player p) {
        if (versionHandler.isLatest())
            return;

        if (!Cfg.get().getOrDefault("update-checker.enable", true) || !Cfg.get().getOrDefault("update-checker.op", true))
            return;

        if (!p.isOp())
            return;

        if (versionHandler.getPluginName() == null || versionHandler.getLatestVersion() == null || versionHandler.getCurrentVersion() == null)
            return;

        p.sendMessage(
            ColorParser.of(Translation.of("update-checker.update-found-player"))
                .parseMinimessagePlaceholder("plugin_name", versionHandler.getPluginName())
                .parseMinimessagePlaceholder("version_current", versionHandler.getCurrentVersion().getVersionFull())
                .parseMinimessagePlaceholder("version_latest", versionHandler.getLatestVersion().getVersionFull())
                .parseMinimessagePlaceholder("download_link", config.getLatestReleaseLink())
                .build()
        );
    }
}
