# Disable Entity

<p align="center">
  <a href="#"><img src="https://img.shields.io/badge/Minecraft-1.21.8-62B47A?style=flat-square&logo=minecraft&logoColor=white" alt="Minecraft 1.21.8"></a>
  <a href="#"><img src="https://img.shields.io/badge/Fabric-Client--only-DBD0B0?style=flat-square" alt="Fabric Client-only"></a>
  <a href="#"><img src="https://img.shields.io/badge/Java-%3E%3D%2021-b07219?style=flat-square&logo=openjdk&logoColor=white" alt="Java 21+"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-blue?style=flat-square" alt="MIT License"></a>
  <a href="#"><img src="https://img.shields.io/badge/Version-1.0.0-ff69b4?style=flat-square" alt="Version 1.0.0"></a>
</p>

<p align="center">
  A client-side Fabric mod that selectively disables expensive visual rendering to improve FPS without affecting server logic, networking, or collision.
</p>

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Configuration](#configuration)
- [Compatibility](#compatibility)
- [Building from Source](#building-from-source)
- [Project Structure](#project-structure)
- [Testing](#testing)
- [License](#license)
- [Acknowledgments](#acknowledgments)

---

## Overview

**Disable Entity** operates entirely on the client. It injects into Minecraft's vanilla render dispatchers to skip expensive visual work before it reaches the GPU, leaving server-side ticking, packets, physics, and collision completely untouched. The mod is built with broad compatibility in mind: it never replaces core renderers or framebuffers, making it safe to run alongside most modern optimization mods.

Whether you need to boost FPS in dense modded worlds, clean up visual clutter for cinematics, or automatically tune settings per-server, Disable Entity provides granular, non-destructive controls.

---

## Features

### Global Control
- **Master Toggle** — Enable or pause all optimizations instantly without losing your detailed choices.

### Entity Rendering
Hide specific entity types before expensive per-entity render work begins:
- Players, hostile mobs, passive/ambient mobs
- Item drops, projectiles, experience orbs
- Armor stands, item frames, paintings, leash knots
- Display entities (block, item, text), vehicles (boats, minecarts), miscellaneous
- **Safeguards**: optionally preserve named entities and tamed pets

### Entity Shadows
- Suppress shadow rendering for all entities globally.

### Nametags
- Hide nametags/labels for players, mobs, and armor stands independently.

### Particles
- **Filter modes**: All, Whitelist, or Blacklist
- **Categories**: Block, Item, Smoke, Flame, Explosion, Spell, Water, Redstone, Ambient, Other

### Block Entities
Skip costly block entity renderers before they are queried:
- Chests (including trapped and ender chests), barrels
- Signs (standing and hanging), banners
- Beacons, mob spawners, shulker boxes
- Furnaces, blast furnaces, smokers
- Enchanting tables, miscellaneous block entities

### Dynamic Block State Freezing
Freeze the *visual appearance* of frequently updating blocks to stop constant model rebuilds. Server-side logic remains fully functional.
- Redstone components, pistons, doors/trapdoors, rails
- Sculk sensors, crafters, observers
- Repeaters, comparators, bells
- Other dynamic blocks

### World Rendering
Disable expensive environmental and camera effects:
- Clouds, weather (rain/snow), fog
- Vignette, view bobbing, screen overlays (pumpkin, freezing, portal)

### Distance Culling
Apply lightweight distance limits as an early-out before rendering work begins:
- Entity render distance: **0–512 blocks** (default: 96)
- Block entity render distance: **0–256 blocks** (default: 64)
- Automatically defers to **EntityCulling** or **MoreCulling** when present.

### Optimization Presets
Four built-in presets for quick setup. Manual changes automatically switch the preset back to **Custom**.

| Preset | Description |
|--------|-------------|
| **Custom** | Your own manually selected settings |
| **Balanced** | Recommended defaults; preserves player visibility and common block entities |
| **Performance** | Aggressive hiding of non-essential entities; 64-block entity / 48-block block-entity culling |
| **Aggressive** | Maximum optimization; 32-block render distance for both entities and block entities |

### Adaptive Tuning
Monitors your average FPS and prompts you to escalate to a higher optimization preset if frame rate stays below a configurable target for a set number of seconds.

### Runtime Hotkeys
11 configurable keybinds for instant in-game toggling (default keys shown):

| Key | Action |
|-----|--------|
| `Insert` | Toggle all optimizations |
| `Home` | Toggle entity hiding |
| `End` | Toggle particle hiding |
| `Page Up` | Toggle block entity hiding |
| `Page Down` | Toggle nametag hiding |
| `F7` | Toggle block state freezing |
| `F8` | Toggle world rendering |
| `F6` | Toggle performance overlay |
| `Delete` | Reset to defaults |
| `Print Screen` | Copy configuration to clipboard |
| `Scroll Lock` | Paste configuration from clipboard |

### Performance Overlay
A small HUD element showing how many entities, block entities, particles, nametags, shadows, world features, and frozen block states were skipped each frame.

### FPS Delta Feedback
Optional temporary on-screen message that estimates the FPS change ~2 seconds after toggling a feature.

### Server Profiles
Automatically save and load configuration profiles per multiplayer server address. Profiles are stored separately from your global config.

### Config Import / Export
Copy your active configuration as JSON to the system clipboard and paste it into another installation.

---

## Requirements

| Dependency | Version |
|------------|---------|
| Minecraft | **1.21.8** |
| Fabric Loader | **>= 0.19.2** |
| Java | **>= 21** |
| Fabric API | any |
| Cloth Config | **>= 19.0.147** |

**Suggested (optional):**
- [ModMenu](https://modrinth.com/mod/modmenu) — provides the in-game settings screen entrypoint.

> This mod is **client-only**. It does not need to be installed on the server and has no server-side component.

---

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft **1.21.8**.
2. Download and place the following in your `mods` folder:
   - [Fabric API](https://modrinth.com/mod/fabric-api)
   - [Cloth Config](https://modrinth.com/mod/cloth-config)
   - **Disable Entity** `.jar`
3. Launch the game.

---

## Configuration

### In-Game GUI (Recommended)
If **ModMenu** is installed, open **Options &rarr; Mods &rarr; Disable Entity &rarr; Configure** from the main menu or pause screen.

### Manual Configuration
The configuration file is located at:

```
.minecraft/config/disable-entity.json
```

Server profiles are stored at:

```
.minecraft/config/disable-entity-profiles.json
```

- `configVersion` is managed automatically (current: `4`).
- Corrupted configs are backed up to `disable-entity.json.broken` and reset to defaults.

### Presets
Select a preset in the GUI or apply one programmatically. The active preset is persisted; manual changes revert the preset to **Custom**.

### Keybinds
All runtime toggles can be rebound in **Options &rarr; Controls &rarr; Key Binds &rarr; Disable Entity**.

---

## Compatibility

Disable Entity is designed to coexist with major optimization mods by staying on vanilla dispatcher-level hooks and never replacing renderers or framebuffers.

| Mod | Compatibility Notes |
|-----|---------------------|
| **Sodium** | Uses dispatcher-level cancellation only |
| **Iris** | No framebuffer or shader pipeline hooks |
| **EntityCulling** | Detected; mod avoids custom occlusion logic and disables overlapping distance culling |
| **Lithium** | No server-side behavior modified |
| **MoreCulling** | Detected; stays on vanilla dispatcher hooks and disables overlapping distance culling |
| **ImmediatelyFast** | No framebuffer or buffer-system hooks |
| **Enhanced Block Entities** | Block-entity filtering remains non-invasive |
| **Dynamic FPS** | Does not alter focus or tick-rate behavior |
| **Indium** | Block-state freezing uses dispatcher-level hooks |
| **Continuity** | Connected textures remain active on frozen block states |

---

## Building from Source

### Prerequisites
- Java **21** or newer
- Gradle (wrapper included)

### Commands

```bash
# Clone the repository
git clone https://github.com/newnonsick/disable-entity.git
cd disable-entity

# Build the mod JAR
./gradlew build

# Run the development client
./gradlew runClient

# Run unit tests
./gradlew test
```

The built artifact will be at:

```
build/libs/disable-entity-1.0.0.jar
```

> **Note:** IntelliJ IDEA is not fully compatible with the version of fabric-loom used in this project. Use command-line Gradle for reliable builds.

---

## Project Structure

```
disable-entity/
├── src/
│   ├── main/
│   │   ├── java/newnonsick/disable_entity/
│   │   │   ├── config/          # Config model, manager, presets
│   │   │   ├── compat/          # Mod-compatibility detection
│   │   │   └── util/            # Shared utilities (particle categories, block registry)
│   │   └── resources/
│   │       ├── assets/disable-entity/   # Textures, language, icon
│   │       ├── fabric.mod.json
│   │       └── disable-entity.client.mixins.json
│   ├── client/
│   │   ├── java/newnonsick/disable_entity/
│   │   │   ├── client/          # GUI, keybinds, ModMenu entrypoint
│   │   │   ├── mixin/           # Client-side mixins (entity, particle, world, block states)
│   │   │   └── util/            # RenderRules, PerformanceTracker, AdaptiveTuningManager
│   │   └── resources/
│   └── test/
│       └── java/newnonsick/disable_entity/   # JUnit 5 tests
├── build.gradle
├── gradle.properties
└── LICENSE
```

---

## Testing

The project includes JUnit 5 tests covering:

- Configuration sanitization and migration
- Optimization preset detection and application
- Compatibility decision logic
- Thread safety of the config manager (`ReentrantReadWriteLock`)
- Dynamic block registry behavior

Run tests with:

```bash
./gradlew test
```

---

## License

This project is licensed under the [MIT License](LICENSE).

Copyright (c) 2026 Thitivath Mongkolgittichot

---

## Acknowledgments

- [FabricMC](https://fabricmc.net/) — for the mod loader and tooling
- [Cloth Config](https://shedaniel.gitlab.io/cloth-config/) — for the configuration GUI
- [ModMenu](https://modrinth.com/mod/modmenu) — for in-game mod screen integration
