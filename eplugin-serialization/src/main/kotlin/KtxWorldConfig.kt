@file:Suppress("UNUSED")

package top.e404.eplugin.config

import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import top.e404.eplugin.EPlugin

/**
 * 代表一个按世界区分的配置文件, 支持全局配置和每世界独立配置
 *
 * @property plugin 属于的插件
 * @property path 文件在插件数据目录中的路径
 * @property default 默认配置文件
 * @since 1.0.0
 */
abstract class KtxWorldConfig<F, T : WorldConfig<F>>(
    override val plugin: EPlugin,
    override val path: String,
    override val default: ConfigDefault = EmptyConfigDefault,
    override val serializer: KSerializer<in T>,
    override val format: StringFormat = defaultYaml,
) : KtxConfig<T>(plugin, path, default, serializer, format) {
    fun <R> getConfig(world: String, block: F.() -> R): R {
        val wc = config.world[world] ?: config.global
        return wc!!.block()
    }
}
