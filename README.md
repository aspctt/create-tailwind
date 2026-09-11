# <p align=center> Create: Tailwind </p>

<div align="center">

![Version](https://img.shields.io/badge/Available_for-1.21.1-blue)
![Requires](https://img.shields.io/badge/Requires-Create_6.0.x-blueviolet)
![License](https://img.shields.io/badge/License-Not_decided-lightgrey)

![NeoForge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/neoforge_vector.svg)
![Forge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/unsupported/forge_vector.svg)

</div>

## Description

Create: Tailwind adds compact jetpacks and boosters that run on compressed air. Worn on their own they fly like a jetpack. Worn together with an elytra they act as a booster instead, pushing your glide forward.

The mod is in early development. The jetpack is in, and the booster is still to come:

* Craft the jetpack from a copper backtank, two iron sheets, two brass sheets and two propellers.
* Wear it in the chest slot, or in a back slot from [Curios](https://modrinth.com/mod/curios) or [Accessories](https://modrinth.com/mod/accessories), and you can fly the way creative mode does, by double-tapping jump. It shows on your back in any of them. With Accessories it follows the body as it is actually rendered, so reshaped player models such as Fresh Animations keep it in place.
* The jetpack holds compressed air like Create's backtank and charges the same way: set it down, turn the shaft input on top, and pick it back up with an empty hand. It keeps its air, enchantments and name through placing and breaking, and takes Create's Capacity enchantment.
* Flight draws air from the jetpack and cuts out when it runs dry. Create's diving helmet and air-powered tools can draw on it too.
* Jetpack flight stays at the normal flying speed. Sprinting does not speed it up.
* The exhaust leaves a short smoke trail from both nozzles, and the engine sounds while you fly: a rush of air that grows with speed, Create's cogwheel rumble and a low hum behind it, and a steam hiss as the tank vents.
* Fall damage still applies, and flying removes Invisibility. Both, along with the air cost, can be changed in the server config.
* The config screen, opened from the mod list, needs [YACL](https://modrinth.com/mod/yacl). It lets each player turn the smoke trail and engine sound off, shorten or thin out the trail, and set the engine volume, and in singleplayer it also edits the server settings. Without YACL the mod still works, and the config files can be edited by hand.

Version history is in [CHANGE_LOG.md](./CHANGE_LOG.md), and the text used on the mod pages is in [DESCRIPTION-MODRINTH.md](./DESCRIPTION-MODRINTH.md) and [DESCRIPTION-CURSEFORGE.md](./DESCRIPTION-CURSEFORGE.md).

## Installation

Place the jar in the mods folder of your Minecraft instance, alongside NeoForge and Create.

**REMOVE ANY OLD VERSIONS BEFORE INSTALLING**.

## Dependencies

* Minecraft 1.21.1
* NeoForge 21.1.250 or newer
* Create 6.0.10 or newer, below 6.1.0
* Optional: Curios or Accessories, to wear the jetpack in a back slot
* Optional: Yet Another Config Lib 3.6.0 or newer, for the in-game config screen

## Building

The build is organised with [Stonecutter](https://stonecutter.kikugie.dev/), which compiles one source tree for several targets. Each target is a subproject under `versions/`, named `<minecraft version>-<loader>`, declared in `settings.gradle.kts` and configured by its own `gradle.properties`. Each loader has its own build script, `build.neoforge.gradle.kts` and `build.fabric-remap.gradle.kts`, and `common.gradle.kts` holds what they share.

```
./gradlew build                          # build every declared target
./gradlew :1.21.1-neoforge:build         # build one
./gradlew :1.21.1-neoforge:runClient     # run one, sharing the root run/ directory
./gradlew :1.21.1-neoforge:runData       # regenerate data under src/generated
```

Jars are written to `versions/<target>/build/libs`, named `CreateTailwind-<version>+<minecraft version>-<loader>.jar`.

| Target | Status |
|---|---|
| 1.21.1-neoforge | declared |
| 1.21.1-fabric | prepared, not declared: Create's Fabric port has no 1.21.1 build to compile against yet |

The Fabric target compiles only the `fabric` package, which holds its entry point. Everything else is written against NeoForge and Create's NeoForge build, and waits for a Create Fabric release on 1.21.1. Declaring the target is one line in `settings.gradle.kts`, and `//? if fabric` and `//? if neoforge` are available to the source for the places the two loaders differ.

## Licensing

The licence for Create: Tailwind has not been decided yet. Until it is, the project is **All Rights Reserved**, as stated in [LICENSE](./LICENSE).

Please note the copyrights and trademarks in [NOTICE](./NOTICE), which will also list any third-party models and textures along with the permission they are used under.

## Credits

### Core Team

* aspctt - code, project lead

### Built against

* The Create Team - [Create](https://github.com/Creators-of-Create/Create), whose compressed air powers this mod's jetpacks and boosters
* NeoForged - [NeoForge](https://github.com/neoforged/NeoForge), and the MDK this project started from
* mezz - [Just Enough Items](https://github.com/mezz/JustEnoughItems), used in the development environment
