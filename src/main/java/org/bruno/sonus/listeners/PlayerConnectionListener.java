package org.bruno.sonus.listeners;

import org.bruno.sonus.handlers.SoundHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    private final SoundHandler soundHandler;

    public PlayerConnectionListener(SoundHandler soundHandler) {
        this.soundHandler = soundHandler;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        soundHandler.handlePlayerQuit(event.getPlayer());
    }
}