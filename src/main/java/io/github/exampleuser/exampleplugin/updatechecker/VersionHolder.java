package io.github.exampleuser.exampleplugin.updatechecker;

import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.milkdrinkers.javasemver.Version;
import io.github.milkdrinkers.javasemver.exception.VersionParseException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A class to hold the current and latest version of the plugin including misc data.
 */
class VersionHolder {
    private boolean isLatest;
    private @Nullable String pluginName;
    private @Nullable Version currentVersion;
    private @Nullable Version latestVersion;

    @SuppressWarnings("UnstableApiUsage")
    public VersionHolder(ExamplePlugin instance) {
        this.isLatest = true;
        this.pluginName = instance.getPluginMeta().getName();
        try {
            this.currentVersion = Version.of(instance.getPluginMeta().getVersion());
        } catch (VersionParseException e) {
            this.currentVersion = Version.of(0, 0, 1, "", "");
        }
        this.latestVersion = Version.of(0, 0, 1, "", "");
    }

    /**
     * Returns if the plugins is of the latest version.
     *
     * @return the boolean
     */
    public boolean isLatest() {
        return isLatest;
    }

    @ApiStatus.Internal
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

    @ApiStatus.Internal
    private void setPluginName(@Nullable String pluginName) {
        this.pluginName = pluginName;
    }

    /**
     * Gets current version of the plugin.
     *
     * @return the current version or null if it failed parse from the plugin.yml
     */
    public @Nullable Version getCurrentVersion() {
        return currentVersion;
    }

    @ApiStatus.Internal
    private void setCurrentVersion(@Nullable Version currentVersion) {
        this.currentVersion = currentVersion;
    }

    /**
     * Gets latest version.
     *
     * @return the latest version or null if it failed to fetch from github
     */
    public @Nullable Version getLatestVersion() {
        return latestVersion;
    }

    @ApiStatus.Internal
    protected void setLatestVersion(@Nullable Version latestVersion) {
        this.latestVersion = latestVersion;

        if (latestVersion == null || getCurrentVersion() == null)
            return;

        setLatest(Version.isNewerOrEqual(getCurrentVersion(), latestVersion));
    }
}
