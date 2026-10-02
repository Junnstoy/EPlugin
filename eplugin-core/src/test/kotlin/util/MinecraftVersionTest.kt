package top.e404.eplugin.util

import org.junit.jupiter.api.Test
import top.e404.eplugin.update.EUpdater
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MinecraftVersionTest {
    @Test
    fun legacyVersions() {
        assertEquals(EUpdater.Ver(1, 13, 2), parseMinecraftVersion("1.13.2-R0.1-SNAPSHOT"))
        assertEquals(EUpdater.Ver(1, 21, 0), parseMinecraftVersion("1.21-R0.1-SNAPSHOT"))
        assertEquals(EUpdater.Ver(1, 21, 4), parseMinecraftVersion("1.21.4"))
    }

    @Test
    fun yearBasedVersionsDoNotUseTheBuildNumberAsTheRevision() {
        assertEquals(EUpdater.Ver(26, 3, 0), parseMinecraftVersion("26.3.build.142-beta"))
        assertEquals(EUpdater.Ver(26, 3, 1), parseMinecraftVersion("26.3.1.build.142-stable"))
        assertEquals(EUpdater.Ver(26, 3, 0), parseMinecraftVersion("26.3-R0.1-SNAPSHOT"))
        assertEquals(EUpdater.Ver(26, 3, 0), parseMinecraftVersion("26.3"))
    }

    @Test
    fun invalidAndOverflowingVersionsReturnNull() {
        listOf("", "unknown", "prefix26.3", "26", "26.3garbage", "26.3.1.2",
            "999999999999.3", "26.999999999999", "26.3.999999999999").forEach {
            assertNull(parseMinecraftVersion(it), it)
        }
    }
}
