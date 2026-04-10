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

### 监听器

```java
doodleView.setOnDrawingListener(new DoodleView.OnDrawingListener() {
    @Override
    public void onDrawStart() {
        // 开始绘制
    }

    @Override
    public void onDrawEnd() {
        // 结束绘制
    }

    @Override
    public void onClear() {
        // 画布被清空
    }
});
```

## 完整示例

```java
public class MainActivity extends AppCompatActivity {
    private DoodleView doodleView;
    private static final int[] COLORS = {
        Color.BLACK, Color.RED, Color.BLUE, 
        Color.GREEN, Color.YELLOW, Color.MAGENTA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doodleView = findViewById(R.id.doodleView);
        
        // 设置初始画笔
        doodleView.setColor(Color.BLACK);
        doodleView.setStrokeWidth(8f);

        // 设置绘制监听
        doodleView.setOnDrawingListener(new DoodleView.OnDrawingListener() {
            @Override
            public void onDrawStart() {
                Log.d("DoodleView", "开始绘制");
            }

            @Override
            public void onDrawEnd() {
                Log.d("DoodleView", "结束绘制");
            }

            @Override
            public void onClear() {
                Log.d("DoodleView", "画布已清空");
            }
        });
    }

    // 颜色选择
    public void onColorSelected(View view) {
        int index = Integer.parseInt(view.getTag().toString());
        doodleView.setColor(COLORS[index]);
    }

    // 橡皮擦
    public void onEraserToggle(View view) {
        doodleView.setEraser(!doodleView.isEraser());
    }

    // 保存
    public void onSave(View view) {
        File file = new File(getExternalFilesDir(null), "doodle_" + System.currentTimeMillis() + ".png");
        doodleView.saveToFile(file.getAbsolutePath(), Bitmap.CompressFormat.PNG);
        Toast.makeText(this, "已保存: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
    }
}
```

## 布局示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <!-- 涂鸦区域 -->
    <com.doodleview.DoodleView
        android:id="@+id/doodleView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:background="@android:color/white"/>

    <!-- 工具栏 -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="8dp">

        <!-- 颜色按钮 -->
        <Button
            android:layout_width="40dp"
            android:layout_height="40dp"
            android:background="#FF0000"
            android:tag="1"
            android:onClick="onColorSelected"/>

        <!-- 橡皮擦 -->
        <Button
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="橡皮擦"
            android:onClick="onEraserToggle"/>

        <!-- 撤销 -->
        <Button
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="撤销"
            android:onClick="onUndo"/>

        <!-- 重做 -->
        <Button
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="重做"
            android:onClick="onRedo"/>

        <!-- 保存 -->
        <Button
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="保存"
            android:onClick="onSave"/>
    </LinearLayout>
</LinearLayout>
```

## 实现原理

DoodleView 基于 Android Custom View 实现，核心原理：

1. **触摸事件处理** - 拦截 onTouchEvent，记录 Path 路径
2. **Canvas 绑定 Path** - 每条 Path 独立绘制，支持单独撤销
3. **撤销栈管理** - 维护 undo/redo 栈，实现撤销重做
4. **离屏 Bitmap** - 所有绘制先在 Bitmap 上完成，最后一次性绘制到 Canvas

## 项目结构

```
DoodleView/
├── library/                    # 涂鸦库核心模块
│   └── src/main/java/com/doodleview/
│       ├── DoodleView.java     # 核心涂鸦视图
│       └── DoodleConfig.java   # 配置类
├── app/                        # 示例应用
│   └── src/main/java/com/doodleview/demo/
│       └── MainActivity.java
└── README.md
```

## License

MIT License
