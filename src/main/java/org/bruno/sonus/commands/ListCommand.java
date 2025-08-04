package org.bruno.sonus.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bruno.sonus.data.SoundProfile;
import org.bruno.sonus.data.SoundRegion;
import org.bruno.sonus.handlers.MessagesHandler;
import org.bruno.sonus.handlers.RegionHandler;
import org.bruno.sonus.handlers.SoundProfileHandler;
import org.bruno.sonus.helpers.ColorHelper;
import org.bruno.sonus.helpers.MessagesHelper;
import org.bruno.sonus.helpers.PermissionsHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;

public class ListCommand implements SubCommand {

    private final RegionHandler regionHandler;
    private final SoundProfileHandler soundProfileHandler;
    private final MessagesHelper messagesHelper;
    private final MessagesHandler messagesHandler;

    public ListCommand(RegionHandler regionHandler, SoundProfileHandler soundProfileHandler, MessagesHelper messagesHelper, MessagesHandler messagesHandler) {
        this.regionHandler = regionHandler;
        this.soundProfileHandler = soundProfileHandler;
        this.messagesHelper = messagesHelper;
        this.messagesHandler = messagesHandler;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!PermissionsHelper.hasListPermission(sender)) {
            messagesHelper.sendCommandSenderMessage(sender, messagesHandler.getNoPermission());
            return true;
        }

        Collection<SoundRegion> regions = regionHandler.getAllRegions();

        if (regions.isEmpty()) {
            messagesHelper.sendCommandSenderMessage(sender, messagesHandler.getNoRegionsDefined());
            return true;
        }

        sender.sendMessage(ColorHelper.parse("&b&m--------&r &#B0D0FFSonus Sound Regions (" + regions.size() + ") &b&m--------"));
        sender.sendMessage(" ");

        // For players, send an interactive, clickable list.
        if (sender instanceof Player player) {
            for (SoundRegion region : regions) {
                sendInteractiveRegionLine(player, region);
            }
        } else {
            // For the console, send a simple text list.
            for (SoundRegion region : regions) {
                sendSimpleRegionLine(sender, region);
            }
        }

        sender.sendMessage(" ");

        return true;
    }

    /**
     * Sends a simple, non-interactive line of region info to the console.
     */
    private void sendSimpleRegionLine(CommandSender sender, SoundRegion region) {
        SoundProfile profile = soundProfileHandler.getProfileForRegion(region.name());
        String profileName = (profile != null) ? profile.sound() : "§cNone";
        String message = String.format("§f▪ %s §7(World: %s) §7| Sound: §f%s",
                region.name(),
                region.world().getName(),
                profileName
        );
        sender.sendMessage(message);
    }

    /**
     * Builds and sends a clickable TextComponent for a single region to a player.
     */
    private void sendInteractiveRegionLine(Player player, SoundRegion region) {
        SoundProfile profile = soundProfileHandler.getProfileForRegion(region.name());
        String profileName = (profile != null) ? profile.sound() : "§cNone";

        TextComponent message = new TextComponent(TextComponent.fromLegacyText("§b▪ "));

        TextComponent regionComponent = new TextComponent(TextComponent.fromLegacyText(ColorHelper.parse("&#B0D0FF" + region.name())));

        // Calculate the center of the region for teleportation
        Vector center = region.min().clone().add(region.max()).multiply(0.5);
        String teleportCommand = String.format("/teleport %d %d %d", center.getBlockX(), center.getBlockY(), center.getBlockZ());

        regionComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, teleportCommand));

        String hoverText = String.format("§bWorld: §f%s\n§bCoords: §f%d, %d, %d\n\n§aClick to teleport to the center.",
                region.world().getName(), center.getBlockX(), center.getBlockY(), center.getBlockZ());
        regionComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextComponent.fromLegacyText(hoverText))));

        message.addExtra(regionComponent);
        message.addExtra(new TextComponent(TextComponent.fromLegacyText(" §7(Sound: " + profileName + ")")));

        player.spigot().sendMessage(message);
    }
}