package me.xsaveopt.randomchunk;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class RandomChunkPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        double chance = config.getDouble("corruption-chance", 0.15);
        boolean chaotic = config.getBoolean("styles.chaotic-heights", true);
        boolean holes = config.getBoolean("styles.holes", true);
        boolean biomeShift = config.getBoolean("styles.biome-shift", true);
        List<String> worldFilter = config.getStringList("worlds");

        getServer().getPluginManager().registerEvents(
                new ChunkCorruptionListener(chance, chaotic, holes, biomeShift, worldFilter, getLogger()), this);

        getLogger().info("RandomChunkGenerator enabled (corruption chance " + chance + ").");
    }

    @Override
    public void onDisable() {
        getLogger().info("RandomChunkGenerator disabled.");
    }
}
