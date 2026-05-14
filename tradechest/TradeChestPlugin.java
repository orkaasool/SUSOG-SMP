package com.orkasool.tradechest;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class TradeChestPlugin extends JavaPlugin {

    public static NamespacedKey IS_TRADE_CHEST;
    public static NamespacedKey OWNER_UUID;
    public static NamespacedKey OWNER_ITEM;
    public static NamespacedKey OFFER_UUID;
    public static NamespacedKey OFFER_ITEM;
    
    // NEW: Key to track if a player has received their chest
    public static NamespacedKey HAS_ACQUIRED_CHEST;

    @Override
    public void onEnable() {
        IS_TRADE_CHEST = new NamespacedKey(this, "is_trade_chest");
        OWNER_UUID = new NamespacedKey(this, "owner_uuid");
        OWNER_ITEM = new NamespacedKey(this, "owner_item");
        OFFER_UUID = new NamespacedKey(this, "offer_uuid");
        OFFER_ITEM = new NamespacedKey(this, "offer_item");
        
        // Initialize the new key
        HAS_ACQUIRED_CHEST = new NamespacedKey(this, "has_acquired_chest");

        getCommand("tradechest").setExecutor(new TradeChestCommand(this));
        getServer().getPluginManager().registerEvents(new TradeChestListener(this), this);
        
        getLogger().info("TradeChest has been enabled!");
    }
}