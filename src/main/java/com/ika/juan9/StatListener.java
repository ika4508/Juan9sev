package com.ika.juan9;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.projectiles.ProjectileSource;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StatListener implements Listener {

    private final Juan9 plugin;
    private final StatManager statManager;

    private static final String GUI_TITLE =
            "§8[ 캐릭터 스탯 ]";

    private static final DecimalFormat NUMBER_FORMAT =
            new DecimalFormat("#,###");

    public StatListener(
            Juan9 plugin,
            StatManager statManager
    ) {

        this.plugin = plugin;
        this.statManager = statManager;
    }

    // =========================================================
    // /stat GUI
    // =========================================================

    public void openStatGui(
            Player player
    ) {

        if (player == null
                || !player.isOnline()) {
            return;
        }

        // =====================================================
        // 레벨 정보
        // =====================================================

        int level =
                statManager.getLevel(player);

        long exp =
                statManager.getExp(player);

        long requiredExp =
                statManager.getRequiredExp(level);

        int points =
                statManager.getStatPoints(player);

        double progress;

        if (requiredExp <= 0) {

            progress = 0.0;

        } else {

            progress =
                    (double) exp
                            / (double) requiredExp;
        }

        progress =
                Math.max(
                        0.0,
                        Math.min(
                                1.0,
                                progress
                        )
                );

        int percentage =
                (int) Math.floor(
                        progress * 100.0
                );

        String expBar =
                createExpBar(
                        progress,
                        20
                );

        // =====================================================
        // 스탯
        // =====================================================

        int health =
                statManager.getStat(
                        player,
                        "health"
                );

        int attack =
                statManager.getStat(
                        player,
                        "attack"
                );

        int speed =
                statManager.getStat(
                        player,
                        "speed"
                );

        int defense =
                statManager.getStat(
                        player,
                        "defense"
                );

        // =====================================================
        // 54칸 큰 상자
        // =====================================================

        Inventory gui =
                Bukkit.createInventory(
                        null,
                        54,
                        GUI_TITLE
                );

        // =====================================================
        // 기본 배경
        // =====================================================

        ItemStack background =
                createItem(
                        Material.GRAY_STAINED_GLASS_PANE,
                        " "
                );

        for (int i = 0;
             i < 54;
             i++) {

            gui.setItem(
                    i,
                    background
            );
        }

        // =====================================================
        // 레벨
        // =====================================================

        gui.setItem(
                11,
                createItem(
                        Material.NETHER_STAR,

                        "§e§l[ 레벨 ]",

                        "",

                        "§7현재 레벨",
                        "§e§lLv. " + level,

                        "",

                        "§7경험치 진행도",
                        expBar,

                        "§f"
                                + formatNumber(exp)
                                + " §7/ §f"
                                + formatNumber(requiredExp)
                                + " EXP",

                        "§7진행률: §a"
                                + percentage
                                + "%",

                        "",

                        "§8경험치를 획득하여",
                        "§8다음 레벨에 도달할 수 있습니다."
                )
        );

        // =====================================================
        // 현재 경험치
        // =====================================================

        gui.setItem(
                13,
                createItem(
                        Material.EXPERIENCE_BOTTLE,

                        "§a§l[ 경험치 ]",

                        "",

                        "§7현재 경험치",
                        "§a"
                                + formatNumber(exp)
                                + " EXP",

                        "",

                        "§7다음 레벨까지",
                        "§e"
                                + formatNumber(
                                Math.max(
                                        0L,
                                        requiredExp - exp
                                )
                        )
                                + " EXP §7남음"
                )
        );

        // =====================================================
        // 스탯 포인트
        // =====================================================

        gui.setItem(
                15,
                createItem(
                        Material.EMERALD,

                        "§b§l[ 스탯 포인트 ]",

                        "",

                        "§7사용 가능",
                        "§e§l"
                                + points
                                + " P",

                        "",

                        "§8레벨업 시 1P를 획득합니다.",

                        "",

                        "§7아래 능력치를 클릭하여",
                        "§7포인트를 사용할 수 있습니다."
                )
        );

        // =====================================================
        // 구분선
        // =====================================================

        ItemStack separator =
                createItem(
                        Material.BLACK_STAINED_GLASS_PANE,
                        " "
                );

        for (int slot = 18;
             slot <= 26;
             slot++) {

            gui.setItem(
                    slot,
                    separator
            );
        }

        // =====================================================
        // 체력
        // =====================================================

        gui.setItem(
                29,
                createItem(
                        Material.REDSTONE,

                        "§c§l[ 체력 ]",

                        "",

                        "§7투자 포인트",
                        "§f" + health,

                        "",

                        "§7추가 최대 체력",
                        "§c+"
                                + (health * 2),

                        "",

                        "§8스탯 1당 최대체력 +2",

                        "",

                        "§e▶ 클릭하여 +1",
                        "§7소모: §f1P"
                )
        );

        // =====================================================
        // 공격력
        // =====================================================

        gui.setItem(
                31,
                createItem(
                        Material.NETHERITE_SWORD,

                        "§c§l[ 공격력 ]",

                        "",

                        "§7투자 포인트",
                        "§f" + attack,

                        "",

                        "§7추가 피해량",
                        "§c+"
                                + String.format(
                                "%.1f",
                                attack * 0.5
                        ),

                        "",

                        "§8스탯 1당 피해량 +0.5",

                        "",

                        "§e▶ 클릭하여 +1",
                        "§7소모: §f1P"
                )
        );

        // =====================================================
        // 이동속도
        // =====================================================

        gui.setItem(
                33,
                createItem(
                        Material.FEATHER,

                        "§b§l[ 이동속도 ]",

                        "",

                        "§7투자 포인트",
                        "§f" + speed,

                        "",

                        "§7추가 이동속도",
                        "§b+"
                                + String.format(
                                "%.3f",
                                speed * 0.002
                        ),

                        "",

                        "§8스탯 1당 이동속도 +0.002",

                        "",

                        "§e▶ 클릭하여 +1",
                        "§7소모: §f1P"
                )
        );

        // =====================================================
        // 방어력
        // =====================================================

        gui.setItem(
                35,
                createItem(
                        Material.IRON_CHESTPLATE,

                        "§a§l[ 방어력 ]",

                        "",

                        "§7투자 포인트",
                        "§f" + defense,

                        "",

                        "§7추가 방어력",
                        "§a+"
                                + String.format(
                                "%.1f",
                                defense * 0.5
                        ),

                        "",

                        "§8스탯 1당 방어력 +0.5",

                        "",

                        "§e▶ 클릭하여 +1",
                        "§7소모: §f1P"
                )
        );

        // =====================================================
        // 하단 종합정보
        // =====================================================

        gui.setItem(
                49,
                createItem(
                        Material.BOOK,

                        "§f§l[ 캐릭터 정보 ]",

                        "",

                        "§7레벨: §eLv."
                                + level,

                        "§7경험치: §a"
                                + formatNumber(exp)
                                + " / "
                                + formatNumber(requiredExp),

                        "§7스탯 포인트: §b"
                                + points
                                + "P",

                        "",

                        "§7체력: §f"
                                + health,

                        "§7공격력: §f"
                                + attack,

                        "§7이동속도: §f"
                                + speed,

                        "§7방어력: §f"
                                + defense
                )
        );

        player.openInventory(gui);
    }

    // =========================================================
    // 경험치 진행 바
    // =========================================================

    private String createExpBar(
            double progress,
            int length
    ) {

        int filled =
                (int) Math.round(
                        progress * length
                );

        filled =
                Math.max(
                        0,
                        Math.min(
                                length,
                                filled
                        )
                );

        StringBuilder bar =
                new StringBuilder();

        // 채워진 부분
        bar.append("§a");

        for (int i = 0;
             i < filled;
             i++) {

            bar.append("■");
        }

        // 남은 부분
        bar.append("§8");

        for (int i = filled;
             i < length;
             i++) {

            bar.append("■");
        }

        return bar.toString();
    }

    // =========================================================
    // 숫자 천 단위 쉼표
    // =========================================================

    private String formatNumber(
            long number
    ) {

        return NUMBER_FORMAT.format(
                number
        );
    }

    // =========================================================
    // GUI 아이템 생성
    // =========================================================

    private ItemStack createItem(
            Material material,
            String name,
            String... loreLines
    ) {

        ItemStack item =
                new ItemStack(material);

        ItemMeta meta =
                item.getItemMeta();

        if (meta != null) {

            meta.setDisplayName(
                    name
            );

            List<String> lore =
                    new ArrayList<>();

            for (String line : loreLines) {
                lore.add(line);
            }

            meta.setLore(
                    lore
            );

            item.setItemMeta(
                    meta
            );
        }

        return item;
    }

    // =========================================================
    // GUI 클릭
    // =========================================================

    @EventHandler
    public void onStatGuiClick(
            InventoryClickEvent event
    ) {

        if (!event
                .getView()
                .getTitle()
                .equals(GUI_TITLE)) {

            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked()
                instanceof Player player)) {

            return;
        }

        int slot =
                event.getRawSlot();

        if (slot < 0) {
            return;
        }

        if (slot >= event
                .getView()
                .getTopInventory()
                .getSize()) {

            return;
        }

        String statType;

        switch (slot) {

            case 29:
                statType = "health";
                break;

            case 31:
                statType = "attack";
                break;

            case 33:
                statType = "speed";
                break;

            case 35:
                statType = "defense";
                break;

            default:
                return;
        }

        int points =
                statManager.getStatPoints(
                        player
                );

        if (points <= 0) {

            player.sendMessage(
                    "§c[스탯] 사용할 수 있는 스탯 포인트가 없습니다."
            );

            return;
        }

        statManager.addStat(
                player,
                statType,
                1
        );

        statManager.setStatPoints(
                player,
                points - 1
        );

        applyStats(
                player
        );

        player.sendMessage(
                "§a[스탯] §f"
                        + getStatDisplayName(statType)
                        + "§a 스탯이 §e+1§a 증가했습니다."
        );

        // GUI 갱신
        openStatGui(
                player
        );
    }

    // =========================================================
    // 드래그 방지
    // =========================================================

    @EventHandler
    public void onStatGuiDrag(
            InventoryDragEvent event
    ) {

        if (event
                .getView()
                .getTitle()
                .equals(GUI_TITLE)) {

            event.setCancelled(true);
        }
    }

    private String getStatDisplayName(
            String statType
    ) {

        return switch (statType) {

            case "health" ->
                    "체력";

            case "attack" ->
                    "공격력";

            case "speed" ->
                    "이동속도";

            case "defense" ->
                    "방어력";

            case "regen" ->
                    "회복력";

            default ->
                    statType;
        };
    }

    // =========================================================
    // 실제 능력치 적용
    // =========================================================

    public void applyStats(
            Player player
    ) {

        if (player == null
                || !player.isOnline()) {

            return;
        }

        // =====================================================
        // 체력
        // =====================================================

        int healthStat =
                statManager.getStat(
                        player,
                        "health"
                );

        AttributeInstance maxHealth =
                player.getAttribute(
                        Attribute.MAX_HEALTH
                );

        if (maxHealth != null) {

            double totalHealth =
                    20.0
                            + (healthStat * 2.0);

            maxHealth.setBaseValue(
                    totalHealth
            );

            if (player.getHealth()
                    > totalHealth) {

                player.setHealth(
                        totalHealth
                );
            }
        }

        // =====================================================
        // 공격력 Attribute는 기본값 유지
        // 실제 보너스는 DamageEvent에서 적용
        // =====================================================

        AttributeInstance attack =
                player.getAttribute(
                        Attribute.ATTACK_DAMAGE
                );

        if (attack != null) {

            attack.setBaseValue(
                    1.0
            );
        }

        // =====================================================
        // 이동속도
        // =====================================================

        int speedStat =
                statManager.getStat(
                        player,
                        "speed"
                );

        AttributeInstance speed =
                player.getAttribute(
                        Attribute.MOVEMENT_SPEED
                );

        if (speed != null) {

            double totalSpeed =
                    0.1
                            + (speedStat * 0.002);

            totalSpeed =
                    Math.min(
                            totalSpeed,
                            1.0
                    );

            speed.setBaseValue(
                    totalSpeed
            );
        }

        // =====================================================
        // 방어력
        // =====================================================

        int defenseStat =
                statManager.getStat(
                        player,
                        "defense"
                );

        AttributeInstance armor =
                player.getAttribute(
                        Attribute.ARMOR
                );

        if (armor != null) {

            armor.setBaseValue(
                    defenseStat * 0.5
            );
        }
    }

    // =========================================================
    // 접속
    // =========================================================

    @EventHandler
    public void onPlayerJoin(
            PlayerJoinEvent event
    ) {

        Bukkit.getScheduler()
                .runTaskLater(
                        plugin,

                        () -> applyStats(
                                event.getPlayer()
                        ),

                        1L
                );
    }

    // =========================================================
    // 부활
    // =========================================================

    @EventHandler
    public void onPlayerRespawn(
            PlayerRespawnEvent event
    ) {

        Bukkit.getScheduler()
                .runTaskLater(
                        plugin,

                        () -> applyStats(
                                event.getPlayer()
                        ),

                        1L
                );
    }

    // =========================================================
    // 공격 스탯
    //
    // 1P = 추가 피해 +0.5
    // =========================================================

    @EventHandler(
            priority = EventPriority.NORMAL,
            ignoreCancelled = true
    )
    public void onDamage(
            EntityDamageByEntityEvent event
    ) {

        Player attacker =
                null;

        // 직접 공격
        if (event.getDamager()
                instanceof Player player) {

            attacker =
                    player;
        }

        // 투사체
        else if (event.getDamager()
                instanceof Projectile projectile) {

            ProjectileSource shooter =
                    projectile.getShooter();

            if (shooter
                    instanceof Player player) {

                attacker =
                        player;
            }
        }

        if (attacker == null) {
            return;
        }

        int attackStat =
                statManager.getStat(
                        attacker,
                        "attack"
                );

        if (attackStat <= 0) {
            return;
        }

        double bonusDamage =
                attackStat * 0.5;

        event.setDamage(
                event.getDamage()
                        + bonusDamage
        );
    }

    // =========================================================
    // 회복 스탯
    // 현재 GUI에는 미표시
    // =========================================================

    @EventHandler(
            priority = EventPriority.NORMAL,
            ignoreCancelled = true
    )
    public void onRegainHealth(
            EntityRegainHealthEvent event
    ) {

        if (!(event.getEntity()
                instanceof Player player)) {

            return;
        }

        int regen =
                statManager.getStat(
                        player,
                        "regen"
                );

        if (regen <= 0) {
            return;
        }

        event.setAmount(
                event.getAmount()
                        + (regen * 0.2)
        );
    }
}