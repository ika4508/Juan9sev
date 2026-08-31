package com.ika.juan9;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvseeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("플레이어만 사용할 수 있는 명령어입니다.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.isOp()) {
            player.sendMessage("§c[오류] 관리자(OP) 권한이 없습니다.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§e사용법: /invsee <플레이어이름>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage("§c[오류] 대상 플레이어 '" + args[0] + "'을(를) 찾을 수 없거나 오프라인 상태입니다.");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage("§c[오류] 자신의 인벤토리는 E 키로 열어주세요.");
            return true;
        }

        // 대상 플레이어의 실시간 인벤토리 오픈
        player.openInventory(target.getInventory());
        player.sendMessage("§a[알림] §f" + target.getName() + "§a님의 인벤토리를 열었습니다.");
        return true;
    }
}