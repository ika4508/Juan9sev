package com.ika.juan9;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

public class MoneyCommand implements CommandExecutor {

    private final Juan9 plugin;
    private final DecimalFormat df = new DecimalFormat("#,###");

    public MoneyCommand(Juan9 plugin) {
        this.plugin = plugin;
    }

    private int getBalance(Player player) {
        return plugin.getConfig().getInt("bank." + player.getUniqueId(), 0);
    }

    private void setBalance(Player player, int amount) {
        plugin.getConfig().set("bank." + player.getUniqueId(), amount);
        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean isOp = !(sender instanceof Player) || sender.isOp();

        // 1. /money 목록
        if (args.length >= 1 && (args[0].equalsIgnoreCase("목록") || args[0].equalsIgnoreCase("list"))) {
            if (!isOp) {
                sender.sendMessage("§c[오류] 관리자(OP) 권한이 없습니다.");
                return true;
            }

            ConfigurationSection bankSection = plugin.getConfig().getConfigurationSection("bank");
            if (bankSection == null || bankSection.getKeys(false).isEmpty()) {
                sender.sendMessage("§e[은행] 등록된 계좌 데이터가 없습니다.");
                return true;
            }

            sender.sendMessage("§6§l===== [ 전체 플레이어 계좌 현황 ] =====");
            int totalServerMoney = 0;
            int count = 0;

            for (String uuidStr : bankSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                    String name = offlinePlayer.getName();
                    if (name == null) name = "알 수 없는 유저";

                    int balance = bankSection.getInt(uuidStr, 0);
                    totalServerMoney += balance;
                    count++;

                    sender.sendMessage("§7- §f" + name + " : §e" + df.format(balance) + " GOLD");
                } catch (IllegalArgumentException ignored) {
                }
            }

            sender.sendMessage("§6§l=================================");
            sender.sendMessage("§a총 계좌 수: §f" + count + "개 | §a서버 총액: §e" + df.format(totalServerMoney) + " GOLD");
            return true;
        }

        // 2. /money 지급 <닉네임> <금액>
        if (args.length >= 1 && (args[0].equalsIgnoreCase("지급") || args[0].equalsIgnoreCase("give"))) {
            if (!isOp) {
                sender.sendMessage("§c[오류] 하지마라.");
                return true;
            }

            if (args.length < 3) {
                sender.sendMessage("§e사용법: /money 지급 <플레이어> <금액>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null || !target.isOnline()) {
                sender.sendMessage("§c[오류] 대상 플레이어가 온라인이 아닙니다.");
                return true;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§c[오류] 올바른 숫자를 입력해주세요.");
                return true;
            }

            if (amount <= 0) {
                playerSendMessage(sender, "§c[오류] 1 GOLD 이상만 지급할 수 있습니다.");
                return true;
            }

            int targetNewBalance = getBalance(target) + amount;
            setBalance(target, targetNewBalance);

            sender.sendMessage("§a[관리자] §f" + target.getName() + "님에게 §e" + df.format(amount) + " GOLD§f를 지급했습니다. (현재 대상 잔액: §a" + df.format(targetNewBalance) + " GOLD§f)");
            target.sendMessage("§a[은행] §f관리자로부터 §e" + df.format(amount) + " GOLD§f가 지급되었습니다.");
            return true;
        }

        // 3. /money (본인 잔액 조회)
        if (!(sender instanceof Player)) {
            sender.sendMessage("콘솔 사용법: /money 목록 또는 /money 지급 <플레이어> <금액>");
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            int balance = getBalance(player);
            player.sendMessage("§6[은행] §f현재 보유 잔액: §a" + df.format(balance) + " GOLD");
            return true;
        }

        // 도움말
        if (player.isOp()) {
            player.sendMessage("§e사용법: /money, /money 목록, /money 지급 <플레이어> <금액>");
        } else {
            player.sendMessage("§e사용법: /money");
        }

        return true;
    }

    private void playerSendMessage(CommandSender sender, String msg) {
        sender.sendMessage(msg);
    }
}