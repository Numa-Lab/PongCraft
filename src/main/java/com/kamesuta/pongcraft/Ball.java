package com.kamesuta.pongcraft;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Cod;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

public class Ball {
    public static final double OFFSET_HEIGHT = 1.5;

    public final Entity entity;
    public final Vector veloctiy;
    @Nullable
    public final Player ballPlayer;

    private Ball(Entity entity, Vector veloctiy, @Nullable Player ballPlayer) {
        this.entity = entity;
        this.veloctiy = veloctiy;
        this.ballPlayer = ballPlayer;
    }

    public Location getLocation() {
        return entity.getLocation().clone().add(0, OFFSET_HEIGHT, 0);
    }

    public void destroy() {
        entity.remove();
    }

    public static Ball createBall(Location location) {
        ArmorStand entity = location.getWorld().spawn(location.clone().add(0, -OFFSET_HEIGHT, 0), ArmorStand.class);
        entity.setGravity(false);
        entity.setInvulnerable(true);
        entity.addScoreboardTag("ball");
        entity.setMarker(true);
        entity.setInvisible(true);
        entity.setItem(EquipmentSlot.HEAD, new ItemStack(Material.WHITE_GLAZED_TERRACOTTA));

        Player ballPlayer = Bukkit.getPlayer(Config.ballPlayer);
        if (ballPlayer != null) {
//            ArmorStand spacer = location.getWorld().spawn(location, ArmorStand.class);
//            spacer.setGravity(false);
//            spacer.setInvulnerable(true);
//            spacer.addScoreboardTag("ball");
//            spacer.setInvisible(true);
//            spacer.setSilent(true);
//            entity.addPassenger(spacer);
//
//            spacer.addPassenger(ballPlayer);
            ballPlayer.setGlowing(true);
        }
        return new Ball(entity, new Vector(Config.ballSpeed, 0, Config.ballSpeed), ballPlayer);
    }
}
