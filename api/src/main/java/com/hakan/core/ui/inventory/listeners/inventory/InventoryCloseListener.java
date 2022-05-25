package com.hakan.core.ui.inventory.listeners.inventory;

import com.hakan.core.HCore;
import com.hakan.core.ui.inventory.HInventory;
import com.hakan.core.ui.inventory.HInventoryHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import javax.annotation.Nonnull;

/**
 * This class handles inventory
 * close event for CLOSABLE option.
 */
public final class InventoryCloseListener implements Listener {

    /**
     * Inventory close event.
     *
     * @param event Event.
     */
    @EventHandler
    public void onClose(@Nonnull InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        HInventoryHandler.findByPlayer(player).ifPresent(hInventory -> {
            if (hInventory.hasOption(HInventory.Option.CLOSABLE)) {
                hInventory.onClose(hInventory, player);
                HInventoryHandler.getContent().remove(player.getUniqueId());
                player.updateInventory();
            } else {
                HCore.syncScheduler().after(1).run(() -> player.openInventory(hInventory.toInventory()));
            }
        });
    }
}