package com.ika.juan9;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CashCommand implements CommandExecutor {

    private final Juan9 plugin;
    private final DecimalFormat df = new DecimalFormat("#,###");

    public CashCommand(Juan9 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (!player.isOp()) {
            player.sendMessage("§c권한이 없습니다.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§e사용법: /cash <금액> [개수]");
            return true;
        }

        int amount;
        int count = 1;

        try {
            amount = Integer.parseInt(args[0]);
            if (args.length >= 2) {
                count = Integer.parseInt(args[1]);
            }
        } catch (NumberFormatException e) {
            player.sendMessage("§c[오류] 올바른 숫자를 입력해주세요.");
            return true;
        }

        if (amount <= 0 || count <= 0) {
            player.sendMessage("§c[오류] 금액과 개수는 1 이상이어야 합니다.");
            return true;
        }

        ItemStack cash = new ItemStack(Material.ARMS_UP_POTTERY_SHERD, count);
        ItemMeta meta = cash.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§6§l[공식 화폐] §e" + df.format(amount) + " GOLD");

            List<String> lore = new ArrayList<>();
            lore.add("§7서버 공식 인증 실물 화폐입니다.");
            lore.add("§8(모루로 이름을 변경해도 위조할 수 없습니다.)");
            meta.setLore(lore);

            NamespacedKey key = new NamespacedKey(plugin, "cash_amount");
            meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, amount);

            cash.setItemMeta(meta);
        }

        player.getInventory().addItem(cash);
        player.sendMessage("§a[알림] §e" + df.format(amount) + " GOLD§a권 화폐 " + count + "개가 지급되었습니다.");
        return true;
    }
}