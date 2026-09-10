plugins {
    id("java-library")
    id("maven-publish")
    id("net.fabricmc.fabric-loom-remap") version "1.17.20"
    id("idea")
}

apply(from = rootProject.file("common.gradle.kts"))

fun prop(name: String): String = property(name) as String

loom {
    runs {
        // One run directory shared by every target, so worlds and options survive switching between them.
        named("client") { runDirectory.set(rootProject.file("run")) }
        named("server") { runDirectory.set(rootProject.file("run-server")) }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${prop("minecraft_version")}")
    // Official Mojang mappings rather than Yarn, so this loader's half of the source reads the same as the
    // NeoForge half and one shared tree compiles for both.
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${prop("loader_version")}")
    // Nothing calls into Fabric API yet, but Create's Fabric port requires it, so it is here for when Create
    // itself can be added alongside.
    modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("fabric_api_version")}")
}

// Only the Fabric entry point compiles here. Everything else is written against NeoForge and Create's
// NeoForge build, so it waits for a Create Fabric build on 1.21.1 to be ported to. The generated recipes are
// left out for the same reason: they name Create's recipe types and this mod's items, neither of which exist
// on this target yet.
sourceSets.main.get().java.include("**/fabric/**")

// Expand the declared properties into the mod metadata template. The shared keys come from
// common.gradle.kts, and fabric.mod.json needs none of its own yet.
@Suppress("UNCHECKED_CAST")
val commonMetadataProperties = extra["commonMetadataProperties"] as Map<String, String>

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    inputs.properties(commonMetadataProperties)
    expand(commonMetadataProperties)
    from(rootProject.file("src/main/templates/fabric"))
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}
sourceSets.main.get().resources.srcDir(generateModMetadata)
