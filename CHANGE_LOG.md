# Create: Tailwind :: Change Log
- - -

* Unreleased: 1.0.0
	+ Project setup: Stonecutter build with a NeoForge 1.21.1 target, and a Fabric 1.21.1 target prepared but not declared until Create's Fabric port reaches 1.21.1
	+ Build against NeoForge 21.1.250 and Create 6.0.10
	+ Name the jar CreateTailwind-<version>+<minecraft version>-<loader>.jar, and ship LICENSE and NOTICE inside it
	+ License the code under GPL-3.0-only, with the assets All Rights Reserved
	+ Add the jetpack, crafted from a copper backtank, two iron sheets, two brass sheets and two propellers, and list a full one beside the empty one in the creative tabs
	+ Add jetpack flight, adapted from Create: Backtank is Jetpack: a worn jetpack grants creative-style flight that draws air from it and cuts out when it runs dry
	+ Count a jetpack worn in the chest slot, a Curios back slot, or an Accessories back slot, and draw it on the player's back in each
	+ Charge the jetpack the way Create charges its backtank: set it down, drive the shaft input on top, and pick it back up with an empty hand. Its air, enchantments and name survive placing and breaking
	+ Store the jetpack's air as Create's backtank air, so it takes the Capacity enchantment and supplies Create's diving helmet and air-powered tools from any of its slots
	+ Warn "Jetpack pressure low" and "Jetpack pressure depleted" in place of Create's backtank wording when air drawn from a jetpack runs down
	+ Keep jetpack flight at the normal flying speed by not letting the player sprint while jetpack flying
	+ Boost an elytra glide with a jetpack worn alongside the elytra, in place of jetpack flight: holding forward pushes the glide the way the player looks, up to a top speed, and draws air. Adapted from Do a Barrel Roll's thrusting, whose acceleration and top speed are the defaults
	+ Count an elytra in the chest slot, a Curios back slot from Elytra Slot, or an Accessories cape slot from Accessories Compat: Vanilla, and give players a second Curios back slot when Elytra Slot is installed so both fit
	+ Keep Do a Barrel Roll's own thrust from adding to the jetpack's boost when both are installed
	+ Tip the exhaust with the body while gliding, so the smoke and sparks leave the nozzles trailing behind the player
	+ Add a server config for the air cost per tick, fall damage immunity, removing Invisibility while flying, and the elytra boost's acceleration and top speed
	+ Leave a short campfire smoke trail from each of the jetpack's two nozzles: each puff shoots down out of the nozzle, then rises at half a campfire's speed
	+ Give jetpack flight an engine sound that follows each flying player: a rush of air that grows with speed, Create's cogwheel rumble and a lowered beacon hum behind it, and a faint steam hiss from Create on takeoff and every few seconds, more often at speed, fading in and out on takeoff and landing
	+ Show a flickering white jet of air under each nozzle of a flying jetpack, glowing in any light and lengthening with speed
	+ Throw amber sparks from the nozzles of a flying jetpack, spawned by each client rather than sent by the server
	+ Add a client config to turn the flame, sparks, smoke trail and engine sound off, set the trail's lifetime and density, and set the engine volume
	+ Add a config screen built with Yet Another Config Lib, optional at runtime, covering the client config and, in singleplayer, the server config
