# [EPlugin](https://github.com/4o4E/EPlugin)

> `spigot`-`kotlin` 插件框架
> 
> 按照功能区分模块, 支持按需引入

若模块没有声明则默认使用`SpigotAPI-1.13.2`作为依赖(在这之前的版本没有非空注解, 和kotlin相性不佳)

## 开始使用

### 通过 Maven 仓库引入

```kotlin
val epluginVer = "1.4.0" // 在release中查看最新版本
fun eplugin(module: String, version: String = epluginVer) = "top.e404:eplugin-$module:$version"

repositories {
    maven("https://nexus.e404.top:3443/repository/maven-snapshots/")
}

dependencies {
    // eplugin core
    implementation(eplugin("core"))
    // 序列化模块(可选)
    implementation(eplugin("serialization"))
    // papi依赖(可选|papi依赖需要手动导入)
    implementation(eplugin("hook-placeholderapi"))
    // placeholderAPI 手动导入papi依赖
    compileOnly("me.clip:placeholderapi:2.11.1")
}

tasks {
    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.jar")
        // 推荐对kotlin和eplugin进行relocate, 避免不同版本的依赖互相冲突
        relocate("kotlin", "top.e404.eclean.relocate.kotlin")
        relocate("top.e404.eplugin", "top.e404.eclean.relocate.eplugin")
    }
}
```

### 本地构建

若无法访问 Maven 仓库，可通过本地构建引入：

1. `git clone https://github.com/4o4E/EPlugin.git`
2. `gradlew publishToMavenLocal`

然后在项目中使用 `mavenLocal()` 替代上述 Maven 仓库：

```kotlin
repositories {
    mavenLocal()
}
```

## 使用`EPlugin`的实际项目

