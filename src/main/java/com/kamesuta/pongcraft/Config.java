package com.kamesuta.pongcraft;

import net.kunmc.lab.configlib.BaseConfig;
import net.kunmc.lab.configlib.value.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Config extends BaseConfig {
    // ON
    public static boolean isEnabled = false;

    // クールダウンタイム
    public IntegerValue cooldownTimeMs = new IntegerValue(1000);

    // ボールの速度
    public DoubleValue ballSpeed = new DoubleValue(0.3);

    // 跳ね返したときのボールの速度倍率
    public DoubleValue ballSpeedMultiplier = new DoubleValue(1.03);

    // 跳ね返したときのボールの最大速度倍率
    public DoubleValue ballSpeedMaxMultiplier = new DoubleValue(2.0);

    // ボールの音の大きさ
    public FloatValue soundVolume = new FloatValue(2.6f);

    // おまえボールな
    public UUIDValue ballPlayer = new UUIDValue();

    // ボールスポーン位置
    public LocationValue ballPosition = new LocationValue();

    public Config(@NotNull Plugin plugin) {
        super(plugin);
    }
}