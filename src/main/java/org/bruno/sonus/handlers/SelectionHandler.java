package org.bruno.sonus.handlers;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SelectionHandler {
    private final Map<UUID, Location> pos1Selections = new ConcurrentHashMap<>();
    private final Map<UUID, Location> pos2Selections = new ConcurrentHashMap<>();

    public void setPos1(Player player, Location location) {
        pos1Selections.put(player.getUniqueId(), location);
    }

    public void setPos2(Player player, Location location) {
        pos2Selections.put(player.getUniqueId(), location);
    }

    @Nullable
    public Location getPos1(Player player) {
        return pos1Selections.get(player.getUniqueId());
    }

    @Nullable
    public Location getPos2(Player player) {
        return pos2Selections.get(player.getUniqueId());
    }

    /**
     * Checks if a player has selected both corners for a region.
     * @param player The player to check.
     * @return true if both positions are set.
     */
    public boolean hasFullSelection(Player player) {
        return pos1Selections.containsKey(player.getUniqueId()) && pos2Selections.containsKey(player.getUniqueId());
    }

    /**
     * Clears a player's selection data, typically when they quit the server.
     * @param player The player whose data to clear.
     */
    public void clearSelection(Player player) {
        pos1Selections.remove(player.getUniqueId());
        pos2Selections.remove(player.getUniqueId());
    }
}
