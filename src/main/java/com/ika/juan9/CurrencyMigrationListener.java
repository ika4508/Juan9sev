package com.ika.juan9;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CurrencyMigrationListener implements Listener {

    private final CurrencyItemFactory currencyItemFactory;

    public CurrencyMigrationListener(CurrencyItemFactory currencyItemFactory) {
        this.currencyItemFactory = currencyItemFactory;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        refreshInventory(event.getPlayer().getInventory());
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        refreshInventory(event.getInventory());
        refreshInventory(event.getPlayer().getInventory());
    }

    private void refreshInventory(Inventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            currencyItemFactory.refreshCashItem(item);
        }
    }
}
