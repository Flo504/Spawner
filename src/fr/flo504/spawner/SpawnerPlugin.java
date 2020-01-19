package fr.flo504.spawner;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnerPlugin extends JavaPlugin implements Listener {

    private static SpawnerPlugin instance;
    private static Language spawnerNames;

    @Override
    public void onEnable() {

        instance = this;

        spawnerNames = new Language(this, "spawnernames.lang");

        Bukkit.getPluginManager().registerEvents(new SpawnerListener(), this);

        super.onEnable();
    }

    public static SpawnerPlugin getInstance() {
        return instance;
    }

    public static Language getSpawnerNames() {
        return spawnerNames;
    }
}
