package net.azisaba.tsl.worldlocteleporter.teleporter;

import net.azisaba.tsl.worldlocteleporter.config.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
                WLTP.worldTeleporter(player, loc, this.config);
                event.getInventory().close();
                return;
            }
        }
        // そうじゃない時はイベントキャンセルする
        event.setCancelled(true);
    }

    // 指定されたブロックが設置された時 (CRAFTING_TABLEのCustomModelData=100)
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSetting (PlayerInteractEvent event) {
        // 右クリックの時以外は捨てる
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        // 指定アイテムを手に持っているとき以外は捨てる
        if (event.getItem() == null) return;
        if (event.getItem().getType() != Material.CRAFTING_TABLE) return;
        if (event.getItem().getItemMeta().getCustomModelData() != 100) return;

        // イベントをキャンセルして，プレイヤーにコマンド実行させる
        event.setCancelled(true);
        event.getPlayer().performCommand("wltp");
    }
}
