package com.kamesuta.pongcraft.command;

import com.kamesuta.pongcraft.Ball;
import com.kamesuta.pongcraft.Config;
import com.kamesuta.pongcraft.PongCraft;
import dev.kotx.flylib.command.Command;
import dev.kotx.flylib.command.CommandContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class StartCommand extends Command {
    public StartCommand() {
        super("start");
    }

    @Override
    public void execute(CommandContext ctx) {
        if (Config.isEnabled) {
            ctx.fail("PongCraftは既に有効です.");
            return;
        }

        Config.isEnabled = true;
        ctx.success("PongCraftを有効化しました.");

        // ボールを削除
        Bukkit.selectEntities(ctx.getSender(), "@e[tag=ball]").forEach(Entity::remove);

        // ボールを作る
        // #TODO コマンドブロック対応
        Ball ball = Ball.createBall(ctx.getPlayer().getLocation());

        PongCraft.instance.balls.add(ball);

        // #TODO プレイヤーにパドルをかぶせる
    }
}