package com.ika.juan9;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class FirstJoinListener implements Listener {

    private final Juan9 plugin;

    public FirstJoinListener(Juan9 plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // 1. 처음 서버에 접속한 신규 플레이어인 경우
        if (!player.hasPlayedBefore()) {
            // sendTitle(메인 타이틀, 서브 타이틀, 페이드인Ticks, 유지Ticks, 페이드아웃Ticks)
            // 20 Ticks = 1초
            String mainTitle = ChatColor.GOLD + "" + ChatColor.BOLD + "환영합니다!";
            String subTitle = ChatColor.YELLOW + player.getName() + "님, 이로엔 서버에 오신 것을 환영합니다!";

            // 화면 중앙 타이틀 전송 (0.5초 동안 서서히 등장, 3초간 유지, 1초 동안 사라짐)
            player.sendTitle(mainTitle, subTitle, 10, 60, 20);
        }
        // 2. 기존에 접속했던 플레이어가 재접속한 경우
        else {
            String mainTitle = ChatColor.GREEN + "" + ChatColor.BOLD + "이로엔";
            String subTitle = ChatColor.GRAY + player.getName() + "Made by Ika / Endpage";

            player.sendTitle(mainTitle, subTitle, 10, 50, 20);
        }
    }
}