package me.xsaveopt.randomchunk;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class ChunkCorruptionListener implements Listener {

    private enum Style { CHAOTIC, HOLES, BIOME_SHIFT }

    private final double corruptionChance;
    private final List<Style> styles;
    private final List<String> worldFilter;
    private final List<Biome> biomes;
    private final Logger logger;

    public ChunkCorruptionListener(double corruptionChance, boolean chaoticHeights, boolean holes,
                                   boolean biomeShift, List<String> worldFilter, Logger logger) {
        this.corruptionChance = corruptionChance;
        this.worldFilter = worldFilter;
        this.logger = logger;
        this.styles = new ArrayList<>();
        if (chaoticHeights) styles.add(Style.CHAOTIC);
        if (holes) styles.add(Style.HOLES);
        if (biomeShift) styles.add(Style.BIOME_SHIFT);
        this.biomes = buildBiomes();
        logger.info("Loaded " + biomes.size() + " biomes for biome-shift.");
    }

    private static List<Biome> buildBiomes() {
        List<Biome> result = new ArrayList<>();
        for (Biome biome : Registry.BIOME) {
            if (biome == Biome.THE_VOID || biome == Biome.CUSTOM) {
                continue;
            }
            result.add(biome);
        }
        return result;
    }

    @EventHandler
    public void onChunkPopulate(ChunkPopulateEvent event) {
        if (styles.isEmpty()) {
            return;
        }

        World world = event.getWorld();
        if (!worldFilter.isEmpty() && !worldFilter.contains(world.getName())) {
            return;
        }

        Chunk chunk = event.getChunk();
        long seed = world.getSeed()
                ^ ((long) chunk.getX() * 0x9E3779B97F4A7C15L)
                ^ ((long) chunk.getZ() * 0xC2B2AE3D27D4EB4FL);
        Random random = new Random(seed);

        if (random.nextDouble() >= corruptionChance) {
            return;
        }

        Style style = styles.get(random.nextInt(styles.size()));
        switch (style) {
            case CHAOTIC -> chaoticHeights(world, chunk, random);
            case HOLES -> voidOut(world, chunk);
            case BIOME_SHIFT -> biomeShift(world, chunk, random);
        }
    }

    private void chaoticHeights(World world, Chunk chunk, Random random) {
        int floor = world.getMinHeight() + 1;
        int ceiling = world.getMaxHeight() - 1;
        int sea = world.getSeaLevel();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = (chunk.getX() << 4) + x;
                int worldZ = (chunk.getZ() << 4) + z;

                int target = Math.min(ceiling, sea - 12 + random.nextInt(48));
                int currentTop = Math.min(ceiling, world.getHighestBlockYAt(worldX, worldZ));
                int top = Math.max(target, currentTop);

                for (int y = floor; y <= top; y++) {
                    Material material = y <= target ? Material.STONE : Material.AIR;
                    chunk.getBlock(x, y, z).setType(material, false);
                }
            }
        }
    }

    private void voidOut(World world, Chunk chunk) {
        int floor = world.getMinHeight() + 1;
        int ceiling = world.getMaxHeight() - 1;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = (chunk.getX() << 4) + x;
                int worldZ = (chunk.getZ() << 4) + z;
                int currentTop = Math.min(ceiling, world.getHighestBlockYAt(worldX, worldZ));

                for (int y = floor; y <= currentTop; y++) {
                    Block block = chunk.getBlock(x, y, z);
                    if (!block.getType().isAir()) {
                        block.setType(Material.AIR, false);
                    }
                }
            }
        }
    }

    private void biomeShift(World world, Chunk chunk, Random random) {
        if (biomes.isEmpty()) {
            logger.warning("biome-shift selected but no biomes are available.");
            return;
        }
        Biome biome = biomes.get(random.nextInt(biomes.size()));
        logger.info("biome-shift @ " + chunk.getX() + "," + chunk.getZ() + " -> " + biome.getKey());

        int floor = world.getMinHeight();
        int ceiling = world.getMaxHeight() - 1;
        int baseX = chunk.getX() << 4;
        int baseZ = chunk.getZ() << 4;

        for (int x = 0; x < 16; x += 4) {
            for (int z = 0; z < 16; z += 4) {
                for (int y = floor; y <= ceiling; y += 4) {
                    world.setBiome(baseX + x, y, baseZ + z, biome);
                }
            }
        }

        Material surface = surfaceMaterial(biome);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int top = Math.min(ceiling, world.getHighestBlockYAt(baseX + x, baseZ + z));
                Block block = chunk.getBlock(x, top, z);
                if (!block.getType().isAir()) {
                    block.setType(surface, false);
                }
            }
        }
    }

    private static Material surfaceMaterial(Biome biome) {
        String key = biome.getKey().getKey();
        if (key.contains("snow") || key.contains("frozen") || key.contains("ice") || key.contains("grove")) {
            return Material.SNOW_BLOCK;
        }
        if (key.contains("badlands")) return Material.RED_SAND;
        if (key.contains("desert") || key.contains("beach")) return Material.SAND;
        if (key.contains("mushroom")) return Material.MYCELIUM;
        if (key.contains("swamp") || key.contains("mangrove")) return Material.MUD;
        if (key.contains("crimson")) return Material.CRIMSON_NYLIUM;
        if (key.contains("warped")) return Material.WARPED_NYLIUM;
        if (key.contains("soul")) return Material.SOUL_SOIL;
        if (key.contains("basalt")) return Material.BASALT;
        if (key.contains("nether")) return Material.NETHERRACK;
        if (key.contains("end")) return Material.END_STONE;
        if (key.contains("dripstone")) return Material.DRIPSTONE_BLOCK;
        if (key.contains("lush")) return Material.MOSS_BLOCK;
        if (key.contains("peaks") || key.contains("stony") || key.contains("windswept")) return Material.STONE;
        return Material.GRASS_BLOCK;
    }
}
