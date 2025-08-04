package org.bruno.sonus.helpers;

import org.bruno.sonus.Sonus;
import org.bruno.sonus.utils.Constants;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A helper class to manage all custom configuration files for the Sonus plugin.
 */
public final class FileHelper {
    private final Sonus plugin;
    private final Logger logger;

    private FileConfiguration regionsConfig;
    private FileConfiguration soundsConfig;
    private FileConfiguration messagesConfig;

    public FileHelper(Sonus plugin, Logger logger) {
        this.plugin = plugin;
        this.logger = logger;
        initialize();
    }

    /**
     * Initializes all custom configuration files.
     */
    public void initialize() {
        logger.info("Loading Sonus configuration files...");

        this.regionsConfig = setupCustomFile(Constants.Files.REGIONS_FILE);
        this.soundsConfig = setupCustomFile(Constants.Files.SOUNDS_FILE);
        this.messagesConfig = setupCustomFile(Constants.Files.MESSAGES_FILE);

        logger.info("All Sonus configuration files loaded successfully.");
    }

    /**
     * Reloads all custom configuration files from the disk.
     */
    public void reloadAll() {
        logger.info("Reloading Sonus configuration files...");

        this.regionsConfig = setupCustomFile(Constants.Files.REGIONS_FILE);
        this.soundsConfig = setupCustomFile(Constants.Files.SOUNDS_FILE);
        this.messagesConfig = setupCustomFile(Constants.Files.MESSAGES_FILE);

        logger.info("All Sonus files have been reloaded.");
    }

    public FileConfiguration getRegionsConfig() { return this.regionsConfig; }
    public FileConfiguration getSoundsConfig() { return this.soundsConfig; }
    public FileConfiguration getMessagesConfig() {
        return this.messagesConfig;
    }

    public void saveRegionsConfig() {
        try {
            regionsConfig.save(new File(plugin.getDataFolder(), Constants.Files.REGIONS_FILE));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not save the regions.yml file!", e);
        }
    }

    public void saveSoundsConfig() {
        try {
            soundsConfig.save(new File(plugin.getDataFolder(), Constants.Files.SOUNDS_FILE));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not save the sounds.yml file!", e);
        }
    }

    /**
     * A generic method to handle the setup of any custom .yml file.
     * It ensures the file exists (creating it from defaults if necessary) and returns its configuration.
     *
     * @param fileName The name of the file (e.g., "regions.yml").
     * @return The loaded FileConfiguration for that file.
     */
    private FileConfiguration setupCustomFile(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            logger.info("File not found: " + fileName + ". Creating from defaults.");
            plugin.saveResource(fileName, false);
        }

        return YamlConfiguration.loadConfiguration(file);
    }
}
