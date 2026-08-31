package com.ika.juan9;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CheckCommand implements CommandExecutor {

    private final DecimalFormat df = new DecimalFormat("#,###");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (!player.isOp()) {
            player.sendMessage("§c권한이 없습니다.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§e사용법: /check <금액>");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage("§c[오류] 올바른 숫자를 입력해주세요.");
            return true;
        }

        if (amount <= 0) {
            player.sendMessage("§c[오류] 1 GOLD 이상만 발급할 수 있습니다.");
            return true;
        }

        ItemStack check = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = check.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e§l[은행 수표] §f" + df.format(amount) + " GOLD");
            List<String> lore = new ArrayList<>();
            lore.add("§7우클릭 시 계좌로 입금됩니다.");
            lore.add("§6금액: §f" + amount);
            meta.setLore(lore);
            check.setItemMeta(meta);
        }

        player.getInventory().addItem(check);
        player.sendMessage("§a[알림] §e" + df.format(amount) + " GOLD§a 상당의 수표가 발급되었습니다.");
        return true;
    }
}