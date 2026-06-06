# RandomChunkGenerator

A PaperMC plugin that lets the world generate normally, then randomly corrupts scattered chunks. Corruption is deterministic per chunk (world seed + coordinates) and applied once, when the chunk first generates.

Styles:

- **Chaotic heights** — jagged stone walls and pits.
- **Holes** — the chunk is voided out down to bedrock.
- **Biome shift** — the chunk is reassigned to a random foreign biome, breaking the seams.

## Build

```bash
mvn clean package
```

Output: `target/random-chunk-generator-<version>.jar`. Drop it in `plugins/` and restart. Requires 1.21+.

## Configuration

`plugins/RandomChunkGenerator/config.yml`:

```yaml
corruption-chance: 0.15        # 0.0-1.0 chance a newly generated chunk is corrupted

styles:
  chaotic-heights: true
  holes: true
  biome-shift: true

worlds: []                     # empty = all worlds; otherwise only the listed world names
```
