package org.bruno.sonus.commands;

import org.bruno.sonus.handlers.MessagesHandler;
import org.bruno.sonus.helpers.MessagesHelper;
import org.bruno.sonus.helpers.PermissionsHelper;
import org.bruno.sonus.Sonus;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

public class ReloadCommand implements SubCommand {

    private final Sonus plugin;
    private final MessagesHelper messagesHelper;
    private final MessagesHandler messagesHandler;

    public ReloadCommand(Sonus plugin, MessagesHelper messagesHelper, MessagesHandler messagesHandler) {
        this.plugin = plugin;
        this.messagesHelper = messagesHelper;
        this.messagesHandler = messagesHandler;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!PermissionsHelper.hasReloadPermission(sender)) {
            messagesHelper.sendCommandSenderMessage(sender, messagesHandler.getNoPermission());
            return true;
        }

        messagesHelper.sendCommandSenderMessage(sender, messagesHandler.getReloadStart());

        try {
            plugin.reload();
            messagesHelper.sendCommandSenderMessage(sender, messagesHandler.getReloadSuccess());
        } catch (Exception e) {
            messagesHelper.sendCommandSenderMessage(sender, "&cAn error occurred during reload. Please check the console for details.");
            plugin.getLogger().log(Level.SEVERE, "A critical error occurred during plugin reload.", e);
        }

        return true;
    }
}