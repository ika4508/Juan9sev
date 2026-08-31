package com.ika.juan9;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class KillNpcCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.isOp()) {
            player.sendMessage("§c[오류] 관리자(OP) 권한이 없습니다.");
            return true;
        }

        // 반경 4블록 내에서 가장 가까운 NPC 주민 탐색 및 강제 삭제
        Villager target = null;
        double closestDist = Double.MAX_VALUE;

        for (Entity entity : player.getNearbyEntities(4, 4, 4)) {
            if (entity instanceof Villager) {
                double dist = entity.getLocation().distanceSquared(player.getLocation());
                if (dist < closestDist) {
                    closestDist = dist;
                    target = (Villager) entity;
                }
            }
        }

        if (target != null) {
            String name = target.getCustomName() != null ? target.getCustomName() : "NPC";
            target.remove(); // 데미지 이벤트와 무관하게 엔티티 즉각 소멸
            player.sendMessage("§a[알림] 가까이 있는 " + name + "§a을(를) 완전히 제거했습니다.");
        } else {
            player.sendMessage("§c[오류] 반경 4블록 이내에 제거할 NPC(주민)가 없습니다. 더 가까이 다가가서 입력해주세요.");
        }

        return true;
    }
}