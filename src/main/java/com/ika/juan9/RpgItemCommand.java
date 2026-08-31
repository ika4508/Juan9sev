package com.ika.juan9;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RpgItemCommand implements CommandExecutor {

    private final SpecialItemListener specialItemListener;

    public RpgItemCommand(SpecialItemListener specialItemListener) {
        this.specialItemListener = specialItemListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && !sender.isOp()) {
            sender.sendMessage("§c[오류] 관리자(OP) 권한이 없습니다.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§e사용법: /rpgitem <도약/개쩌는도약/화살통/개쩌는화살통/스텟포인트/대지강타/귀환/단거리귀환/장거리귀환> [플레이어]");
            return true;
        }

        Player target = (sender instanceof Player) ? (Player) sender : null;
        if (args.length >= 2) {
            target = Bukkit.getPlayer(args[1]);
        }

        if (target == null) {
            sender.sendMessage("§c[오류] 대상 플레이어를 찾을 수 없습니다.");
            return true;
        }

        String itemType = args[0];
        ItemStack resultItem = null;

        if (itemType.equalsIgnoreCase("도약") || itemType.equalsIgnoreCase("leap")) {
            resultItem = specialItemListener.createLeapItem();
        } else if (itemType.equalsIgnoreCase("개쩌는도약") || itemType.equalsIgnoreCase("awesomeleap")) {
            resultItem = specialItemListener.createAwesomeLeapItem();
        } else if (itemType.equalsIgnoreCase("화살통") || itemType.equalsIgnoreCase("quiver")) {
            resultItem = specialItemListener.createQuiverItem();
        } else if (itemType.equalsIgnoreCase("개쩌는화살통") || itemType.equalsIgnoreCase("awesomequiver") || itemType.equalsIgnoreCase("awesome")) {
            resultItem = specialItemListener.createAwesomeQuiverItem();
        } else if (itemType.equalsIgnoreCase("스텟포인트") || itemType.equalsIgnoreCase("스탯포인트") || itemType.equalsIgnoreCase("stat") || itemType.equalsIgnoreCase("point")) {
            resultItem = specialItemListener.createStatPointItem();
        } else if (itemType.equalsIgnoreCase("대지강타") || itemType.equalsIgnoreCase("slam") || itemType.equalsIgnoreCase("earthslam")) {
            resultItem = specialItemListener.createEarthSlamItem();
        } else if (itemType.equalsIgnoreCase("귀환") || itemType.equalsIgnoreCase("return") || itemType.equalsIgnoreCase("tp")) {
            resultItem = specialItemListener.createReturnScrollItem();
        } else if (itemType.equalsIgnoreCase("단거리귀환") || itemType.equalsIgnoreCase("단거리")) {
            resultItem = specialItemListener.createShortReturnScrollItem();
        } else if (itemType.equalsIgnoreCase("장거리귀환") || itemType.equalsIgnoreCase("장거리")) {
            resultItem = specialItemListener.createLongReturnScrollItem();
        }

        if (resultItem == null) {
            sender.sendMessage("§c[오류] 올바른 아이템 이름을 입력해주세요. (도약 / 개쩌는도약 / 화살통 / 개쩌는화살통 / 스텟포인트 / 대지강타 / 귀환 / 단거리귀환 / 장거리귀환)");
            return true;
        }

        target.getInventory().addItem(resultItem);
        sender.sendMessage("§a[알림] §f" + target.getName() + "님에게 " + resultItem.getItemMeta().getDisplayName() + "§a을(를) 지급했습니다.");
        return true;
    }
}