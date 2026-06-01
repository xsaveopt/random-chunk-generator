# RandomChunkGenerator

A Minecraft PaperMC plugin that lets the world generate normally — vanilla terrain and vanilla biomes — then randomly corrupts scattered chunks so the world is suddenly broken in places.

## How it works

The plugin does **not** replace world generation. Vanilla builds the normal world (real terrain, caves, structures, and biomes). When a new chunk finishes generating, the plugin rolls a per-chunk seed and, with a configurable chance, corrupts that chunk using one of three styles:

- **Chaotic heights** — jagged stone walls and pits replace the terrain.
- **Holes** — the chunk is voided out down to bedrock, leaving a sudden gap.
- **Scrambled blocks** — existing blocks are replaced with random materials.

Corruption is deterministic per chunk (world seed + chunk coordinates), so the same chunk always breaks the same way, and only happens once when the chunk is first generated.

## Compatibility

Uses only stable Bukkit/Paper API (events, `Chunk`, `Block`, `Material`) — no custom generator and no dependency on the volatile `Biome` registry. Built against `1.21.11-R0.1-SNAPSHOT` with `api-version: 1.21`, so it runs on 1.21+ and forward.

## How to Build

```bash
mvn clean package
```

The built JAR will be at `target/random-chunk-generator-<version>.jar`.

## How to Use

Drop the JAR into your server's `plugins/` folder and restart. It applies to every world by default — no `bukkit.yml` generator entry needed.

## Configuration

`plugins/RandomChunkGenerator/config.yml`:

```yaml
corruption-chance: 0.15        # 0.0-1.0 chance a newly generated chunk is corrupted

styles:
  chaotic-heights: true
  holes: true
  scrambled-blocks: true

worlds: []                     # empty = all worlds; otherwise only the listed world names
```
