# CraftGui-BELike

Minecraft 1.21.4 Fabric **客户端**模组，增强原版配方书 GUI。

---

## 一、简要介绍

### 核心功能

| 功能 | 说明                                               |
|------|--------------------------------------------------|
| **收藏配方** | 对着配方按 `F + Button1` 键收藏，星标角标显示                   |
| **自定义配方分类** | 数据驱动的 JSON 配置，支持通配符模式匹配（`*bricks`），自定义 Tab 图标与名称 |
| **7 种配方排序器** | 默认/材料匹配/注册表顺序/原版优先/模组优先/拼音/保持组拆分排序，一键循环切换        |
| **严格搜索模式** | 基于物品本地化名称和注册表 ID 的全文搜索，替代原版整组搜索                  |
| **鼠标滚轮翻页** | 配方书内滚动鼠标翻页，`Ctrl` 加速                             |
| **布局分流** | 原版 Tab 及收藏 保持在左侧，自定义分类 Tab 显示在配方书上方              |

### 客户端命令

- `/favoriteRecipe count` — 查看已收藏配方数量
- `/favoriteRecipe clear confirm` — 清除当前玩家收藏
- `/favoriteRecipe clear confirm all` — 清除所有玩家收藏

---

## 二、玩家安装

| 要求 | 版本 |
|------|------|
| Minecraft | **1.21.4** |
| Fabric Loader | **>= 0.16.0** |
| Fabric API | **任意版本** |
| Java | **>= 21** |

