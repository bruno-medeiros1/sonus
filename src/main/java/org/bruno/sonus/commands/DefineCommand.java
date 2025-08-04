package org.bruno.sonus.commands;

import org.bruno.sonus.handlers.MessagesHandler;
import org.bruno.sonus.handlers.RegionHandler;
import org.bruno.sonus.handlers.SelectionHandler;
import org.bruno.sonus.helpers.MessagesHelper;
import org.bruno.sonus.helpers.PermissionsHelper;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DefineCommand implements SubCommand {

    private final RegionHandler regionHandler;
    private final SelectionHandler selectionHandler;
    private final MessagesHelper messagesHelper;
    private final MessagesHandler messagesHandler;

    public DefineCommand(RegionHandler regionHandler, SelectionHandler selectionHandler, MessagesHelper messagesHelper, MessagesHandler messagesHandler) {
        this.regionHandler = regionHandler;
        this.selectionHandler = selectionHandler;
        this.messagesHelper = messagesHelper;
        this.messagesHandler = messagesHandler;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            messagesHelper.sendCommandSenderMessage(sender, "&cThis command can only be run by a player.");
            return true;
        }

        if (!PermissionsHelper.hasDefinePermission(player)) {
            messagesHelper.sendCommandSenderMessage(sender, messagesHandler.getNoPermission());
            return true;
        }

        if (args.length < 1) {
            messagesHelper.sendPlayerMessage(player, "&cUsage: /sonus define <region_name>");
            return true;
        }

        if (!selectionHandler.hasFullSelection(player)) {
            messagesHelper.sendPlayerMessage(player, "&cYou must select two corners with the wand first!");
            return true;
        }

        String regionName = args[0];
        Location pos1 = selectionHandler.getPos1(player);
        Location pos2 = selectionHandler.getPos2(player);

        boolean success = regionHandler.defineRegion(regionName, pos1, pos2);
        if (success) {
            messagesHelper.sendPlayerMessage(player, messagesHandler.getRegionDefined().replace("{0}", regionName));
        } else {
            messagesHelper.sendPlayerMessage(player, messagesHandler.getRegionAlreadyExists().replace("{0}", regionName));
        }

        return true;
    }
}