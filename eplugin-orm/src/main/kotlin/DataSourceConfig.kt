package top.e404.eplugin.mysql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.serialization.Serializable
import top.e404.eorm.EOrm
import top.e404.eorm.dialect.SqlDialect
import java.sql.Connection

@Serializable
data class DataSourceConfig(
    val url: String,
    val driver: String,
    val username: String,
    val password: String,
    val isAutoCommit: Boolean = true,
    val pool: PoolConfig,
) {
    val hikariConfig by lazy {
        HikariConfig().also {
            it.jdbcUrl = url
            it.driverClassName = driver
            it.username = username
            it.password = password
            it.isAutoCommit = true
            it.maximumPoolSize = pool.maximumPoolSize
            it.minimumIdle = pool.minimumIdle
            it.idleTimeout = pool.idleTimeout
            it.maxLifetime = pool.maxLifetime
            it.connectionTimeout = pool.connectionTimeout
        }
    }

    val dataSource by lazy { HikariDataSource(hikariConfig) }
}

@Serializable
data class PoolConfig(
    val maximumPoolSize: Int,
    val minimumIdle: Int,
    val idleTimeout: Long,
    val maxLifetime: Long,
    val connectionTimeout: Long,
)

open class DB(val name: String, val dialect: SqlDialect) {
    @Volatile
    private lateinit var datasource: HikariDataSource

    private val eorm by lazy { EOrm(datasource, dialect) }

    // 延迟关闭旧数据源
    open fun stop() {
        if (::datasource.isInitialized) datasource.close()
    }

    open fun load(config: DataSourceConfig) {
        datasource = config.dataSource
    }

    fun <T> useDb(block: (Connection) -> T) = datasource.connection.use(block)

}
