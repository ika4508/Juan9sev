package com.ika.juan9;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Iterator;

public class RegionListener implements Listener {

    private final Juan9 plugin;

    public RegionListener(Juan9 plugin) {
        this.plugin = plugin;
    }

    // 좌표가 보호 구역 내부인지 판별하는 헬퍼 메서드
    private boolean isInsideRegion(Location loc) {
        int x1 = plugin.getConfig().getInt("region.x1", 0);
        int x2 = plugin.getConfig().getInt("region.x2", 0);
        int z1 = plugin.getConfig().getInt("region.z1", 0);
        int z2 = plugin.getConfig().getInt("region.z2", 0);

        if (x1 == 0 && x2 == 0 && z1 == 0 && z2 == 0) return false;

        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        return (x >= minX && x <= maxX && z >= minZ && z <= maxZ);
    }

    // 1. 블록 파괴 방지
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (isInsideRegion(event.getBlock().getLocation())) {
            if (player.isOp()) return;
            event.setCancelled(true);
            player.sendMessage("§c[알림] 이 구역에서는 블록을 파괴할 수 없습니다.");
        }
    }

    // 2. 블록 설치 방지
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (isInsideRegion(event.getBlock().getLocation())) {
            if (player.isOp()) return;
            event.setCancelled(true);
            player.sendMessage("§c[님아...] 되겠냐?");
        }
    }

    // 3. 엔티티 폭발 방지 (크리퍼, TNT, 리스폰 앵커, 엔드 크리스탈 등)
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Iterator<Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            Block block = it.next();
            if (isInsideRegion(block.getLocation())) {
                it.remove(); // 보호 구역 내 블록만 폭발 목록에서 제외
            }
        }
    }

    // 4. 블록 폭발 방지 (침대 폭발 등)
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        Iterator<Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            Block block = it.next();
            if (isInsideRegion(block.getLocation())) {
                it.remove();
            }
        }
    }
}