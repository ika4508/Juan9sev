package com.ika.juan9;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class SpawnBlacksmithCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.isOp()) {
            player.sendMessage("§c권한이 없습니다.");
            return true;
        }

        Location loc = player.getLocation();

        // 최신 Paper 1.21 안전 소환 방식
        player.getWorld().spawn(loc, Villager.class, villager -> {
            villager.setCustomName("§6§l[대장장이] §f강화");
            villager.setCustomNameVisible(true);
            villager.setProfession(Villager.Profession.WEAPONSMITH);
            villager.setVillagerType(Villager.Type.PLAINS);
            villager.setAI(false);
            villager.setInvulnerable(true);
            villager.setSilent(true);
            villager.setCollidable(false);
        });

        player.sendMessage("§a[알림] 대장장이 NPC가 현재 위치에 소환되었습니다!");
        return true;
    }
}