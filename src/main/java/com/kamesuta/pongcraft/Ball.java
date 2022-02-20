package com.kamesuta.pongcraft;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Ball {
    public static final double OFFSET_HEIGHT = 1.2;

    public final Entity entity;
    public final Vector veloctiy;

    private Ball(Entity entity, Vector veloctiy) {
        this.entity = entity;
        this.veloctiy = veloctiy;
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
        return new Ball(entity, new Vector(Config.ballSpeed, 0, Config.ballSpeed));
    }
}
