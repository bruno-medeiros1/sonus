package org.bruno.sonus;

/*
 * This file is part of Sonus.
 *
 * Sonus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * Sonus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

import org.bruno.sonus.commands.*;
import org.bruno.sonus.handlers.*;
import org.bruno.sonus.helpers.FileHelper;
import org.bruno.sonus.helpers.FoliaHelper;
import org.bruno.sonus.helpers.MessagesHelper;
import org.bruno.sonus.listeners.PlayerConnectionListener;
import org.bruno.sonus.listeners.WandListener;
import org.bruno.sonus.utils.Constants;
import org.bruno.sonus.utils.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;

import java.util.Objects;
import java.util.logging.Level;

public final class Sonus extends JavaPlugin {
    private ConfigHandler configHandler;
    private PluginInfoHandler pluginInfoHandler;
    private RegionHandler regionHandler;
    private SoundHandler soundHandler;
    private SoundProfileHandler soundProfileHandler;
    private UpdaterHandler updaterHandler;
    private MessagesHandler messagesHandler;
    private SelectionHandler selectionHandler;

    private MessagesHelper messagesHelper;
    private FileHelper fileHelper;
    private FoliaHelper foliaHelper;

    private ServerVersion serverVersion;
    
    @Override
    public void onLoad() {
        this.serverVersion = ServerVersion.getCurrent();
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        try {
            setupComponents();
            setupListeners();
            setupCommands();

            startAllPluginTasks();
            setupIntegrations();

            sendStartupMessages();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "A critical error occurred during plugin startup. Disabling Sonus.", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        sendOnDisableMessages();

        shutdownAllPluginTasks();
        HandlerList.unregisterAll(this);
    }

    public void startAllPluginTasks() {
        if (soundHandler != null) soundHandler.start();
    }

    public void reload() {
        // Stop all repeating tasks
        shutdownAllPluginTasks();

        // Reload configuration files from disk
        reloadConfig();
        fileHelper.reloadAll();

        // Tell handlers to update their internal values from the reloaded configs
        configHandler.reload(getConfig());
        messagesHandler.reload(fileHelper.getMessagesConfig());
        messagesHelper.setPrefix(messagesHandler.getPrefix());

        // Restart all repeating tasks with the new settings
        startAllPluginTasks();
    }

    private void setupComponents() throws Exception {
        // Load configuration files and initialize helpers
        saveDefaultConfig();
        this.serverVersion = ServerVersion.getCurrent();
        this.foliaHelper = new FoliaHelper(this);
        this.fileHelper = new FileHelper(this, getLogger());
        this.messagesHelper = new MessagesHelper(this.serverVersion);

        try {
            this.pluginInfoHandler = new PluginInfoHandler(this.getPluginMeta());
        } catch (NoSuchMethodError e) {
            this.pluginInfoHandler = new PluginInfoHandler(this.getDescription());
        }

        this.configHandler = new ConfigHandler(this.getConfig(), getLogger());
        this.messagesHandler = new MessagesHandler(this.fileHelper.getMessagesConfig());
        messagesHelper.setPrefix(this.messagesHandler.getPrefix());
        messagesHelper.setDebugMode(this.configHandler.isDebugModeEnabled());

        // Handlers Initialization
        this.regionHandler = new RegionHandler(this.fileHelper, getLogger());
        this.soundProfileHandler = new SoundProfileHandler(this.fileHelper, getLogger(), this.regionHandler);
        this.soundHandler = new SoundHandler(this.regionHandler, this.foliaHelper, this.soundProfileHandler, this.messagesHelper);
        this.selectionHandler = new SelectionHandler();
        this.updaterHandler = new UpdaterHandler(getLogger(), this.foliaHelper, this.configHandler, this.pluginInfoHandler);
    }

    private void setupIntegrations() {
        new Metrics(this, Constants.Integrations.BSTATS_ID);
        updaterHandler.performCheck();
    }

    private void setupListeners() {
        getLogger().info("Registering event listeners...");

        // Initialize all listeners and store their instances
        var playerConnectionListener = new PlayerConnectionListener(this.soundHandler, this.messagesHelper, this.configHandler, this.pluginInfoHandler);
        var wandListener = new WandListener(this, this.selectionHandler, this.messagesHelper, this.messagesHandler);

        // Register all listeners instances
        Bukkit.getPluginManager().registerEvents(playerConnectionListener, this);
        Bukkit.getPluginManager().registerEvents(wandListener, this);
    }

    private void setupCommands() {
        getLogger().info("Registering commands...");

        var helpCommand = new HelpCommand(this, this.messagesHelper);
        var reloadCommand = new ReloadCommand(this, this.messagesHelper, this.messagesHandler);
        var wandCommand = new WandCommand(this, this.messagesHelper, this.messagesHandler);
        var defineCommand = new DefineCommand(this.regionHandler, this.selectionHandler, this.messagesHelper, this.messagesHandler);
        var deleteCommand = new DeleteCommand(this.regionHandler, this.messagesHelper, this.messagesHandler);
        var listCommand = new ListCommand(this.regionHandler, this.soundProfileHandler, this.messagesHelper, this.messagesHandler);
        var setCommand = new SetCommand(this.regionHandler, this.soundProfileHandler, this.messagesHelper, this.messagesHandler);

        SonusCommand mainCommand = new SonusCommand(getLogger(), this.messagesHelper);
        mainCommand.registerSubCommand("help", helpCommand);
        mainCommand.registerSubCommand("reload", reloadCommand);
        mainCommand.registerSubCommand("wand", wandCommand);
        mainCommand.registerSubCommand("define", defineCommand);
        mainCommand.registerSubCommand("delete", deleteCommand);
        mainCommand.registerSubCommand("list", listCommand);
        mainCommand.registerSubCommand("set", setCommand);

        Objects.requireNonNull(getCommand("so")).setExecutor(mainCommand);
        Objects.requireNonNull(getCommand("so")).setTabCompleter(mainCommand);
    }

    public void shutdownAllPluginTasks() {
        if (soundHandler != null)
            soundHandler.shutdown();
    }

    private void sendStartupMessages() {
        messagesHelper.sendConsoleMessage("&b┌───────────────────────────────────────────┐");
        messagesHelper.sendConsoleMessage(" ");
        messagesHelper.sendConsoleMessage("  &b&lSonus &fv" + pluginInfoHandler.getCurrentVersion());
        messagesHelper.sendConsoleMessage("  &7by CodingMaestro");
        messagesHelper.sendConsoleMessage(" ");

        String platform = foliaHelper.isFolia() ? "&dFolia" : "&fSpigot/Paper/Purpur";
        messagesHelper.sendConsoleMessage("  &bStatus:");
        messagesHelper.sendConsoleMessage("    &7Platform: " + platform);
        messagesHelper.sendConsoleMessage(" ");
        messagesHelper.sendConsoleMessage("  &aPlugin has been enabled successfully!");
        messagesHelper.sendConsoleMessage(" ");
        messagesHelper.sendConsoleMessage("&b└───────────────────────────────────────────┘");

        messagesHelper.sendDebugMessage(" ");
        messagesHelper.sendDebugMessage("&6&lDEBUG MODE IS ENABLED. THIS WILL SPAM YOUR CONSOLE.");
        messagesHelper.sendDebugMessage("&6&lIT IS NOT INTENDED FOR PRODUCTION USE.");
        messagesHelper.sendDebugMessage(" ");
    }

    private void sendOnDisableMessages() {
        messagesHelper.sendConsoleMessage("&b┌───────────────────────────────────────────┐");
        messagesHelper.sendConsoleMessage(" ");
        messagesHelper.sendConsoleMessage("  &b&lSonus &fv" + pluginInfoHandler.getCurrentVersion());
        messagesHelper.sendConsoleMessage("  &7by CodingMaestro");
        messagesHelper.sendConsoleMessage(" ");
        messagesHelper.sendConsoleMessage("  &cPlugin has been disabled. All tasks stopped and data saved.");
        messagesHelper.sendConsoleMessage(" ");
        messagesHelper.sendConsoleMessage("&b└───────────────────────────────────────────┘");
    }
}
