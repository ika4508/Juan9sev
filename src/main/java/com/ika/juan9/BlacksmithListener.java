package com.ika.juan9;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlacksmithListener implements Listener {

    private final Juan9 plugin;
    private final NamespacedKey upgradeKey;
    private final DecimalFormat df = new DecimalFormat("#,###");
    private final Random random = new Random();
    public static final String GUI_TITLE = "§8[ 대장간 - 무기 강화 ]";

    public BlacksmithListener(Juan9 plugin) {
        this.plugin = plugin;
        this.upgradeKey = new NamespacedKey(plugin, "upgrade_level");
    }

    private int getBalance(Player player) {
        return plugin.getConfig().getInt("bank." + player.getUniqueId(), 0);
    }

    private void setBalance(Player player, int amount) {
        plugin.getConfig().set("bank." + player.getUniqueId(), amount);
        plugin.saveConfig();
    }

    // 1. 무기 재질별 기본 시작 비용 (10배 기준)
    public int getBaseCostByMaterial(Material mat) {
        String name = mat.name();

        if (name.startsWith("WOODEN_") || name.startsWith("GOLDEN_")) {
            return 50;
        } else if (name.startsWith("STONE_")) {
            return 80;
        } else if (name.startsWith("IRON_") || mat == Material.BOW || mat == Material.CROSSBOW || mat == Material.TRIDENT) {
            return 150;
        } else if (name.startsWith("DIAMOND_") || mat == Material.MACE) {
            return 300;
        } else if (name.startsWith("NETHERITE_")) {
            return 600;
        }

        return 100;
    }

    // 2. 최종 강화 비용
    public int getUpgradeCost(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return 100;
        int baseCost = getBaseCostByMaterial(item.getType());
        int currentLevel = getUpgradeLevel(item);
        return (int) (baseCost * Math.pow(2, currentLevel));
    }

    // 3. 단계별 강화 배율 (배율 테이블)
    public double getDamageMultiplier(int level) {
        switch (level) {
            case 0: return 1.00;
            case 1: return 1.10;
            case 2: return 1.25;
            case 3: return 1.40;
            case 4: return 1.80;
            case 5: return 2.30;
            case 6: return 3.00;
            case 7: return 5.00;
            case 8: return 7.50;
            default: return 7.50 + ((level - 8) * 1.50);
        }
    }

    // [성공 확률, 파괴 확률] 반환 (나머지는 실패 확률)
    public int[] getChances(int currentLevel) {
        // {성공%, 파괴%}
        switch (currentLevel) {
            case 0: return new int[]{90, 0};
            case 1: return new int[]{70, 0};
            case 2: return new int[]{50, 0};
            case 3: return new int[]{30, 5};
            case 4: return new int[]{22, 10};
            case 5: return new int[]{15, 30};
            case 6: return new int[]{10, 40};
            case 7: return new int[]{6, 94};
            default: return new int[]{1, 99};
        }
    }

    public boolean isUpgradableWeapon(Material mat) {
        String name = mat.name();
        return name.endsWith("_SWORD") || name.endsWith("_AXE") || mat == Material.BOW ||
                mat == Material.CROSSBOW || mat == Material.TRIDENT || mat == Material.MACE;
    }

    public void applyUpgrade(ItemStack weapon, int newLevel) {
        ItemMeta meta = weapon.getItemMeta();
        if (meta == null) return;

        String currentName = meta.hasDisplayName() ? meta.getDisplayName() : null;
        String baseName = currentName != null ? currentName.replaceAll("§[0-9a-fk-or]\\+[0-9]+ ", "") : weapon.getType().name();

        if (newLevel <= 0) {
            meta.setDisplayName(baseName);
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            if (lore != null) {
                lore.removeIf(line -> line.startsWith("§6[강화]"));
                meta.setLore(lore);
            }
            meta.getPersistentDataContainer().remove(upgradeKey);
        } else {
            meta.setDisplayName("§6§l+" + newLevel + " §f" + baseName);

            int totalPercent = (int) Math.round(getDamageMultiplier(newLevel) * 100);
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            if (lore == null) lore = new ArrayList<>();

            lore.removeIf(line -> line.startsWith("§6[강화]"));
            lore.add(0, "§6[강화] §e+" + newLevel + "강화 §7(공격력 배율: " + totalPercent + "%)");
            meta.setLore(lore);

            meta.getPersistentDataContainer().set(upgradeKey, PersistentDataType.INTEGER, newLevel);
        }

        weapon.setItemMeta(meta);
    }

    public void openBlacksmithGui(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, GUI_TITLE);

        // 회색/검은색 유리판 -> 연두색(녹색) 유리판으로 변경
        ItemStack filler = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            filler.setItemMeta(fillerMeta);
        }
        for (int i = 0; i < 27; i++) {
            if (i != 13) {
                inv.setItem(i, filler);
            }
        }

        int balance = getBalance(player);
        ItemStack info = new ItemStack(Material.GOLD_INGOT);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName("§6§l[ 내 은행 잔액 ]");
            List<String> lore = new ArrayList<>();
            lore.add("§7현재 계좌 잔액: §e" + df.format(balance) + " GOLD");
            lore.add("§8(강화 비용은 계좌에서 즉시 출금됩니다.)");
            infoMeta.setLore(lore);
            info.setItemMeta(infoMeta);
        }
        inv.setItem(11, info);

        updateUpgradeButton(inv, null, player);

        player.openInventory(inv);
    }

    private void updateUpgradeButton(Inventory inv, ItemStack weapon, Player player) {
        ItemStack btn = new ItemStack(Material.ANVIL);
        ItemMeta meta = btn.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6§l[ 장비 강화하기 ]");
            List<String> lore = new ArrayList<>();

            if (weapon == null || weapon.getType() == Material.AIR) {
                lore.add("§7가운데 슬롯에 강화할 무기를 올려주세요.");
            } else if (!isUpgradableWeapon(weapon.getType())) {
                lore.add("§c강화할 수 없는 아이템입니다.");
            } else {
                int currentLevel = getUpgradeLevel(weapon);
                int cost = getUpgradeCost(weapon);
                int[] chances = getChances(currentLevel);
                int successRate = chances[0];
                int breakRate = chances[1];
                int failRate = 100 - successRate - breakRate;

                int nextLevel = currentLevel + 1;
                int currentMult = (int) Math.round(getDamageMultiplier(currentLevel) * 100);
                int nextMult = (int) Math.round(getDamageMultiplier(nextLevel) * 100);

                lore.add("§f현재 상태: §e+" + currentLevel + "강화 §7(공격력 배율: " + currentMult + "%)");
                lore.add("§a성공 시: §6+" + nextLevel + "강화 §7(공격력 배율: " + nextMult + "%)");
                lore.add(" ");
                lore.add("§a성공 확률: §e§l" + successRate + "%");
                lore.add("§7실패 확률: §f§l" + failRate + "% §8(수치 유지)");
                if (breakRate > 0) {
                    lore.add("§c파괴 확률: §4§l" + breakRate + "% §8(아이템 소멸)");
                } else {
                    lore.add("§b파괴 확률: §90% §8(파괴 안전)");
                }
                lore.add(" ");
                lore.add("§f필요 비용: §e" + df.format(cost) + " GOLD §8(기본: " + getBaseCostByMaterial(weapon.getType()) + "G)");
                lore.add(" ");
                if (getBalance(player) >= cost) {
                    lore.add("§a▶ 클릭하여 강화 진행");
                } else {
                    lore.add("§c계좌 잔액이 부족합니다.");
                }
            }
            meta.setLore(lore);
            btn.setItemMeta(meta);
        }
        inv.setItem(15, btn);
    }

    public int getUpgradeLevel(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 0;
        return meta.getPersistentDataContainer().getOrDefault(upgradeKey, PersistentDataType.INTEGER, 0);
    }

    @EventHandler
    public void onVillagerInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Entity entity = event.getRightClicked();
        if (entity instanceof Villager && entity.getCustomName() != null && entity.getCustomName().startsWith("§6§l[대장장이]")) {
            event.setCancelled(true);
            openBlacksmithGui(event.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;

        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();
        int rawSlot = event.getRawSlot();

        if (rawSlot != 13 && rawSlot < 27) {
            event.setCancelled(true);
        }

        if (rawSlot == 15) {
            ItemStack weapon = inv.getItem(13);
            if (weapon == null || weapon.getType() == Material.AIR || !isUpgradableWeapon(weapon.getType())) {
                player.sendMessage("§c[대장간] 강화할 수 있는 무기를 가운데 슬롯에 올려주세요.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return;
            }

            int currentLevel = getUpgradeLevel(weapon);
            int cost = getUpgradeCost(weapon);
            int balance = getBalance(player);

            if (balance < cost) {
                player.sendMessage("§c[대장간] 잔액이 부족합니다. (필요: " + df.format(cost) + " GOLD / 보유: " + df.format(balance) + " GOLD)");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return;
            }

            setBalance(player, balance - cost);

            int[] chances = getChances(currentLevel);
            int successRate = chances[0];
            int breakRate = chances[1];

            int roll = random.nextInt(100);

            if (roll < successRate) {
                // 1. 성공
                int nextLevel = currentLevel + 1;
                applyUpgrade(weapon, nextLevel);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1.2f);
                player.sendMessage("§a§l[대장간] §e강화에 성공했습니다! §6(+" + nextLevel + "강화 완료)");
                inv.setItem(13, weapon);
            } else if (roll < successRate + breakRate) {
                // 2. 파괴
                inv.setItem(13, null);
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 0.7f);
                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 1.2f);
                player.spawnParticle(Particle.EXPLOSION, player.getLocation().add(0, 1, 0), 1);
                player.sendMessage("§4§l[대장간] §c강화 충격을 견디지 못하고 무기가 산산조각 났습니다!");
            } else {
                // 3. 실패 (유지)
                player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, 0.8f);
                player.sendMessage("§c§l[대장간] §c강화에 실패했습니다... (수치 유지)");
                inv.setItem(13, weapon);
            }

            openBlacksmithGui(player);
            return;
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.getOpenInventory().getTitle().equals(GUI_TITLE)) {
                updateUpgradeButton(inv, inv.getItem(13), player);
            }
        }, 1L);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;

        Player player = (Player) event.getPlayer();
        ItemStack weapon = event.getInventory().getItem(13);
        if (weapon != null && weapon.getType() != Material.AIR) {
            event.getInventory().setItem(13, null);
            for (ItemStack drop : player.getInventory().addItem(weapon).values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), drop);
            }
        }
    }

    @EventHandler
    public void onWeaponAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            ItemStack mainHand = player.getInventory().getItemInMainHand();

            int level = getUpgradeLevel(mainHand);
            if (level > 0) {
                double multiplier = getDamageMultiplier(level);
                event.setDamage(event.getDamage() * multiplier);
            }
        }
    }
}