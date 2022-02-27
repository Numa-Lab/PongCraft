package com.kamesuta.pongcraft;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class Ball {
    public final Entity entity;
    public final Vector veloctiy;
    @Nullable
    public final Player ballPlayer;

    private final ProtocolManager protocol = ProtocolLibrary.getProtocolManager();
    private PacketContainer packet;

    private Ball(Entity entity, Vector veloctiy, @Nullable Player ballPlayer) {
        this.entity = entity;
        this.veloctiy = veloctiy;
        this.ballPlayer = ballPlayer;

        if (ballPlayer != null) {
            packet = protocol.createPacket(PacketType.Play.Server.MOUNT);
            packet.getIntegers().write(0, entity.getEntityId());
            packet.getIntegerArrays().write(0, new int[]{ballPlayer.getEntityId()});
        }
    }

    public void destroy() {
        entity.remove();
    }

    public void sendRidePacket() {
        if (ballPlayer != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                try {
                    protocol.sendServerPacket(p, packet);
                } catch (InvocationTargetException e) {
                    PongCraft.LOGGER.warning("Failed to send packet to " + p.getName());
                }
            }
        }
    }

    public static Ball createBall(Location location, Player ballPlayer) {
        ArmorStand entity = location.getWorld().spawn(location, ArmorStand.class);
        entity.setGravity(false);
        entity.setInvulnerable(true);
        entity.addScoreboardTag("ball");
        entity.setMarker(true);
        entity.setInvisible(true);

        if (ballPlayer != null) {
            entity.addPassenger(ballPlayer);
            ballPlayer.setGlowing(true);
        }
        int which = ThreadLocalRandom.current().nextInt(2) * 2 - 1;
        return new Ball(entity, new Vector(which * PongCraft.config.ballSpeed.value(), 0, PongCraft.config.ballSpeed.value()), ballPlayer);
    }

    public static void givePaddle(Player player) {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        Optional<Team> teamOpt = Optional.ofNullable(sb.getEntryTeam(player.getName()));
        if (!teamOpt.map(e -> "red".equals(e.getName())).orElse(false)
                && !teamOpt.map(e -> "blue".equals(e.getName())).orElse(false)) {
            // 赤と青チーム以外はパドルを被せない
            return;
        }

        FallingBlock paddle = player.getWorld().spawnFallingBlock(player.getLocation(), Material.CRACKED_STONE_BRICKS, (byte) 0);
        //paddle.setInvisible(true);
        //paddle.setAI(false);
        paddle.setGravity(false);
        paddle.setSilent(true);
        paddle.setInvulnerable(true);
        paddle.addScoreboardTag("paddle");
        paddle.addScoreboardTag("paddle_" + player.getName());
        player.addPassenger(paddle);
    }

    public static void removePaddle(Player player) {
        for (Entity e : player.getWorld().getEntitiesByClass(FallingBlock.class)) {
            if (e.getScoreboardTags().contains("paddle") && e.getScoreboardTags().contains("paddle_" + player.getName())) {
                e.remove();
            }
        }
    }
}
