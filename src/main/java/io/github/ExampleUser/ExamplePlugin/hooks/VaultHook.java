package io.github.ExampleUser.ExamplePlugin.hooks;

import io.github.ExampleUser.ExamplePlugin.ExamplePlugin;
import io.github.ExampleUser.ExamplePlugin.Reloadable;
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
    private @Nullable RegisteredServiceProvider<Permission> rspPermission;
    private @Nullable RegisteredServiceProvider<Chat> rspChat;
    private boolean isVaultLoaded = false;

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
        setVaultLoaded(false);
    }

    @Override
    public void onEnable() {
        if (!plugin.getServer().getPluginManager().isPluginEnabled("Vault"))
            return;

        setVaultEconomy(plugin.getServer().getServicesManager().getRegistration(Economy.class));
        setVaultPermissions(plugin.getServer().getServicesManager().getRegistration(Permission.class));
        setVaultChat(plugin.getServer().getServicesManager().getRegistration(Chat.class));

        setVaultLoaded(true);
    }

    @Override
    public void onDisable() {
        if (!isVaultLoaded()) return;

        setVaultEconomy(null);
        setVaultPermissions(null);
        setVaultChat(null);

        setVaultLoaded(false);
    }

    /**
     * Is vault loaded boolean.
     *
     * @return the boolean
     */
    public boolean isVaultLoaded() {
        return isVaultLoaded;
    }

    /**
     * Sets if vault is currently loaded.
     *
     * @param loaded If vault is currently loaded
     */
    @ApiStatus.Internal
    private void setVaultLoaded(boolean loaded) {
        isVaultLoaded = loaded;
    }

    /**
     * Gets vault economy instance. Should only be used after {@link #isVaultLoaded()}.
     *
     * @return vault instance
     */
    public Economy getVaultEconomy() {
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
    private void setVaultEconomy(@Nullable RegisteredServiceProvider<Economy> rsp) {
        this.rspEconomy = rsp;
    }

    /**
     * Gets vault permissions instance. Should only be used after {@link #isVaultLoaded()}.
     *
     * @return vault instance
     */
    public Permission getVaultPermissions() {
        if (rspPermission == null)
            throw new NullPointerException("The plugin tried to use Vault without it being loaded. Use the VaultHook#isVaultLoaded method before using vault methods.");
        return rspPermission.getProvider();
    }

    /**
     * Sets the vault permissions service provider.
     *
     * @param rsp The service provider providing {@link Permission}
     */
    @ApiStatus.Internal
    private void setVaultPermissions(@Nullable RegisteredServiceProvider<Permission> rsp) {
        this.rspPermission = rsp;
    }

    /**
     * Gets vault Chat instance. Should only be used after {@link #isVaultLoaded()}.
     *
     * @return vault instance
     */
    public Chat getVaultChat() {
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
    private void setVaultChat(@Nullable RegisteredServiceProvider<Chat> rsp) {
        this.rspChat = rsp;
    }
}
