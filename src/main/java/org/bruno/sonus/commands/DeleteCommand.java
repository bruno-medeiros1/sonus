package org.bruno.sonus.commands;

import org.bruno.sonus.handlers.MessagesHandler;
import org.bruno.sonus.handlers.RegionHandler;
import org.bruno.sonus.helpers.MessagesHelper;
import org.bruno.sonus.helpers.PermissionsHelper;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class DeleteCommand implements SubCommand {

    private final RegionHandler regionHandler;
    private final MessagesHelper messagesHelper;
    private final MessagesHandler messagesHandler;

    public DeleteCommand(RegionHandler regionHandler, MessagesHelper messagesHelper, MessagesHandler messagesHandler) {
        this.regionHandler = regionHandler;
        this.messagesHelper = messagesHelper;
        this.messagesHandler = messagesHandler;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!PermissionsHelper.hasDeletePermission(sender)) {
            messagesHelper.sendCommandSenderMessage(sender, messagesHandler.getNoPermission());
            return true;
        }

        if (args.length < 1) {
            messagesHelper.sendCommandSenderMessage(sender, "&cUsage: /sonus delete <region_name>");
            return true;
        }

        String regionName = args[0];

        // Delegate the deletion logic to the RegionHandler
        boolean success = regionHandler.deleteRegion(regionName);

        if (success) {
            messagesHelper.sendCommandSenderMessage(sender, messagesHandler.getRegionDeleted().replace("{0}", regionName));
        } else {
            messagesHelper.sendCommandSenderMessage(sender, messagesHandler.getRegionNotFound().replace("{0}", regionName));
        }

        return true;
    }

    @Override
    public List<String> getSubcommandCompletions(CommandSender sender, String[] args) {
        if (args.length == 2) {
            String input = args[1].toLowerCase();
            // Assuming RegionHandler has a method to get all region names
            return regionHandler.getAllRegions().stream()
                    .map(region -> region.name())
                    .filter(name -> name.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
