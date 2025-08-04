package org.bruno.sonus.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bruno.sonus.Sonus;
import org.bruno.sonus.helpers.ColorHelper;
import org.bruno.sonus.utils.Constants;
import org.bruno.sonus.helpers.MessagesHelper;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HelpCommand implements SubCommand {

    private final Sonus plugin;
    private final List<HelpEntry> allCommands = new ArrayList<>();
    private static final int COMMANDS_PER_PAGE = 5;

    private record HelpEntry(String command, String description, String permission) {}

    public HelpCommand(Sonus plugin, MessagesHelper messagesHelper) {
        this.plugin = plugin;
        initializeHelpEntries();
    }

    private void initializeHelpEntries() {
        allCommands.add(new HelpEntry("/sonus reload", "Reloads the plugin's configuration files.", Constants.Permissions.CMD_RELOAD));
        allCommands.add(new HelpEntry("/sonus define <name>", "Defines a new sound region with your wand selection.", Constants.Permissions.CMD_DEFINE));
        allCommands.add(new HelpEntry("/sonus delete <name>", "Deletes an existing sound region.", Constants.Permissions.CMD_DELETE));
        allCommands.add(new HelpEntry("/sonus list", "Lists all currently defined sound regions.", Constants.Permissions.CMD_LIST));
        allCommands.add(new HelpEntry("/sonus wand", "Gives you the region selection wand.", Constants.Permissions.CMD_WAND));
        allCommands.add(new HelpEntry("/sonus set <region_name> <sound_profile>", "Sets a profile sound to a region.", Constants.Permissions.CMD_SET));

    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // Filter commands based on sender's permissions
        List<HelpEntry> allowedCommands = allCommands.stream()
                .filter(entry -> sender.hasPermission(entry.permission()))
                .toList();

        // Calculate pagination
        int totalPages = (int) Math.ceil((double) allowedCommands.size() / COMMANDS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        int page = 1;
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
                if (page < 1 || page > totalPages) {
                    sender.sendMessage("§cInvalid page number.");
                    return true;
                }
                if (sender instanceof Player p) {
                    p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 0.8f, 1.0f);
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid page number. Please use a number.");
                return true;
            }
        }

        sendHeader(sender);

        int startIndex = (page - 1) * COMMANDS_PER_PAGE;
        for (int i = 0; i < COMMANDS_PER_PAGE; i++) {
            int listIndex = startIndex + i;
            if (listIndex < allowedCommands.size()) {
                sendHelpLine(sender, allowedCommands.get(listIndex));
            }
        }

        sendNavigationFooter(sender, page, totalPages);
        return true;
    }

    @Override
    public List<String> getSubcommandCompletions(CommandSender sender, String[] args) {
        if (args.length == 1) {
            long allowedCount = allCommands.stream().filter(entry -> sender.hasPermission(entry.permission())).count();
            int totalPages = (int) Math.ceil((double) allowedCount / COMMANDS_PER_PAGE);

            return IntStream.rangeClosed(1, totalPages)
                    .mapToObj(String::valueOf)
                    .filter(s -> s.startsWith(args[0]))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    private void sendHeader(CommandSender sender) {
        String primary = ColorHelper.parse("&#B0D0FF");
        String secondary = "§b";
        String text = "§7";
        sender.sendMessage(primary + "§m----------------------------------------------------");
        sender.sendMessage("");
        sender.sendMessage(primary + "§lSonus " + secondary + "v" + plugin.getPluginMeta().getVersion());
        sender.sendMessage(text + "All available commands are listed below.");
        sender.sendMessage("");
    }

    private void sendHelpLine(CommandSender sender, HelpEntry entry) {
        TextComponent message = new TextComponent(TextComponent.fromLegacyText("§b» "));

        TextComponent commandComponent = new TextComponent(TextComponent.fromLegacyText("§b" + entry.command()));
        commandComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, entry.command()));
        commandComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(TextComponent.fromLegacyText("§bClick to suggest the command.\n§7Permission: §f" + entry.permission()))
        ));

        message.addExtra(commandComponent);
        message.addExtra(new TextComponent(TextComponent.fromLegacyText(" §7- " + entry.description())));

        sender.spigot().sendMessage(message);
    }

    private void sendNavigationFooter(CommandSender sender, int currentPage, int totalPages) {
        sender.sendMessage("");

        TextComponent footer = new TextComponent(ColorHelper.parse("&#B0D0FF&m------------&r&b[ &b"));

        if (currentPage > 1) {
            TextComponent prevButton = new TextComponent(TextComponent.fromLegacyText("« Prev"));
            prevButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sonus help " + (currentPage - 1)));
            prevButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Go to previous page")));
            footer.addExtra(prevButton);
        } else {
            footer.addExtra(new TextComponent("§7§m« Prev"));
        }

        footer.addExtra(new TextComponent(String.format(" §r§6| §fPage %d/%d §6|§r ", currentPage, totalPages)));

        if (currentPage < totalPages) {
            TextComponent nextButton = new TextComponent(TextComponent.fromLegacyText("Next »"));
            nextButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sonus help " + (currentPage + 1)));
            nextButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Go to next page")));
            footer.addExtra(nextButton);
        } else {
            footer.addExtra(new TextComponent("§7§mNext »"));
        }

        footer.addExtra(new TextComponent(ColorHelper.parse(" &r&#B0D0FF&m]&m------------")));
        sender.spigot().sendMessage(footer);
    }
}
