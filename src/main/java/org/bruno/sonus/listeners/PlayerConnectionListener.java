package org.bruno.sonus.listeners;

import org.bruno.sonus.handlers.ConfigHandler;
import org.bruno.sonus.handlers.PluginInfoHandler;
import org.bruno.sonus.handlers.SoundHandler;
import org.bruno.sonus.helpers.MessagesHelper;
import org.bruno.sonus.helpers.PermissionsHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    private final SoundHandler soundHandler;
    private final MessagesHelper messagesHelper;
    private final ConfigHandler configHandler;
    private final PluginInfoHandler pluginInfoHandler;

    public PlayerConnectionListener(SoundHandler soundHandler, MessagesHelper messagesHelper, ConfigHandler configHandler, PluginInfoHandler pluginInfoHandler) {
        this.soundHandler = soundHandler;
        this.messagesHelper = messagesHelper;
        this.configHandler = configHandler;
        this.pluginInfoHandler = pluginInfoHandler;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean isCheckForUpdatesEnabled = configHandler.isCheckForUpdatesEnabled();
        if (!isCheckForUpdatesEnabled) return;

        Player player = event.getPlayer();
        if (PermissionsHelper.hasUpdateNotifyPermission(player) && pluginInfoHandler.isUpdateAvailable()) {
            messagesHelper.sendUpdateNotification(player, this.pluginInfoHandler.getLatestVersion());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        soundHandler.handlePlayerQuit(event.getPlayer());
    }
}