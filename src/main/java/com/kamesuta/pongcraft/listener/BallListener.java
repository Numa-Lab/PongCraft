package com.kamesuta.pongcraft.listener;

import com.kamesuta.pongcraft.Ball;
import com.kamesuta.pongcraft.Config;
import com.kamesuta.pongcraft.ForceTeleport;
import com.kamesuta.pongcraft.PongCraft;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Optional;

public class BallListener implements Listener {
    public static final NamespacedKey KEY_COOLDOWN = new NamespacedKey(PongCraft.instance, "cooldown");
    public static final NamespacedKey KEY_PLAYER_X = new NamespacedKey(PongCraft.instance, "player_x");

    public BallListener() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!Config.isEnabled) {
                    return;
                }

                for (Ball ball : PongCraft.instance.balls) {
                    // ボールを動かす
                    Location tpLocation = ball.entity.getLocation().clone().add(ball.veloctiy);
                    if (tpLocation.getBlock().getType() == Material.AIR || tpLocation.getBlock().getType() == Material.CAVE_AIR)
                        ForceTeleport.teleportForce(ball.entity, tpLocation);

                    // 乗せるパケット
                    ball.sendRidePacket();

                    // #TODO 角に当てたり、動きながら当てたら早くなったりするようにする
                    // #TODO 反発力あげる、スピード上げる、跳ね返る向きをランダムにしたりする

                    // ボールの位置を取得する
                    final Location ballPos = ball.entity.getLocation();

                    // 壁にぶつかったら跳ね返る
                    RayTraceResult result = ball.entity.getWorld().rayTraceBlocks(
                            ball.entity.getBoundingBox().getCenter().toLocation(ball.entity.getWorld()),
                            ball.veloctiy.clone().normalize(), 4);
                    if (result != null && result.getHitBlock() != null
                            && !result.getHitBlock().isPassable()
                            && ball.entity.getBoundingBox().expand(0.7).overlaps(result.getHitBlock().getBoundingBox())) {
                        // 跳ね返る
                        Vector normal = result.getHitBlockFace().getDirection();
                        ball.veloctiy.subtract(normal.clone().multiply(2 * ball.veloctiy.dot(normal)));

                        // パーティクルを出す
                        ballPos.getWorld().spawnParticle(Particle.CRIT, ballPos.clone().add(0, .5, 0), 20, .1, .1, .1, 0.5);

                        // 音を出す
                        ballPos.getWorld().playSound(ballPos, Sound.ENTITY_BLAZE_HURT, PongCraft.config.soundVolume.value(), 1);
                    }

                    // 現在の時刻を取得
                    long timeMs = System.currentTimeMillis();

                    // プレイヤー(パドル)にぶつかったら跳ね返る
                    Optional<Player> isHit = ball.entity.getLocation().getNearbyPlayers(3)
                            .stream()
                            .filter(player -> {
                                // ボールプレイヤーがボールに当たらないようにする
                                if (player.equals(ball.ballPlayer)) {
                                    return false;
                                }

                                // 英霊が当たらないようにする
                                if (player.getGameMode() == GameMode.SPECTATOR) {
                                    return false;
                                }

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
                                    if (lastHitTime + PongCraft.config.cooldownTimeMs.value() > timeMs)
                                        return false;

                                    // ボールを少し速くする
                                    ball.veloctiy.multiply(PongCraft.config.ballSpeedMultiplier.value());

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
                            ball.veloctiy.setZ(ball.veloctiy.getZ() * 0.5 + diffZ / 2 * PongCraft.config.ballSpeed.value());

                            // パーティクルの座標
                            Location hitPos = player.getLocation().clone();
                            hitPos.setX(ballPos.getX());

                            // パーティクルを出す
                            // /particle minecraft:crit ~ ~.5 ~ 0.1 0.1 0.1 0.5 30 normal
                            hitPos.getWorld().spawnParticle(Particle.CRIT, hitPos, 50, .1, .1, 1, 0.5);

                            // 音を出す
                            hitPos.getWorld().playSound(hitPos, Sound.ENTITY_BLAZE_HURT, PongCraft.config.soundVolume.value(), 1);
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

    @EventHandler
    private void onRiderDismount(EntityDismountEvent e) {
        Entity vehicle = e.getDismounted();
        boolean b = PongCraft.instance.balls.stream().anyMatch(ball -> ball.entity.equals(vehicle));
        if (b) {
            e.setCancelled(true);
        }
    }

//    @EventHandler
//    public void onJoin(PlayerJoinEvent event) {
//        // PongCraftモードがONのときのみ
//        if (!Config.isEnabled) {
//            return;
//        }
//
//        // #TODO パドルをかぶってなかったらかぶせる
//    }
}
