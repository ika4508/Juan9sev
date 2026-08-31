package com.ika.juan9;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class ShopCustomListener implements Listener {

    private final Juan9 plugin;
    private final ShopManager shopManager;

    public ShopCustomListener(Juan9 plugin, ShopManager shopManager) {
        this.plugin = plugin;
        this.shopManager = shopManager;
    }

    // 주민 우클릭 시 해당 주민 이름의 상점 오픈
    @EventHandler
    public void onVillagerInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Entity entity = event.getRightClicked();
        if (entity instanceof Villager && entity.getCustomName() != null) {
            // 주민 이름에서 색상 코드를 제거한 순수 이름 추출 (예: "§a[상점] 잡화점" -> "잡화점")
            String rawName = ChatColor.stripColor(entity.getCustomName());

            // 주민 이름이 [상점]으로 시작하면 상점 종류 파악
            if (rawName.startsWith("[상점]")) {
                event.setCancelled(true);
                String shopId = rawName.replace("[상점]", "").trim(); // 상점 ID 추출
                shopManager.openShopGui(event.getPlayer(), shopId);
            }
        }
    }

    // 편집 GUI를 닫을 때 제목을 확인하여 해당 상점 ID로 저장
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();

        if (title.startsWith(ShopManager.EDIT_PREFIX)) {
            // GUI 타이틀에서 상점 ID 추출
            String shopId = title.replace(ShopManager.EDIT_PREFIX, "").trim();
            shopManager.saveShopItems(shopId, event.getInventory());
            event.getPlayer().sendMessage("§a[상점] '" + shopId + "' 상점의 품목이 성공적으로 저장되었습니다!");
        }
    }

    // 상점 GUI 클릭 취소 (아이템 도난 방지)
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.startsWith(ShopManager.SHOP_PREFIX)) {
            if (event.getRawSlot() < 54) {
                event.setCancelled(true);
                // ※ 필요시 이곳에 구매 클릭 로직 추가
            }
        }
    }
}