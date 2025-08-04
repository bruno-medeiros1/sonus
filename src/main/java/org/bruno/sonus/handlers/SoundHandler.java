package org.bruno.sonus.handlers;

import org.bruno.sonus.data.SoundProfile;
import org.bruno.sonus.data.SoundRegion;
import org.bruno.sonus.helpers.FoliaHelper;
import org.bruno.sonus.helpers.MessagesHelper;
import org.bruno.sonus.utils.CancellableTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SoundHandler {
    private final RegionHandler regionHandler;
    private final FoliaHelper foliaHelper;
    private final SoundProfileHandler soundProfileHandler;
    private final MessagesHelper messagesHelper;

    // Tracks the current region for each player to detect changes.
    private final Map<UUID, String> playerCurrentRegion = new ConcurrentHashMap<>();

    // Tracks the active looping sound task for each player.
    private final Map<UUID, CancellableTask> activeLoopingTasks = new ConcurrentHashMap<>();
    private CancellableTask mainUpdateTask;

    public SoundHandler(RegionHandler regionHandler, FoliaHelper foliaHelper, SoundProfileHandler soundProfileHandler,
                        MessagesHelper messagesHelper) {
        this.regionHandler = regionHandler;
        this.foliaHelper = foliaHelper;
        this.soundProfileHandler = soundProfileHandler;
        this.messagesHelper = messagesHelper;
    }

    public void start() {
        // This task runs every half-second to check player locations.
        this.mainUpdateTask = foliaHelper.runTaskTimerGlobal(this::updateAllPlayerSounds, 20L, 10L);
    }

    public void shutdown() {
        if (mainUpdateTask != null) mainUpdateTask.cancel();
        // Stop all looping sounds for all players
        activeLoopingTasks.values().forEach(CancellableTask::cancel);
        activeLoopingTasks.clear();
    }

    private void updateAllPlayerSounds() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            SoundRegion currentRegion = regionHandler.getRegionForLocation(player.getLocation());
            String currentRegionName = (currentRegion != null) ? currentRegion.name() : null;
            String previousRegionName = playerCurrentRegion.get(player.getUniqueId());

            // If the player's region has changed, update their sound.
            if (!Objects.equals(currentRegionName, previousRegionName)) {

                // Stop the sound from the previous region
                if (previousRegionName != null) {
                    messagesHelper.sendDebugMessage("&cPlayer " + player.getName() + " exited region: &f" + previousRegionName + ". Stopping sound...");
                    stopSoundForPlayer(player, previousRegionName);
                }

                // Start the sound for the new region
                if (currentRegionName != null) {
                    SoundProfile profile = soundProfileHandler.getProfileForRegion(currentRegionName);
                    String soundName = (profile != null) ? profile.sound() : "None";
                    messagesHelper.sendDebugMessage("&aPlayer " + player.getName() + " entered region: &f" + currentRegionName + ". Playing &f" + soundName + " sound...");
                    startSoundForPlayer(player, currentRegionName);
                }

                // Update the player's tracked location
                if (currentRegionName != null) {
                    playerCurrentRegion.put(player.getUniqueId(), currentRegionName);
                } else {
                    playerCurrentRegion.remove(player.getUniqueId());
                }
            }
        }
    }

    private void startSoundForPlayer(@NotNull Player player, @NotNull String regionName) {
        SoundProfile profile = soundProfileHandler.getProfileForRegion(regionName);
        if (profile == null) return;

        player.playSound(player.getLocation(), profile.sound(), profile.source(), profile.volume(), profile.pitch());

        if (profile.loop()) {
            CancellableTask loopingTask = foliaHelper.runTaskTimerForEntity(player, () -> {
                Location stableLocation = player.getLocation().getBlock().getLocation();
                SoundRegion checkRegion = regionHandler.getRegionForLocation(stableLocation);
                if (checkRegion != null && checkRegion.name().equalsIgnoreCase(regionName)) {
                    player.playSound(player.getLocation(), profile.sound(), profile.source(), profile.volume(), profile.pitch());
                }
            }, (long) profile.loopTime() * 20L, (long) profile.loopTime() * 20L);

            activeLoopingTasks.put(player.getUniqueId(), loopingTask);
        }
    }

    private void stopSoundForPlayer(@NotNull Player player, @NotNull String previousRegionName) {
        // Stop any active looping sound for the player
        CancellableTask loopingTask = activeLoopingTasks.remove(player.getUniqueId());
        if (loopingTask != null) {
            loopingTask.cancel();
        }

        SoundProfile previousProfile = soundProfileHandler.getProfileForRegion(previousRegionName);
        if (previousProfile != null) {
            // Tell the player's client to stop playing that specific sound from its specific category.
            player.stopSound(previousProfile.sound(), previousProfile.source());
        }

        // TODO: Implement fade-out logic here if desired.
        // A simple approach is to stop sounds from common categories. A more advanced
        // system would track the specific sound being played.
        // player.stopSound(SoundCategory.AMBIENT);
        // player.stopSound(SoundCategory.MUSIC);
    }

    public void handlePlayerQuit(@NotNull Player player) {
        // Get the player's last known region before removing them from the map.
        String previousRegionName = playerCurrentRegion.remove(player.getUniqueId());
        if (previousRegionName != null) {
            stopSoundForPlayer(player, previousRegionName);
        }
    }
}
