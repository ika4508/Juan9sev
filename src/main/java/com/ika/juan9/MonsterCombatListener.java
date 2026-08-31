package com.ika.juan9;

import org.bukkit.entity.Boss;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PiglinAbstract;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

public class MonsterCombatListener implements Listener {

    private final Juan9 plugin;

    public MonsterCombatListener(Juan9 plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMonsterDamage(EntityDamageByEntityEvent event) {
        Entity attacker = getAttacker(event.getDamager());

        if (!isHostileMonster(attacker)) {
            return;
        }

        double multiplier = plugin.getConfig().getDouble("monster.damage-multiplier", 1.5);
        if (!Double.isFinite(multiplier) || multiplier < 0.0) {
            multiplier = 1.5;
        }

        event.setDamage(event.getDamage() * multiplier);
    }

    private Entity getAttacker(Entity damager) {
        if (!(damager instanceof Projectile projectile)) {
            return damager;
        }

        ProjectileSource shooter = projectile.getShooter();
        return shooter instanceof Entity entity ? entity : null;
    }

    private boolean isHostileMonster(Entity entity) {
        return entity instanceof Enemy
                || entity instanceof Monster
                || entity instanceof Slime
                || entity instanceof PiglinAbstract
                || entity instanceof Hoglin
                || entity instanceof ComplexLivingEntity
                || entity instanceof Boss;
    }
}
