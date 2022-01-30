package com.kamesuta.pongcraft;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class Ball {
    public final Entity entity;
    public final Vector veloctiy;

    private Ball(Entity entity, Vector veloctiy) {
        this.entity = entity;
        this.veloctiy = veloctiy;
    }

    public void destroy() {
        entity.remove();
    }

    public static Ball createBall(Location location) {
        ArmorStand entity = location.getWorld().spawn(location, ArmorStand.class);
        entity.setGravity(false);
        entity.setInvulnerable(true);
        entity.addScoreboardTag("ball");
        return new Ball(entity, new Vector(0.1, 0, 0.1));
    }
}
