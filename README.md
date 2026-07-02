# AndroidCoreUtils

AndroidCoreUtils 是一个面向 Android App 的通用工具库。`core_util` 是正式发布给其他 App 远程依赖的库模块，`app` 只用于本地 demo 和发布验证。

## 接入方式

在使用方项目的 `settings.gradle` 添加 JitPack 仓库：

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://jitpack.io") }
        google()
        mavenCentral()
    }
}
```

业务模块依赖：

```gradle
dependencies {
    implementation "com.github.wukuiqing49:AndroidCoreUtils:v0.0.1"
}
```

## 初始化

推荐在 `Application.onCreate()` 中初始化：

```kotlin
CoreUtils.init(
    context = this,
    config = CoreUtilsConfig(
        debug = BuildConfig.DEBUG,
        initStorage = true,
        initImageLoader = true,
        initLog = false,
        logCaptureCrash = false,
        showLogStackInfo = false,
        registerCoilSingleton = false
    )
)
```

`initLog` 默认关闭。即使开启日志，也只有 `logCaptureCrash = true` 时才会设置全局崩溃处理器；公共库接入建议保持关闭，避免接管宿主 App 的崩溃链路。

## 工具类说明

| 工具类 | 说明 |
| --- | --- |
| `CoreUtils` | 统一初始化入口，集中初始化 MMKV、图片缓存、日志和 FileProvider authority。 |
| `SpUtils` | 基于 MMKV 的本地键值存储，支持默认实例、命名实例、加密实例，以及基础类型、`ByteArray`、`Set<String>`、`Parcelable`。 |
| `FileUtil` | 文件读写、复制、删除、大小、扩展名、MIME、MD5/SHA-256、`Uri` 转文件、`FileProvider` Uri。 |
| `UriUtil` | 读取 `Uri` 的文件名、大小、MIME，并支持复制到缓存或目标文件。 |
| `PhoneUtil` | 品牌、型号、系统版本、AndroidId、模拟器判断、设备摘要。 |
| `PackageUtil` | 包名、版本名、版本号、应用名、图标、安装检测、打开 App、打开设置页、签名 SHA-256。 |
| `NetworkUtil` | 网络是否可用、Wi-Fi/移动网络/以太网判断、网络类型、打开网络设置。 |
| `KeyboardUtil` | 显示/隐藏软键盘、切换软键盘、判断键盘是否可见。 |
| `ClipboardUtil` | 复制文本、读取文本、清空剪贴板。 |
| `IntentUtil` | 打开浏览器、拨号、短信、邮件、应用市场、应用设置页。 |
| `ShareUtil` | 分享文本、单文件、多文件。 |
| `FormatUtil` | 文件大小、小数、数字、百分比、手机号脱敏、空值兜底。 |
| `DateUtil` | 时间格式化/解析、当天开始/结束、加天数、是否今天、日期差、友好时间。 |
| `ConvertUtil` | `dp2px`、`px2dp`、`sp2px`、`px2sp`。 |
| `ScreenUtil` | 屏幕宽高和基础尺寸转换。 |
| `ViewUtil` | `visible/gone/invisible`、防重复点击。 |
| `ValidateUtil` | 邮箱、URL、手机号、数字、身份证、空值校验。 |
| `DigestUtil` | 字符串 MD5/SHA-1/SHA-256、Base64 编解码。 |
| `NotificationUtil` | 通知权限检查和跳转通知设置页。 |
| `PhotoPickerHelper` | 基于 Android Photo Picker 的图片/视频选择、拍照、录像、媒体库查询。 |
| `ImageLoaderUtil` | 基于 Coil 3 的图片加载、GIF、圆角、圆形、灰度、固定缓存。 |
| `ImageCompressUtil` | 基于 Luban 的图片压缩，支持 path 和 `Uri`。 |
| `ALog` | 控制台日志、文件日志、可选崩溃日志、日志上传入口。 |
| `SmartJumpUtils` | 电商链接智能跳转，支持淘宝、京东、拼多多、抖音、闲鱼等 scheme。 |
| `ThemeManager` | 明暗主题管理，基于 `AppCompatDelegate`。 |
| `ProcessUtils` | 当前进程名、主进程、子进程判断。 |
| `SafeUtil` | Toast 扩展和 `Context` 有效性判断。 |

## 常用示例

存储：

```kotlin
SpUtils.put("token", token)
val token = SpUtils.getString("token")

