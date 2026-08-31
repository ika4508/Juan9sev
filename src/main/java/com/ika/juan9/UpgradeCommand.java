package com.ika.juan9;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UpgradeCommand implements CommandExecutor {

    private final BlacksmithListener blacksmithListener;

    public UpgradeCommand(BlacksmithListener blacksmithListener) {
        this.blacksmithListener = blacksmithListener;
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

        if (args.length == 0) {
            player.sendMessage("§e사용법: /upgrade <강화수치>");
            player.sendMessage("§7(0 입력 시 강화를 초기화/제거합니다.)");
            return true;
        }

        int targetLevel;
        try {
            targetLevel = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage("§c[오류] 올바른 숫자를 입력해주세요.");
            return true;
        }

        if (targetLevel < 0) {
            player.sendMessage("§c[오류] 0 이상의 숫자를 입력해주세요.");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage("§c[오류] 주 손에 강화할 무기를 들어주세요.");
            return true;
        }

        if (!blacksmithListener.isUpgradableWeapon(item.getType())) {
            player.sendMessage("§c[오류] 강화 가능한 무기(검, 도끼, 활, 삼지창 등)가 아닙니다.");
            return true;
        }

        blacksmithListener.applyUpgrade(item, targetLevel);

        if (targetLevel > 0) {
            player.sendMessage("§a[관리자] 손에 든 무기를 §e§l+" + targetLevel + "강화§a로 강제 설정했습니다.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1.2f);
        } else {
            player.sendMessage("§a[관리자] 손에 든 무기의 강화 효과를 제거했습니다.");
            player.playSound(player.getLocation(), Sound.BLOCK_GRINDSTONE_USE, 1f, 1f);
        }

        return true;
    }
}