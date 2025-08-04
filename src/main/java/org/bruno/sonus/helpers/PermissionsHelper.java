package org.bruno.sonus.helpers;

import org.bruno.sonus.utils.Constants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A utility class for handling all plugin permission checks.
 */
public final class PermissionsHelper {

    private PermissionsHelper() {}

    //<editor-fold desc="COMMAND PERMISSIONS (Sender-Agnostic)">

    public static boolean hasHelpPermission(CommandSender sender) {
        return sender.hasPermission(Constants.Permissions.ALL) ||
                sender.hasPermission(Constants.Permissions.ALL_COMMANDS) ||
                sender.hasPermission(Constants.Permissions.CMD_HELP);
    }

    public static boolean hasReloadPermission(CommandSender sender) {
        return sender.hasPermission(Constants.Permissions.ALL) ||
                sender.hasPermission(Constants.Permissions.ALL_COMMANDS) ||
                sender.hasPermission(Constants.Permissions.CMD_RELOAD);
    }

    public static boolean hasWandPermission(CommandSender sender) {
        return sender.hasPermission(Constants.Permissions.ALL) ||
                sender.hasPermission(Constants.Permissions.ALL_COMMANDS) ||
                sender.hasPermission(Constants.Permissions.CMD_WAND);
    }

    public static boolean hasDefinePermission(CommandSender sender) {
        return sender.hasPermission(Constants.Permissions.ALL) ||
                sender.hasPermission(Constants.Permissions.ALL_COMMANDS) ||
                sender.hasPermission(Constants.Permissions.CMD_DEFINE);
    }

    public static boolean hasDeletePermission(CommandSender sender) {
        return sender.hasPermission(Constants.Permissions.ALL) ||
                sender.hasPermission(Constants.Permissions.ALL_COMMANDS) ||
                sender.hasPermission(Constants.Permissions.CMD_DELETE);
    }

    public static boolean hasListPermission(CommandSender sender) {
        return sender.hasPermission(Constants.Permissions.ALL) ||
                sender.hasPermission(Constants.Permissions.ALL_COMMANDS) ||
                sender.hasPermission(Constants.Permissions.CMD_LIST);
    }

    public static boolean hasSetPermission(CommandSender sender) {
        return sender.hasPermission(Constants.Permissions.ALL) ||
                sender.hasPermission(Constants.Permissions.ALL_COMMANDS) ||
                sender.hasPermission(Constants.Permissions.CMD_SET);
    }

    //</editor-fold>

    //<editor-fold desc="FEATURE PERMISSIONS (Player-Specific)">

    public static boolean hasUpdateNotifyPermission(Player player){
        return player.hasPermission(Constants.Permissions.ALL) ||
                player.hasPermission(Constants.Permissions.NOTIFY_UPDATE);
    }

    //</editor-fold>
}
