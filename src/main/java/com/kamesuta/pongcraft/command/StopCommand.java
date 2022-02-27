package com.kamesuta.pongcraft.command;

import com.kamesuta.pongcraft.Ball;
import com.kamesuta.pongcraft.Config;
import com.kamesuta.pongcraft.PongCraft;
import dev.kotx.flylib.command.Command;
import dev.kotx.flylib.command.CommandContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class StopCommand extends Command {
    public StopCommand() {
        super("stop");
    }

    @Override
    public void execute(CommandContext ctx) {
        if (!Config.isEnabled) {
            ctx.fail("PongCraftは既に無効です.");
            return;
        }

        Config.isEnabled = false;
        ctx.success("PongCraftを無効化しました.");

        // ボールを削除
        PongCraft.instance.balls.forEach(Ball::destroy);
        PongCraft.instance.balls.clear();

        // ボールを削除
        Bukkit.selectEntities(ctx.getSender(), "@e[tag=ball]").forEach(Entity::remove);
        // パドルを削除
        Bukkit.selectEntities(ctx.getSender(), "@e[tag=paddle]").forEach(Entity::remove);
    }
}