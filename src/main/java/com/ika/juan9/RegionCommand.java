package com.ika.juan9;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegionCommand implements CommandExecutor {

    private final Juan9 plugin;

    public RegionCommand(Juan9 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (!player.isOp()) {
            player.sendMessage("§c구역을 설정할 권한이 없습니다.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§e사용법: /setregion 1  또는  /setregion 2");
            return true;
        }

        int x = player.getLocation().getBlockX();
        int z = player.getLocation().getBlockZ();

        if (args[0].equals("1")) {
            plugin.getConfig().set("region.x1", x);
            plugin.getConfig().set("region.z1", z);
            plugin.saveConfig();
            player.sendMessage("§a[알림] 구역 1번 지점이 현재 위치 (X:" + x + ", Z:" + z + ")로 설정되었습니다.");
        } else if (args[0].equals("2")) {
            plugin.getConfig().set("region.x2", x);
            plugin.getConfig().set("region.z2", z);
            plugin.saveConfig();
            player.sendMessage("§a[알림] 구역 2번 지점이 현재 위치 (X:" + x + ", Z:" + z + ")로 설정되었습니다.");
        }

        return true;
    }
}