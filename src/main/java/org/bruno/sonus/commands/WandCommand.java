package org.bruno.sonus.commands;

import org.bruno.sonus.Sonus;
import org.bruno.sonus.handlers.MessagesHandler;
import org.bruno.sonus.helpers.ColorHelper;
import org.bruno.sonus.helpers.MessagesHelper;
import org.bruno.sonus.helpers.PermissionsHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public class WandCommand implements SubCommand {

    private final Sonus plugin;
    private final MessagesHelper messagesHelper;
    private final MessagesHandler messagesHandler;

    public WandCommand(Sonus plugin, MessagesHelper messagesHelper, MessagesHandler messagesHandler) {
        this.plugin = plugin;
        this.messagesHelper = messagesHelper;
        this.messagesHandler = messagesHandler;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            messagesHelper.sendCommandSenderMessage(sender, "&cThis command can only be run by a player.");
            return true;
        }

        if (!PermissionsHelper.hasWandPermission(player)) {
            messagesHelper.sendCommandSenderMessage(sender, messagesHandler.getNoPermission());
            return true;
        }

        ItemStack wand = createWandItem();
        player.getInventory().addItem(wand);

        messagesHelper.sendPlayerMessage(player, messagesHandler.getWandGiven());
        return true;
    }

    /**
     * Creates the ItemStack for the region selection wand.
     * @return The wand ItemStack.
     */
    private ItemStack createWandItem() {
        ItemStack item = new ItemStack(Material.GOLDEN_AXE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ColorHelper.parse("&#B0D0FFSonus Selection Wand"));
            List<String> lore = Arrays.asList(
                    ColorHelper.parse("&7Left-Click a block to set Position 1."),
                    ColorHelper.parse("&7Right-Click a block to set Position 2.")
            );
            meta.setLore(lore);

            // Add an enchantment glow to make it look special
            meta.addEnchant(Enchantment.LURE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);

            // Add an NBT tag to identify this as our special wand
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "sonus_wand"),
                    PersistentDataType.BYTE,
                    (byte) 1
            );
            item.setItemMeta(meta);
        }
        return item;
    }
}
