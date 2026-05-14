package com.ghastbucket;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ghastbucket extends JavaPlugin implements Listener {

    private NamespacedKey ageKey;
    // set to track players who caught an entity in the current tick
    private final Set<UUID> catchCooldown = new HashSet<>();

    @Override
    public void onEnable() {
        ageKey = new NamespacedKey(this, "ghastling_age");
        getServer().getPluginManager().registerEvents(this, this);
    }

    // catching the Ghastling
    @EventHandler
    public void onCatch(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (event.getRightClicked().getType() == EntityType.HAPPY_GHAST) {
            
            HappyGhast ghastling = (HappyGhast) event.getRightClicked();
            
            if (ghastling.getAge() < 0) {
                Player player = event.getPlayer();
                ItemStack item = player.getInventory().getItemInMainHand();

                if (item.getType() == Material.BUCKET) {
                    event.setCancelled(true);
                    // avoid processing block interactions in the same tick as catching
                    catchCooldown.add(player.getUniqueId());
                    
                    getServer().getScheduler().runTask(this, () -> catchCooldown.remove(player.getUniqueId()));

                    int capturedAge = ghastling.getAge();
                    ghastling.remove();
                    
                    if (item.getAmount() == 1) {
                        player.getInventory().setItemInMainHand(getGhastlingBucket(capturedAge));
                    } else {
                        item.setAmount(item.getAmount() - 1);
                        player.getInventory().addItem(getGhastlingBucket(capturedAge));
                    }
                    player.playSound(player.getLocation(), Sound.ITEM_BUCKET_FILL_POWDER_SNOW, 1.0f, 1.0f);
                    player.updateInventory();
                }
            }
        }
    }

    // releasing the Ghastling
    @EventHandler
    public void onRelease(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        
        // avoid processing block interactions in the same tick as catching
        if (catchCooldown.contains(player.getUniqueId())) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.POWDER_SNOW_BUCKET && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            
            if (meta.getPersistentDataContainer().has(ageKey, PersistentDataType.INTEGER)) {
                
                event.setCancelled(true);

                // retrieve the saved age
                int savedAge = meta.getPersistentDataContainer().get(ageKey, PersistentDataType.INTEGER);

                Location spawnLoc = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation();
                spawnLoc.add(0.5, 0, 0.5); 
                
                // spawn the Happy Ghast
                HappyGhast spawnedEntity = spawnLoc.getWorld().spawn(spawnLoc, HappyGhast.class, ghast -> {
                    ghast.setAge(savedAge);
                });

                if (item.getAmount() == 1) {
                    player.getInventory().setItemInMainHand(new ItemStack(Material.BUCKET));
                } else {
                    item.setAmount(item.getAmount() - 1);
                    player.getInventory().addItem(new ItemStack(Material.BUCKET));
                }
                player.updateInventory(); 
            }
        }
    }

    private ItemStack getGhastlingBucket(int age) {
        ItemStack bucket = new ItemStack(Material.POWDER_SNOW_BUCKET);
        ItemMeta meta = bucket.getItemMeta();
        
        meta.setDisplayName(ChatColor.WHITE + "Bucket of Ghastling");
        
        meta.getPersistentDataContainer().set(ageKey, PersistentDataType.INTEGER, age);
        
        // display the age
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Age: " + age);
        meta.setLore(lore);
        
        bucket.setItemMeta(meta);
        return bucket;
    }
}