- [EClean](https://github.com/4o4E/EClean)
- [Boom](https://github.com/4o4E/Boom)

## 项目结构

### 核心模块

| 模块 | 说明 |
|------|------|
| [eplugin-core](eplugin-core) | Bukkit 插件开发核心框架。`EPlugin` 抽象类封装 JavaPlugin，提供消息发送（前缀/颜色码）、占位符替换、debug 日志、bStats 统计、异步/同步任务调度等基础能力。包含配置文件管理、命令系统（`ECommand`/`ESubCommand`/Tab 补全）、事件监听器封装、插件 Hook 机制、版本更新检查器（`EUpdater`）、计分板、BossBar 倒计时、权重随机抽取表，以及大量工具函数（玩家/物品/位置/文本/JSON 等） |

### 序列化模块

| 模块 | 说明 |
|------|------|
| [eplugin-serialization](eplugin-serialization) | 基于 kotlinx.serialization 的配置文件序列化模块。`KtxConfig` 将配置文件自动反序列化为 Kotlin 数据类（默认 YAML），`KtxMultiFileConfig` 支持多文件目录配置。提供大量 Bukkit 对象的自定义序列化器，涵盖 Location、ItemStack、Material、Enchantment、PotionEffect、Color、Sound、Title、Biome、World、UUID、Duration 等 |
| [eplugin-serialization-adventure](eplugin-serialization-adventure) | 提供 Adventure `Component` 与 JSON 字符串之间的 kotlinx.serialization 序列化器，基于 `JSONComponentSerializer` |
| [eplugin-serialization-mmoitems](eplugin-serialization-mmoitems) | MMOItems 物品属性数据的完整序列化/反序列化支持，覆盖所有 `StatData` 和 `RandomStatData` 类型，支持属性合并 |
| [eplugin-serialization-multitem](eplugin-serialization-multitem) | 统一的多来源物品序列化接口 `Item`，支持原版 MC 物品、MMOItems 物品和 ItemsAdder 物品三种类型的序列化配置，提供物品生成和匹配功能 |
| [eplugin-serialization-particle](eplugin-serialization-particle) | Bukkit 粒子效果的完整序列化配置体系，支持 Normal、DustOptions、DustTransition、ItemStack、BlockData、Vibration、Float、Int 等所有粒子类型，可从 YAML 配置生成并播放粒子 |
| [eplugin-serialization-worldedit](eplugin-serialization-worldedit) | 方块替换配置的序列化模型 `ReplaceConfig`，用正则匹配输入方块类型并定义替换输出列表 |
| [eplugin-serialization-worldguard](eplugin-serialization-worldguard) | 基于 WorldGuard 区域的分层配置体系，定义 `RegionConfig` 接口（含全局/每世界/每区域三级配置），以及 `ERegionConfig` 抽象类实现按位置自动匹配最优先配置 |

### 菜单模块

| 模块 | 说明 |
|------|------|
| [eplugin-menu](eplugin-menu) | Bukkit 箱子/熔炉 GUI 菜单框架。`EMenuManager` 管理玩家与菜单实例的映射关系，处理物品拾取、放入、交换、Shift 点击等交互逻辑。支持多种菜单类型（`ChestMenu`/`FurnaceMenu`/`InventoryMenu`），槽位系统包括背景槽、输入槽、按钮、多态按钮，以及区域（`MenuZone`/`MenuButtonZone`） |

### 游戏模块

| 模块 | 说明 |
|------|------|
| [eplugin-game](eplugin-game) | 小游戏框架模块。`GameManager` 管理游戏配置和动态地图，`GameInstance` 维护玩家/旁观者列表和游戏阶段状态机（WAITING→READY→GAMING→ENDING），通过 `GameStageHandler` 子类处理各阶段逻辑，支持玩家加入/退出/旁观、聊天频道、游戏 tick 循环等 |
| [eplugin-game-mini](eplugin-game-mini) | 迷你游戏工具模块。包含 `BlocksMatrix` 方块矩阵翻转小游戏（类似"关灯游戏"），支持打乱和完成检测 |

### 基础设施模块

| 模块 | 说明 |
|------|------|
| [eplugin-orm](eplugin-orm) | EOrm ORM 框架集成模块。提供可序列化的 `DataSourceConfig` 数据源配置（含 HikariCP 连接池参数），以及 `DB` 封装类管理 EOrm 实例生命周期，支持 DSL 查询、CRUD、事务等完整 ORM 能力 |
| [eplugin-reflect](eplugin-reflect) | Java 反射工具库。`ReflectData` 缓存 Field/Method 反射信息，提供带缓存的字段读写、方法调用和多参数构造器实例化，简化私有成员的反射访问 |
| [eplugin-channel](eplugin-channel) | BungeeCord Plugin Messaging Channel 通信封装。`EChannelHandler` 负责注册/注销频道、接收数据包并按子频道名分发给对应的 `ESubChannelHandler` 处理 |
| [eplugin-adventure](eplugin-adventure) | Adventure 组件库辅助扩展。提供 `ItemStack.display` 扩展属性，将物品显示名称转换为 Adventure `Component` |

### Hook 模块

封装第三方插件 API，统一提供 `EHook` 接口实现插件检测与挂钩。

| 模块 | 说明 |
|------|------|
| [eplugin-hook-worldedit](eplugin-hook-worldedit) | 封装 WorldEdit API，提供区域清除、Schematic 加载/保存、剪贴板粘贴（支持居中）等操作，包含 `EClipboardManager` 批量管理剪贴板 |
| [eplugin-hook-worldguard](eplugin-hook-worldguard) | 封装 WorldGuard API，提供按位置/世界名查询保护区域，以及遍历区域内坐标的扩展函数 |
| [eplugin-hook-placeholderapi](eplugin-hook-placeholderapi) | 封装 PlaceholderAPI，提供占位符解析替换功能及 `PapiExpansion` 抽象类方便注册自定义占位符 |
| [eplugin-hook-vault](eplugin-hook-vault) | 封装 Vault API，提供对经济（Economy）、权限（Permission）和聊天（Chat）服务的统一访问 |
| [eplugin-hook-playerpoints](eplugin-hook-playerpoints) | 封装 PlayerPoints 点券插件 API，提供查询余额、给予/扣除点券、获取排行榜等操作 |
| [eplugin-hook-mythicmobs](eplugin-hook-mythicmobs) | 封装 MythicMobs API，提供获取/生成怪物、获取物品、施放技能（支持变量）、背包中计数/扣除物品等功能 |
| [eplugin-hook-mmoitems](eplugin-hook-mmoitems) | 封装 MMOItems API，提供通过类型+ID 获取物品、NBT 识别物品、背包中计数/扣除物品等功能 |
| [eplugin-hook-mmocore](eplugin-hook-mmocore) | 封装 MMOCore API，提供获取玩家 RPG 数据（等级、法力值）及设置法力值的功能 |
| [eplugin-hook-mythiclib](eplugin-hook-mythiclib) | 封装 MythicLib API，提供对修饰符、伤害、实体、属性、元素和技能管理器的统一访问 |
| [eplugin-hook-itemsadder](eplugin-hook-itemsadder) | 封装 ItemsAdder API，提供获取自定义物品、查询物品信息、列出所有物品/命名空间、背包中计数/扣除物品等功能 |
| [eplugin-hook-modelengine](eplugin-hook-modelengine) | 封装 Model Engine API，提供为实体应用/移除自定义模型的功能 |
| [eplugin-hook-slimefun](eplugin-hook-slimefun) | 封装 Slimefun API，提供通过 ID 获取物品、识别物品/方块的 Slimefun ID、背包中计数/扣除物品等功能 |
| [eplugin-hook-libsdisguises](eplugin-hook-libsdisguises) | 封装 LibsDisguises API，提供完整的实体伪装功能，包括创建/应用/移除伪装、查询伪装状态、控制可见性等 |
| [eplugin-hook-protocollib](eplugin-hook-protocollib) | 封装 ProtocolLib API，提供对 `ProtocolManager` 实例的访问，用于数据包级别的协议操作 |
| [eplugin-hook-nova](eplugin-hook-nova) | 封装 Nova 插件 API，提供查询指定位置是否存在 Nova 方块的功能及 `NovaProtection` 保护集成接口 |
| [eplugin-hook-orangeengine](eplugin-hook-orangeengine) | 封装 OrangeEngine 模型引擎 API，提供为实体设置模型和播放动画的功能 |
| [eplugin-hook-adyeshach](eplugin-hook-adyeshach) | 封装 Adyeshach 虚拟实体框架 API，提供对公共/私有实体管理器、序列化器、类型/元数据/控制器注册表、全息图等的完整访问 |
| [eplugin-hook-bentobox](eplugin-hook-bentobox) | 封装 BentoBox 空岛插件框架 API，提供获取用户、按位置/玩家查询岛屿、检查权限等级等功能 |
| [eplugin-hook-psr](eplugin-hook-psr) | 封装 ProtocolStringReplacer 插件 API，提供字符串替换规则配置（支持正则/包含匹配模式） |
| [eplugin-hook-playerparticles](eplugin-hook-playerparticles) | 封装 PlayerParticles 插件 API，提供注册自定义粒子样式的抽象基类 |

### 代理端模块

| 模块 | 说明 |
|------|------|
| [eplugin-bungeecord-core](eplugin-bungeecord-core) | BungeeCord 端核心框架，提供配置文件管理（YAML/文本/多语言）、事件监听器、聊天拦截器、Hook 管理器、命令系统及服务器/玩家/文本工具函数 |
| [eplugin-bungeecord-serialization](eplugin-bungeecord-serialization) | BungeeCord 端序列化配置模块，提供基于 kotlinx.serialization 的配置文件加载/保存、Map 类型配置及 DoubleRange、IntRange、Regex 等基础类型序列化器 |
| [eplugin-velocity-core](eplugin-velocity-core) | Velocity 代理端核心框架，提供 `EPluginVelocity` 抽象基类，包含日志/调试消息、多语言管理、权限检查、bStats 统计、任务调度、命令框架及配置文件管理 |

## 许可证

本项目使用 [GPL-3.0](LICENSE) 许可证开源。
