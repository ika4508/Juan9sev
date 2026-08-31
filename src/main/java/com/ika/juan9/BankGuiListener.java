package com.ika.juan9;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BankGuiListener implements Listener {

    private final Juan9 plugin;
    private final NamespacedKey cashKey;
    private final DecimalFormat df = new DecimalFormat("#,###");
    private static final String GUI_TITLE = "§8[ 중앙 은행 ATM ]";

    public BankGuiListener(Juan9 plugin) {
        this.plugin = plugin;
        this.cashKey = new NamespacedKey(plugin, "cash_amount");
    }

    private int getBalance(Player player) {
        return plugin.getConfig().getInt("bank." + player.getUniqueId(), 0);
    }

    private void setBalance(Player player, int amount) {
        plugin.getConfig().set("bank." + player.getUniqueId(), amount);
        plugin.saveConfig();
    }

    // 공식 화폐 아이템 생성 (도자기 조각)
    private ItemStack createCashItem(int amount, int count) {
        ItemStack cash = new ItemStack(Material.ARMS_UP_POTTERY_SHERD, count);
        ItemMeta meta = cash.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6§l[공식 화폐] §e" + df.format(amount) + " GOLD");
            List<String> lore = new ArrayList<>();
            lore.add("§7서버 공식 인증 실물 화폐입니다.");
            lore.add("§8(모루로 이름을 변경해도 위조할 수 없습니다.)");
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(cashKey, PersistentDataType.INTEGER, amount);
            cash.setItemMeta(meta);
        }
        return cash;
    }

    // 은행 GUI 열기
    public void openBankGui(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, GUI_TITLE);

        // 1. 배경 장식 (회색 유리판)
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            filler.setItemMeta(fillerMeta);
        }
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, filler);
        }

        // 2. [슬롯 11] 현금 입금 버튼 (호퍼)
        ItemStack depositBtn = new ItemStack(Material.HOPPER);
        ItemMeta depositMeta = depositBtn.getItemMeta();
        if (depositMeta != null) {
            depositMeta.setDisplayName("§a§l[ 현금 입금 ]");
            List<String> lore = new ArrayList<>();
            lore.add("§7인벤토리에 소지한 공식 화폐를");
            lore.add("§7모두 계좌에 입금합니다.");
            lore.add(" ");
            lore.add("§e▶ 클릭하여 전액 입금");
            depositMeta.setLore(lore);
            depositBtn.setItemMeta(depositMeta);
        }
        inv.setItem(11, depositBtn);

        // 3. [슬롯 13] 계좌 잔액 현황 (금괴)
        int balance = getBalance(player);
        ItemStack infoBtn = new ItemStack(Material.GOLD_INGOT);
        ItemMeta infoMeta = infoBtn.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName("§6§l[ 내 계좌 정보 ]");
            List<String> lore = new ArrayList<>();
            lore.add("§7현재 보유 잔액:");
            lore.add("§e§l" + df.format(balance) + " GOLD");
            infoMeta.setLore(lore);
            infoBtn.setItemMeta(infoMeta);
        }
        inv.setItem(13, infoBtn);

        // 4. [슬롯 15, 16, 17] 출금 버튼 (1 GOLD / 10 GOLD / 100 GOLD)
        inv.setItem(15, createWithdrawButton(1));
        inv.setItem(16, createWithdrawButton(10));
        inv.setItem(17, createWithdrawButton(100));

        player.openInventory(inv);
    }

    private ItemStack createWithdrawButton(int amount) {
        ItemStack btn = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta meta = btn.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e§l[ " + df.format(amount) + " GOLD 출금 ]");
            List<String> lore = new ArrayList<>();
            lore.add("§7계좌에서 §e" + df.format(amount) + " GOLD§7를 인출하여");
            lore.add("§7공식 화폐 실물로 지급받습니다.");
            lore.add(" ");
            lore.add("§e▶ 클릭하여 출금");
            meta.setLore(lore);
            btn.setItemMeta(meta);
        }
        return btn;
    }

    // 은행원 NPC 우클릭 감지
    @EventHandler
    public void onVillagerInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Entity entity = event.getRightClicked();
        if (entity instanceof Villager && entity.getCustomName() != null && entity.getCustomName().startsWith("§b§l[은행원]")) {
            event.setCancelled(true);
            openBankGui(event.getPlayer());
        }
    }

    // GUI 클릭 이벤트 처리
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;

        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        // 1. 현금 전액 입금 (슬롯 11)
        if (slot == 11) {
            int totalDeposited = 0;
            ItemStack[] contents = player.getInventory().getContents();

            for (int i = 0; i < contents.length; i++) {
                ItemStack item = contents[i];
                if (item != null && item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null && meta.getPersistentDataContainer().has(cashKey, PersistentDataType.INTEGER)) {
                        Integer val = meta.getPersistentDataContainer().get(cashKey, PersistentDataType.INTEGER);
                        if (val != null && val > 0) {
                            totalDeposited += val * item.getAmount();
                            player.getInventory().setItem(i, null);
                        }
                    }
                }
            }

            if (totalDeposited > 0) {
                int newBalance = getBalance(player) + totalDeposited;
                setBalance(player, newBalance);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);
                player.sendMessage("§a[은행] §e" + df.format(totalDeposited) + " GOLD§a를 입금했습니다! (현재 잔액: §f" + df.format(newBalance) + " GOLD§a)");
                openBankGui(player);
            } else {
                player.sendMessage("§c[알림] 인벤토리에 입금할 공식 화폐가 없습니다.");
            }
            return;
        }

        // 2. 출금 버튼 처리 (슬롯 15: 1 GOLD, 슬롯 16: 10 GOLD, 슬롯 17: 100 GOLD)
        int withdrawAmount = 0;
        if (slot == 15) withdrawAmount = 1;
        else if (slot == 16) withdrawAmount = 10;
        else if (slot == 17) withdrawAmount = 100;

        if (withdrawAmount > 0) {
            int currentBalance = getBalance(player);
            if (currentBalance < withdrawAmount) {
                player.sendMessage("§c[오류] 계좌 잔액이 부족합니다. (보유 잔액: " + df.format(currentBalance) + " GOLD)");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return;
            }

            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage("§c[오류] 인벤토리에 빈 공간이 없습니다.");
                return;
            }

            setBalance(player, currentBalance - withdrawAmount);
            player.getInventory().addItem(createCashItem(withdrawAmount, 1));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1.5f);
            player.sendMessage("§a[은행] §e" + df.format(withdrawAmount) + " GOLD§a를 출금했습니다. (남은 잔액: §f" + df.format(currentBalance - withdrawAmount) + " GOLD§a)");
            openBankGui(player);
        }
    }
}