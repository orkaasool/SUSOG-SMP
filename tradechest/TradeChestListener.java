package com.orkasool.tradechest;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.UUID;

public class TradeChestListener implements Listener {

    private final TradeChestPlugin plugin;
    private final int OWNER_HEAD = 1;
    private final int OWNER_SLOT = 2;
    private final int OFFER_SLOT = 6;
    private final int OFFER_HEAD = 7;

    public TradeChestListener(TradeChestPlugin plugin) {
        this.plugin = plugin;
    }

    // --- 1. PLACING & BREAKING CHEST (UNCHANGED) ---
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (!item.hasItemMeta()) return;
        if (item.getItemMeta().getPersistentDataContainer().has(TradeChestPlugin.IS_TRADE_CHEST, PersistentDataType.BYTE)) {
            Block block = event.getBlockPlaced();
            if (block.getState() instanceof Chest chest) {
                chest.getPersistentDataContainer().set(TradeChestPlugin.IS_TRADE_CHEST, PersistentDataType.BYTE, (byte) 1);
                chest.getPersistentDataContainer().set(TradeChestPlugin.OWNER_UUID, PersistentDataType.STRING, event.getPlayer().getUniqueId().toString());
                chest.update();
                event.getPlayer().sendMessage(Component.text("Trade Chest placed!", NamedTextColor.GREEN));
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!(block.getState() instanceof Chest chest)) return;
        PersistentDataContainer pdc = chest.getPersistentDataContainer();
        if (!pdc.has(TradeChestPlugin.IS_TRADE_CHEST, PersistentDataType.BYTE)) return;

        Player player = event.getPlayer();
        if (!player.getUniqueId().toString().equals(pdc.get(TradeChestPlugin.OWNER_UUID, PersistentDataType.STRING))) {
            event.setCancelled(true);
            player.sendMessage(Component.text("Only the owner can break this.", NamedTextColor.RED));
            return;
        }
        if (pdc.has(TradeChestPlugin.OWNER_ITEM, PersistentDataType.BYTE_ARRAY) || pdc.has(TradeChestPlugin.OFFER_ITEM, PersistentDataType.BYTE_ARRAY)) {
            event.setCancelled(true);
            player.sendMessage(Component.text("You must empty the chest first.", NamedTextColor.RED));
            return;
        }
        event.setDropItems(false);
        ItemStack drop = new ItemStack(Material.CHEST);
        drop.editMeta(m -> {
            m.displayName(Component.text("Trade Chest", NamedTextColor.GOLD));
            m.getPersistentDataContainer().set(TradeChestPlugin.IS_TRADE_CHEST, PersistentDataType.BYTE, (byte) 1);
        });
        block.getWorld().dropItemNaturally(block.getLocation(), drop);
    }

    // --- 2. OPENING THE CHEST ---
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;
        Block block = event.getClickedBlock();
        if (block == null || !(block.getState() instanceof Chest chest)) return;
        if (!chest.getPersistentDataContainer().has(TradeChestPlugin.IS_TRADE_CHEST, PersistentDataType.BYTE)) return;

