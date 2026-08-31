package com.ika.juan9;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.text.DecimalFormat;

public class CashCommand implements CommandExecutor {

    private final CurrencyItemFactory currencyItemFactory;
    private final DecimalFormat df = new DecimalFormat("#,###");

    public CashCommand(CurrencyItemFactory currencyItemFactory) {
        this.currencyItemFactory = currencyItemFactory;
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

        if (amount <= 0 || count <= 0 || count > 64) {
            player.sendMessage("§c[오류] 금액은 1 이상, 개수는 1~64로 입력해주세요.");
            return true;
        }

        ItemStack cash = currencyItemFactory.createCashItem(amount, count);
        player.getInventory().addItem(cash).values().forEach(
                leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover)
        );
        player.sendMessage("§a[알림] §e" + df.format(amount) + " GOLD§a권 화폐 " + count + "개가 지급되었습니다.");
        return true;
    }
}
