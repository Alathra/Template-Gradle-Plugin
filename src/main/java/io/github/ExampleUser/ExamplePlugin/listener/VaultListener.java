package io.github.ExampleUser.ExamplePlugin.listener;

import io.github.ExampleUser.ExamplePlugin.ExamplePlugin;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Event Listener for the Vault Hook {@link io.github.ExampleUser.ExamplePlugin.hooks.VaultHook}.
 */
public class VaultListener implements Listener {
    /**
     * Update the Vault hooks RegisteredServiceProviders in {@link io.github.ExampleUser.ExamplePlugin.hooks.VaultHook}. <br>This ensures the Vault hook is lazily loaded and working properly, even on reloads.
     *
     * @param e event
     */
    @SuppressWarnings("unchecked")
    @EventHandler
    public void onServiceRegisterEvent(ServiceRegisterEvent e) {
        RegisteredServiceProvider<?> rsp = e.getProvider();
        Object rspProvider = rsp.getProvider();
        if (rspProvider instanceof Economy) {
            ExamplePlugin.getVaultHook().setEconomy((RegisteredServiceProvider<Economy>) rsp);
        } else if (rspProvider instanceof Permission) {
            ExamplePlugin.getVaultHook().setPermissions((RegisteredServiceProvider<Permission>) rsp);
        } else if (rspProvider instanceof Chat) {
            ExamplePlugin.getVaultHook().setChat((RegisteredServiceProvider<Chat>) rsp);
        }
    }
}
