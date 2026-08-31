package com.ika.juan9;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopManager {

    private final Juan9 plugin;
    public static final String EDIT_PREFIX = "§8[편집] 상점: ";
    public static final String SHOP_PREFIX = "§8[상점] ";

    public ShopManager(Juan9 plugin) {
        this.plugin = plugin;
    }

    // 1. 특정 상점 ID의 편집 GUI 오픈
    public void openEditGui(Player player, String shopId) {
        String title = EDIT_PREFIX + shopId;
        Inventory inv = Bukkit.createInventory(null, 54, title);
        FileConfiguration config = plugin.getConfig();

        String path = "shops." + shopId + ".items";
        if (config.contains(path)) {
            for (String slotStr : config.getConfigurationSection(path).getKeys(false)) {
                int slot = Integer.parseInt(slotStr);
                ItemStack item = config.getItemStack(path + "." + slotStr);
                if (slot < 54) {
                    inv.setItem(slot, item);
                }
            }
        }
        player.openInventory(inv);
    }

    // 2. 특정 상점 ID 데이터 저장
    public void saveShopItems(String shopId, Inventory inv) {
        String path = "shops." + shopId + ".items";
        plugin.getConfig().set(path, null); // 기존 데이터 초기화

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item != null) {
                plugin.getConfig().set(path + "." + i, item);
            }
        }
        plugin.saveConfig();
    }

    // 3. 일반 플레이어용 특정 상점 GUI 오픈
    public void openShopGui(Player player, String shopId) {
        String title = SHOP_PREFIX + shopId;
        Inventory inv = Bukkit.createInventory(null, 54, title);
        FileConfiguration config = plugin.getConfig();

        String path = "shops." + shopId + ".items";
        if (config.contains(path)) {
            for (String slotStr : config.getConfigurationSection(path).getKeys(false)) {
                int slot = Integer.parseInt(slotStr);
                ItemStack item = config.getItemStack(path + "." + slotStr);
                if (slot < 54) {
                    inv.setItem(slot, item);
                }
            }
        }
        player.openInventory(inv);
    }
}