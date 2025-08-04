package org.bruno.sonus.handlers;

import org.bukkit.configuration.file.FileConfiguration;

public final class MessagesHandler {
    private FileConfiguration fileConfiguration;

    private String prefix;
    private String noPermission;

    private String reloadStart;
    private String reloadSuccess;

    private String regionDefined;
    private String regionDeleted;
    private String regionAlreadyExists;
    private String regionNotFound;
    private String noRegionsDefined;
    private String wandGiven;
    private String pos1Set;
    private String pos2Set;

    public MessagesHandler(FileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;

        loadMessages();
    }

    /**
     * Reloads all message strings from a new FileConfiguration object.
     * @param newFileConfiguration The newly reloaded messages.yml config object.
     */
    public void reload(FileConfiguration newFileConfiguration) {
        this.fileConfiguration = newFileConfiguration;

        loadMessages();
    }

    public void loadMessages() {
        // General Messages
        this.prefix = fileConfiguration.getString("prefix", "&#B0D0FFSonus &b» ");
        this.noPermission = fileConfiguration.getString("no-permission", "&cYou do not have permission to use this command.");

        // Reload Command Messages
        this.reloadStart = fileConfiguration.getString("reload-start", "&eReloading Sonus... Please wait.");
        this.reloadSuccess = fileConfiguration.getString("reload-success", "&aPlugin successfully reloaded! All configuration files are up to date.");

        // Region Command Messages
        this.regionDefined = fileConfiguration.getString("region-defined", "&aSuccessfully defined new sound region '{0}'!");
        this.regionDeleted = fileConfiguration.getString("region-deleted", "&aSuccessfully deleted sound region '{0}'.");
        this.regionAlreadyExists = fileConfiguration.getString("region-already-exists", "&cError: A region with the name '{0}' already exists.");
        this.regionNotFound = fileConfiguration.getString("region-not-found", "&cError: No region found with the name '{0}'.");
        this.noRegionsDefined = fileConfiguration.getString("no-regions-defined", "&7There are no sound regions defined yet.");
        this.wandGiven = fileConfiguration.getString("wand-given", "&aYou have been given the region selection wand.");
        this.pos1Set = fileConfiguration.getString("pos1-set", "&ePosition 1 set. Now select the second corner.");
        this.pos2Set = fileConfiguration.getString("pos2-set", "&ePosition 2 set. Use /sonus define <name> to create the region.");
    }

    public String getPrefix() { return prefix; }
    public String getNoPermission() { return noPermission; }
    public String getReloadStart() { return reloadStart; }
    public String getReloadSuccess() { return reloadSuccess; }
    public String getRegionDefined() { return regionDefined; }
    public String getRegionDeleted() { return regionDeleted; }
    public String getRegionAlreadyExists() { return regionAlreadyExists; }
    public String getRegionNotFound() { return regionNotFound; }
    public String getNoRegionsDefined() { return noRegionsDefined; }
    public String getWandGiven() { return wandGiven; }
    public String getPos1Set() { return pos1Set; }
    public String getPos2Set() { return pos2Set; }
}
