package com.ika.juan9;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCenterCommand implements CommandExecutor {

    private final Juan9 plugin;

    public SetCenterCommand(Juan9 plugin) {
        this.plugin = plugin;
    }

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

        Location loc = player.getLocation();
        plugin.getConfig().set("center.world", loc.getWorld().getName());
        plugin.getConfig().set("center.x", loc.getX());
        plugin.getConfig().set("center.y", loc.getY());
        plugin.getConfig().set("center.z", loc.getZ());
        plugin.getConfig().set("center.yaw", (double) loc.getYaw());
        plugin.getConfig().set("center.pitch", (double) loc.getPitch());
        plugin.saveConfig();

        player.sendMessage("§a[알림] 현재 위치가 §e중앙 귀환 지점§a으로 설정되었습니다!");
        return true;
    }
}