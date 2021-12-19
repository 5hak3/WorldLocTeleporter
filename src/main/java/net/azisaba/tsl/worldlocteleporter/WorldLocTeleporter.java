package net.azisaba.tsl.worldlocteleporter;

import net.azisaba.tsl.worldlocteleporter.config.WLTPConfig;
import net.azisaba.tsl.worldlocteleporter.teleporter.WLTP;
import net.azisaba.tsl.worldlocteleporter.teleporter.WLTPListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class WorldLocTeleporter extends JavaPlugin {
    private WLTPConfig config;

    @Override
    public void onEnable() {
        this.config = new WLTPConfig(this);
        getLogger().info("[WLTP] Config読み込み完了");
        Objects.requireNonNull(getCommand("wltpreload")).setExecutor(this.config);
        Objects.requireNonNull(getCommand("wltplist")).setExecutor(this.config);
        Objects.requireNonNull(getCommand("wltp")).setExecutor(new WLTP(this.config));
        Objects.requireNonNull(getCommand("getcustomct")).setExecutor(new WLTP(this.config));
        getServer().getPluginManager().registerEvents(new WLTPListener(this.config), this);
    }
}
