package com.ika.juan9;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;

public class MonsterDamageCommand implements CommandExecutor {

    private static final double MAX_MULTIPLIER = 100.0;

    private final Juan9 plugin;
    private final DecimalFormat format = new DecimalFormat("0.##");

    public MonsterDamageCommand(Juan9 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            double multiplier = plugin.getConfig().getDouble("monster.damage-multiplier", 1.5);
            sender.sendMessage("§e[몬스터] 현재 피해 배율: §f" + format.format(multiplier) + "배");
            return true;
        }

        if (!sender.isOp()) {
            sender.sendMessage("§c[오류] 관리자(OP) 권한이 없습니다.");
            return true;
        }

        double multiplier;
        try {
            multiplier = Double.parseDouble(args[0]);
        } catch (NumberFormatException exception) {
            sender.sendMessage("§c[오류] 올바른 배율을 입력해주세요. 예: /mobdamage 1.5");
            return true;
        }

        if (!Double.isFinite(multiplier) || multiplier < 0.0 || multiplier > MAX_MULTIPLIER) {
            sender.sendMessage("§c[오류] 피해 배율은 0 이상 100 이하로 입력해주세요.");
            return true;
        }

        plugin.getConfig().set("monster.damage-multiplier", multiplier);
        plugin.saveConfig();

        sender.sendMessage("§a[몬스터] 피해 배율을 §e" + format.format(multiplier) + "배§a로 변경했습니다.");
        return true;
    }
}
