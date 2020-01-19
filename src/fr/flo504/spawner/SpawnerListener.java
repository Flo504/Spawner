package fr.flo504.spawner;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;

public class SpawnerListener implements Listener {

    private final static Language spawnerNames = SpawnerPlugin.getSpawnerNames();

    @EventHandler
    public void onDestroySpawner(BlockBreakEvent e){
        if(e.isCancelled()){
            return;
        }

        final Block block = e.getBlock();

        if(!block.getType().equals(Material.SPAWNER)){
            return;
        }

        e.setExpToDrop(0);
        e.setDropItems(false);

        final Location blockLocation = block.getLocation();

        final ItemStack spawnerItem = new ItemStack(Material.SPAWNER);

        final ItemMeta meta = spawnerItem.getItemMeta();

        final CreatureSpawner spawnerBlock = (CreatureSpawner) block.getState();
        final EntityType spawnedType = spawnerBlock.getSpawnedType();

        meta.setDisplayName(spawnerNames.getMessage(spawnedType.toString().toLowerCase(Locale.ENGLISH)));

        spawnerItem.setItemMeta(meta);

        final ItemStack finalItemStack = TagUtils.applyDataToItemStack(spawnerItem, blockLocation);

        block.getWorld().dropItem(blockLocation, finalItemStack);

    }

    @EventHandler
    public void onPlaceSpawner(BlockPlaceEvent e){

        if(e.isCancelled()){
            return;
        }

        final Block block = e.getBlock();

        if(!block.getType().equals(Material.SPAWNER)){
            return;
        }

        final ItemStack spawnerItem = e.getItemInHand();

        TagUtils.applyDataToBlockSpawner(spawnerItem, block.getLocation());

    }

}
