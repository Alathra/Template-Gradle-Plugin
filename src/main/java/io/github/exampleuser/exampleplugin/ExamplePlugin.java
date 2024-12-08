package io.github.exampleuser.exampleplugin;

import com.github.milkdrinkers.colorparser.ColorParser;
import io.github.exampleuser.exampleplugin.command.CommandHandler;
import io.github.exampleuser.exampleplugin.config.ConfigHandler;
import io.github.exampleuser.exampleplugin.database.handler.DatabaseHandlerBuilder;
import io.github.exampleuser.exampleplugin.hook.*;
import io.github.exampleuser.exampleplugin.listener.ListenerHandler;
import io.github.exampleuser.exampleplugin.translation.TranslationManager;
import io.github.exampleuser.exampleplugin.updatechecker.UpdateChecker;
import io.github.exampleuser.exampleplugin.utility.DB;
import io.github.exampleuser.exampleplugin.utility.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Main class.
 */
public class ExamplePlugin extends JavaPlugin {
    private static ExamplePlugin instance;
    private ConfigHandler configHandler;
    private TranslationManager translationManager;
    private CommandHandler commandHandler;
    private ListenerHandler listenerHandler;
    private UpdateChecker updateChecker;

    // Hooks
    private static BStatsHook bStatsHook;
    private static VaultHook vaultHook;
    private static PacketEventsHook packetEventsHook;
    private static PAPIHook papiHook;

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
        configHandler = new ConfigHandler(instance);
        translationManager = new TranslationManager(instance);
        DB.init(
            new DatabaseHandlerBuilder()
                .withConfigHandler(configHandler)
                .withLogger(getComponentLogger())
                .build()
        );
        commandHandler = new CommandHandler(instance);
        listenerHandler = new ListenerHandler(instance);
        updateChecker = new UpdateChecker();
        bStatsHook = new BStatsHook(instance);
        vaultHook = new VaultHook(instance);
        packetEventsHook = new PacketEventsHook(instance);
        papiHook = new PAPIHook(instance);

        configHandler.onLoad();
        translationManager.onLoad();
        DB.getHandler().onLoad();
        commandHandler.onLoad();
        listenerHandler.onLoad();
        updateChecker.onLoad();
        bStatsHook.onLoad();
        vaultHook.onLoad();
        packetEventsHook.onLoad();
        papiHook.onLoad();
    }

    @Override
    public void onEnable() {
        configHandler.onEnable();
        translationManager.onEnable();
        DB.getHandler().onEnable();
        commandHandler.onEnable();
        listenerHandler.onEnable();
        updateChecker.onEnable();
        bStatsHook.onEnable();
        vaultHook.onEnable();
        packetEventsHook.onEnable();
        papiHook.onEnable();

        if (!DB.isReady()) {
            Logger.get().warn(ColorParser.of("<yellow>DatabaseHolder handler failed to start. Database support has been disabled.").build());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if (vaultHook.isHookLoaded()) {
            Logger.get().info(ColorParser.of("<green>Vault has been found on this server. Vault support enabled.").build());
        } else {
            Logger.get().warn(ColorParser.of("<yellow>Vault is not installed on this server. Vault support has been disabled.").build());
        }

        if (packetEventsHook.isHookLoaded()) {
            Logger.get().info(ColorParser.of("<green>PacketEvents has been found on this server. PacketEvents support enabled.").build());
        } else {
            Logger.get().warn(ColorParser.of("<yellow>PacketEvents is not installed on this server. PacketEvents support has been disabled.").build());
        }
    }

    @Override
    public void onDisable() {
        configHandler.onDisable();
        translationManager.onDisable();
        DB.getHandler().onDisable();
        commandHandler.onDisable();
        listenerHandler.onDisable();
        updateChecker.onDisable();
        bStatsHook.onDisable();
        vaultHook.onDisable();
        packetEventsHook.onDisable();
        papiHook.onDisable();
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
     * Gets update checker.
     *
     * @return the update checker
     */
    @NotNull
    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    /**
     * Gets bStats hook.
     *
     * @return the bStats hook
     */
    @NotNull
    public static BStatsHook getBStatsHook() {
        return bStatsHook;
    }

    /**
     * Gets vault hook.
     *
     * @return the vault hook
     */
    @NotNull
    public static VaultHook getVaultHook() {
        return vaultHook;
    }

    /**
     * Gets PacketEvents hook.
     *
     * @return the PacketEvents hook
     */
    @NotNull
    public static PacketEventsHook getPacketEventsHook() {
        return packetEventsHook;
    }
}
