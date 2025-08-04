package org.bruno.sonus.handlers;

import org.bruno.sonus.data.SoundRegion;
import org.bruno.sonus.helpers.FileHelper;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class RegionHandler {
    private final FileHelper fileHelper;
    private final Logger logger;

    private final Map<String, SoundRegion> regions = new ConcurrentHashMap<>();

    public RegionHandler(FileHelper fileHelper, Logger logger) {
        this.fileHelper = fileHelper;
        this.logger = logger;

        loadRegions();
    }

    /**
     * Loads all regions from the regions.yml file into the cache.
     */
    public void loadRegions() {
        regions.clear();
        ConfigurationSection regionsSection = fileHelper.getRegionsConfig().getConfigurationSection("regions");
        if (regionsSection == null) {
            logger.info("No regions found in regions.yml. Ready to create new ones!");
            return;
        }

        for (String regionName : regionsSection.getKeys(false)) {
            String path = "regions." + regionName;
            String worldName = fileHelper.getRegionsConfig().getString(path + ".world");

            if (worldName == null || worldName.isEmpty()) {
                logger.warning("Could not load region '" + regionName + "' because its world is not specified.");
                continue;
            }

            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                logger.warning("Could not load region '" + regionName + "' because the world '" + worldName + "' is not loaded or does not exist.");
                continue;
            }

            // Reverted to the simpler getVector() logic
            Vector pos1 = fileHelper.getRegionsConfig().getVector(path + ".pos1");
            Vector pos2 = fileHelper.getRegionsConfig().getVector(path + ".pos2");

            if (pos1 == null || pos2 == null) {
                logger.warning("Could not load region '" + regionName + "' due to invalid position data in regions.yml.");
                continue;
            }

            Vector min = Vector.getMinimum(pos1, pos2);
            Vector max = Vector.getMaximum(pos1, pos2);
            regions.put(regionName.toLowerCase(), new SoundRegion(regionName, world, min, max));
            logger.info("Region " + regionName + "with vectors " +  min + " | " + max + "created");
        }
        logger.info("Successfully loaded " + regions.size() + " sound regions.");
    }

    /**
     * Saves all cached regions to the regions.yml file.
     */
    public void saveRegions() {
        // Set the 'regions' section to null to clear it before saving
        fileHelper.getRegionsConfig().set("regions", null);

        for (SoundRegion region : regions.values()) {
            String path = "regions." + region.name();

            // We save the original corners, not the calculated min/max, for easier editing.
            Vector pos1 = region.min();
            Vector pos2 = region.max();

            fileHelper.getRegionsConfig().set(path + ".world", region.world().getName());
            fileHelper.getRegionsConfig().set(path + ".pos1", pos1);
            fileHelper.getRegionsConfig().set(path + ".pos2", pos2);
        }

        fileHelper.saveRegionsConfig();
        logger.info("Successfully saved " + regions.size() + " sound regions.");
    }

    /**
     * Defines a new sound region from two corner locations.
     * @param name The unique name for the region.
     * @param pos1 The first corner location.
     * @param pos2 The second corner location.
     * @return true if the region was created successfully, false if a region with that name already exists.
     */
    public boolean defineRegion(String name, Location pos1, Location pos2) {
        if (regions.containsKey(name.toLowerCase())) {
            return false; // Region with this name already exists
        }

        Vector min = Vector.getMinimum(pos1.toVector(), pos2.toVector());
        Vector max = Vector.getMaximum(pos1.toVector(), pos2.toVector());
        SoundRegion newRegion = new SoundRegion(name, pos1.getWorld(), min, max);

        regions.put(name.toLowerCase(), newRegion);
        saveRegions(); // Save immediately after defining
        return true;
    }

    /**
     * Deletes a sound region.
     * @param name The name of the region to delete.
     * @return true if the region was found and deleted, false otherwise.
     */
    public boolean deleteRegion(String name) {
        SoundRegion removed = regions.remove(name.toLowerCase());
        if (removed != null) {
            saveRegions(); // Save immediately after deleting
            return true;
        }
        return false;
    }

    /**
     * Finds the SoundRegion that contains a given location.
     * @param location The location to check.
     * @return The SoundRegion, or null if the location is not in any defined region.
     */
    @Nullable
    public SoundRegion getRegionForLocation(Location location) {
        for (SoundRegion region : regions.values()) {
            if (region.contains(location)) {
                return region;
            }
        }
        return null;
    }

    /**
     * Gets a specific SoundRegion by its name (case-insensitive).
     * @param regionName The name of the region to find.
     * @return The SoundRegion object, or null if no region with that name exists.
     */
    @Nullable
    public SoundRegion getRegion(String regionName) {
        return regions.get(regionName.toLowerCase());
    }

    /**
     * Gets a collection of all loaded regions.
     * @return A read-only collection of SoundRegions.
     */
    public Collection<SoundRegion> getAllRegions() {
        return regions.values();
    }
}
