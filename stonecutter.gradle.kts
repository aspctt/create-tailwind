plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.21.1-neoforge"

stonecutter parameters {
    // Available to source files as `//$ minecraft` swaps and in `//? if` conditions.
    swaps["minecraft"] = "\"${node.metadata.version}\";"

    // Which loader this target builds for, so the places the two differ can say so inline with
    // `//? if neoforge {` rather than through a copy of the file per loader.
    val fabric = node.metadata.project.endsWith("-fabric")
    constants.put("fabric", fabric)
    constants.put("neoforge", !fabric)

    // No replacements yet, as there is only one Minecraft version. A pure rename that arrives with a later
    // one belongs here, and anything that changes arity, arguments or semantics belongs in an inline
    // `//? if` where it is visible.
}
