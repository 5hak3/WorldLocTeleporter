package net.azisaba.tsl.worldlocteleporter.config;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Set;

public class WLTPConfig implements CommandExecutor {
    private final String prefix = "[WLTP] ";
    private final JavaPlugin plugin;

    @Getter
    private Integer waitTime;
    private final HashMap<String,WLTPLocation> locs;

    public WLTPConfig(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.locs = new HashMap<>();
        this.loadConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("wltpreload")) {
            if(!(this.loadConfig()))
                sender.sendMessage(ChatColor.RED + prefix + "config.yml の読み込みに失敗しました．");
            else
                sender.sendMessage(ChatColor.YELLOW + prefix + "config.yml を読み込みました．");
        }
        else if (command.getName().equalsIgnoreCase("wltplist")) this.listConfig(sender);
        else return false;
        return true;
    }

    /**
     * config.ymlを読み込んで設定を格納する
     * @return できたかどうか
     */
    private boolean loadConfig() {
        this.plugin.saveDefaultConfig();
        this.plugin.reloadConfig();
        FileConfiguration fc = this.plugin.getConfig();//.getConfigurationSection("Default")

        // General.* を読み込む
        ConfigurationSection cs = fc.getConfigurationSection("General");
        if (cs != null) {
            // General.waittime を読み込む
            if (cs.contains("waittime")) {
                if (cs.get("waittime") instanceof Integer)
                    this.waitTime = (Integer) cs.get("waittime");
                else {
                    this.plugin.getLogger().info(ChatColor.RED + prefix + "waittime は Integer ではありません．");
                    return false;
                }
            } else {
                this.plugin.getLogger().info(ChatColor.RED + prefix + "waittime がありません．");
                return false;
            }
        } else {
            this.plugin.getLogger().info(ChatColor.RED + prefix + "General がありません．");
            return false;
        }

        // Locations.* を読み込む
        cs = fc.getConfigurationSection("Locations");
        if (cs != null){
            for (String key: cs.getKeys(false)){
                ConfigurationSection location = cs.getConfigurationSection(key);
                assert location != null;
                if (location.contains("DisplayName") && location.contains("DisplayMaterial") &&
                    location.contains("ToWorld") && location.contains("ToLoc")) {
                    // 一旦取得してObjectに入れておく
                    Object dispName = location.get("DisplayName");
                    Object dispMat = location.get("DisplayMaterial");
                    Object world = location.get("ToWorld");
                    Object loc = location.get("ToLoc");
                    // これらのObjectがStringかを確かめてWLTPLocationを生成する
                    if (dispName instanceof String && dispMat instanceof String &&
                        world instanceof String && loc instanceof String)
                        this.locs.put(
                                // 転移先名
                                key,
                                // 転移先情報
                                new WLTPLocation((String)dispName, (String)dispMat, (String)world, (String)loc)
                        );
                    else {
                        this.plugin.getLogger().info(ChatColor.RED + prefix + "テンプレートに従ってください．");
                        return false;
                    }
                } else {
                    this.plugin.getLogger().info(ChatColor.RED + prefix + "テンプレートに従ってください．");
                    return false;
                }
            }
        } else {
            this.plugin.getLogger().info(ChatColor.RED + prefix + "Locations がありません．");
            return false;
        }

        return true;
    }

    /**
     * 設定を表示する
     */
    private void listConfig (CommandSender sender) {
//        if (sender.hasPermission("wltp.op")) {
            sender.sendMessage(ChatColor.YELLOW + prefix + "TP待機時間: " + this.waitTime.toString());
//        }
        sender.sendMessage(ChatColor.YELLOW + prefix + "転移先リスト:");
        for (String key: this.locs.keySet()) {
            sender.sendMessage(ChatColor.YELLOW + "  " + key + ": " + this.locs.get(key).dispName);
        }
    }

    public boolean containLoc (String key) {
        return this.locs.containsKey(key);
    }

    public WLTPLocation getLocation (String key) {
        if (this.containLoc(key)) return this.locs.get(key);
        else return null;
    }

    public Set<String> getKeySet () {
        return this.locs.keySet();
    }
}
