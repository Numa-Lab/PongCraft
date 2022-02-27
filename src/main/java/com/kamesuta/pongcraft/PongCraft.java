package com.kamesuta.pongcraft;

import com.kamesuta.pongcraft.command.MainCommand;
import com.kamesuta.pongcraft.listener.BallListener;
import dev.kotx.flylib.FlyLib;
import net.kunmc.lab.configlib.ConfigCommand;
import net.kunmc.lab.configlib.ConfigCommandBuilder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class PongCraft extends JavaPlugin {
    public static Logger LOGGER;
    public static PongCraft instance;
    public static Config config;

    public List<Ball> balls = new ArrayList<Ball>();

    @Override
    public void onEnable() {
        instance = this;
        LOGGER = getLogger();

        config = new Config(this);
        config.saveConfigIfAbsent();
        config.loadConfig();

        ConfigCommand configCommand = new ConfigCommandBuilder(config).build();


        FlyLib.create(this, builder -> {
            builder.command(new MainCommand("pongcraft", configCommand));
        });

        getServer().getPluginManager().registerEvents(new BallListener(), this);
    }

    @Override
    public void onDisable() {
    }
}
