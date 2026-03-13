package me.sratabix.randomchunk;

import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomBiomeProvider extends BiomeProvider {

    private final List<Biome> overworldBiomes;
    private final List<Biome> netherBiomes;
    private final List<Biome> endBiomes;

    public RandomBiomeProvider() {
        // Categorize biomes based on their typical dimension
        this.overworldBiomes = Stream.of(Biome.values())
                .filter(b -> !b.name().startsWith("NETHER_") && !b.name().startsWith("THE_END") && b != Biome.SMALL_END_ISLANDS && b != Biome.END_BARRENS && b != Biome.END_HIGHLANDS && b != Biome.END_MIDLANDS && b != Biome.THE_VOID)
                .collect(Collectors.toList());
        
        this.netherBiomes = Stream.of(Biome.values())
                .filter(b -> b.name().startsWith("NETHER_") || b == Biome.SOUL_SAND_VALLEY || b == Biome.WARPED_FOREST || b == Biome.CRIMSON_FOREST || b == Biome.BASALT_DELTAS)
                .collect(Collectors.toList());

        this.endBiomes = Stream.of(Biome.values())
                .filter(b -> b.name().startsWith("THE_END") || b == Biome.SMALL_END_ISLANDS || b == Biome.END_BARRENS || b == Biome.END_HIGHLANDS || b == Biome.END_MIDLANDS)
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
        // Chunk coordinates
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        // Generate a seed based on chunk coordinates and world seed
        long seed = worldInfo.getSeed() ^ ((long) chunkX * 0x4F8921L + (long) chunkZ * 0x1A2B3CL);
        Random random = new Random(seed);

        List<Biome> pool = getBiomesForEnvironment(worldInfo.getEnvironment());
        return pool.get(random.nextInt(pool.size()));
    }

    @Override
    public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
        return getBiomesForEnvironment(worldInfo.getEnvironment());
    }

    private List<Biome> getBiomesForEnvironment(World.Environment environment) {
        switch (environment) {
            case NETHER:
                return netherBiomes;
            case THE_END:
                return endBiomes;
            default:
                return overworldBiomes;
        }
    }
}
