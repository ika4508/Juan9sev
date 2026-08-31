package com.ika.juan9;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.List;

public class CheckUseListener implements Listener {

    private final Juan9 plugin;
    private final DecimalFormat df = new DecimalFormat("#,###");

    public CheckUseListener(Juan9 plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item.getType() == Material.PAPER && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasLore() && meta.getDisplayName().startsWith("§e§l[은행 수표]")) {
                    List<String> lore = meta.getLore();
                    if (lore != null && lore.size() >= 2) {
                        try {
                            String amountLine = lore.get(1).replace("§6금액: §f", "");
                            int amount = Integer.parseInt(amountLine);

                            item.setAmount(item.getAmount() - 1);

                            int currentBalance = plugin.getConfig().getInt("bank." + player.getUniqueId(), 0);
                            int newBalance = currentBalance + amount;
                            plugin.getConfig().set("bank." + player.getUniqueId(), newBalance);
                            plugin.saveConfig();

                            player.sendMessage("§a[은행] §e" + df.format(amount) + " GOLD§a가 계좌로 입금되었습니다! (현재 잔액: §f" + df.format(newBalance) + " GOLD§a)");
                            event.setCancelled(true);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        }
    }
}