package io.github.exampleuser.exampleplugin.translation;

import io.github.exampleuser.exampleplugin.ExamplePlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Utility class for interacting with the Translations of the plugin.
 */
public class Translation {
    private static @Nullable ExamplePlugin plugin;
    private static @Nullable TranslationManager translationManager;
    private static boolean isUsingDefault = false; // Is the loaded translation the fallback translation

    /**
     * Internal method that ensures all static fields have been initialized.
     */
    private static void init() {
        if (plugin == null)
            plugin = ExamplePlugin.getInstance();

        if (translationManager == null)
            translationManager = plugin.getTranslationManager();
    }

    /**
     * Protected method run when the translation are reloaded to cache static fields.
     */
    protected static void reload() {
        if (plugin == null || translationManager == null)
            return;

        if (translationManager.getLoadedTranslation() != null && translationManager.getDefaultTranslation() != null)
            isUsingDefault = Objects.equals(translationManager.getLoadedTranslation().translationName(), translationManager.getDefaultTranslation().translationName());
    }

    /**
     * Fetch a string from the translations by its key.
     * <br/>
     * Fetches from the translation specified by the user.
     * If that fails, fetches from the default translation.
     * If that fails, returns the default value.
     * <br/>
     *
     * @param key The key to the translation
     * @return a string value
     * @apiNote Internally this executes {@link #of(String, String)} with "" as def.
     */
    public static String of(final String key) {
        return of(key, "");
    }

    /**
     * Fetch a string from the translations by its key.
     * <br/>
     * Fetches from the translation specified by the user.
     * If that fails, fetches from the default translation.
     * If that fails, returns the default value.
     * <br/>
     *
     * @param key The key to the translation
     * @param def The default value to return if no valid value was found
     * @return a string value
     */
    public static String of(final String key, final String def) {
        init();

        if (plugin == null || translationManager == null)
            return def;

        // Translations not loaded, return default
        if (translationManager.getLoadedTranslation() == null)
            return def;

        if (isUsingDefault) {
            // Translations not loaded, return default
            if (translationManager.getDefaultTranslation() == null)
                return def;

            return translationManager.getDefaultTranslation().translation().getOrDefault(
                key,
                def
            );
        } else {
            return translationManager.getLoadedTranslation().translation().getOrDefault(
                key,
                translationManager.getDefaultTranslation() != null ? translationManager.getDefaultTranslation().translation().getOrDefault(key, def) : def
            );
        }
    }

    /**
     * Fetch a list of strings from the translations by its key.
     * <br/>
     * Fetches from the translation specified by the user.
     * If that fails, fetches from the default translation.
     * If that fails, returns the default value.
     * <br/>
     *
     * @param key The key to the translation
     * @return a list of strings
     */
    public static List<String> ofList(final String key) {
        init();

        if (plugin == null || translationManager == null)
            return Collections.emptyList();

        // Translations not loaded, return default
        if (translationManager.getLoadedTranslation() == null)
            return Collections.emptyList();

        final List<String> result = translationManager.getLoadedTranslation().translation().getStringList(key);

        if (isUsingDefault) {
            // Translations not loaded, return default
            if (translationManager.getDefaultTranslation() == null)
                return Collections.emptyList();

            return translationManager.getDefaultTranslation().translation().getStringList(key);
        } else {
            return !result.isEmpty() ? result : translationManager.getDefaultTranslation() != null ? translationManager.getDefaultTranslation().translation().getStringList(key) : Collections.emptyList();
        }
    }

    /**
     * Fetch a list of strings from the translations by its key.
     * <br/>
     * Fetches from the translation specified by the user.
     * If that fails, fetches from the default translation.
     * If that fails, returns the default value.
     * <br/>
     *
     * @param key The key to the translation
     * @param def The default value to return if no valid value was found
     * @return a list of strings
     */
    public static List<String> ofList(final String key, final List<String> def) {
        init();

        if (plugin == null || translationManager == null)
            return Collections.emptyList();

        // Translations not loaded, return default
        if (translationManager.getLoadedTranslation() == null)
            return Collections.emptyList();

        final List<String> loadedResult = translationManager.getLoadedTranslation().translation().getStringList(key);

        if (isUsingDefault) {
            return !loadedResult.isEmpty() ? loadedResult : def;
        } else {
            final List<String> defaultResult = translationManager.getDefaultTranslation() != null ? translationManager.getDefaultTranslation().translation().getStringList(key) : def;

            return !loadedResult.isEmpty() ? loadedResult : !defaultResult.isEmpty() ? defaultResult : def;
        }
    }

    /**
     * Returns a list of all entries in the translation file.
     *
     * @return list of strings
     */
    public static List<String> getAllKeys() {
        if (plugin == null || translationManager == null)
            return Collections.emptyList();

        // Translations not loaded, return default
        if (translationManager.getLoadedTranslation() == null)
            return Collections.emptyList();

        return translationManager.getLoadedTranslation().translation().keySet().stream().toList();
    }
}