SpUtils.put("token", token, id = "user")
val userToken = SpUtils.getString("token", id = "user")

SpUtils.put("token", token, id = "secure", cryptKey = "your_crypt_key")
val secureToken = SpUtils.getString("token", id = "secure", cryptKey = "your_crypt_key")
```

图片加载：

```kotlin
CacheManager.init(context)
imageView.loadUrl(url, isCircle = true)
```

如果宿主希望把本库的 `ImageLoader` 注册为 Coil 全局单例：

```kotlin
CoreUtils.init(
    this,
    CoreUtilsConfig(registerCoilSingleton = true)
)
```

相册选择：

```kotlin
val picker = PhotoPickerHelper.with(activity)
    .register { uris ->
        // handle selected uris
    }
picker.launch(PickMediaType.IMAGE_ONLY)
```

文件分享：

```kotlin
val uri = FileUtil.getUriForFile(context, file)
ShareUtil.shareFile(context, uri, FileUtil.getMimeType(file.name))
```

防重复点击：

```kotlin
button.setDebouncedClickListener {
    // click
}
```

## 权限和 Manifest 说明

库 Manifest 默认声明：

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

并包含淘宝、京东、拼多多、抖音、闲鱼和浏览器的 `<queries>`，用于 Android 11+ 包可见性和 App scheme 跳转。

`PhotoPickerHelper.launch()` 使用系统 Photo Picker，不需要相册读取权限。`queryMediaList()` 是直接查询 MediaStore，属于读取媒体库，Android 13+ 需要 `READ_MEDIA_IMAGES` / `READ_MEDIA_VIDEO`，Android 14+ 还要考虑部分照片访问策略。

拍照、文件分享、`FileUtil.getUriForFile()` 需要宿主配置 FileProvider。默认 authority 是：

```text
${applicationId}.fileprovider
```

可通过初始化覆盖：

```kotlin
CoreUtils.init(
    this,
    CoreUtilsConfig(fileProviderAuthority = "${BuildConfig.APPLICATION_ID}.fileprovider")
)
```

## ALog 分析

`ALog` 适合作为自有 App 的轻量日志系统：

- 控制台日志可开关。
- 文件日志按日期和大小滚动。
- 默认只输出业务消息，开启 `showLogStackInfo` 后追加短源码位置：`message (File.kt:42#method)`。
- 支持过期日志清理。
- 使用单线程异步写入，避免阻塞 UI。
- 可上传日志目录中的文件。
- `captureCrash = true` 时会设置全局 `UncaughtExceptionHandler`，崩溃时写入日志再交给系统默认处理器。

注意点：

- `ALog.init()` 默认不会接管崩溃链路，只有传入 `captureCrash = true` 才会开启；建议只在自有 App 中显式开启。
- `close()` 是异步关闭；崩溃或进程退出前需要尽量落盘时，使用 `flushSync()` / `closeSync()`。
- 对外发布时推荐保持 `CoreUtilsConfig(initLog = false, logCaptureCrash = false)`。

## SpUtils 分析

`SpUtils` 基于 MMKV，适合替代大部分 SharedPreferences 场景：

- 读写 API 简单。
- 支持常见基础类型。
- 支持 `Parcelable`。
- 支持 `id` 参数创建命名 MMKV，适合多用户、多账号、按业务模块隔离数据。
- 支持 `cryptKey` 参数创建 encrypted MMKV，适合 token、账号信息等敏感数据。
- `CoreUtils.init()` 会默认初始化它。

注意点：

- 未初始化时会抛出明确异常，这是合理的。
- `getMMKV()` 返回了 MMKV 实例，因此 MMKV 依赖需要保留为发布依赖。
- 加密存储必须保证同一个 `id` 使用同一个 `cryptKey`，否则会读不到原数据。
- `Set<*>` 只支持 `Set<String>`，传入其他元素类型会抛出异常。

## 图片工具分析

图片能力由 `CacheManager`、`ImageLoaderUtil`、`ImageCompressUtil` 组成：

- `CacheManager` 创建统一 Coil `ImageLoader`，配置内存缓存、磁盘缓存、GIF decoder 和 OkHttp 网络加载。
- `ImageLoaderUtil` 封装 URL/File/resId 加载，支持圆形、圆角、灰度、GIF、固定缓存。
- `ImageCompressUtil` 用 Luban 压缩图片，支持 path 和 `Uri`。

注意点：

- `ImageLoaderUtil` 依赖 `CacheManager.init()`，建议通过 `CoreUtils.init(initImageLoader = true)` 初始化。
- `CacheManager.init()` 默认不会调用 Coil `SingletonImageLoader.setSafe`，不会抢宿主 App 的 Coil 全局配置。只有 `registerSingleton = true` 或 `CoreUtilsConfig(registerCoilSingleton = true)` 时才会注册全局单例。
- `loadPinned()` 会把 bitmap 额外写入内存和磁盘缓存，适合头像、礼物图标等高频资源，不建议对大量大图使用。
- `ImageCompressUtil.compress(uri)` 会把 `Uri` 复制成临时文件再压缩，稳定性好，但会占用缓存空间，使用方需要按需清理。

## 本地源码引用

同工作区源码依赖：

```gradle
include ":core_util"
project(":core_util").projectDir = file("../AndroidCoreUtils/core_util")
```

```gradle
dependencies {
    implementation project(":core_util")
}
```

## 发布

版本号来自根目录 `version.properties`：

```properties
VERSION_NAME=0.0.1
```

### 自动发版并递增版本号

推荐使用发布脚本统一处理版本号、验证、提交和 tag：

```powershell
.\scripts\release-util.ps1 -Bump patch
```

`-Bump` 支持：

- `patch`：`0.0.1 -> 0.0.2`
- `minor`：`0.0.1 -> 0.1.0`
- `major`：`0.0.1 -> 1.0.0`

也可以手动指定版本号：

```powershell
.\scripts\release-util.ps1 -Version 0.1.0
```

脚本会自动执行：

- 读取并递增 `version.properties` 中的 `VERSION_NAME`。
- 同步更新 README 中的依赖版本和 `gradle/libs.versions.toml`。
- 构建 `:app:assembleDebug` 和 `:core_util:assembleRelease`。
- 发布到 Maven Local 并用远程坐标方式验证 sample。
- 创建 release commit：`release util vX.Y.Z`。
- 创建 Git tag：`vX.Y.Z`。
- 默认推送 `main` 分支和 tag 到 `origin`。

常用参数：

```powershell
# 只本地生成 commit/tag，不 push
.\scripts\release-util.ps1 -Bump patch -SkipPush

# 工作区有未提交改动时，把当前改动一起纳入发版提交
.\scripts\release-util.ps1 -Bump patch -AllowDirty

# 指定远程和分支
.\scripts\release-util.ps1 -Bump minor -Remote origin -Branch main
```

推送 tag 后，JitPack 会按 tag 构建版本，例如：

```gradle
implementation "com.github.wukuiqing49:AndroidCoreUtils:v0.0.2"
```

### 手动发布和验证

发布到 Maven Local：

```powershell
.\gradlew.bat publishUtilToMavenLocal
```

验证 Maven Local 产物能被 sample 以远程坐标方式引用：

```powershell
.\gradlew.bat verifyUtilPublishLocal
```

发布到 GitHub Packages：

```powershell
.\gradlew.bat publishUtilToGitHubPackages
```

发布到 GitHub Packages 需要提供凭据：

```powershell
$env:GITHUB_ACTOR="你的 GitHub 用户名"
$env:GITHUB_TOKEN="具有 packages:write 权限的 token"
.\gradlew.bat publishUtilToGitHubPackages
```

JitPack 会执行：

```bash
./gradlew publishUtilToMavenLocal -x test
```
