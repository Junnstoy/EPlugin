package top.e404.eplugin.mysql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.serialization.Serializable
import top.e404.eorm.EOrm
import top.e404.eorm.dialect.SqlDialect

/**
 * 数据源配置, 支持通过 kotlinx.serialization 从 YAML 等配置文件反序列化
 *
 * @property url JDBC 连接地址
 * @property driver JDBC 驱动类名
 * @property username 数据库用户名
 * @property password 数据库密码
 * @property isAutoCommit 是否自动提交, 默认为 true
 * @property pool 连接池配置
 */
@Serializable
data class DataSourceConfig(
    val url: String,
    val driver: String,
    val username: String,
    val password: String,
    val isAutoCommit: Boolean = true,
    val pool: PoolConfig,
) {
    /** 根据配置生成 [HikariConfig] */
    val hikariConfig by lazy {
        HikariConfig().also {
            it.jdbcUrl = url
            it.driverClassName = driver
            it.username = username
            it.password = password
            it.isAutoCommit = isAutoCommit
            it.maximumPoolSize = pool.maximumPoolSize
            it.minimumIdle = pool.minimumIdle
            it.idleTimeout = pool.idleTimeout
            it.maxLifetime = pool.maxLifetime
            it.connectionTimeout = pool.connectionTimeout
        }
    }

    /** 根据配置生成 [HikariDataSource] */
    val dataSource by lazy { HikariDataSource(hikariConfig) }
}

/**
 * HikariCP 连接池配置
 *
 * @property maximumPoolSize 最大连接数
 * @property minimumIdle 最小空闲连接数
 * @property idleTimeout 空闲连接超时时间(毫秒)
 * @property maxLifetime 连接最大存活时间(毫秒)
 * @property connectionTimeout 连接超时时间(毫秒)
 */
@Serializable
data class PoolConfig(
    val maximumPoolSize: Int,
    val minimumIdle: Int,
    val idleTimeout: Long,
    val maxLifetime: Long,
    val connectionTimeout: Long,
)

/**
 * 数据库访问封装, 桥接 [EOrm] 与 EPlugin 配置体系
 *
 * @property name 数据库实例名称, 用于日志标识
 * @property dialect SQL 方言
 */
open class DB(val name: String, val dialect: SqlDialect) {
    @Volatile
    private lateinit var datasource: HikariDataSource

    /** EOrm 实例, 在 [load] 后可用 */
    val eorm: EOrm get() = _eorm ?: error("DB '$name' 尚未初始化, 请先调用 load()")
    private var _eorm: EOrm? = null

    /** 关闭数据源, 释放连接池资源 */
    open fun stop() {
        _eorm = null
        if (::datasource.isInitialized) datasource.close()
    }

    /**
     * 加载数据源配置, 若已有旧数据源则先关闭
     *
     * @param config 数据源配置
     */
    open fun load(config: DataSourceConfig) {
        if (::datasource.isInitialized) datasource.close()
        datasource = config.dataSource
        _eorm = EOrm(datasource, dialect)
    }

    /**
     * 获取原始 JDBC 连接并执行操作, 连接会在 block 结束后自动关闭
     *
     * @param block 使用连接执行的操作
     * @return block 的返回值
     */
    fun <T> useDb(block: (java.sql.Connection) -> T) = datasource.connection.use(block)
}
