plugins {
    id("java-library")
    id("maven-publish")
    id("net.neoforged.moddev") version "2.0.146"
    id("idea")
}

apply(from = rootProject.file("common.gradle.kts"))

fun prop(name: String): String = property(name) as String

repositories {
    // JEI, with ModMaven as a fallback mirror.
    maven("https://maven.blamejared.com/") { name = "Jared's maven" }
    maven("https://modmaven.dev") { name = "ModMaven" }
    // Create, Ponder and Flywheel.
    maven("https://maven.createmod.net") { name = "Create" }
    // Registrate.
    maven("https://maven.ithundxr.dev/snapshots") { name = "ithundxr" }
}

neoForge {
    version = prop("neo_version")

    parchment {
        mappingsVersion = prop("parchment_mappings_version")
        minecraftVersion = prop("parchment_minecraft_version")
    }

    // Access Transformers are automatically detected at
    // src/main/resources/META-INF/accesstransformer.cfg

    runs {
        create("client") {
            client()
            logLevel = org.slf4j.event.Level.DEBUG
            // One run directory shared by every target, so worlds and options survive switching between them.
            gameDirectory = rootProject.file("run")
        }
        create("server") {
            server()
            programArgument("--nogui")
            logLevel = org.slf4j.event.Level.DEBUG
            gameDirectory = rootProject.file("run-server")
        }
        // Writes generated data (recipes, tags, models) into the shared tree rather than this target's
        // directory, so what is generated is what every target and the repository see.
        create("data") {
            data()
            gameDirectory = rootProject.file("run-data")
            programArguments.addAll(
                "--mod", prop("mod_id"), "--all",
                "--output", rootProject.file("src/generated/resources").absolutePath,
                "--existing", rootProject.file("src/main/resources").absolutePath,
            )
        }
    }

    mods {
        create(prop("mod_id")) {
            sourceSet(sourceSets.main.get())
        }
    }
}

// Generated resources sit outside src/main, which is the only tree Stonecutter wires into a target, so they
// are added by path. They are JSON that no target varies, so nothing is lost by skipping the preprocessor.
sourceSets.main.get().resources.srcDir(rootProject.file("src/generated/resources"))

// The other loader's entry point is the one file that cannot compile here. It lives in a package of its own
// so it can simply be left out.
sourceSets.main.get().java.exclude("**/fabric/**")

dependencies {
    // JEI: compile against the API only, and load the full mod in the development run.
    compileOnly("mezz.jei:jei-${prop("minecraft_version")}-neoforge-api:${prop("jei_version")}")
    runtimeOnly("mezz.jei:jei-${prop("minecraft_version")}-neoforge:${prop("jei_version")}")

    // Create, and the libraries its recipe and registration APIs are built on.
    implementation("com.simibubi.create:create-${prop("minecraft_version")}:${prop("create_version")}:slim") {
        isTransitive = false
    }
    implementation("net.createmod.ponder:ponder-neoforge:${prop("ponder_version")}+mc${prop("minecraft_version")}")
    compileOnly("dev.engine-room.flywheel:flywheel-neoforge-api-${prop("minecraft_version")}:${prop("flywheel_version")}")
    runtimeOnly("dev.engine-room.flywheel:flywheel-neoforge-${prop("minecraft_version")}:${prop("flywheel_version")}")
    implementation("com.tterrag.registrate:Registrate:${prop("registrate_version")}")
}

// Expand the declared properties into the mod metadata template. The shared keys come from
// common.gradle.kts; the ones below exist only in neoforge.mods.toml.
@Suppress("UNCHECKED_CAST")
val commonMetadataProperties = extra["commonMetadataProperties"] as Map<String, String>

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = commonMetadataProperties + mapOf(
        "neo_version" to prop("neo_version"),
        "neo_version_range" to prop("neo_version_range"),
        "create_version_range" to prop("create_version_range"),
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from(rootProject.file("src/main/templates/neoforge"))
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}
sourceSets.main.get().resources.srcDir(generateModMetadata)
neoForge.ideSyncTask(generateModMetadata)
