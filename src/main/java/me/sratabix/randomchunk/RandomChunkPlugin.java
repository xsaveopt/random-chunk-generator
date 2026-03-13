package me.sratabix.randomchunk;

import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public class RandomChunkPlugin extends JavaPlugin {

    private static Logger logger;

    @Override
    public void onEnable() {
        logger = getLogger();
        logger.info("RandomChunkGenerator has been enabled!");
    }

    @Override
    public void onDisable() {
        logger.info("RandomChunkGenerator has been disabled!");
    }

    @Override
    public @Nullable ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, @Nullable String id) {
        return new RandomChunkGenerator();
    }

    @Override
    public @Nullable BiomeProvider getDefaultBiomeProvider(@NotNull String worldName, @Nullable String id) {
        return new RandomBiomeProvider();
    }
}
