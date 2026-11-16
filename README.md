# <p align=center style="text-align: center;"> <img alt="Banner" src="https://i.ibb.co/QvKbNXW6/Create-Tailwind-Title.png"> </p>

<p align="center" style="text-align: center;">
	<img alt="Available for" src="https://img.shields.io/badge/Available_for-1.21.1-blue">
	<img alt="Requires" src="https://img.shields.io/badge/Requires-Create_6.0.x-blueviolet">
	<img alt="License" src="https://img.shields.io/badge/License-GPL--3.0--only_code,_ARR_assets-red">
</p>

<p align="center" style="text-align: center;">
	<img alt="Available for NeoForge" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/neoforge_vector.svg">
	<img alt="Won't support Forge" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/unsupported/forge_vector.svg">
</p>

<p align="center" style="text-align: center;">
	<a alt="Buy Me a Coffee" href="https://buymeacoffee.com/aspctt"><img alt="Buy Me a Coffee" height="40" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/donate/buymeacoffee-singular_vector.svg"></a>
</p>

<p align="center" style="text-align: center;">
	<a alt="Available on GitHub" href="https://github.com/aspctt/create-tailwind"><img alt="Available on GitHub" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/github_vector.svg"></a>
	<a alt="Available on Modrinth" href="https://modrinth.com/mod/create-tailwind"><img alt="Available on Modrinth" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/modrinth_vector.svg"></a>
	<a alt="Available on CurseForge" href="https://www.curseforge.com/minecraft/mc-mods/create-tailwind"><img alt="Available on CurseForge" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/curseforge_vector.svg"></a>
</p>

## Description

Create: Tailwind adds compact jetpacks and boosters that run on compressed air. Worn alone, they let you fly the way creative mode does. Worn with an elytra, they act as a booster instead and push your glide forward.

The mod is in early development, and so far it has one jetpack, the Netherite Jetpack, which does both:

