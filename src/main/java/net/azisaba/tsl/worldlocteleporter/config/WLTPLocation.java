package net.azisaba.tsl.worldlocteleporter.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WLTPのコンフィグで扱う各転送先を格納する用のクラス
 */
public class WLTPLocation {
    // ゲッタを用意するのがめんどくさいんでpublicにしつつ
    // 値は変更されないのでfinalにしておく
    public final String dispName;   // 表示名
    public final Material dispMat;  // 表示マテリアル
    public final World world;       // ワールド
    public final Location loc;      // 座標

    public WLTPLocation(@NotNull String dispName, @NotNull String dispMat, @NotNull String world, @NotNull String loc) {
        // 表示名はそのまま
        this.dispName = dispName;
        // マテリアルは取得できなければオーク看板に
        if (Material.getMaterial(dispMat) != null)
            this.dispMat = Material.getMaterial(dispMat);
        else
            this.dispMat = Material.OAK_SIGN;
        // ワールドを取得
        this.world = Bukkit.getWorld(world);
        // ワールドが取得できていれば座標を取得
        // ワールドが取得できていなければ座標をnullにしておく
        if (this.world != null) {
            // なんかいい感じにパースして座標を取得
            List<Integer> locarr = Arrays.stream(loc.split(",",3)).map(Integer::parseInt).collect(Collectors.toList());
            this.loc = new Location(this.world, locarr.get(0), locarr.get(1), locarr.get(2));
        }
        else
            this.loc = null;
    }
}
