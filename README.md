# Disable Entity

A client-side Fabric mod for Minecraft 1.21.8 that selectively disables expensive visual rendering to improve performance.

## Overview

Disable Entity is a client-side optimization mod that reduces rendering overhead by selectively skipping expensive visual work. It operates entirely on the client side, leaving server logic, packets, ticking, and collision unaffected. The mod integrates with Minecraft's render pipeline at the dispatcher level, ensuring broad compatibility with other optimization mods.

## Features

### Entity Management
- Hide entities by category (players, hostile mobs, passive mobs, items, projectiles, armor stands, etc.)
- Configurable per-entity-type controls
- Preserves essential gameplay interactions

### Visual Optimizations
- **Entity Shadows**: Suppress shadow rendering for all entities
- **Nametags**: Hide nametags for players, mobs, and armor stands
- **Particles**: Filter particles with whitelist/blacklist modes across 9 categories
- **Block Entities**: Hide expensive block entities (chests, signs, beacons, spawners, etc.)
- **Dynamic Block States**: Freeze or simplify block state updates (redstone, pistons, doors, rails, etc.)

### Distance-Based Culling
- Configurable render distance for entities (0-512 blocks)
- Configurable render distance for block entities (0-256 blocks)
- Reduce overdraw in large modded worlds

### World Rendering
- Disable clouds, weather effects, vignette, hand bob, fog, and overlays

### Presets
Four built-in presets for quick optimization:
- **Custom**: User-defined settings
- **Balanced**: Recommended settings with player visibility maintained
- **Performance**: Aggressive optimization hiding all non-essential entities
- **Aggressive**: Maximum performance with 32-block render distance

## Requirements

| Requirement | Version |
|-------------|---------|
| Minecraft   | 1.21.8  |
| Fabric Loader | >= 0.19.2 |
| Java        | >= 21    |
| Fabric API  | *        |
| Cloth Config | >= 19.0.147 |

## Installation

1. Install [Fabric Loader](https://fabricmc.net/) for Minecraft 1.21.8
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Install [Cloth Config](https://modrinth.com/mod/cloth-config)
4. Download the latest release
5. Place the `.jar` file in your `mods` folder
6. Launch the game

## Configuration

### GUI Configuration
Access settings through **Mod Menu** in the main menu or in-game pause menu.

### Manual Configuration
The config file is located at:
```
config/disable-entity.json
```

### Keybinds
Toggle features using in-game keybinds configured in Controls.

### Import/Export
Export your configuration to JSON and import it on other installations.

## Compatibility

Disable Entity is designed to work alongside popular optimization mods:

| Mod | Status |
|-----|--------|
| Sodium | Compatible |
| Iris | Compatible |
| EntityCulling | Compatible |
| Lithium | Compatible |
| MoreCulling | Compatible |
| ImmediatelyFast | Compatible |
| Enhanced Block Entities | Compatible |
| Dynamic FPS | Compatible |
| Indium | Compatible |
| Continuity | Compatible |

The mod uses vanilla-dispatcher level hooks to remain non-invasive and compatible with mods that replace rendering systems.

## Building from Source

### Prerequisites
- Java 21 or higher
- Gradle 9.x

### Build Commands

```bash
# Clone the repository
git clone https://github.com/newnonsick/disable-entity.git
cd disable-entity

# Build the mod
./gradlew build

# Run the development environment
./gradlew runClient
```

The built JAR will be located at `build/libs/`.

## Project Structure

```
disable-entity/
├── src/
│   ├── main/
│   │   ├── java/newnonsick/disable_entity/
│   │   │   ├── config/          # Configuration management
│   │   │   ├── compat/         # Mod compatibility detection
│   │   │   └── util/           # Utility classes
│   │   └── resources/
│   ├── client/                  # Client-side mixins
│   └── test/                    # Unit tests
├── gradle/wrapper/              # Gradle wrapper
├── build.gradle                 # Build configuration
├── gradle.properties            # Project properties
└── LICENSE                      # License file
```

## Testing

The project includes unit tests for configuration management, optimization presets, and utility classes:

```bash
./gradlew test
```

## License

This project is licensed under [MIT](LICENSE).

## Acknowledgments

- [Fabric](https://fabricmc.net/) for the mod loader
- [Cloth](https://shedaniel.gitlab.io/cloth-config/) for the config UI
- [Fabric community](https://fabricmc.net/wiki/community:start) for continued support