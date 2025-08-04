package org.bruno.sonus.commands;

import org.bruno.sonus.helpers.MessagesHelper;
import org.bruno.sonus.helpers.PermissionsHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SonusCommand implements CommandExecutor, TabCompleter {
    private final Logger logger;
    private final MessagesHelper messagesHelper;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public SonusCommand(Logger logger, MessagesHelper messagesHelper) {
        this.logger = logger;
        this.messagesHelper = messagesHelper;
    }

    /**
     * Registers a new subcommand with the main command handler.
     * @param name The name of the subcommand (e.g., "help", "define").
     * @param command The SubCommand instance that will handle the logic.
     */
    public void registerSubCommand(String name, SubCommand command) {
        subCommands.put(name.toLowerCase(), command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            SubCommand helpCommand = subCommands.get("help");
            if (helpCommand != null) {
                helpCommand.execute(sender, new String[0]);
            } else {
                messagesHelper.sendCommandSenderMessage(sender, "&cThe help command is not available.");
            }
            return true;
        }

        String subCommandName = args[0].toLowerCase();
        SubCommand subCommand = subCommands.get(subCommandName);

        if (subCommand == null) {
            messagesHelper.sendCommandSenderMessage(sender, "&cUnknown subcommand. Use /so help for a list of commands.");
            return true;
        }

        try {
            // Pass the remaining arguments to the subcommand.
            String[] subCommandArgs = Arrays.copyOfRange(args, 1, args.length);
            subCommand.execute(sender, subCommandArgs);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An unexpected error occurred while executing command '" + subCommandName + "' for " + sender.getName(), e);
            messagesHelper.sendCommandSenderMessage(sender, "&cAn unexpected error occurred. Please contact an administrator.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();

            // Use a helper method to check permissions for each subcommand
            if (PermissionsHelper.hasHelpPermission(sender)) completions.add("help");
            if (PermissionsHelper.hasReloadPermission(sender)) completions.add("reload");
            if (PermissionsHelper.hasWandPermission(sender)) completions.add("wand");
            if (PermissionsHelper.hasDefinePermission(sender)) completions.add("define");
            if (PermissionsHelper.hasDeletePermission(sender)) completions.add("delete");
            if (PermissionsHelper.hasListPermission(sender)) completions.add("list");
            if (PermissionsHelper.hasSetPermission(sender)) completions.add("set");

            // Return suggestions that start with what the player has already typed
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());

        }
        else if (args.length > 1)
        {
            // Player is typing arguments for a subcommand
            String subCommandName = args[0].toLowerCase();
            SubCommand subCommand = subCommands.get(subCommandName);

            if (subCommand != null) {
                // Pass only the relevant arguments to the subcommand's completer
                return subCommand.getSubcommandCompletions(sender, args);
            }
        }

        return List.of();
    }
}