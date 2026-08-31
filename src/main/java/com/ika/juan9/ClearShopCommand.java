package com.ika.juan9;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearShopCommand implements CommandExecutor {

    private final Juan9 plugin;

    public ClearShopCommand(Juan9 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && !((Player) sender).isOp()) {
            sender.sendMessage("§c권한이 없습니다.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§e사용법: /clearshop <상점이름>");
            return true;
        }

        String shopName = args[0];
        plugin.getConfig().set("shops." + shopName, null);
        plugin.saveConfig();

        sender.sendMessage("§a[알림] '" + shopName + "' 상점의 모든 거래 품목이 삭제되었습니다.");
        return true;
    }
}