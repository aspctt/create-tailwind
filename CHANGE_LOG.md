# Create: Tailwind :: Change Log
- - -

* Unreleased: 1.0.0
	+ Project setup: Stonecutter build with a NeoForge 1.21.1 target, and a Fabric 1.21.1 target prepared but not declared until Create's Fabric port reaches 1.21.1
	+ Build against NeoForge 21.1.250 and Create 6.0.10
	+ Name the jar CreateTailwind-<version>+<minecraft version>-<loader>.jar, and ship LICENSE and NOTICE inside it
	+ Add jetpack flight, adapted from Create: Backtank is Jetpack: a worn backtank grants creative-style flight that draws air from it and cuts out when it runs dry
	+ Count a backtank worn in the chest slot, a Curios back slot, or an Accessories back slot, and make backtanks equippable in both back slots
	+ Draw a backtank worn in a Curios or Accessories back slot on the player's back, the way Create draws one in the chest slot
	+ Keep jetpack flight at the normal flying speed by not letting the player sprint while jetpack flying
	+ Add a server config for the air cost per tick, fall damage immunity, and removing Invisibility while flying
	+ Leave a short campfire smoke trail from the bottom of the backtank
	+ Give jetpack flight an engine sound that follows each flying player: a rush of air that grows with speed, Create's cogwheel rumble and a lowered beacon hum behind it, and Create's steam hiss on takeoff and about once a second, fading in and out on takeoff and landing
	+ Add a client config to turn the smoke trail and engine sound off, set the trail's lifetime and density, and set the engine volume
	+ Add a config screen built with Yet Another Config Lib, optional at runtime, covering the client config and, in singleplayer, the server config
