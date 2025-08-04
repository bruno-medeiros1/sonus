package org.bruno.sonus.listeners;

import org.bruno.sonus.Sonus;
import org.bruno.sonus.handlers.MessagesHandler;
import org.bruno.sonus.handlers.SelectionHandler;
import org.bruno.sonus.helpers.MessagesHelper;
import org.bruno.sonus.helpers.PermissionsHelper;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class WandListener implements Listener {

    private final Sonus plugin;
    private final SelectionHandler selectionHandler;
    private final MessagesHelper messagesHelper;
    private final MessagesHandler messagesHandler;

    public WandListener(Sonus plugin, SelectionHandler selectionHandler, MessagesHelper messagesHelper, MessagesHandler messagesHandler) {
        this.plugin = plugin;
        this.selectionHandler = selectionHandler;
        this.messagesHelper = messagesHelper;
        this.messagesHandler = messagesHandler;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = event.getItem();

        // Check if the player is holding the wand
        if (itemInHand == null || !isWand(itemInHand)) {
            return;
        }

        // Ensure the player has permission to use the wand
        if (!PermissionsHelper.hasWandPermission(player)) {
            return;
        }

        Action action = event.getAction();
        if (action == Action.LEFT_CLICK_BLOCK) {
            event.setCancelled(true); // Prevent breaking the block
            Location pos1 = event.getClickedBlock().getLocation();
            selectionHandler.setPos1(player, pos1);
            messagesHelper.sendPlayerMessage(player, messagesHandler.getPos1Set());
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true); // Prevent interacting with the block
            Location pos2 = event.getClickedBlock().getLocation();
            selectionHandler.setPos2(player, pos2);
            messagesHelper.sendPlayerMessage(player, messagesHandler.getPos2Set());
        }
    }

    private boolean isWand(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "sonus_wand"), PersistentDataType.BYTE);
    }
}
