package com.kamesuta.pongcraft.command;

import com.kamesuta.pongcraft.Ball;
import com.kamesuta.pongcraft.Config;
import com.kamesuta.pongcraft.PongCraft;
import dev.kotx.flylib.command.Command;
import dev.kotx.flylib.command.CommandContext;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

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
        // パドルを削除
        Bukkit.selectEntities(ctx.getSender(), "@e[tag=paddle]").forEach(Entity::remove);

        // ボールを作る
        // #TODO コマンドブロック対応
        Ball ball = Ball.createBall(ctx.getPlayer().getLocation());

        PongCraft.instance.balls.add(ball);

        // #TODO プレイヤーにパドルをかぶせる

        // 頭の上に豚を乗せる
        for (Player player : Bukkit.getOnlinePlayers()) {
            FallingBlock pig = ball.entity.getWorld().spawnFallingBlock(ball.entity.getLocation(), Material.CRACKED_STONE_BRICKS, (byte) 0);
            //pig.setInvisible(true);
            //pig.setAI(false);
            pig.setGravity(false);
            pig.setSilent(true);
            pig.setInvulnerable(true);
            pig.addScoreboardTag("paddle");
            player.addPassenger(pig);
        }
    }
}