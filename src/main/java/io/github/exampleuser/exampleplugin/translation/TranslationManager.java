package io.github.exampleuser.exampleplugin.translation;

import io.github.milkdrinkers.crate.shaded.snakeyaml.snakeyaml.Yaml;
import io.github.milkdrinkers.colorparser.ColorParser;
import io.github.milkdrinkers.crate.Config;
import io.github.milkdrinkers.crate.ConfigBuilder;
import io.github.milkdrinkers.crate.internal.FileData;
import io.github.milkdrinkers.crate.internal.provider.CrateProviders;
import io.github.milkdrinkers.crate.internal.settings.ConfigSetting;
import io.github.milkdrinkers.crate.internal.settings.DataType;
import io.github.milkdrinkers.crate.internal.settings.ReloadSetting;
import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.exampleuser.exampleplugin.Reloadable;
import io.github.exampleuser.exampleplugin.utility.FileUtils;
import io.github.exampleuser.exampleplugin.utility.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

/**
 * A class that generates/loads {@literal &} provides access to all translation files.
 */
public class TranslationManager implements Reloadable {
    private final ExamplePlugin plugin;
    private static final String DEFAULT_TRANSLATION = "en-US";
    private static final String DEFAULT_DIR = "translations";
    private static final String RESOURCE_DIR = "translations";

    private InternalTranslation defaultInternalTranslation; // Always DEFAULT_LOCALE
    private InternalTranslation loadedInternalTranslation; // The translation requested by the user, or fallback if it couldn't be loaded

    /**
     * Instantiates a new TranslationManager.
     *
     * @param plugin the plugin instance
     */
    public TranslationManager(ExamplePlugin plugin) {
        this.plugin = plugin;
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
        final Path translationsPath = plugin.getDataFolder().toPath().resolve(DEFAULT_DIR);

        // Create DEFAULT_DIR if not exists
        if (!translationsPath.toFile().exists()) {
            try {
                if (!translationsPath.toFile().mkdirs())
                    throw new IllegalStateException("Failed to create %s directory!".formatted(DEFAULT_DIR));
            } catch (Exception ignored) {
                Logger.get().error(ColorParser.of("<red>Failed to create %s directory when loading plugin!".formatted(DEFAULT_DIR)).build());
            }
        }

        // Extract shipped locale files from .jar into DEFAULT_DIR
        extractDefaultTranslations();
        loadTranslations();
    }

    /**
     * On plugin disable.
     */
    @Override
    public void onDisable(ExamplePlugin plugin) {
    }

    /**
     * On plugin reload.
     */
    public void onReload() {
        if (getLoadedTranslation() != null && getDefaultTranslation() != null) {
            loadTranslations();
            Translation.reload();
        }
    }

    /**
     * Load a translation file into memory.
     * If the translation file does not exist, a blank file will be created.
     * If the requested translation file also exists on the classpath, the defaults will be copied over.
     *
     * @param translationName the file name excluding file extension
     * @return a {@link InternalTranslation} object
     */
    private InternalTranslation loadTranslation(final String translationName) {
        // Log that the requested translation doesn't exist
        if (!exists(translationName))
            Logger.get().warn("Translation file %s.yml does not exist. Creating blank file and using fallback %s.yml!".formatted(translationName, DEFAULT_TRANSLATION));

        Config translationConfig = new Config(translationName, plugin.getDataFolder().toPath().resolve(DEFAULT_DIR).toString());

        // Create & load file with defaults from input-stream if there is one
        translationConfig.setReloadSetting(ReloadSetting.MANUALLY);

        return new InternalTranslation(translationName, translationConfig);
    }

    /**
     * Check if a translation file exists.
     *
     * @param translationName the file name excluding file extension
     * @return boolean
     */
    private boolean exists(final String translationName) {
        Path translationsPath = plugin.getDataFolder().toPath().resolve(DEFAULT_DIR);
        Path translationPath = translationsPath.resolve(translationName + ".yml");

        if (!translationsPath.toFile().exists())
            return false;

        return translationPath.toFile().exists();
    }