* Craft the Netherite Jetpack from a netherite backtank, a nether star, two sturdy sheets, two brass sheets and two propellers.
* Wear it in the chest slot, or in a back slot from [Curios](https://modrinth.com/mod/curios) or [Accessories](https://modrinth.com/mod/accessories), and double-tap jump to fly the way creative mode does. It shows on your back whichever slot it is in. With Accessories it follows the body as it is actually rendered, so reshaped player models such as Fresh Animations keep it in place.
* The jetpack holds compressed air like Create's backtank and charges the same way. Set it down, turn the shaft input on top and pick it back up with an empty hand. It keeps its air, enchantments and name through placing and breaking, and takes Create's Capacity enchantment.
* A glass gauge on each side shows the air inside, worn or placed, and fills as the jetpack charges.
* Flight draws air from the jetpack and cuts out when the tank runs dry. Create's diving helmet and air-powered tools can draw on the same air.
* While you fly or boost, the time your air lasts shows beside the hotbar, where Create shows a backtank's air while diving. It flashes red once the tank runs low.
* Jetpack flight stays at the normal flying speed, and sprinting does not speed it up.
* Wear an elytra as well and the jetpack stops granting flight and boosts your glide instead. Hold forward while gliding and it pushes you the way you are looking, up to a top speed, drawing air as it goes. A broken elytra does not count, so the jetpack flies on its own again.
* Either one can take the chest slot while the other goes in a slot from Curios or Accessories, or both can go in slots. The elytra fits a Curios back slot with [Elytra Slot](https://modrinth.com/mod/elytra-slot), and Create: Tailwind then adds a second back slot so both fit. With Accessories it goes in the cape slot through Accessories Compat: Vanilla.
* The boost's acceleration and top speed default to Do a Barrel Roll's thrust settings and can be changed in the server config. Do a Barrel Roll is not needed. When it is installed, the boost works with its flight controls and its own thrust stands aside while the jetpack is boosting.
* Each nozzle blows a glowing jet of white air that grows with speed, while amber sparks fall from the nozzles and the exhaust leaves a short smoke trail. The nozzle openings glow white hot with a soft halo round them while the jetpack runs, then cool through ember colours back to black over about eight seconds after it stops.
* The engine sounds while you fly or boost. It is a rush of air that grows with speed, with Create's cogwheel rumble and a low hum behind it, and the tank vents a steam hiss on takeoff and every few seconds after, more often the faster you go.
* Fall damage still applies, and flying or boosting removes Invisibility, since the exhaust gives you away. Both can be changed in the server config, along with the air cost.
* The config screen opens from the mod list and needs [YACL](https://modrinth.com/mod/yacl). Each player can turn off the flame and glow, the sparks, the smoke trail and the engine sound. They can also shorten or thin out the trail and set the engine volume. In a singleplayer world that is not open to LAN, the screen edits the server settings too. Without YACL the mod still works, and the config files can be edited by hand.

Version history is in [CHANGELOG.md](./CHANGELOG.md), and the text used on the mod pages is in [DESCRIPTION.md](./DESCRIPTION.md).

## Installation

Place the jar in the mods folder of your Minecraft instance, alongside NeoForge and Create.

**Remove any old versions before installing.**

## Dependencies

* Minecraft 1.21.1
* NeoForge 21.1.250 or newer
* Create 6.0.10 or newer, below 6.1.0
* Optional: Curios or Accessories, to wear the jetpack in a back slot
* Optional: Elytra Slot (with Curios) or Accessories Compat: Vanilla (with Accessories), to wear an elytra in a slot alongside the jetpack
* Optional: Do Another Barrel Roll, which the elytra boost works alongside
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

The Fabric target compiles only the `fabric` package, which holds its entry point. Everything else is written against NeoForge and Create's NeoForge build, and waits for a Create Fabric release on 1.21.1. Declaring the target is one line in `settings.gradle.kts`, and the source can use `//? if fabric` and `//? if neoforge` where the two loaders differ.

### Releasing

`./gradlew publishMods` uploads every target with its sources jar to Modrinth and CurseForge, and creates one GitHub release for the version with the main jars attached, using [mod-publish-plugin](https://modmuss50.github.io/mod-publish-plugin/). Each platform gets the targets one at a time, from the oldest Minecraft version to the newest. The notes come from the version's section of `CHANGELOG.md`, and the portals list each jar for the Minecraft versions in `release_minecraft_versions`.

Releases normally go out through the Release workflow in the Actions tab, started by hand with Run workflow. Push the version's tag first, since the workflow refuses to run unless the tag exists and marks the commit it is building. Ticking "dry run" shows what would be uploaded without sending anything. The workflow reads `MODRINTH_TOKEN` and `CURSEFORGE_TOKEN` from the repository's Actions secrets and uses its own token for GitHub. `MODRINTH_TOKEN` is a Modrinth personal access token that can create, read and write versions, and `CURSEFORGE_TOKEN` is a CurseForge API token.

To publish from a local machine instead, set those two tokens and a `GITHUB_TOKEN` that can write the repository's contents as environment variables, then run `./gradlew publishMods`. With any of the three missing, the run is a dry run, which uploads nothing and writes what it would have sent under each project's `build/publishMods`.

## Licensing

The code of Create: Tailwind is licensed under the [GNU General Public License v3.0 only](./LICENSE). The assets, meaning everything under `src/main/resources/assets/createtailwind` and the mod icon, are **All Rights Reserved** and are not covered by that licence.

* Modpacks may include the mod **by reference**, the way a CurseForge manifest or a Modrinth index does, so the launcher fetches it from an official page. Re-hosting, bundling or altering the jar is not permitted.

The copyrights and trademarks are set out in [NOTICE](./NOTICE), which also credits third-party code and will list any third-party models and textures with the permission they are used under.

## Credits

### Core Team

* aspctt: code and project lead

### Adapted from

* [Create: Backtank is Jetpack](https://github.com/mchhui/Create-BacktankIsJetpack) by Hueihuea, which the jetpack's flight is adapted from
* [Do a Barrel Roll](https://github.com/enjarai/do-a-barrel-roll) by enjarai, whose elytra thrust the boost is adapted from

### Built against

* [Create](https://github.com/Creators-of-Create/Create) by the Create Team, whose compressed air powers this mod's jetpacks and boosters
* [NeoForge](https://github.com/neoforged/NeoForge) by NeoForged, and the MDK this project started from
* [Just Enough Items](https://github.com/mezz/JustEnoughItems) by mezz, used in the development environment
