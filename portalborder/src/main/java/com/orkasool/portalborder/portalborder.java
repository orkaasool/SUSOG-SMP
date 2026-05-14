package com.orkasool.portalborder;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class portalborder extends JavaPlugin implements Listener {

    // Set your border limit here
    private final int BORDER_LIMIT = 2500;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("PortalBorder enabled. Nether portal limit set to +-" + BORDER_LIMIT);
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        World world = event.getWorld();

        // We only care about portal generation in the Nether
        if (world.getEnvironment() == World.Environment.NETHER) {
            
            if (event.getBlocks().isEmpty()) return;

            // Get the location of the first block in the portal structure
            Location loc = event.getBlocks().get(0).getLocation();
            
            if (isOutOfBounds(loc.getBlockX(), loc.getBlockZ())) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Checks if the given X and Z coordinates exceed the defined limit.
     */
    private boolean isOutOfBounds(int x, int z) {
        return Math.abs(x) > BORDER_LIMIT || Math.abs(z) > BORDER_LIMIT;
    }
}