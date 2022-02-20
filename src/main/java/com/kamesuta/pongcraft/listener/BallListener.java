package com.kamesuta.pongcraft.listener;

import com.kamesuta.pongcraft.Ball;
import com.kamesuta.pongcraft.Config;
import com.kamesuta.pongcraft.PongCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class BallListener implements Listener {
    public static final NamespacedKey KEY_COOLDOWN = new NamespacedKey(PongCraft.instance, "cooldown");

    public BallListener() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Ball ball : PongCraft.instance.balls) {
                    ball.entity.teleport(ball.entity.getLocation().clone().add(ball.veloctiy));

                    // #TODO 角に当てたり、動きながら当てたら早くなったりするようにする
                    // #TODO 反発力あげる、スピード上げる、跳ね返る向きをランダムにしたりする

                    // #TODO 壁にぶつかったら跳ね返る
                    // ボールの位置を取得する
                    final Location ballPos = ball.entity.getLocation();

                    //block.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, block.getLocation(), 5, .2, .2, .2);
                    //block.getRelative(BlockFace.DOWN).setType(Material.YELLOW_CONCRETE);

                    if (ballPos.clone().add(0, 0, -0.25).getBlock().getType().isSolid()) {
                        // 北のブロックにあたったら
                        ball.veloctiy.setZ(Math.abs(ball.veloctiy.getZ()));
                    } else if (ballPos.clone().add(0, 0, 0.25).getBlock().getType().isSolid()) {
                        // 南のブロックにあたったら
                        ball.veloctiy.setZ(-Math.abs(ball.veloctiy.getZ()));
                    }
                    if (ballPos.clone().add(0.25, 0, 0).getBlock().getType().isSolid()) {
                        // 東のブロックにあたったら
                        ball.veloctiy.setX(-Math.abs(ball.veloctiy.getX()));
                    } else if (ballPos.clone().add(-0.25, 0, 0).getBlock().getType().isSolid()) {
                        // 西のブロックにあたったら
                        ball.veloctiy.setX(Math.abs(ball.veloctiy.getX()));
                    }

                    // #TODO プレイヤー(パドル)にぶつかったら跳ね返る
                    boolean isHit = ball.entity.getLocation().getNearbyPlayers(3)
                            .stream()
                            .anyMatch(player -> {
                                // 詳細な当たり判定を取る
                                Location p = player.getLocation();
                                Location q = ballPos;

                                double px = p.getX();
                                double pz = p.getZ();
                                double qx = q.getX();
                                double qz = q.getZ();

                                // 矩形の中にいる人に絞った
                                if (Math.abs(px - qx) < 0.2 && Math.abs(pz - qz) < 2) {
                                    // 最後にあたった時間を取得
                                    long lastHitTime = player.getPersistentDataContainer().getOrDefault(KEY_COOLDOWN, PersistentDataType.LONG, 0L);

                                    // 現在の時刻を取得
                                    long timeMs = System.currentTimeMillis();

                                    // クールダウン未満だったらヒットしない
                                    if (lastHitTime + Config.cooldownTimeMs > timeMs)
                                        return false;

                                    // クールダウンをセット
                                    player.getPersistentDataContainer().set(KEY_COOLDOWN, PersistentDataType.LONG, timeMs);

                                    // 当たる
                                    return true;
                                }

                                return false;
                            });

                    if (isHit) {
                        // X速度を逆にする
                        ball.veloctiy.setX(-ball.veloctiy.getX());
                    }
                }
            }
        }.runTaskTimer(PongCraft.instance, 0, 1);


        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getWorlds().forEach(world -> {
                    world.getEntities().stream()
                            .filter(e -> e.getScoreboardTags().contains("paddle"))
                            .forEach(e -> {
                                e.setTicksLived(1);
                            });
                });
            }
        }.runTaskTimer(PongCraft.instance, 0, 20 * 5);
    }

    public void onJoin(PlayerJoinEvent event) {
        // PongCraftモードがONのときのみ
        if (!Config.isEnabled) {
            return;
        }

        // #TODO パドルをかぶってなかったらかぶせる
    }
}