    /**
     * Loads the user defined translation and fallback into memory.
     */
    private void loadTranslations() {
        final String requestedTranslation = plugin.getConfigHandler().getConfig().getOrSetDefault("translation", DEFAULT_TRANSLATION);

        defaultInternalTranslation = loadTranslation(DEFAULT_TRANSLATION); // Load default translation, used as fallback
        if (requestedTranslation.equals(DEFAULT_TRANSLATION)) { // Load requested translation or point to default object
            loadedInternalTranslation = defaultInternalTranslation;
        } else {
            loadedInternalTranslation = loadTranslation(plugin.getConfigHandler().getConfig().getOrSetDefault("translation", DEFAULT_TRANSLATION)); // Loads the requested translation
        }
    }

    /**
     * Extracts any translations that exist in the jar and updates old ones with missing default entries.
     */
    private void extractDefaultTranslations() {
        try {
            final Path translationsResourcePath = Path.of(RESOURCE_DIR);
            final Path translationsPath = plugin.getDataFolder().toPath().resolve(DEFAULT_DIR);

            for (String fileName : FileUtils.resourceListFiles(translationsResourcePath)) {

                // If file already exists, add missing entries (Add new language strings)
                if (exists(FileUtils.stripExtension(fileName))) {
                    try (InputStream stream = plugin.getResource(RESOURCE_DIR + "/" + fileName)) {
                        if (stream == null) {
                            Logger.get().error(ColorParser.of("<red>Failed to read translation %s from resources when loading plugin! Translation is null.".formatted(fileName)).build());
                            continue;
                        }

                        // Load translation file so it can be appended to
                        final Config translationConfig = ConfigBuilder.fromPath(FileUtils.stripExtension(fileName), translationsPath.toString())
                            .setConfigSetting(ConfigSetting.PRESERVE_COMMENTS)
                            .setReloadSetting(ReloadSetting.MANUALLY)
                            .setDataType(DataType.SORTED)
                            .create();

                        final Yaml yaml = new Yaml(CrateProviders.yamlLoaderOptions());

                        // Copy missing entries from inputStream to config
                        try (
                            final InputStreamReader isr = new InputStreamReader(stream, StandardCharsets.UTF_8);
                            final BufferedReader reader = new BufferedReader(isr)
                        ) {
                            final Map<String, Object> data = yaml.load(reader);
                            final FileData newData = new FileData(data, DataType.SORTED);

                            for (String key : newData.keySet()) {
                                if (!translationConfig.contains(key))
                                    translationConfig.set(key, newData.get(key));
                            }

                            translationConfig.forceReload(); // Apply file changes
                        }
                    } catch (Exception e) {
                        Logger.get().warn(ColorParser.of("<red>Failed to apply new translation updates to %s when loading plugin!".formatted(fileName)).build());
                    }
                    continue;
                }

                // Copy file from classpath into directory
                FileUtils.extractResource(translationsResourcePath.resolve(fileName), translationsPath.resolve(fileName), true);
            }
        } catch (Exception e) {
            Logger.get().warn(ColorParser.of("<red>Failed to extract and update default translations when loading plugin!").build());
        }
    }

    /**
     * Get the loaded translation.
     *
     * @return a {@link InternalTranslation} object or null if not loaded
     * @implNote Only used internally in {@link Translation}
     */
    @Nullable
    InternalTranslation getLoadedTranslation() {
        return loadedInternalTranslation;
    }

    /**
     * Get the loaded default translation.
     *
     * @return a {@link InternalTranslation} object or null if not loaded
     * @implNote Only used internally in {@link Translation}
     */
    @Nullable
    InternalTranslation getDefaultTranslation() {
        return defaultInternalTranslation;
    }
}
