package com.orkasool.tradechest;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class TradeChestCommand implements CommandExecutor {

    private final TradeChestPlugin plugin;

    public TradeChestCommand(TradeChestPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        
        // --- ADMIN RESET COMMAND: /tradechest reset <player> ---
        if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
            if (!sender.hasPermission("tradechest.admin")) {
                sender.sendMessage(Component.text("You don't have permission to reset chest limits.", NamedTextColor.RED));
                return true;
            }
            
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null && target.isOnline()) {
                target.getPersistentDataContainer().remove(TradeChestPlugin.HAS_ACQUIRED_CHEST);
                sender.sendMessage(Component.text("Reset Trade Chest limit for " + target.getName(), NamedTextColor.GREEN));
                target.sendMessage(Component.text("Your Trade Chest limit has been reset by an admin.", NamedTextColor.YELLOW));
            } else {
                sender.sendMessage(Component.text("Player not found or offline.", NamedTextColor.RED));
            }
            return true;
        }

        // --- PLAYER COMMAND: /tradechest ---
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }

        if (!player.hasPermission("tradechest.give")) {
            player.sendMessage(Component.text("No permission.", NamedTextColor.RED));
            return true;
        }

        PersistentDataContainer playerPdc = player.getPersistentDataContainer();

        // 1. Check if they already acquired one
        if (playerPdc.has(TradeChestPlugin.HAS_ACQUIRED_CHEST, PersistentDataType.BYTE)) {
            player.sendMessage(Component.text("You have already acquired a Trade Chest! You can only have one.", NamedTextColor.RED));
            return true;
        }

        // 2. Create the custom chest item
        ItemStack chestItem = new ItemStack(Material.CHEST);
        ItemMeta meta = chestItem.getItemMeta();
        meta.displayName(Component.text("Trade Chest", NamedTextColor.GOLD));
        meta.getPersistentDataContainer().set(TradeChestPlugin.IS_TRADE_CHEST, PersistentDataType.BYTE, (byte) 1);
        chestItem.setItemMeta(meta);

        // 3. Give the item and tag the player so they can't get another
        player.getInventory().addItem(chestItem);
        playerPdc.set(TradeChestPlugin.HAS_ACQUIRED_CHEST, PersistentDataType.BYTE, (byte) 1);
        
        player.sendMessage(Component.text("You received your Trade Chest!", NamedTextColor.GREEN));

        return true;
    }
}