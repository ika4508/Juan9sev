package com.ika.juan9;

import org.bukkit.entity.Boss;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PiglinAbstract;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class MonsterExpListener implements Listener {

    private final StatManager statManager;

    public MonsterExpListener(
            StatManager statManager
    ) {

        this.statManager =
                statManager;
    }

    // =========================================================
    // 적대적 몬스터 경험치 드롭 조정
    //
    // 기존 경험치의 10%
    // 최소 1 EXP
    // =========================================================

    @EventHandler
    public void onMonsterDeath(
            EntityDeathEvent event
    ) {

        Entity entity =
                event.getEntity();

        if (!isHostileMonster(entity)) {
            return;
        }

        int originalExp =
                event.getDroppedExp();

        if (originalExp <= 0) {
            return;
        }

        int reducedExp =
                Math.max(
                        1,
                        (int) Math.round(
                                originalExp * 0.1
                        )
                );

        event.setDroppedExp(
                reducedExp
        );
    }

    // =========================================================
    // 플레이어 경험치 획득
    // =========================================================

    @EventHandler
    public void onPlayerExpChange(
            PlayerExpChangeEvent event
    ) {

        Player player =
                event.getPlayer();

        int amount =
                event.getAmount();

        if (amount <= 0) {
            return;
        }

        // 커스텀 RPG 경험치 지급
        statManager.addExp(
                player,
                amount
        );

        // =====================================================
        // 바닐라 경험치는 제거
        //
        // 바닐라 경험치도 같이 얻고 싶다면
        // 아래 줄만 삭제
        // =====================================================

        event.setAmount(0);
    }

    // =========================================================
    // 적대적 몬스터 판별
    // =========================================================

    private boolean isHostileMonster(
            Entity entity
    ) {

        if (entity instanceof Enemy) {
            return true;
        }

        if (entity instanceof Monster) {
            return true;
        }

        if (entity instanceof Slime) {
            return true;
        }

        if (entity instanceof PiglinAbstract) {
            return true;
        }

        if (entity instanceof Hoglin) {
            return true;
        }

        if (entity instanceof ComplexLivingEntity) {
            return true;
        }

        return entity instanceof Boss;
    }
}