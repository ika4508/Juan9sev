package com.ika.juan9;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Boss;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PiglinAbstract;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class MonsterHealthListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMonsterSpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Creeper creeper
                && !creeper.getScoreboardTags().contains("explosion_radius_doubled")) {
            creeper.setExplosionRadius(creeper.getExplosionRadius() * 2);
            creeper.addScoreboardTag("explosion_radius_doubled");
        }

        // 적대적 몬스터 판별 및 LivingEntity 체크
        if (isHostileMonster(entity) && entity instanceof LivingEntity) {
            LivingEntity monster = (LivingEntity) entity;

            // 이미 체력이 2배로 늘어난 상태인지 체크하여 중복 적용 방지
            if (monster.getScoreboardTags().contains("hp_doubled")) {
                return;
            }

            // 최신 버전 기준 Attribute.MAX_HEALTH 사용
            AttributeInstance maxHealthAttr = monster.getAttribute(Attribute.MAX_HEALTH);
            if (maxHealthAttr != null) {
                double originalMax = maxHealthAttr.getBaseValue();
                double doubledMax = originalMax * 2.0;

                // 최대 체력 2배 적용
                maxHealthAttr.setBaseValue(doubledMax);
                // 현재 체력도 2배가 된 최대 체력으로 충전
                monster.setHealth(doubledMax);

                // 중복 방지 태그 부여
                monster.addScoreboardTag("hp_doubled");
            }
        }
    }

    private boolean isHostileMonster(Entity entity) {
        if (entity instanceof Enemy || entity instanceof Monster) {
            return true;
        }
        if (entity instanceof Slime || entity instanceof PiglinAbstract || entity instanceof Hoglin) {
            return true;
        }
        if (entity instanceof ComplexLivingEntity || entity instanceof Boss) {
            return true;
        }
        return false;
    }
}
