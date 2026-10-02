@file:Suppress("UNUSED")

package top.e404.eplugin.util

import org.bukkit.Bukkit
import top.e404.eplugin.update.EUpdater

val mcVer by lazy { parseMinecraftVersion(Bukkit.getBukkitVersion()) }

private val minecraftVersionPattern = Regex("^(\\d+)\\.(\\d+)(?:\\.(\\d+))?(?:[-+].*|\\.build\\..*)?$")

/** Parses the Minecraft version, without treating Paper's build number as a revision. */
internal fun parseMinecraftVersion(version: String): EUpdater.Ver? {
    val match = minecraftVersionPattern.matchEntire(version) ?: return null
    return EUpdater.Ver(
        match.groupValues[1].toIntOrNull() ?: return null,
        match.groupValues[2].toIntOrNull() ?: return null,
        match.groups[3]?.value?.toIntOrNull() ?: if (match.groups[3] == null) 0 else return null
    )
}
