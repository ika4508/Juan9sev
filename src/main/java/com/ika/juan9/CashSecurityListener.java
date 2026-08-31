package com.ika.juan9;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CashSecurityListener implements Listener {

    private final NamespacedKey cashKey;

    public CashSecurityListener(Juan9 plugin) {
        this.cashKey = new NamespacedKey(plugin, "cash_amount");
    }

    // 1. 모루(Anvil)를 통한 화폐 위조 및 조작 차단
    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        ItemStack firstItem = event.getInventory().getFirstItem();
        if (firstItem != null && firstItem.hasItemMeta()) {
            ItemMeta meta = firstItem.getItemMeta();
            if (meta != null && meta.getPersistentDataContainer().has(cashKey, PersistentDataType.INTEGER)) {
                // 모루 결과물 생성을 차단
                event.setResult(null);
            }
        }
    }

    // 2. 일반 바닐라 주민과의 거래 악용 방지 (우리가 만든 [상점] 주민 외에는 상호작용 차단)
    @EventHandler
    public void onVillagerInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (event.getRightClicked() instanceof Villager) {
            Villager villager = (Villager) event.getRightClicked();

            // 플러그인 커스텀 상점 NPC가 아닌 일반 야생 주민일 경우
            if (villager.getCustomName() == null || !villager.getCustomName().startsWith("§e[상점] ")) {
                ItemStack handItem = event.getPlayer().getInventory().getItemInMainHand();
                if (handItem.hasItemMeta()) {
                    ItemMeta meta = handItem.getItemMeta();
                    if (meta != null && meta.getPersistentDataContainer().has(cashKey, PersistentDataType.INTEGER)) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§c[알림] 공식 화폐는 일반 주민과 거래할 수 없습니다.");
                    }
                }
            }
        }
    }
}