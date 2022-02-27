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
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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

        if (PongCraft.config.ballPosition.value() == null) {
            ctx.fail("ballPositionがセットされていません");
            return;
        }

        @Nullable Player ballPlayer = Bukkit.getPlayer(PongCraft.config.ballPlayer.value());
        if (ballPlayer == null) {
            ctx.fail("ballPlayerが見つかりません");
            return;
        }

        Config.isEnabled = true;
        ctx.success("PongCraftを有効化しました.");

        // ボールを削除
        PongCraft.instance.balls.clear();
        Bukkit.selectEntities(ctx.getSender(), "@e[tag=ball]").forEach(Entity::remove);
        // パドルを削除
        Bukkit.selectEntities(ctx.getSender(), "@e[tag=paddle]").forEach(Entity::remove);

        // ボールを作る
        Ball ball = Ball.createBall(PongCraft.config.ballPosition.value(), ballPlayer);

        PongCraft.instance.balls.add(ball);

        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective ob = sb.getObjective("control");
        if (ob != null) {
            Score ballSpeed = ob.getScore("ballSpeed");
            ballSpeed.setScore(1000);
        }

        // プレイヤーにパドルをかぶせる
        // 頭の上にFallingBlockを乗せ続ける
        for (Player player : Bukkit.getOnlinePlayers()) {
            // ボールプレイヤーにはパドルを乗せない
            if (player.equals(ball.ballPlayer))
                continue;

            Ball.givePaddle(player);
        }
    }
}