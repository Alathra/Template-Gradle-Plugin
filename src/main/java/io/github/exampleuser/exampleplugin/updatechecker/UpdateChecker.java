package io.github.exampleuser.exampleplugin.updatechecker;

import com.github.milkdrinkers.colorparser.ColorParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.exampleuser.exampleplugin.Reloadable;
import io.github.exampleuser.exampleplugin.translation.Translation;
import io.github.exampleuser.exampleplugin.utility.Cfg;
import io.github.exampleuser.exampleplugin.utility.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * The type Update checker.
 */
public class UpdateChecker implements Reloadable {
    // Options
    private final static String GITHUB_USER = "Alathra"; // The GitHub user/organization name
    private final static String GITHUB_REPO = "Template-Gradle-Plugin"; // The GitHub repository
    public final static String LATEST_RELEASE = "https://github.com/%s/%s/releases/latest".formatted(GITHUB_USER, GITHUB_REPO);
    public final static String LATEST_RELEASE_API = "https://api.github.com/repos/%s/%s/releases/latest".formatted(GITHUB_USER, GITHUB_REPO);

    // Data
    private boolean isLatest = true;
    private @Nullable String pluginName;
    private @Nullable SemanticVersion currentVersion;
    private @Nullable SemanticVersion latestVersion;

    /**
     * On plugin load.
     */
    @Override
    public void onLoad() {
    }

    /**
     * On plugin enable.
     */
    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void onEnable() {
        setPluginName(ExamplePlugin.getInstance().getPluginMeta().getName());
        setCurrentVersion(SemanticVersion.of(ExamplePlugin.getInstance().getPluginMeta().getVersion()));
        performUpdateCheck();
    }

    /**
     * On plugin disable.
     */
    @Override
    public void onDisable() {
    }

    private void performUpdateCheck() {
        final boolean shouldLog = Cfg.get().getOrDefault("update-checker.enable", true) && Cfg.get().getOrDefault("update-checker.console", true);

        try (final HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(LATEST_RELEASE_API))
                .timeout(Duration.ofSeconds(10))
                .build();

            httpClient
                .sendAsync(req, HttpResponse.BodyHandlers.ofInputStream())
                .whenComplete((resp, err) -> {
                    if (err != null) {
                        if (shouldLog)
                            Logger.get().warn(ColorParser.of(Translation.of("update-checker.update-failed")).parseMinimessagePlaceholder("error", err.getMessage()).build());
                        return;
                    }
                    setLatestVersion(parseLatestVersion(resp.body()));
                    checkVersion();
                });
        } catch (Exception err) {
            if (shouldLog)
                Logger.get().warn(ColorParser.of(Translation.of("update-checker.update-failed")).parseMinimessagePlaceholder("error", err.getMessage()).build());
        }
    }

    private @Nullable SemanticVersion parseLatestVersion(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            final JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            final String ver = json.getAsJsonPrimitive("tag_name").getAsString().toUpperCase();
            return SemanticVersion.of(ver);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse version JSON response.");
        }
    }

    /**
     * Compare the current and latest.
     */
    public void checkVersion() {
        if (pluginName == null)
            return;
        if (currentVersion == null)
            return;
        if (latestVersion == null)
            return;

        final boolean shouldLog = Cfg.get().getOrDefault("update-checker.enable", true) && Cfg.get().getOrDefault("update-checker.console", true);

        if (SemanticVersion.isNewer(latestVersion, currentVersion)) {
            setLatest(false);

            if (!shouldLog)
                return;

            Logger.get().info(
                ColorParser.of(Translation.of("update-checker.update-found-console"))
                    .parseMinimessagePlaceholder("plugin_name", pluginName)
                    .parseMinimessagePlaceholder("version_current", currentVersion.getVersionFull())
                    .parseMinimessagePlaceholder("version_latest", latestVersion.getVersionFull())
                    .parseMinimessagePlaceholder("download_link", LATEST_RELEASE)
                    .build()
            );

        } else if (SemanticVersion.isOlderOrEqual(latestVersion, currentVersion) || SemanticVersion.isEqual(latestVersion, currentVersion)) {
            setLatest(true);

            if (!shouldLog)
                return;

            Logger.get().info(
                ColorParser.of(Translation.of("update-checker.running-latest"))
                    .parseMinimessagePlaceholder("plugin_name", pluginName)
                    .build()
            );
        }
    }

    /**
     * Returns if the plugins is of the latest version.
     *
     * @return the boolean
     */
    public boolean isLatest() {
        return isLatest;
    }

    private void setLatest(boolean latest) {
        isLatest = latest;
    }

    /**
     * Gets the plugin name.
     *
     * @return the plugin name
     */
    public @Nullable String getPluginName() {
        return pluginName;
    }

    private void setPluginName(@Nullable String pluginName) {
        this.pluginName = pluginName;
    }

    /**
     * Gets current version of the plugin.
     *
     * @return the current version or null if it failed parse from the plugin.yml
     */
    public @Nullable SemanticVersion getCurrentVersion() {
        return currentVersion;
    }

    private void setCurrentVersion(@Nullable SemanticVersion currentVersion) {
        this.currentVersion = currentVersion;
    }

    /**
     * Gets latest version.
     *
     * @return the latest version or null if it failed to fetch from github
     */
    public @Nullable SemanticVersion getLatestVersion() {
        return latestVersion;
    }

    private void setLatestVersion(@Nullable SemanticVersion latestVersion) {
        this.latestVersion = latestVersion;
    }
}
