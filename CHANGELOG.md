# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed

- The jetpack is now the Netherite Jetpack, remodelled in netherite, brass and leather
- Its ID is now `createtailwind:netherite_jetpack`. Jetpacks from 1.0.0 carry over, whether worn, carried, stored or placed

### Fixed

- The placed jetpack's selection box now covers the whole model, and the shaft input as it turns

## [1.0.0] - 2026-09-11

First release.

### Added

- The jetpack, crafted from a copper backtank, two iron sheets, two brass sheets and two propellers, with a full one beside the empty one in the creative tabs
- Jetpack flight, adapted from Create: Backtank is Jetpack: double-tap jump to fly, drawing air until the tank runs dry
- Wearing the jetpack in the chest slot, or a back slot from Curios or Accessories
- Compressed air storage like Create's backtank: charge it placed down with a shaft, and it takes the Capacity enchantment and feeds the diving helmet and air-powered tools
- Elytra boost, adapted from Do a Barrel Roll's thrust: wear an elytra too and hold forward while gliding to speed up
- Elytra support in the chest slot, a Curios back slot from Elytra Slot, or an Accessories cape slot from Accessories Compat: Vanilla
- Do a Barrel Roll support, with its own thrust standing aside while the jetpack boosts
- Exhaust effects: a glowing jet of air, amber sparks and a smoke trail, kept behind the camera in first person
- An engine sound that grows with speed
- Client and server configs, with a config screen when Yet Another Config Lib is installed

[Unreleased]: https://github.com/aspctt/create-tailwind/compare/1.0.0...HEAD
[1.0.0]: https://github.com/aspctt/create-tailwind/releases/tag/1.0.0
