package org.bruno.sonus.data;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * Represents a cuboid (box-shaped) region in the world.
 * This is an immutable data object.
 */
public record SoundRegion(
        String name,
        World world,
        Vector min, // The minimum corner of the cuboid
        Vector max  // The maximum corner of the cuboid
) {
    /**
     * Checks if a given location is inside this region.
     * @param location The location to check.
     * @return true if the location is within the bounds of this region.
     */
    public boolean contains(Location location) {
        if (!location.getWorld().equals(this.world)) {
            return false;
        }
        return location.toVector().isInAABB(this.min, this.max);
    }
}