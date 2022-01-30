package com.kamesuta.pongcraft.listener;

import com.kamesuta.pongcraft.Ball;
import com.kamesuta.pongcraft.Config;
import com.kamesuta.pongcraft.PongCraft;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class BallListener implements Listener {
    public BallListener() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Ball ball : PongCraft.instance.balls) {
                    ball.entity.teleport(ball.entity.getLocation().clone().add(ball.veloctiy));

                    // #TODO 壁にぶつかったら跳ね返る
                    // ボールの位置を取得する
                    Location ballPos = ball.entity.getLocation();
                    Block block = ballPos.getBlock();

                    //block.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, block.getLocation(), 5, .2, .2, .2);
                    block.getRelative(BlockFace.DOWN).setType(Material.YELLOW_CONCRETE);

                    Block 北 = block.getRelative(BlockFace.NORTH);
                    Block 南 = block.getRelative(BlockFace.SOUTH);
                    Block 東 = block.getRelative(BlockFace.EAST);
                    Block 西 = block.getRelative(BlockFace.WEST);
                    if (北.getType().isSolid()) {
                        // 北のブロックにあたったら
                        ball.veloctiy.setZ(Math.abs(ball.veloctiy.getZ()));
                    } else if (南.getType().isSolid()) {
                        // 南のブロックにあたったら
                        ball.veloctiy.setZ(-Math.abs(ball.veloctiy.getZ()));
                    }
                    if (東.getType().isSolid()) {
                        // 東のブロックにあたったら
                        ball.veloctiy.setX(-Math.abs(ball.veloctiy.getX()));
                    } else if (西.getType().isSolid()) {
                        // 西のブロックにあたったら
                        ball.veloctiy.setX(Math.abs(ball.veloctiy.getX()));
                    }

                    // #TODO プレイヤー(パドル)にぶつかったら跳ね返る
                }
            }
        }.runTaskTimer(PongCraft.instance, 0, 1);
    }

    public void onJoin(PlayerJoinEvent event) {
        // PongCraftモードがONのときのみ
        if (!Config.isEnabled) {
            return;
        }

        // #TODO パドルをかぶってなかったらかぶせる
    }
}
