package org.bruno.sonus.handlers;

import org.bruno.sonus.data.SoundProfile;
import org.bruno.sonus.helpers.FileHelper;
import org.bruno.sonus.utils.SetSoundResult;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class SoundProfileHandler {
    private final FileHelper fileHelper;
    private final Logger logger;
    private final RegionHandler regionHandler;

    private final Map<String, SoundProfile> soundProfiles = new ConcurrentHashMap<>();
    private final Map<String, String> regionSoundMap = new ConcurrentHashMap<>();

    public SoundProfileHandler(FileHelper fileHelper, Logger logger, RegionHandler regionHandler) {
        this.fileHelper = fileHelper;
        this.logger = logger;
        this.regionHandler = regionHandler;

        loadSoundProfiles();
    }

    public void loadSoundProfiles() {
        soundProfiles.clear();
        regionSoundMap.clear();

        ConfigurationSection profilesSection = fileHelper.getSoundsConfig().getConfigurationSection("sound-profiles");
        if (profilesSection != null) {
            for (String profileName : profilesSection.getKeys(false)) {
                String path = "sound-profiles." + profileName;
                try {
                    String sound = fileHelper.getSoundsConfig().getString(path + ".sound");
                    SoundCategory source = SoundCategory.valueOf(fileHelper.getSoundsConfig().getString(path + ".source", "MASTER").toUpperCase());
                    float volume = (float) fileHelper.getSoundsConfig().getDouble(path + ".volume", 1.0);
                    float pitch = (float) fileHelper.getSoundsConfig().getDouble(path + ".pitch", 1.0);
                    boolean loop = fileHelper.getSoundsConfig().getBoolean(path + ".loop", false);
                    int loopTime = fileHelper.getSoundsConfig().getInt(path + ".loop-time", 60);

                    soundProfiles.put(profileName.toUpperCase(), new SoundProfile(sound, source, volume, pitch, loop, loopTime));
                } catch (Exception e) {
                    logger.warning("Failed to load sound profile '" + profileName + "'. Please check its format.");
                }
            }
        }
        logger.info("Loaded " + soundProfiles.size() + " sound profiles.");

        ConfigurationSection regionSoundsSection = fileHelper.getSoundsConfig().getConfigurationSection("region-sounds");
        if (regionSoundsSection != null) {
            for (String regionName : regionSoundsSection.getKeys(false)) {
                regionSoundMap.put(regionName.toLowerCase(), regionSoundsSection.getString(regionName).toUpperCase());
            }
        }
    }

    /**
     * Binds a sound profile to a region and saves it to the sounds.yml file.
     * @param regionName The name of the region to bind to.
     * @param profileName The name of the sound profile to assign.
     * @return An enum indicating the result of the operation (SUCCESS, REGION_NOT_FOUND, PROFILE_NOT_FOUND).
     */
    public SetSoundResult setRegionSound(String regionName, String profileName) {
        if (regionHandler.getRegion(regionName) == null) {
            return SetSoundResult.REGION_NOT_FOUND;
        }

        // Validate that the sound profile exists
        if (!soundProfiles.containsKey(profileName.toUpperCase())) {
            return SetSoundResult.PROFILE_NOT_FOUND;
        }

        // Update the configuration and save the file
        fileHelper.getSoundsConfig().set("region-sounds." + regionName.toLowerCase(), profileName.toUpperCase());
        fileHelper.saveSoundsConfig();

        // Reload the internal map to reflect the change immediately
        loadSoundProfiles();

        return SetSoundResult.SUCCESS;
    }

    /**
     * Gets the SoundProfile associated with a given region name.
     * @param regionName The name of the region.
     * @return The SoundProfile, or null if none is assigned.
     */
    @Nullable
    public SoundProfile getProfileForRegion(String regionName) {
        String profileName = regionSoundMap.get(regionName.toLowerCase());
        if (profileName == null) return null;
        return soundProfiles.get(profileName);
    }

    /**
     * Gets a read-only set of all loaded sound profile keys.
     * This is useful for tab-completion.
     * @return A Set of all sound profile names.
     */
    public Set<String> getSoundProfileKeys() {
        return soundProfiles.keySet();
    }
}
