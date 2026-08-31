package com.ika.juan9;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class StatManager {

    private final Juan9 plugin;

    private File file;
    private FileConfiguration config;

    public StatManager(Juan9 plugin) {
        this.plugin = plugin;
        initFile();
    }

    private void initFile() {

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        file = new File(
                plugin.getDataFolder(),
                "stats.yml"
        );

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // 레벨
    // =========================================================

    public int getLevel(Player player) {

        return config.getInt(
                player.getUniqueId() + ".level",
                1
        );
    }

    public void setLevel(
            Player player,
            int level
    ) {

        config.set(
                player.getUniqueId() + ".level",
                Math.max(1, level)
        );

        save();
    }

    // =========================================================
    // 현재 경험치
    // =========================================================

    public long getExp(Player player) {

        return config.getLong(
                player.getUniqueId() + ".exp",
                0L
        );
    }

    public void setExp(
            Player player,
            long exp
    ) {

        config.set(
                player.getUniqueId() + ".exp",
                Math.max(0L, exp)
        );

        save();
    }

    // =========================================================
    // 레벨업 필요 경험치
    //
    // Lv.1 -> Lv.2 = 100
    // 이후 매 레벨마다 이전 필요 EXP의 130%
    //
    // 100
    // 130
    // 169
    // 220
    // 286
    // 372
    // ...
    // =========================================================

    public long getRequiredExp(int level) {

        if (level <= 1) {
            return 100L;
        }

        long requiredExp = 100L;

        for (int i = 1; i < level; i++) {

            double next =
                    requiredExp * 1.30;

            if (next >= Long.MAX_VALUE) {
                return Long.MAX_VALUE;
            }

            requiredExp =
                    Math.round(next);
        }

        return requiredExp;
    }

    public long getRequiredExp(Player player) {

        return getRequiredExp(
                getLevel(player)
        );
    }

    // =========================================================
    // 경험치 지급
    // =========================================================

    public void addExp(
            Player player,
            long amount
    ) {

        if (amount <= 0) {
            return;
        }

        int level =
                getLevel(player);

        long currentExp =
                getExp(player);

        long exp;

        // overflow 방지
        if (Long.MAX_VALUE - currentExp < amount) {
            exp = Long.MAX_VALUE;
        } else {
            exp = currentExp + amount;
        }

        int levelUps = 0;

        // =====================================================
        // 한 번에 여러 레벨 상승 가능
        // =====================================================

        while (true) {

            long requiredExp =
                    getRequiredExp(level);

            if (exp < requiredExp) {
                break;
            }

            if (requiredExp == Long.MAX_VALUE) {
                break;
            }

            exp -= requiredExp;

            level++;
            levelUps++;
        }

        String path =
                player.getUniqueId().toString();

        config.set(
                path + ".level",
                level
        );

        config.set(
                path + ".exp",
                exp
        );

        // 레벨업 수만큼 스탯 포인트 지급
        if (levelUps > 0) {

            int currentPoints =
                    config.getInt(
                            path + ".statPoints",
                            0
                    );

            config.set(
                    path + ".statPoints",
                    currentPoints + levelUps
            );
        }

        save();

        // =====================================================
        // 레벨업 메시지
        // =====================================================

        if (levelUps > 0) {

            player.sendMessage(
                    "§6§l━━━━━━━━━━━━━━━━━━━━"
            );

            player.sendMessage(
                    "§e§l        LEVEL UP!"
            );

            player.sendMessage("");

            player.sendMessage(
                    "§f현재 레벨: §e§lLv."
                            + level
            );

            player.sendMessage(
                    "§f스탯 포인트: §a+"
                            + levelUps
                            + "P"
            );

            player.sendMessage("");

            player.sendMessage(
                    "§6§l━━━━━━━━━━━━━━━━━━━━"
            );
        }
    }

    // =========================================================
    // 스탯 포인트
    // =========================================================

    public int getStatPoints(
            Player player
    ) {

        return config.getInt(
                player.getUniqueId()
                        + ".statPoints",
                0
        );
    }

    public void setStatPoints(
            Player player,
            int points
    ) {

        config.set(
                player.getUniqueId()
                        + ".statPoints",
                Math.max(0, points)
        );

        save();
    }

    public void addStatPoints(
            Player player,
            int amount
    ) {

        int current =
                getStatPoints(player);

        setStatPoints(
                player,
                current + amount
        );
    }

    // =========================================================
    // 개별 스탯
    // =========================================================

    public int getStat(
            Player player,
            String statType
    ) {

        return config.getInt(
                player.getUniqueId()
                        + ".stats."
                        + statType,
                0
        );
    }

    public void setStat(
            Player player,
            String statType,
            int value
    ) {

        config.set(
                player.getUniqueId()
                        + ".stats."
                        + statType,
                Math.max(0, value)
        );

        save();
    }

    public void addStat(
            Player player,
            String statType,
            int amount
    ) {

        int current =
                getStat(
                        player,
                        statType
                );

        setStat(
                player,
                statType,
                current + amount
        );
    }

    // =========================================================
    // 전체 초기화
    // =========================================================

    public void resetPlayerAll(
            Player player
    ) {

        String path =
                player.getUniqueId().toString();

        config.set(
                path + ".level",
                1
        );

        config.set(
                path + ".exp",
                0L
        );

        config.set(
                path + ".statPoints",
                0
        );

        config.set(
                path + ".stats.health",
                0
        );

        config.set(
                path + ".stats.attack",
                0
        );

        config.set(
                path + ".stats.speed",
                0
        );

        config.set(
                path + ".stats.defense",
                0
        );

        config.set(
                path + ".stats.regen",
                0
        );

        save();
    }
}