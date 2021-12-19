package net.azisaba.tsl.worldlocteleporter.teleporter;

import net.azisaba.tsl.worldlocteleporter.config.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class WLTPListener implements Listener {
    private final WLTPConfig config;

    public WLTPListener(WLTPConfig config) {
        this.config = config;
    }
    // Inventory内がクリックされたとき
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick (InventoryClickEvent event) {
        // WLTPHolderのとき以外は捨てる
        if (!(event.getInventory().getHolder() instanceof WLTPHolder)) return;

        // やろうとしたプレイヤーを取得
        Player player = (Player) event.getView().getPlayer();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;
        // labelを識別してもいいけど今のところはしなくていい

        // keyがConfigに含まれているかをチェック
        if (clicked.getLore()!=null){
            String key = clicked.getLore().get(0);
            // Loreが転移先に含まれていればステージしてインベントリを閉じる
            if (this.config.containLoc(key)){
                WLTPLocation loc = this.config.getLocation(key);
                if (!player.hasPermission("wltp.op"))
                    player.sendMessage(ChatColor.YELLOW + "[WLTP] " + config.getWaitTime().toString() + "秒後に" + loc.dispName + "へ移動します．");
                WLTP.worldTeleporter(player, loc);
                event.getInventory().close();
                return;
            }
        }
        // そうじゃない時はイベントキャンセルする
        event.setCancelled(true);
    }
}
