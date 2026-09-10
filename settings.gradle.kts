pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.neoforged.net/releases") { name = "NeoForged" }
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.8"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

stonecutter {
    create(rootProject) {
        // One target per loader per Minecraft version. The node name carries the loader and the value is
        // the Minecraft version it builds against, so the loader is readable from the target you are on.
        version("1.21.1-neoforge", "1.21.1")
        // Prepared but not declared. versions/1.21.1-fabric, its build script and its fabric.mod.json are
        // in place, but Create's Fabric port has no 1.21.1 build for this mod to compile against. Declaring
        // the target is this one line.
        // version("1.21.1-fabric", "1.21.1")
        vcsVersion = "1.21.1-neoforge"

        // Loom and NeoForge ModDev cannot both own the same source set, and applying either one
        // imperatively costs the typed accessors its configuration block relies on. Giving each loader its
        // own build script keeps both blocks ordinary, and the shared half lives in common.gradle.kts.
        // 1.21.1 still ships obfuscated, so Fabric builds it with Loom's remapping plugin.
        mapBuilds { _, node ->
            if (node.project.endsWith("-fabric")) "build.fabric-remap.gradle.kts" else "build.neoforge.gradle.kts"
        }
    }
}

rootProject.name = "createtailwind"
