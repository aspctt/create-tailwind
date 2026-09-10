// Everything both loaders' build scripts need. Applied from each of them rather than being a plugin of its
// own, so the properties it reads resolve against the target being built.

fun prop(name: String): String = project.property(name) as String

fun extraBuildMetadata(): String {
    val buildNumber = System.getenv("GITHUB_RUN_NUMBER") ?: return ""
    return ".build.$buildNumber"
}

// Everything after the "+" is semver build metadata, so it is ignored when versions are compared while
// still naming the jar and showing up in the mods list: CreateAirbound-1.0.0+1.21.1-neoforge.jar.
version = "${prop("mod_version")}+${prop("minecraft_version")}-${prop("mod_loader")}" + extraBuildMetadata()
group = prop("mod_group_id")

// An applied script gets no typed accessors from the applying script's plugins block, so the extensions
// are reached by type rather than by name.
configure<BasePluginExtension> {
    archivesName = prop("mod_archives_name")
}

// Mojang ships Java 21 to end users in 1.21.1.
val javaVersion = 21
configure<JavaPluginExtension> {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
}

// Expand the declared properties into the mod metadata templates. Each loader's script contributes the
// keys only its own metadata file uses.
val commonMetadataProperties: Map<String, String> = mapOf(
    "minecraft_version" to prop("minecraft_version"),
    "minecraft_version_range" to prop("minecraft_version_range"),
    "loader_version_range" to prop("loader_version_range"),
    "mod_id" to prop("mod_id"),
    "mod_name" to prop("mod_name"),
    "mod_license" to prop("mod_license"),
    "mod_version" to project.version.toString(),
    "mod_authors" to prop("mod_authors"),
    // fabric.mod.json wants a JSON array rather than the one comma-separated string the toml takes.
    "mod_authors_json" to prop("mod_authors").split(",").joinToString(", ") { "\"" + it.trim() + "\"" },
    "mod_description" to prop("mod_description"),
    "java_version" to javaVersion.toString(),
)
extra["commonMetadataProperties"] = commonMetadataProperties

// The licence and notices travel inside the jar, suffixed so they cannot collide with another mod's.
tasks.named<Jar>("jar") {
    // A local rather than a script-level value: the configuration cache cannot serialise a lambda that
    // reaches back into the script object, and a top-level val is a member of it.
    val suffix = "_" + prop("mod_archives_name")
    from(rootProject.files("LICENSE", "NOTICE")) {
        rename { it + suffix }
    }
}

configure<PublishingExtension> {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = prop("mod_archives_name")
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = rootProject.file("repo").toURI()
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

configure<org.gradle.plugins.ide.idea.model.IdeaModel> {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
