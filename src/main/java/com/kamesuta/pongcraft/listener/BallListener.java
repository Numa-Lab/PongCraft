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

import java.util.Optional;
import java.util.OptionalInt;
import java.util.logging.Level;

public class BallListener implements Listener {
    public static final NamespacedKey KEY_COOLDOWN = new NamespacedKey(PongCraft.instance, "cooldown");
    public static final NamespacedKey KEY_PLAYER_X = new NamespacedKey(PongCraft.instance, "player_x");

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

                    // 現在の時刻を取得
                    long timeMs = System.currentTimeMillis();

                    // プレイヤー(パドル)にぶつかったら跳ね返る
                    Optional<Player> isHit = ball.entity.getLocation().getNearbyPlayers(3)
                            .stream()
                            .filter(player -> {
                                // 詳細な当たり判定を取る
                                Location p = player.getLocation();
                                Location q = ballPos;

                                // プレイヤーのX座標の差を取得
                                double diffX = Optional.ofNullable(player.getPersistentDataContainer().get(KEY_PLAYER_X, PersistentDataType.DOUBLE))
                                        .map(posX -> player.getLocation().getX() - posX)
                                        .orElse(0.0);

                                //PongCraft.LOGGER.log(Level.INFO, String.format("diffX: %s", diffX));

                                double px = p.getX();
                                double pz = p.getZ();
                                double qx = q.getX();
                                double qz = q.getZ();

                                // 矩形の中にいる人に絞った
                                double ballPlayerSpeed = Math.abs(ball.veloctiy.getX()) + Math.abs(diffX * 5);
                                if (Math.abs(px - qx) < ballPlayerSpeed && Math.abs(pz - qz) < 2) {
                                    // 最後にあたった時間を取得
                                    long lastHitTime = player.getPersistentDataContainer().getOrDefault(KEY_COOLDOWN, PersistentDataType.LONG, 0L);

                                    // クールダウン未満だったらヒットしない
                                    if (lastHitTime + Config.cooldownTimeMs > timeMs)
                                        return false;

                                    return true;
                                }

                                return false;
                            })
                            .findFirst();

                    if (isHit.isPresent()) {
                        // あたったプレイヤー
                        Player player = isHit.get();

                        // クールダウンをセット
                        player.getPersistentDataContainer().set(KEY_COOLDOWN, PersistentDataType.LONG, timeMs);

                        // プレイヤーの最後のX座標を取得
                        Double lastPosX = player.getPersistentDataContainer().get(KEY_PLAYER_X, PersistentDataType.DOUBLE);
                        if (lastPosX != null) {
                            // 歩いたX座標の差を求める
                            double diffPosX = player.getLocation().getX() - lastPosX;

                            // 当たる
                            int velX = diffPosX > 0 ? 1 : -1;

                            // X速度を逆にする
                            ball.veloctiy.setX(Math.abs(ball.veloctiy.getX()) * velX);

                            // ボールとプレイヤーのZ座標の差を求める
                            double diffZ = ball.entity.getLocation().getZ() - player.getLocation().getZ();

                            // Z速度にdiffZを加算
                            ball.veloctiy.setZ(ball.veloctiy.getZ() * 0.5 + diffZ / 2 * Config.ballSpeed);
                        }
                    }
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    // プレイヤーの位置を取得
                    Location playerPos = player.getLocation();
                    // プレイヤーのX座標をメモる
                    player.getPersistentDataContainer().set(KEY_PLAYER_X, PersistentDataType.DOUBLE, playerPos.getX());
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
