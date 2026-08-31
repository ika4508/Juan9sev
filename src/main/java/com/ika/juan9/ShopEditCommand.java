package com.ika.juan9;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopEditCommand implements CommandExecutor {

    private final Juan9 plugin;
    private final ShopManager shopManager;

    public ShopEditCommand(Juan9 plugin, ShopManager shopManager) {
        this.plugin = plugin;
        this.shopManager = shopManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("juan9.admin")) {
            player.sendMessage("§c권한이 없습니다.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§c사용법: /shopedit <상점이름>");
            return true;
        }

        if (shopManager == null) {
            player.sendMessage("§c상점 매니저를 불러올 수 없습니다.");
            return true;
        }

        // 1. 입력 인자 합치기
        String inputShopName = String.join(" ", args);

        // 2. 색상 코드 및 [상점] 접두사 정화 (순수 shopId 추출)
        String shopId = ChatColor.stripColor(inputShopName)
                .replace("[상점]", "")
                .replace("[편집]", "")
                .trim();

        // 3. ShopManager의 openEditGui 메서드 호출
        shopManager.openEditGui(player, shopId);
        player.sendMessage("§a[!] '§f" + shopId + "§a' 상점 편집 메뉴를 열었습니다.");

        return true;
    }
}