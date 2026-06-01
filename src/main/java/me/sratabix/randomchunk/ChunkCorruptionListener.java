package me.sratabix.randomchunk;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ChunkCorruptionListener implements Listener {

    private enum Style { CHAOTIC, HOLES, SCRAMBLED }

    private static final Set<String> BLACKLIST = Set.of(
            "BEDROCK", "BARRIER", "LIGHT", "SPAWNER", "JIGSAW",
            "COMMAND_BLOCK", "CHAIN_COMMAND_BLOCK", "REPEATING_COMMAND_BLOCK",
            "STRUCTURE_BLOCK", "STRUCTURE_VOID", "END_PORTAL", "END_PORTAL_FRAME",
            "END_GATEWAY", "NETHER_PORTAL", "REINFORCED_DEEPSLATE");

    private final double corruptionChance;
    private final List<Style> styles;
    private final List<String> worldFilter;
    private final List<Material> palette;

    public ChunkCorruptionListener(double corruptionChance, boolean chaoticHeights, boolean holes,
                                   boolean scrambled, List<String> worldFilter) {
        this.corruptionChance = corruptionChance;
        this.worldFilter = worldFilter;
        this.styles = new ArrayList<>();
        if (chaoticHeights) styles.add(Style.CHAOTIC);
        if (holes) styles.add(Style.HOLES);
        if (scrambled) styles.add(Style.SCRAMBLED);
        this.palette = buildPalette();
    }

    private static List<Material> buildPalette() {
        List<Material> palette = new ArrayList<>();
        for (Material material : Material.values()) {
            if (material.isLegacy() || !material.isBlock() || !material.isSolid() || material.isAir()) {
                continue;
            }
            if (BLACKLIST.contains(material.name())) {
                continue;
            }
            palette.add(material);
        }
        return palette;
    }

    @EventHandler
    public void onChunkPopulate(ChunkPopulateEvent event) {
        if (styles.isEmpty() || palette.isEmpty()) {
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
            case SCRAMBLED -> scramble(world, chunk, random);
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

    private void scramble(World world, Chunk chunk, Random random) {
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
                        block.setType(palette.get(random.nextInt(palette.size())), false);
                    }
                }
            }
        }
    }
}
