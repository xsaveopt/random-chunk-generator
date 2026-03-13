package me.sratabix.randomchunk;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexNoiseGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class RandomChunkGenerator extends ChunkGenerator {

    private SimplexNoiseGenerator stabilityNoise;
    private SimplexNoiseGenerator terrainNoise;

    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        if (stabilityNoise == null) {
            stabilityNoise = new SimplexNoiseGenerator(worldInfo.getSeed());
            terrainNoise = new SimplexNoiseGenerator(worldInfo.getSeed() + 1);
        }

        // Determine if this chunk is "Stable" or "Chaotic"
        // Scale 0.01 means regions change every ~100 chunks
        double stability = stabilityNoise.noise(chunkX * 0.05, chunkZ * 0.05);
        boolean isStable = stability > 0.2; // roughly 40% of the world will be stable

        int minHeight = worldInfo.getMinHeight();
        int maxHeight = worldInfo.getMaxHeight();
        
        Material fillMaterial;
        int targetBaseHeight;
        
        switch (worldInfo.getEnvironment()) {
            case NETHER:
                fillMaterial = Material.NETHERRACK;
                targetBaseHeight = 64;
                break;
            case THE_END:
                fillMaterial = Material.END_STONE;
                targetBaseHeight = 48;
                break;
            default:
                fillMaterial = Material.STONE;
                targetBaseHeight = 64;
                break;
        }

        if (isStable) {
            // "Normal" Smooth Terrain logic
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    double realX = (chunkX * 16) + x;
                    double realZ = (chunkZ * 16) + z;
                    
                    // Simple heightmap using world-seeded noise
                    double noiseValue = terrainNoise.noise(realX * 0.02, realZ * 0.02);
                    int height = (int) (minHeight + targetBaseHeight + (noiseValue * 20));

                    for (int y = minHeight; y < height; y++) {
                        chunkData.setBlock(x, y, z, fillMaterial);
                    }
                    
                    addBedrock(worldInfo, chunkData, x, z, minHeight, new Random((long) (realX * 31 + realZ)));
                }
            }
        } else {
            // "Chaotic" Per-Chunk logic
            long chunkSeed = worldInfo.getSeed() ^ ((long) chunkX * 0x7E3779B9L + (long) chunkZ * 0x9E3779B1L);
            Random chunkRandom = new Random(chunkSeed);
            int baseHeight = minHeight + targetBaseHeight + chunkRandom.nextInt(32);

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int localHeight = baseHeight + chunkRandom.nextInt(3);
                    for (int y = minHeight; y < localHeight; y++) {
                        chunkData.setBlock(x, y, z, fillMaterial);
                    }
                    addBedrock(worldInfo, chunkData, x, z, minHeight, chunkRandom);
                }
            }
        }
    }

    private void addBedrock(WorldInfo worldInfo, ChunkData chunkData, int x, int z, int minHeight, Random random) {
        if (worldInfo.getEnvironment() != World.Environment.THE_END) {
            for (int y = minHeight; y < minHeight + 5; y++) {
                if (y == minHeight || random.nextInt(5) > 0) {
                    chunkData.setBlock(x, y, z, Material.BEDROCK);
                }
            }
        }
    }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        int minHeight = worldInfo.getMinHeight();
        int maxHeight = worldInfo.getMaxHeight();

        Material topBlock;
        Material fillerBlock;
        
        switch (worldInfo.getEnvironment()) {
            case NETHER:
                topBlock = Material.NETHERRACK;
                fillerBlock = Material.NETHERRACK;
                break;
            case THE_END:
                topBlock = Material.END_STONE;
                fillerBlock = Material.END_STONE;
                break;
            default:
                topBlock = Material.GRASS_BLOCK;
                fillerBlock = Material.DIRT;
                break;
        }

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = maxHeight - 1; y >= minHeight; y--) {
                    if (!chunkData.getType(x, y, z).isAir()) {
                        chunkData.setBlock(x, y, z, topBlock);
                        for (int i = 1; i <= 3 && y - i >= minHeight; i++) {
                            chunkData.setBlock(x, y - i, z, fillerBlock);
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override public boolean shouldGenerateCaves() { return true; }
    @Override public boolean shouldGenerateDecorations() { return true; }
    @Override public boolean shouldGenerateStructures() { return true; }
    @Override public boolean shouldGenerateMobs() { return true; }
}