        event.setCancelled(true);
        Inventory gui = Bukkit.createInventory(new TradeChestHolder(block), 9, Component.text("Trade Chest"));
        refreshGUI(gui, chest);
        event.getPlayer().openInventory(gui);
    }

    // --- 3. CLICK LOGIC (SIMPLIFIED & NATIVE) ---
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof TradeChestHolder holder)) return;

        Player player = (Player) event.getWhoClicked();
        Chest chest = (Chest) holder.getBlock().getState();
        PersistentDataContainer pdc = chest.getPersistentDataContainer();

        String ownerStr = pdc.get(TradeChestPlugin.OWNER_UUID, PersistentDataType.STRING);
        boolean isOwner = player.getUniqueId().toString().equals(ownerStr);
        boolean hasOffer = pdc.has(TradeChestPlugin.OFFER_ITEM, PersistentDataType.BYTE_ARRAY);

        // A. Clicks in Player's Bottom Inventory
        if (event.getClickedInventory() != null && event.getClickedInventory().equals(event.getView().getBottomInventory())) {
            if (event.isShiftClick()) {
                event.setCancelled(true); // Disable shift-click to prevent items zipping to the wrong slots
                player.sendMessage(Component.text("Please drag and drop items into the trade slots manually.", NamedTextColor.RED));
            }
            return; // Allow picking up items normally
        }

        // B. Clicks in the Top Custom GUI
        event.setCancelled(true); // Default to cancelled to protect GUI background
        if (event.getClickedInventory() == null) return;
        int slot = event.getSlot();

        // ---------------- OWNER LOGIC ----------------
        if (isOwner) {
            if (slot == OWNER_SLOT) {
                if (hasOffer) {
                    player.sendMessage(Component.text("Your item is locked until the trade is completed or scrubbed.", NamedTextColor.RED));
                } else {
                    allowVanillaClickAndSave(event, chest, pdc, TradeChestPlugin.OWNER_ITEM, OWNER_SLOT);
                }
            } 
            else if (slot == OFFER_SLOT && hasOffer) {
                if (event.getClick() == ClickType.DROP || event.getClick() == ClickType.CONTROL_DROP) {
                    finishTrade(chest, pdc, player, false); // Scrub
                } else {
                    finishTrade(chest, pdc, player, true);  // Accept
                }
            }
        } 
        // ---------------- TRADER LOGIC ----------------
        else {
            if (slot == OWNER_SLOT) {
                player.sendMessage(Component.text("You cannot take the owner's item.", NamedTextColor.RED));
            } 
            else if (slot == OFFER_SLOT) {
                if (hasOffer) {
                    String offererStr = pdc.get(TradeChestPlugin.OFFER_UUID, PersistentDataType.STRING);
                    if (player.getUniqueId().toString().equals(offererStr)) {
                        allowVanillaClickAndSave(event, chest, pdc, TradeChestPlugin.OFFER_ITEM, OFFER_SLOT);
                    } else {
                        player.sendMessage(Component.text("Someone else has an offer here.", NamedTextColor.RED));
                    }
                } else {
                    if (event.getCursor() != null && !event.getCursor().getType().isAir()) {
                        pdc.set(TradeChestPlugin.OFFER_UUID, PersistentDataType.STRING, player.getUniqueId().toString());
                        allowVanillaClickAndSave(event, chest, pdc, TradeChestPlugin.OFFER_ITEM, OFFER_SLOT);
                    }
                }
            }
        }
    }

    // Block sweeping items across the custom GUI
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof TradeChestHolder) {
            event.setCancelled(true);
        }
    }

    // --- HELPER 1: Lets Minecraft handle the mouse click natively, then saves the result ---
    private void allowVanillaClickAndSave(InventoryClickEvent event, Chest chest, PersistentDataContainer pdc, NamespacedKey key, int slot) {
        event.setCancelled(false); // Let Minecraft do its normal drag/drop/stack logic natively!

        // Wait exactly 1 tick for the vanilla mouse event to finish moving the item
        Bukkit.getScheduler().runTask(plugin, () -> {
            ItemStack updatedItem = event.getInventory().getItem(slot);
            if (updatedItem == null || updatedItem.getType().isAir()) {
                pdc.remove(key); // They took the item out
                if (slot == OFFER_SLOT) pdc.remove(TradeChestPlugin.OFFER_UUID); // Clear offer lock
            } else {
                pdc.set(key, PersistentDataType.BYTE_ARRAY, updatedItem.serializeAsBytes()); // They put item in
            }
            chest.update();
            refreshGUI(event.getInventory(), chest); // Update the visual player heads
        });
    }

    // --- HELPER 2: Completes or Cancels the Trade ---
    private void finishTrade(Chest chest, PersistentDataContainer pdc, Player owner, boolean success) {
        ItemStack ownerItem = ItemStack.deserializeBytes(pdc.get(TradeChestPlugin.OWNER_ITEM, PersistentDataType.BYTE_ARRAY));
        ItemStack offerItem = ItemStack.deserializeBytes(pdc.get(TradeChestPlugin.OFFER_ITEM, PersistentDataType.BYTE_ARRAY));
        UUID offererUuid = UUID.fromString(pdc.get(TradeChestPlugin.OFFER_UUID, PersistentDataType.STRING));

        pdc.remove(TradeChestPlugin.OFFER_ITEM);
        pdc.remove(TradeChestPlugin.OFFER_UUID);
        if (success) pdc.remove(TradeChestPlugin.OWNER_ITEM);
        chest.update();

        if (success) {
            safeGiveOrDrop(owner.getUniqueId(), offerItem, chest.getLocation());
            safeGiveOrDrop(offererUuid, ownerItem, chest.getLocation());
            owner.sendMessage(Component.text("Trade completed successfully!", NamedTextColor.GREEN));
        } else {
            safeGiveOrDrop(offererUuid, offerItem, chest.getLocation());
            owner.sendMessage(Component.text("Trade scrubbed. Item returned to offerer.", NamedTextColor.YELLOW));
        }
        owner.closeInventory();
    }

    // --- HELPER 3: Draws the GUI & Player Heads ---
    private void refreshGUI(Inventory gui, Chest chest) {
        PersistentDataContainer pdc = chest.getPersistentDataContainer();
        gui.clear();

        // 1. Draw Background Panes
        ItemStack bg = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        bg.editMeta(m -> m.displayName(Component.text(" ")));
        for (int i = 0; i < 9; i++) if (i != OWNER_SLOT && i != OFFER_SLOT) gui.setItem(i, bg);

        // 2. Load Owner's Stuff
        UUID ownerId = UUID.fromString(pdc.get(TradeChestPlugin.OWNER_UUID, PersistentDataType.STRING));
        gui.setItem(OWNER_HEAD, getHead(ownerId, "Chest Owner"));
        if (pdc.has(TradeChestPlugin.OWNER_ITEM, PersistentDataType.BYTE_ARRAY)) {
            gui.setItem(OWNER_SLOT, ItemStack.deserializeBytes(pdc.get(TradeChestPlugin.OWNER_ITEM, PersistentDataType.BYTE_ARRAY)));
        }

        // 3. Load Offerer's Stuff
        if (pdc.has(TradeChestPlugin.OFFER_UUID, PersistentDataType.STRING)) {
            UUID offerId = UUID.fromString(pdc.get(TradeChestPlugin.OFFER_UUID, PersistentDataType.STRING));
            gui.setItem(OFFER_HEAD, getHead(offerId, "Offerer"));
            if (pdc.has(TradeChestPlugin.OFFER_ITEM, PersistentDataType.BYTE_ARRAY)) {
                gui.setItem(OFFER_SLOT, ItemStack.deserializeBytes(pdc.get(TradeChestPlugin.OFFER_ITEM, PersistentDataType.BYTE_ARRAY)));
            }
        } else {
            gui.setItem(OFFER_SLOT, null); // Keep open for new offers
            gui.setItem(OFFER_HEAD, bg);
        }
    }

    private ItemStack getHead(UUID uuid, String title) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        meta.displayName(Component.text(title + ": " + Bukkit.getOfflinePlayer(uuid).getName(), NamedTextColor.AQUA));
        head.setItemMeta(meta);
        return head;
    }

    private void safeGiveOrDrop(UUID targetId, ItemStack item, Location fallback) {
        Player target = Bukkit.getPlayer(targetId);
        if (target != null && target.isOnline()) {
            HashMap<Integer, ItemStack> leftover = target.getInventory().addItem(item);
            leftover.values().forEach(left -> fallback.getWorld().dropItemNaturally(fallback, left));
        } else {
            fallback.getWorld().dropItemNaturally(fallback, item);
        }
    }

    private boolean isTradeChest(Block block) {
        if (block == null || !(block.getState() instanceof Chest chest)) return false;
        return chest.getPersistentDataContainer().has(TradeChestPlugin.IS_TRADE_CHEST, PersistentDataType.BYTE);
    }
    
    // --- 5. ENVIRONMENTAL PROTECTION ---

    // Protects against TNT, Creepers, Ghasts, Withers, etc.
    @EventHandler
    public void onEntityExplode(org.bukkit.event.entity.EntityExplodeEvent event) {
        // Remove any Trade Chests from the list of blocks that are about to be blown up
        event.blockList().removeIf(this::isTradeChest);
    }

    // Protects against Bed explosions (in Nether/End) and Respawn Anchors
    @EventHandler
    public void onBlockExplode(org.bukkit.event.block.BlockExplodeEvent event) {
        event.blockList().removeIf(this::isTradeChest);
    }

    // Protects against the chest burning to ash from Lava or Fire
    @EventHandler
    public void onBlockBurn(org.bukkit.event.block.BlockBurnEvent event) {
        if (isTradeChest(event.getBlock())) {
            event.setCancelled(true);
        }
    }
}