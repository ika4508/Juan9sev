package com.ika.juan9;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class DelTradeCommand implements CommandExecutor {

    private final Juan9 plugin;

    public DelTradeCommand(Juan9 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && !sender.isOp()) {
            sender.sendMessage("§c[오류] 관리자(OP) 권한이 없습니다.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§e사용법: /deltrade <상점이름> <삭제할번호>");
            sender.sendMessage("§7(번호는 0번부터 시작하며, 상점 GUI의 첫 번째 슬롯이 0번입니다.)");
            return true;
        }

        String shopName = args[0];
        int index;

        try {
            index = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§c[오류] 올바른 숫자 번호를 입력해주세요.");
            return true;
        }

        String path = "shops." + shopName + ".trades";
        if (!plugin.getConfig().contains(path)) {
            sender.sendMessage("§c[오류] '" + shopName + "' 상점을 찾을 수 없거나 등록된 상품이 없습니다.");
            return true;
        }

        List<Map<?, ?>> trades = plugin.getConfig().getMapList(path);

        if (trades.isEmpty()) {
            sender.sendMessage("§c[오류] '" + shopName + "' 상점에 삭제할 거래가 없습니다.");
            return true;
        }

        if (index < 0 || index >= trades.size()) {
            sender.sendMessage("§c[오류] 잘못된 번호입니다. (현재 등록된 상품 수: " + trades.size() + "개 / 입력 가능 번호: 0 ~ " + (trades.size() - 1) + ")");
            return true;
        }

        // 해당 인덱스의 거래 항목 삭제
        trades.remove(index);

        // Config 업데이트 및 저장
        plugin.getConfig().set(path, trades);
        plugin.saveConfig();

        sender.sendMessage("§a[알림] '" + shopName + "' 상점의 §e" + index + "번§a 거래 항목을 성공적으로 삭제했습니다!");
        sender.sendMessage("§7(남은 거래 항목: " + trades.size() + "개)");
        return true;
    }
}