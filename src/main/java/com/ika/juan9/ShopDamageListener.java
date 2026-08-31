package com.ika.juan9;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class ShopDamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        // 피해를 입은 엔티티가 상점 주민인지 확인
        if (entity instanceof Villager && entity.getCustomName() != null && entity.getCustomName().startsWith("§e[상점] ")) {
            // 모든 피해 및 피격 리액션 취소
            event.setCancelled(true);
        }
    }
}