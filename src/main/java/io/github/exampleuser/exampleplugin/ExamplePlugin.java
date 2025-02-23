package io.github.exampleuser.exampleplugin;

import io.github.exampleuser.exampleplugin.database.handler.DatabaseHandler;
import io.github.exampleuser.exampleplugin.hook.HookManager;
import io.github.milkdrinkers.colorparser.ColorParser;
import io.github.exampleuser.exampleplugin.command.CommandHandler;
import io.github.exampleuser.exampleplugin.config.ConfigHandler;
import io.github.exampleuser.exampleplugin.database.handler.DatabaseHandlerBuilder;
import io.github.exampleuser.exampleplugin.listener.ListenerHandler;
import io.github.exampleuser.exampleplugin.translation.TranslationManager;
import io.github.exampleuser.exampleplugin.updatechecker.UpdateHandler;
import io.github.exampleuser.exampleplugin.utility.DB;
import io.github.exampleuser.exampleplugin.utility.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Main class.
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class ExamplePlugin extends JavaPlugin {
    private static ExamplePlugin instance;

    // Handlers/Managers
    private ConfigHandler configHandler;
    private TranslationManager translationManager;
    private DatabaseHandler databaseHandler;
    private HookManager hookManager;
    private CommandHandler commandHandler;
    private ListenerHandler listenerHandler;
    private UpdateHandler updateHandler;

    // Handlers list (defines order of load/enable/disable)
    private List<? extends Reloadable> handlers;

    /**
     * Gets plugin instance.
     *
     * @return the plugin instance
     */
    public static ExamplePlugin getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;

        configHandler = new ConfigHandler(this);
        translationManager = new TranslationManager(this);
        databaseHandler = new DatabaseHandlerBuilder()
            .withConfigHandler(configHandler)
            .withLogger(getComponentLogger())
            .build();
        hookManager = new HookManager(this);
        commandHandler = new CommandHandler(this);
        listenerHandler = new ListenerHandler(this);
        updateHandler = new UpdateHandler(this);

        handlers = List.of(
            configHandler,
            translationManager,
            databaseHandler,
            hookManager,
            commandHandler,
            listenerHandler,
            updateHandler
        );

        DB.init(databaseHandler);
        for (Reloadable handler : handlers)
            handler.onLoad(instance);
    }

    @Override
    public void onEnable() {
        for (Reloadable handler : handlers)
            handler.onEnable(instance);

        if (!DB.isReady()) {
            Logger.get().warn(ColorParser.of("<yellow>DatabaseHolder handler failed to start. Database support has been disabled.").build());
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        for (Reloadable handler : handlers.reversed()) // If reverse doesn't work implement a new List with your desired disable order
            handler.onDisable(instance);
    }

    /**
     * Use to reload the entire plugin.
     */
    public void onReload() {
        onDisable();
        onLoad();
        onEnable();
    }

    /**
     * Gets config handler.
     *
     * @return the config handler
     */
    @NotNull
    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    /**
     * Gets config handler.
     *
     * @return the translation handler
     */
    @NotNull
    public TranslationManager getTranslationManager() {
        return translationManager;
    }

    /**
     * Gets hook manager.
     * @return the hook manager
     */
    @NotNull
    public HookManager getHookManager() {
        return hookManager;
    }

    /**
     * Gets update handler.
     *
     * @return the update handler
     */
    @NotNull
    public UpdateHandler getUpdateHandler() {
        return updateHandler;
    }
}