1. 安装 [Fabric Loader](https://fabricmc.net/use/) 和 Fabric API
2. 下载本模组 `.jar` 文件
3. 放入 `.minecraft/mods/` 文件夹
4. 启动游戏

> 本模组为纯客户端模组，无需在服务端安装。

---

## 三、开发者 API 使用

### 3.1 添加依赖

```gradle
// build.gradle
repositories {
    maven { url = "https://jitpack.io" }
}

dependencies {
    modImplementation "com.github.R-Matrix:CraftGui-BELike:<version>"
}
```

推荐使用 `include()` 实现 jar-in-jar 打包，使你的模组无需额外安装本模组即可运行。

### 3.2 注册自定义配方分类

创建一个 JSON 配置文件并放在你的模组资源中：

```
src/main/resources/assets/<你的modid>/recipe_categories/recipe_categories.json
```

```json
{
  "categories": {
    "my_category": {
      "display_name": "yourmod.category.my_category",
      "primary_icon": "minecraft:diamond",
      "secondary_icon": "minecraft:netherite_ingot",
      "recipes": [
        "minecraft:diamond_block",
        "minecraft:diamond_sword",
        "*pickaxe",
        "*shovel"
      ]
    }
  }
}
```

| 字段 | 必填 | 说明 |
|------|------|------|
| `display_name` | 是 | 翻译键，Tab 悬停文本 |
| `primary_icon` | 是 | Tab 主图标（物品 ID） |
| `secondary_icon` | 否 | Tab 副图标（物品 ID） |
| `recipes` | 是 | 配方匹配规则列表 |

**配方匹配语法：**
- `minecraft:diamond` — 精确匹配输出物品 ID
- `*bricks` — 通配符，匹配后缀（正则 `.*bricks`）
- `*block*` — 通配符，匹配包含（正则 `.*block.*`）
- `?` 匹配单个字符

### 3.3 程序化注册分类

参考[文件](src/main/java/xyz/water/rmatrix/cmod/craftguibelike/example/CraftGuiBELikeDataGen.java)

### 3.4 注册自定义排序器

```java
// import: xyz.water.rmatrix.cmod.craftguibelike.utils.SorterManager
// import: xyz.water.rmatrix.cmod.craftguibelike.api.IRecipeSorter
SorterManager.getINSTANCE().register(
    Identifier.of("mymod", "my_sorter"),
    new IRecipeSorter() {
        @Override
        public List<RecipeResultCollection> sortRecipes(List<RecipeResultCollection> recipes) {
            // 自定义排序逻辑
            return recipes;
        }

        @Override
        public String getName() {
            return "My Sorter";
        }
    }
);
```

---

## 四、开发者指南

### 4.1 项目结构

```
CraftGui-BELike/
├── src/main/java/.../craftguibelike/
│   ├── CraftGuiBELike.java              # 共用入口（注册物品）
│   ├── item/ModItem.java                # 注册 favorite_star 物品
│   ├── category/RecipeCategoryDefinition.java  # 分类定义 record
│   ├── datagen/RecipeCategoryProvider.java     # 数据生成器
│   └── example/CraftGuiBELikeDataGen.java      # 数据生成示例
│
├── src/client/java/.../craftguibelike/
│   ├── CraftGuiBELikeClient.java        # 客户端入口（注册Tab、排序器、命令）
│   ├── api/
│   │   ├── IEnhancedRecipeBookCategoryAPI.java  # 分类注册与管理
│   │   ├── IFavoritesManager.java       # 收藏管理
│   │   ├── IRecipeManager.java          # 配方-分类映射
│   │   ├── IRecipeIdToDisplayEntryAdapt.java    # 配方条目查询
│   │   ├── IRecipeSorter.java           # 排序器接口
│   │   └── impl/
│   │       ├── EnhancedRecipeBookCategoryAPIImpl.java  # 分类+配方管理实现
│   │       └── FavoritesManagerImpl.java    # 收藏管理实现
│   ├── button/                          # 3 个自定义按钮
│   │   ├── SortButton.java
│   │   ├── MouseScrollEnableButton.java
│   │   └── StrictSearchButton.java
│   ├── command/ClearFavoriteCommand.java  # /favoriteRecipe 命令
│   ├── registry/RecipeBookTabRegistry.java # 自定义 Tab 注册表
│   ├── sorters/                         # 7 个排序器实现
│   │   ├── DefaultSorter.java
│   │   ├── MaterialMatchSorter.java
│   │   ├── RegistryOrderSorter.java
│   │   ├── VanillaFirstSorter.java
│   │   ├── ModFirstSorter.java
│   │   ├── PinYinSorter.java
│   │   └── ContainGroupSorter.java
│   ├── utils/
│   │   ├── CategoryDetector.java        # JSON 配置解析与通配符匹配
│   │   ├── ClientCategoryManager.java   # 从所有模组加载分类配置
│   │   ├── FavoriteRecipeStorage.java   # 收藏数据 JSON 持久化
│   │   ├── RecipeBookButtonManager.java # UI 按钮生命周期管理
│   │   ├── SorterManager.java           # 排序器注册与循环切换
│   │   └── favoriteMiscUtils/
│   │       ├── ClientRecipeBookHelper.java       # 配方集合拆分
│   │       ├── StrictSearchProvider.java         # 严格搜索提供器
│   │       ├── CraftingHandlerAccess.java        # 配方处理器标记接口
│   │       └── custom_buttonScaleFlagAccess.java # 按钮缩放标记接口
│   └── mixin/client/                   # 18 个 Mixin（详见下表）
│       ├── addNewCategory/              # 5 个：Tab 注册与布局分流
│       ├── buttonsAddToRecipeBookResult/ # 1 个：按钮注入
│       ├── customCategoryDisplayRecipes/ # 1 个：自定义分类配方过滤
│       ├── favoriteRecipe/              # 4 个：收藏系统
│       ├── mouseScollOnRecipeBool/      # 4 个：鼠标滚轮翻页
│       ├── searchLogicRewrite/          # 1 个：搜索逻辑重写
│       └── sorterAndSortButton/         # 1 个：排序器注入
│
└── src/main/resources/
```

### 4.2 API 接口速览

| 接口 | 核心方法 | 作用 |
|------|----------|------|
| `IEnhancedRecipeBookCategoryAPI` | `registerCategory()`, `isRegisteredCategory()` | 分类注册、查询 |
| `IFavoritesManager` | `toggleFavorite()`, `getFavorites()`, `clearFavorites()` | 收藏 CRUD |
| `IRecipeManager` | `getCategoryFromRecipeId()`, `getRecipesUnderCategory()` | 配方-分类映射 |
| `IRecipeIdToDisplayEntryAdapt` | `getEntriesUnderCategory()`, `refreshClientDisplayEntries()` | 客户端配方条目查询 |
| `IRecipeSorter` | `sortRecipes(List)` | 排序逻辑 |

实现类 `EnhancedRecipeBookCategoryAPIImpl` 同时实现 `IEnhancedRecipeBookCategoryAPI`、`IRecipeManager`、`IRecipeIdToDisplayEntryAdapt` 三个接口。通过 `getINSTANCE()` 获取单例。

### 4.3 Mixin 清单

| 包 | Mixin 文件 | 目标类 | 功能 |
|----|-----------|--------|------|
| `addNewCategory` | `AbstractCraftingRecipeBookWidgetMixin` | `AbstractCraftingRecipeBookWidget` | 将自定义 Tab 注入 Tab 列表 |
| | `ClickableWidgetMixin` | `ClickableWidget` | 按钮基础行为扩展 |
| | `RecipeBookWidgetMixin` | `RecipeBookWidget` | 布局分流：自定义Tab→上方，原版Tab→左侧 |
| | `RecipeGroupButtonWidgetMixin` | `RecipeGroupButtonWidget` | Tab 按钮绘制扩展 |
| | `ToggleButtonWidgetMixin` | `ToggleButtonWidget` | 开关按钮绘制扩展 |
| `buttonsAddToRecipeBookResult` | `RecipeBookResultMixin` | `RecipeBookResults` | 注入排序/搜索/滚轮 3 个按钮 |
| `customCategoryDisplayRecipes` | `ClientRecipeBookMixin` | `ClientRecipeBook` | `getResultsForCategory` 拦截，返回自定义分类配方 |
| `favoriteRecipe` | `AnimatedResultButtonMixin` | `AnimatedResultButton` | 收藏星标渲染 |
| | `ClientRecipeBookAccess` | `ClientRecipeBook` | 暴露私有 `recipes` Map |
| | `RecipeBookResultMixin` | `RecipeBookResults` | F 键收藏/取消收藏 |
| | `RecipeBookWidgetMixin` | `RecipeBookWidget` | 收藏Tab 结果填充 |
| `mouseScollOnRecipeBool` | `HandledScreenHokeMixin` | `HandledScreen` | 鼠标滚轮钩子桥接 |
| | `RecipeBookResultAccess` | `RecipeBookResults` | 暴露 `currentPage`/`pageCount` |
| | `RecipeBookScreenMixin` | `RecipeBookScreen` | 滚轮翻页逻辑 |
| | `RecipeBookWidgetAccess` | `RecipeBookWidget` | 暴露坐标字段 |
| `searchLogicRewrite` | `RecipeBookWidgetMixin` | `RecipeBookWidget` | 搜索 `removeIf` 重定向到严格搜索 |
| `sorterAndSortButton` | `RecipeBookWidgetMixin` | `RecipeBookWidget` | 排序器结果注入 |

### 4.4 配方分类 JSON 配置（完整语法）

```json
{
  "categories": {
    "<json_key>": {
      "display_name": "translation.key",
      "primary_icon": "namespace:item_id",
      "secondary_icon": "namespace:item_id",
      "recipes": [
        "namespace:exact_item_id",
        "*suffix_pattern",
        "*contains*",
        "prefix*"
      ]
    }
  }
}
```

| 通配符 | 正则等价 | 示例 | 匹配 |
|--------|----------|------|------|
| `*bricks` | `.*bricks$` | `minecraft:bricks`, `minecraft:stone_bricks` | 后缀匹配 |
| `*block*` | `.*block.*` | `minecraft:command_block`, `mymod:block_breaker` | 包含匹配 |
| `wood*` | `wood.*` | `wooden_pickaxe`, `wood` | 前缀匹配 |
| `?` | `.` | `wo?d` → 匹配 `wood`, `word` | 单字符 |

精确匹配（无通配符）优先级高于模式匹配。

### 4.5 构建

```bash
# 开发运行
./gradlew runClient

# 构建
./gradlew build

# 运行数据生成
./gradlew runDatagen
```

---
