package com.orkasool.tradechest;

import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class TradeChestHolder implements InventoryHolder {
    private final Block block;

    public TradeChestHolder(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null; // Handled dynamically in the listener
    }
}