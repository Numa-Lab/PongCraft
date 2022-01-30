package com.kamesuta.pongcraft;

import com.kamesuta.pongcraft.command.MainCommand;
import com.kamesuta.pongcraft.listener.BallListener;
import dev.kotx.flylib.FlyLib;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class PongCraft extends JavaPlugin {
    public static PongCraft instance;

    public List<Ball> balls = new ArrayList<Ball>();

    @Override
    public void onEnable() {
        instance = this;

        FlyLib.create(this, builder -> {
            builder.command(new MainCommand("pongcraft"));
        });

        getServer().getPluginManager().registerEvents(new BallListener(), this);
    }

    @Override
    public void onDisable() {
    }
}
