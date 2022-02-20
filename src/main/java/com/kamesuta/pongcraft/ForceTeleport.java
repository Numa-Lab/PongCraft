package com.kamesuta.pongcraft;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ForceTeleport {
    // 強制テレポート関数
    public static boolean teleportForce(Entity entity, Location location) {
        try {
            Class<?> nmsClass = entity.getClass();
            while (nmsClass != null && !nmsClass.getSimpleName().equals("CraftEntity")) {
                nmsClass = nmsClass.getSuperclass();
            }
            if (nmsClass == null) {
                return false;
            }

            Field nmsEntityField = nmsClass.getDeclaredField("entity");
            nmsEntityField.setAccessible(true);
            Object nmsEntity = nmsEntityField.get(entity);
            Method nmsSetPositionMethod = nmsEntity.getClass().getMethod("setPosition", double.class, double.class, double.class);
            nmsSetPositionMethod.invoke(nmsEntity, location.getX(), location.getY(), location.getZ());
            return true;
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }
}
