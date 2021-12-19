package net.azisaba.tsl.worldlocteleporter.teleporter;

import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class WLTPHolder implements InventoryHolder {
    @Getter
    public final String label;

    public WLTPHolder(String label) {
        this.label = label;
    }

    // 複数渡されるケース (リストにしないと順序保証ないから壊れそう)
    public WLTPHolder(String... labels) {
        this.label = "".join(",", labels);
    }

    @Override
    public @NotNull Inventory getInventory() {
        // 画面表示用なのでいらない
        return null;
    }
}
