package com.ika.juan9;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatCommand
        implements CommandExecutor {

    private final Juan9 plugin;
    private final StatManager statManager;
    private final StatListener statListener;

    public StatCommand(
            Juan9 plugin,
            StatManager statManager,
            StatListener statListener
    ) {

        this.plugin = plugin;
        this.statManager = statManager;
        this.statListener = statListener;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        // =========================
        // /stat
        // =========================

        if (args.length == 0) {

            if (!(sender
                    instanceof Player player)) {

                sender.sendMessage(
                        "§c[스탯] 콘솔에서는 스탯 GUI를 열 수 없습니다."
                );

                return true;
            }

            statListener.openStatGui(player);

            return true;
        }

        // =========================
        // 아래 명령은 OP 전용
        // =========================

        if (sender instanceof Player
                && !sender.isOp()) {

            sender.sendMessage(
                    "§c[스탯] 관리자(OP) 전용 명령어입니다."
            );

            return true;
        }

        String sub =
                args[0];

        // =========================
        // /stat reset 닉네임
        // /stat 초기화 닉네임
        // =========================

        if (sub.equalsIgnoreCase("reset")
                || sub.equalsIgnoreCase("초기화")) {

            if (args.length < 2) {

                sender.sendMessage(
                        "§e사용법: /stat reset <플레이어>"
                );

                return true;
            }

            Player target =
                    Bukkit.getPlayer(
                            args[1]
                    );

            if (target == null) {

                sender.sendMessage(
                        "§c[스탯] 해당 플레이어를 찾을 수 없습니다."
                );

                return true;
            }

            // 커스텀 데이터 초기화
            statManager.resetPlayerAll(
                    target
            );

            // 바닐라 XP 표시도 초기화
            target.setLevel(0);
            target.setExp(0.0f);

            // 능력치 즉시 초기화
            statListener.applyStats(
                    target
            );

            sender.sendMessage(
                    "§a[스탯] "
                            + target.getName()
                            + "님의 스탯을 초기화했습니다."
            );

            target.sendMessage(
                    "§c[스탯] 관리자가 당신의 스탯을 초기화했습니다."
            );

            return true;
        }

        // =========================
        // /stat give 닉네임 수량
        // /stat 지급 닉네임 수량
        // =========================

        if (sub.equalsIgnoreCase("give")
                || sub.equalsIgnoreCase("지급")) {

            if (args.length < 3) {

                sender.sendMessage(
                        "§e사용법: /stat give <플레이어> <포인트>"
                );

                return true;
            }

            Player target =
                    Bukkit.getPlayer(
                            args[1]
                    );

            if (target == null) {

                sender.sendMessage(
                        "§c[스탯] 해당 플레이어를 찾을 수 없습니다."
                );

                return true;
            }

            int points;

            try {

                points =
                        Integer.parseInt(
                                args[2]
                        );

            } catch (NumberFormatException e) {

                sender.sendMessage(
                        "§c[스탯] 포인트는 숫자로 입력해주세요."
                );

                return true;
            }

            if (points <= 0) {

                sender.sendMessage(
                        "§c[스탯] 지급 포인트는 1 이상이어야 합니다."
                );

                return true;
            }

            statManager.addStatPoints(
                    target,
                    points
            );

            sender.sendMessage(
                    "§a[스탯] "
                            + target.getName()
                            + "님에게 §e"
                            + points
                            + "P§a를 지급했습니다."
            );

            target.sendMessage(
                    "§a[스탯] 관리자에게서 §e"
                            + points
                            + "P§a를 지급받았습니다."
            );

            return true;
        }

        // =========================
        // 도움말
        // =========================

        sender.sendMessage(
                "§e[ 스탯 명령어 ]"
        );

        sender.sendMessage(
                "§f/stat"
                        + " §7- 스탯 GUI 열기"
        );

        sender.sendMessage(
                "§f/stat reset <플레이어>"
                        + " §7- 레벨/EXP/스탯 초기화"
        );

        sender.sendMessage(
                "§f/stat give <플레이어> <포인트>"
                        + " §7- 스탯 포인트 지급"
        );

        return true;
    }
}