package org.bruno.sonus.handlers;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public class ConfigHandler {
    private final Logger logger;

    private FileConfiguration fileConfiguration;

    // General settings
    private boolean isDebugModeEnabled;
    private boolean isCheckForUpdatesEnabled;

    public ConfigHandler(FileConfiguration fileConfiguration, Logger logger) {
        this.fileConfiguration = fileConfiguration;
        this.logger = logger;

        loadConfigVariables();
    }

    /**
     * Loads all values from the FileConfiguration into the handler's fields.
     */
    private void loadConfigVariables() {
        this.isDebugModeEnabled = fileConfiguration.getBoolean("general.debug-mode", false);
        this.isCheckForUpdatesEnabled = fileConfiguration.getBoolean("general.check-for-updates", true);
    }

    /**
     * Reloads the configuration values from a new FileConfiguration object.
     * @param newFileConfiguration The newly reloaded config object.
     */
    public void reload(FileConfiguration newFileConfiguration) {
        this.fileConfiguration = newFileConfiguration;
        loadConfigVariables();
        logger.info("Sonus configuration values have been reloaded.");
    }


    public boolean isDebugModeEnabled() { return isDebugModeEnabled; }
    public boolean isCheckForUpdatesEnabled() { return isCheckForUpdatesEnabled; }
}
