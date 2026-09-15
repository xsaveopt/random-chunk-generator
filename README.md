# RandomChunkGenerator

RandomChunkGenerator is a Paper plugin that lets the world generate as usual and then corrupts a random scattering of chunks as they are populated for the first time.
Whether a chunk is hit, and how, comes from the world seed and the chunk coordinates, so the same seed always corrupts the same chunks in the same way.

A corrupted chunk gets one of three styles, picked evenly from the ones you have enabled.
Chaotic heights rebuilds every column as stone up to a random height around sea level, which leaves jagged walls and pits.
Holes clears every block above the bottom layer of the world.
Biome shift assigns the whole chunk a random biome from the registry and swaps its surface blocks for ones that roughly match, so the seams with its neighbours stand out.

## Install

The plugin needs a Paper server on Minecraft 1.21 or newer.
Tagged versions publish a built jar on the GitHub Releases page, or you can build one yourself with Maven and Java 21:

```sh
mvn clean package
```

That produces target/random-chunk-generator-{version}.jar, which goes in the server's plugins folder before a restart.

## Configuration

The first start writes plugins/RandomChunkGenerator/config.yml, and changes to it take effect on the next restart.

| Key | Meaning |
| --- | --- |
| `corruption-chance` | Probability from 0.0 to 1.0 that a newly populated chunk is corrupted |
| `styles.chaotic-heights` | Enables the chaotic heights style |
| `styles.holes` | Enables the holes style |
| `styles.biome-shift` | Enables the biome shift style |
| `worlds` | World names the plugin applies to, with an empty list meaning every world |

With every style turned off the plugin leaves all chunks alone.

## License

GPL-2.0, see LICENSE.
