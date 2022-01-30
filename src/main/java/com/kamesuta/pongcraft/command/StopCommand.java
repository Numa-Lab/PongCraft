package com.kamesuta.pongcraft.command;

import com.kamesuta.pongcraft.Ball;
import com.kamesuta.pongcraft.Config;
import com.kamesuta.pongcraft.PongCraft;
import dev.kotx.flylib.command.Command;
import dev.kotx.flylib.command.CommandContext;

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
    }
}