package com.kamesuta.pongcraft;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;

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
        return new Ball(entity, new Vector(PongCraft.config.ballSpeed.value(), 0, PongCraft.config.ballSpeed.value()), ballPlayer);
    }
}
