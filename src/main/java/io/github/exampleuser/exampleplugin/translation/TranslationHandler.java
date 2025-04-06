package io.github.exampleuser.exampleplugin.translation;

import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.exampleuser.exampleplugin.Reloadable;
import io.github.exampleuser.exampleplugin.config.ConfigHandler;
import io.github.milkdrinkers.colorparser.ColorParser;
import io.github.milkdrinkers.wordweaver.Translation;
import io.github.milkdrinkers.wordweaver.config.TranslationConfig;

import java.nio.file.Path;

/**
 * A wrapper handler class for handling WordWeaver lifecycle.
 */
public class TranslationHandler implements Reloadable {
    private final ConfigHandler configHandler;

    public TranslationHandler(ConfigHandler configHandler) {
        this.configHandler = configHandler;
    }

    @Override
    public void onLoad(ExamplePlugin plugin) {

    }

    @Override
    public void onEnable(ExamplePlugin plugin) {
        Translation.initialize(TranslationConfig.builder() // Initialize word-weaver
            .translationDirectory(plugin.getDataPath().resolve("lang"))
            .resourcesDirectory(Path.of("lang"))
            .extractLanguages(true)
            .updateLanguages(true)
            .language(configHandler.getConfig().get("language", "en_US"))
            .defaultLanguage("en_US")
            .componentConverter(s -> ColorParser.of(s).parseLegacy().build()) // Use color parser for components by default
            .build()
        );
    }

    @Override
    public void onDisable(ExamplePlugin plugin) {
    }
}
