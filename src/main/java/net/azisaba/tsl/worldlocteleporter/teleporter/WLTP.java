package net.azisaba.tsl.worldlocteleporter.teleporter;

import net.azisaba.tsl.worldlocteleporter.WorldLocTeleporter;
import net.azisaba.tsl.worldlocteleporter.config.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class WLTP implements CommandExecutor {
    private final String prefix;
    private final WLTPConfig config;

    public WLTP(WLTPConfig config) {
        this.prefix = "[WLTP] ";
        this.config = config;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("getCustomCT")) {
            if (!(sender instanceof Player)) return false;
            this.giveCustomCT((Player)sender);
        }
        else if (command.getName().equalsIgnoreCase("wltp")) {
            // argsがあるかないかをみる
            switch (args.length) {
                case 0:
                    this.showWLTPGUI(sender);
                    break;

                case 1:
                    // ワールドへのテレポート
                    if (this.config.containLoc(args[0])) {
                        if ((worldTeleporter(sender, this.config.getLocation(args[0])))) {
                            break;
                        }
                    }
                    sender.sendMessage(ChatColor.RED + prefix + "テレポートに失敗しました");

                default:
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 与えられたWLTPLocationにCommandSenderをテレポートさせる
     * @param sender コマンドの送り主
     * @param loc 転送先座標
     * @return 転送できたかどうか
     */
    public static boolean worldTeleporter (CommandSender sender, WLTPLocation loc){
        int waitSec = 5;

        // Consoleは失敗扱い
        if (!(sender instanceof Player)) return false;
        // loc.locがnullの時は失敗扱い
        if (loc.loc == null) return false;

        Player player = (Player)sender;
        // wltp.op のみ 移動待機時間を0
        if (sender.hasPermission("wltp.op")) waitSec = 0;

        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendTitle(ChatColor.YELLOW +  loc.dispName, "テレポートしました", 3, 60, 1);
                player.sendMessage(ChatColor.YELLOW + "[WLTP] " + loc.dispName + "に移動しました．");
                player.teleport(loc.loc);
            }
        }.runTaskLater(JavaPlugin.getPlugin(WorldLocTeleporter.class), 20 * waitSec);
        return true;
    }

    /**
     * senderにWLTPGUIを表示させる
     * @param sender 表示させる対象
     */
    public void showWLTPGUI (CommandSender sender) {
        // Consoleは失敗扱い
        if (!(sender instanceof Player)) return;
        Player player = (Player)sender;

        // WLTPHolderを渡して作ったInventoryを生成する
        Inventory menu = Bukkit.createInventory(new WLTPHolder("wltpgui"), 54, ChatColor.GREEN + "WorldLocTeleporter GUI");

        // menu に各地点を登録する
        int counter = 0;
        for (String key: this.config.getKeySet()){
            WLTPLocation loc = this.config.getLocation(key);
            if (loc.dispMat == null) continue;
            ItemStack content = new ItemStack(loc.dispMat);
            ItemMeta contentMeta = content.getItemMeta();
            contentMeta.setDisplayName(loc.dispName);
            contentMeta.setLore(new ArrayList<>(Arrays.asList(key)));
            content.setItemMeta(contentMeta);
            menu.setItem(counter,content);
            counter++;
            if (counter>54) break;
        }

        // 登録したmenuをチェスト開音を鳴らしながらプレイヤーに出す
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 2, 1);
        player.openInventory(menu);
    }

    public void giveCustomCT (Player player) {
        ItemStack items = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta meta = items.getItemMeta();
        meta.setCustomModelData(100);
        items.setItemMeta(meta);
        // インベントリに空きがなかったら処理しない
        if(player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + prefix + "インベントリに空きがありません．");
            return;
        }
        player.getInventory().addItem(items);
        player.sendMessage(ChatColor.YELLOW + prefix + "WorldLocTeleporter GUI起動用アイテムを配布しました．");
    }
}
