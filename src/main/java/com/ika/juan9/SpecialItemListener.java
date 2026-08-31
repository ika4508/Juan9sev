package com.ika.juan9;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SpecialItemListener implements Listener {

    private final Juan9 plugin;
    private final NamespacedKey itemKey;
    private final Set<UUID> fallImmunity = new HashSet<>();
    private final Set<UUID> isSlamming = new HashSet<>();

    public SpecialItemListener(Juan9 plugin) {
        this.plugin = plugin;
        this.itemKey = new NamespacedKey(plugin, "rpg_special_item");
    }

    // 1. 일반 도약
    public ItemStack createLeapItem() {
        ItemStack item = new ItemStack(Material.FEATHER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§b§l[전직 유물] §f도약");
            List<String> lore = new ArrayList<>();
            lore.add("§7우클릭 시 바라보는 방향으로 강력하게 도약합니다.");
            lore.add("§8(착지 시 낙하 피해를 받지 않습니다.)");
            lore.add(" ");
            lore.add("§3재사용 대기시간: §f5초");
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, "leap_normal");
            item.setItemMeta(meta);
        }
        return item;
    }

    // 2. 개쩌는 도약
    public ItemStack createAwesomeLeapItem() {
        ItemStack item = new ItemStack(Material.FEATHER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§d§l[전직 전설] §b§l개쩌는 도약");
            List<String> lore = new ArrayList<>();
            lore.add("§7우클릭 시 바라보는 방향으로 쏜살같이 도약합니다.");
            lore.add("§8(착지 시 낙하 피해를 받지 않습니다.)");
            lore.add(" ");
            lore.add("§c재사용 대기시간: §e§l1초");
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, "leap_awesome");
            item.setItemMeta(meta);
        }
        return item;
    }

    // 3. 신속의 화살통
    public ItemStack createQuiverItem() {
        ItemStack item = new ItemStack(Material.BUNDLE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6§l[전직 유물] §e신속의 화살통");
            List<String> lore = new ArrayList<>();
            lore.add("§7인벤토리에 소지하고 있으면");
            lore.add("§7활을 조금만 당겨도 빠른 속도로 발사됩니다.");
            lore.add(" ");
            lore.add("§e패시브 효과: §f화살 차징 및 탄속 +30% 증가");
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, "swift_quiver");
            item.setItemMeta(meta);
        }
        return item;
    }

    // 4. 개쩌는 화살통
    public ItemStack createAwesomeQuiverItem() {
        ItemStack item = new ItemStack(Material.BUNDLE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§d§l[전직 전설] §e§l개쩌는 화살통");
            List<String> lore = new ArrayList<>();
            lore.add("§7인벤토리에 소지하고 있으면");
            lore.add("§7활을 살짝만 당겨도 즉시 풀차징 완충 사격이 발동합니다.");
            lore.add(" ");
            lore.add("§6패시브 효과: §c§l화살 차징 및 탄속 +200% 증가 (3배속 완충)");
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, "awesome_quiver");
            item.setItemMeta(meta);
        }
        return item;
    }

    // 5. 스텟 포인트 영약
    public ItemStack createStatPointItem() {
        ItemStack item = new ItemStack(Material.HONEY_BOTTLE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e§l[RPG 비약] §f스텟의 영약");
            List<String> lore = new ArrayList<>();
            lore.add("§7마법의 기운이 담긴 영약입니다.");
            lore.add("§7우클릭하여 마시면 숨겨진 잠재력이 개방됩니다.");
            lore.add(" ");
            lore.add("§a사용 효과: §e미분배 스텟 포인트 +1P 획득");
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, "stat_potion");
            item.setItemMeta(meta);
        }
        return item;
    }

    // 6. 대지강타
    public ItemStack createEarthSlamItem() {
        ItemStack item = new ItemStack(Material.MACE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§c§l[전직 전설] §6§l대지강타");
            List<String> lore = new ArrayList<>();
            lore.add("§7우클릭 시 공중으로 도약한 뒤");
            lore.add("§7지면으로 급강하하여 §c범위 충격파 피해§7를 줍니다.");
            lore.add(" ");
            lore.add("§c충격파 피해량: §f15.0 (주변 반경 5m)");
            lore.add("§3재사용 대기시간: §f8초");
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, "earth_slam");
            item.setItemMeta(meta);
        }
        return item;
    }

    // 7-1. [귀환] - 무제한
    public ItemStack createReturnScrollItem() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e§l[소모품] §f정신 집중");
            List<String> lore = new ArrayList<>();
            lore.add("§7정신을 집중하여 우물로 돌아갑니다.");
            lore.add(" ");
            lore.add("§c사용 시 1회 소모됩니다.");
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, "return_scroll");
            item.setItemMeta(meta);
        }
        return item;
    }

    // 7-2. [단거리 귀환] - 1000블록 이내
    public ItemStack createShortReturnScrollItem() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e§l[소모품] §f정신 집중 (단거리)");
            List<String> lore = new ArrayList<>();
            lore.add("§7정신을 집중하여 우물로 돌아갑니다.");
            lore.add("§8(우물 기준 §c1,000블록 이내§8에서만 작동합니다.)");
            lore.add(" ");
            lore.add("§c사용 시 1회 소모됩니다.");
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, "return_scroll_short");
            item.setItemMeta(meta);
        }
        return item;
    }

    // 7-3. [장거리 귀환] - 5000블록 이내
    public ItemStack createLongReturnScrollItem() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e§l[소모품] §f정신 집중 (장거리)");
            List<String> lore = new ArrayList<>();
            lore.add("§7정신을 집중하여 우물로 돌아갑니다.");
            lore.add("§8(우물 기준 §c5,000블록 이내§8에서만 작동합니다.)");
            lore.add(" ");
            lore.add("§c사용 시 1회 소모됩니다.");
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, "return_scroll_long");
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onPlayerUseSpecialItem(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack handItem = event.getItem();
        if (handItem == null || !handItem.hasItemMeta()) return;

        ItemMeta meta = handItem.getItemMeta();
        if (meta == null) return;

        String type = meta.getPersistentDataContainer().get(itemKey, PersistentDataType.STRING);
        if (type == null) return;

        Player player = event.getPlayer();

        // 1) 일반 도약
        if (type.equals("leap_normal")) {
            if (player.hasCooldown(Material.FEATHER)) {
                player.sendMessage("§c[알림] 아직 재사용 대기시간 중입니다.");
                return;
            }
            Vector dir = player.getLocation().getDirection().normalize().multiply(1.8).setY(0.75);
            player.setVelocity(dir);
            player.setCooldown(Material.FEATHER, 100);
            fallImmunity.add(player.getUniqueId());
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BREEZE_SHOOT, 1f, 1.2f);
            player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 20, 0.2, 0.2, 0.2, 0.1);
            player.sendMessage("§b§l[도약] §f전방으로 강하게 도약했습니다!");

            // 2) 개쩌는 도약
        } else if (type.equals("leap_awesome")) {
            if (player.hasCooldown(Material.FEATHER)) {
                player.sendMessage("§c[알림] 아직 재사용 대기시간 중입니다.");
                return;
            }
            Vector dir = player.getLocation().getDirection().normalize().multiply(2.0).setY(0.85);
            player.setVelocity(dir);
            player.setCooldown(Material.FEATHER, 20);
            fallImmunity.add(player.getUniqueId());
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1.5f);
            player.getWorld().spawnParticle(Particle.FLASH, player.getLocation().add(0, 1, 0), 1);
            player.getWorld().spawnParticle(Particle.FIREWORK, player.getLocation(), 15, 0.2, 0.2, 0.2, 0.1);
            player.sendMessage("§d§l[개쩌는 도약] §e초고속으로 전방 도약했습니다!");

            // 3) 스텟 영약
        } else if (type.equals("stat_potion")) {
            event.setCancelled(true);
            handItem.setAmount(handItem.getAmount() - 1);
            StatManager statManager = plugin.getStatManager();
            int currentPoints = statManager.getStatPoints(player);
            statManager.setStatPoints(player, currentPoints + 1);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f);
            player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation().add(0, 1.2, 0), 15, 0.3, 0.3, 0.3, 0.1);
            player.sendMessage("§6§l[RPG] §a영약을 사용하여 스텟 포인트 §e1P§a를 획득했습니다! §7(/stat 으로 분배)");

            // 4) 대지강타
        } else if (type.equals("earth_slam")) {
            event.setCancelled(true);
            if (player.hasCooldown(Material.MACE)) {
                player.sendMessage("§c[알림] 아직 재사용 대기시간 중입니다.");
                return;
            }

            player.setCooldown(Material.MACE, 160);
            fallImmunity.add(player.getUniqueId());

            player.setVelocity(new Vector(0, 1.35, 0));
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WIND_CHARGE_THROW, 1f, 0.8f);
            player.getWorld().spawnParticle(Particle.GUST, player.getLocation(), 10, 0.2, 0.2, 0.2, 0.1);
            player.sendMessage("§6§l[대지강타] §f공중으로 솟구칩니다!");

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline() || player.isDead()) return;

                    Vector dir = player.getLocation().getDirection().normalize().multiply(1.5).setY(-1.8);
                    player.setVelocity(dir);
                    isSlamming.add(player.getUniqueId());
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 0.5f);

                    new BukkitRunnable() {
                        int ticks = 0;
                        @Override
                        public void run() {
                            ticks++;
                            if (!player.isOnline() || player.isDead() || ticks > 60) {
                                isSlamming.remove(player.getUniqueId());
                                cancel();
                                return;
                            }
                            if (player.isOnGround()) {
                                isSlamming.remove(player.getUniqueId());
                                triggerEarthSlamImpact(player);
                                cancel();
                            }
                        }
                    }.runTaskTimer(plugin, 2L, 1L);
                }
            }.runTaskLater(plugin, 12L);

            // 5) 귀환 관련 스크롤 (무제한 / 단거리 / 장거리)
        } else if (type.startsWith("return_scroll")) {
            event.setCancelled(true);

            String worldName = plugin.getConfig().getString("center.world");
            if (worldName == null) {
                player.sendMessage("§c[오류] 설정된 우물(중앙 귀환 지점)이 없습니다. 관리자에게 문의하세요 (/setcenter).");
                return;
            }

            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                player.sendMessage("§c[오류] 우물 월드를 찾을 수 없습니다.");
                return;
            }

            if (!player.getWorld().equals(world)) {
                player.sendMessage("§c[오류] 우물이 존재하는 차원(월드)에서만 사용할 수 있습니다.");
                return;
            }

            double x = plugin.getConfig().getDouble("center.x");
            double y = plugin.getConfig().getDouble("center.y");
            double z = plugin.getConfig().getDouble("center.z");
            float yaw = (float) plugin.getConfig().getDouble("center.yaw");
            float pitch = (float) plugin.getConfig().getDouble("center.pitch");
            Location targetLoc = new Location(world, x, y, z, yaw, pitch);

            // 거리 검사 (X, Z 기준 평면 거리)
            double distance = Math.hypot(player.getLocation().getX() - x, player.getLocation().getZ() - z);

            if (type.equals("return_scroll_short") && distance > 1000.0) {
                player.sendMessage("§c[오류] 우물과의 거리가 너무 멉니다! (현재 거리: " + (int) distance + "m / 제한: 1,000m)");
                player.sendMessage("§7장거리 귀환 주문서나 무제한 귀환 주문서를 사용해주세요.");
                return;
            } else if (type.equals("return_scroll_long") && distance > 5000.0) {
                player.sendMessage("§c[오류] 우물과의 거리가 너무 멉니다! (현재 거리: " + (int) distance + "m / 제한: 5,000m)");
                player.sendMessage("§7무제한 귀환 주문서를 사용해주세요.");
                return;
            }

            // 조건 만족 시 1개 소모
            handItem.setAmount(handItem.getAmount() - 1);

            // 출발 이펙트
            Location fromLoc = player.getLocation();
            fromLoc.getWorld().playSound(fromLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            fromLoc.getWorld().spawnParticle(Particle.PORTAL, fromLoc.add(0, 1, 0), 30, 0.3, 0.5, 0.3, 0.1);

            // 순간이동
            player.teleport(targetLoc);

            // 도착 이펙트
            targetLoc.getWorld().playSound(targetLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1.2f);
            targetLoc.getWorld().playSound(targetLoc, Sound.BLOCK_BEACON_ACTIVATE, 0.8f, 1.5f);
            targetLoc.getWorld().spawnParticle(Particle.REVERSE_PORTAL, targetLoc.clone().add(0, 1, 0), 40, 0.3, 0.5, 0.3, 0.1);
            player.sendMessage("§e§l[정신 집중] §a정신을 집중하여 우물로 복귀했습니다!");
        }
    }

    private void triggerEarthSlamImpact(Player player) {
        Location loc = player.getLocation();
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.2f, 0.7f);
        loc.getWorld().playSound(loc, Sound.BLOCK_ANVIL_LAND, 1.5f, 0.5f);

        loc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, loc, 1);
        loc.getWorld().spawnParticle(Particle.BLOCK, loc, 60, 2.0, 0.5, 2.0, Material.DIRT.createBlockData());
        loc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, loc.clone().add(0, 0.5, 0), 10, 1.5, 0.2, 1.5);

        for (Entity target : loc.getWorld().getNearbyEntities(loc, 5.0, 3.0, 5.0)) {
            if (target instanceof LivingEntity && !target.getUniqueId().equals(player.getUniqueId())) {
                LivingEntity livingTarget = (LivingEntity) target;
                livingTarget.damage(15.0, player);
                Vector knockback = livingTarget.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(1.2).setY(0.4);
                livingTarget.setVelocity(knockback);
            }
        }
        player.sendMessage("§c§l[대지강타] §6지면을 강타하여 광역 충격파를 발생시켰습니다!");
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            Player player = (Player) event.getEntity();
            if (fallImmunity.remove(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        double speedMultiplier = 1.0;

        for (ItemStack invItem : player.getInventory().getContents()) {
            if (invItem != null && invItem.hasItemMeta()) {
                ItemMeta meta = invItem.getItemMeta();
                if (meta != null) {
                    String type = meta.getPersistentDataContainer().get(itemKey, PersistentDataType.STRING);
                    if ("awesome_quiver".equals(type)) {
                        speedMultiplier = Math.max(speedMultiplier, 3.0);
                    } else if ("swift_quiver".equals(type)) {
                        speedMultiplier = Math.max(speedMultiplier, 1.3);
                    }
                }
            }
        }

        if (speedMultiplier > 1.0) {
            float originalForce = event.getForce();
            float boostedForce = (float) Math.min(1.0, originalForce * speedMultiplier);

            if (event.getProjectile() instanceof AbstractArrow) {
                AbstractArrow arrow = (AbstractArrow) event.getProjectile();
                if (boostedForce >= 0.9f) {
                    arrow.setCritical(true);
                }
                arrow.setVelocity(arrow.getVelocity().multiply(speedMultiplier));
            }

            if (speedMultiplier >= 3.0) {
                player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.9f, 1.8f);
                player.getWorld().spawnParticle(Particle.CRIT, player.getLocation().add(0, 1.2, 0), 10, 0.2, 0.2, 0.2, 0.1);
            } else {
                player.playSound(player.getLocation(), Sound.ITEM_CROSSBOW_QUICK_CHARGE_3, 0.8f, 1.5f);
            }
        }
    }
}