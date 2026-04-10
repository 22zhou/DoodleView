# DoodleView

Android 手绘涂鸦库 - 简洁、强大、易用

## 功能特性

- 🎨 **涂鸦画布** - 基于 Canvas 的高性能画布
- 🌈 **颜色选择** - 支持任意颜色选择
- 📏 **粗细调节** - 灵活调整画笔粗细
- 🧹 **橡皮擦** - 橡皮擦工具
- ↩️ **撤销/重做** - 完整的撤销重做功能
- 🗑️ **清空画布** - 一键清空所有内容
- 💾 **保存图片** - 支持保存为 PNG/JPEG 图片

## 快速开始

### 添加依赖

```groovy
dependencies {
    implementation 'com.github.22zhou:DoodleView:1.0.0'
}
```

### 在布局中使用

```xml
<com.doodleview.DoodleView
    android:id="@+id/doodleView"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

### 基础使用

```java
DoodleView doodleView = findViewById(R.id.doodleView);

// 设置画笔颜色
doodleView.setColor(Color.RED);

// 设置画笔粗细
doodleView.setStrokeWidth(10f);

// 切换橡皮擦模式
doodleView.setEraser(true);

// 撤销
doodleView.undo();

// 重做
doodleView.redo();

// 清空画布
doodleView.clear();

// 保存为 Bitmap
Bitmap bitmap = doodleView.getBitmap();

// 保存到文件
doodleView.saveToFile(filePath, Bitmap.CompressFormat.PNG);
```

## API 文档

### DoodleView 核心方法

| 方法 | 说明 |
|------|------|
| `setColor(int color)` | 设置画笔颜色 |
| `getColor()` | 获取当前画笔颜色 |
| `setStrokeWidth(float width)` | 设置画笔粗细 |
| `getStrokeWidth()` | 获取当前画笔粗细 |
| `setEraser(boolean isEraser)` | 切换橡皮擦模式 |
| `isEraser()` | 是否处于橡皮擦模式 |
| `undo()` | 撤销上一笔画 |
| `redo()` | 重做上一笔画 |
| `canUndo()` | 是否可以撤销 |
| `canRedo()` | 是否可以重做 |
| `clear()` | 清空画布 |
| `getBitmap()` | 获取当前画布内容为 Bitmap |
| `saveToFile(String path, Bitmap.CompressFormat format)` | 保存到文件 |

## 项目结构

```
DoodleView/
├── library/                    # 涂鸦库核心模块
│   └── src/main/java/com/doodleview/
│       └── DoodleView.java     # 核心涂鸦视图
├── app/                        # 示例应用
│   └── src/main/java/com/doodleview/demo/
│       └── MainActivity.java   # 示例代码
└── README.md
```

## License

MIT License
