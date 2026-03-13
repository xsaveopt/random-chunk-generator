# RandomChunkGenerator

A Minecraft PaperMC plugin that generates a world where every chunk is seeded differently.

## How to Build

This project uses Maven. To build the plugin, run:

```bash
mvn clean package
```

Once completed, the built JAR file will be located at:
`target/random-chunk-generator-1.0-SNAPSHOT.jar`

## How to Use

To use this generator for a specific world, add it to your `bukkit.yml`:

```yaml
worlds:
  world_name:
    generator: RandomChunkGenerator
```

Replace `world_name` with the name of the world you want to apply the generator to.

## Features

- **Unique Per-Chunk Seed**: Every chunk (16x16 blocks) uses its own seed derived from the world seed and its coordinates.
- **Random Biomes**: Each chunk is assigned a random biome.
- **Seed-Influenced Terrain**: The base height and bedrock patterns are determined by the chunk's unique seed.
