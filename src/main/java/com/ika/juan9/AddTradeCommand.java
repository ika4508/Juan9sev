package com.ika.juan9;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AddTradeCommand implements CommandExecutor {

    private final Juan9 plugin;

    public AddTradeCommand(Juan9 plugin) {
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
            player.sendMessage("§e사용법: /addtrade <상점이름>");
            return true;
        }

        String shopName = args[0];

        ItemStack result = player.getInventory().getItemInMainHand();
        ItemStack cost = player.getInventory().getItemInOffHand();

        if (result.getType() == Material.AIR || cost.getType() == Material.AIR) {
            player.sendMessage("§c[오류] 주 손에는 '판매할 아이템', 왼손에는 '요구할 대가 아이템'을 들어주세요.");
            return true;
        }

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("shops." + shopName);
        int nextIndex = (section == null) ? 0 : section.getKeys(false).size();

        plugin.getConfig().set("shops." + shopName + "." + nextIndex + ".result", result);
        plugin.getConfig().set("shops." + shopName + "." + nextIndex + ".cost", cost);
        plugin.saveConfig();

        player.sendMessage("§a[성공] '" + shopName + "' 상점에 새 거래 품목이 등록되었습니다! (번호: " + nextIndex + ")");
        return true;
    }
}