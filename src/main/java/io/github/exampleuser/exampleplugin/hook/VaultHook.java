package io.github.exampleuser.exampleplugin.hook;

import io.github.exampleuser.exampleplugin.ExamplePlugin;
import io.github.exampleuser.exampleplugin.Reloadable;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A hook to interface with the <a href="https://github.com/MilkBowl/VaultAPI">Vault API</a>.
 */
public class VaultHook implements Reloadable {
    private final ExamplePlugin plugin;
    private @Nullable RegisteredServiceProvider<Economy> rspEconomy;
    private @Nullable RegisteredServiceProvider<Permission> rspPermissions;
    private @Nullable RegisteredServiceProvider<Chat> rspChat;

    /**
     * Instantiates a new Vault hook.
     *
     * @param plugin the plugin instance
     */
    public VaultHook(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        if (!isVaultLoaded()) return;

        setEconomy(plugin.getServer().getServicesManager().getRegistration(Economy.class));
        setPermissions(plugin.getServer().getServicesManager().getRegistration(Permission.class));
        setChat(plugin.getServer().getServicesManager().getRegistration(Chat.class));
    }

    @Override
    public void onDisable() {
        if (!isVaultLoaded()) return;

        setEconomy(null);
        setPermissions(null);
        setChat(null);
    }

    /**
     * Check if Vault is present on the server.
     *
     * @return the boolean
     */
    public boolean isVaultLoaded() {
        return plugin.getServer().getPluginManager().isPluginEnabled("Vault");
    }

    /**
     * Check if a vault economy plugin is loaded.
     *
     * @return boolean
     */
    public boolean isEconomyLoaded() {
        return rspEconomy != null && getEconomy() != null;
    }

    /**
     * Gets vault economy instance. Should only be used after {@link #isEconomyLoaded()}.
     *
     * @return vault instance
     */
    public Economy getEconomy() {
        if (rspEconomy == null)
            throw new NullPointerException("The plugin tried to use Vault without it being loaded. Use the VaultHook#isVaultLoaded method before using vault methods.");
        return rspEconomy.getProvider();
    }

    /**
     * Sets the vault economy service provider.
     *
     * @param rsp The service provider providing {@link Economy}
     */
    @ApiStatus.Internal
    public void setEconomy(@Nullable RegisteredServiceProvider<Economy> rsp) {
        this.rspEconomy = rsp;
    }

    /**
     * Check if a vault permissions plugin is loaded.
     *
     * @return boolean
     */
    public boolean isPermissionsLoaded() {
        return rspPermissions != null && getPermissions() != null;
    }

    /**
     * Gets vault permissions instance. Should only be used after {@link #isPermissionsLoaded()}.
     *
     * @return vault instance
     */
    public Permission getPermissions() {
        if (rspPermissions == null)
            throw new NullPointerException("The plugin tried to use Vault without it being loaded. Use the VaultHook#isVaultLoaded method before using vault methods.");
        return rspPermissions.getProvider();
    }

    /**
     * Sets the vault permissions service provider.
     *
     * @param rsp The service provider providing {@link Permission}
     */
    @ApiStatus.Internal
    public void setPermissions(@Nullable RegisteredServiceProvider<Permission> rsp) {
        this.rspPermissions = rsp;
    }

    /**
     * Check if a vault chat plugin is loaded.
     *
     * @return boolean
     */
    public boolean isChatLoaded() {
        return rspChat != null && getChat() != null;
    }

    /**
     * Gets vault Chat instance. Should only be used after {@link #isChatLoaded()}.
     *
     * @return vault instance
     */
    public Chat getChat() {
        if (rspChat == null)
            throw new NullPointerException("The plugin tried to use Vault without it being loaded. Use the VaultHook#isVaultLoaded method before using vault methods.");
        return rspChat.getProvider();
    }

    /**
     * Sets the vault Chat service provider.
     *
     * @param rsp The service provider providing {@link Chat}
     */
    @ApiStatus.Internal
    public void setChat(@Nullable RegisteredServiceProvider<Chat> rsp) {
        this.rspChat = rsp;
    }
}
