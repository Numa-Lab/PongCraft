package com.kamesuta.pongcraft;

import com.kamesuta.pongcraft.command.MainCommand;
import com.kamesuta.pongcraft.listener.BallListener;
import dev.kotx.flylib.FlyLib;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class PongCraft extends JavaPlugin {
    public static Logger LOGGER;
    public static PongCraft instance;

    public List<Ball> balls = new ArrayList<Ball>();

    @Override
    public void onEnable() {
        instance = this;
        LOGGER = getLogger();

        FlyLib.create(this, builder -> {
            builder.command(new MainCommand("pongcraft"));
        });

        getServer().getPluginManager().registerEvents(new BallListener(), this);
    }

    @Override
    public void onDisable() {
    }
}
