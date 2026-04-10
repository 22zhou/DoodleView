package com.doodleview.demo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.doodleview.DoodleView;

import java.io.File;
import java.util.Locale;

/**
 * DoodleView 示例应用
 */
public class MainActivity extends AppCompatActivity {

    private DoodleView doodleView;
    private SeekBar strokeSeekBar;
    private Button eraserButton;
    private Button undoButton;
    private Button redoButton;
    private Button clearButton;
    private Button saveButton;

    // 预设颜色
    private static final int[] COLORS = {
            0xFF000000, // 黑色
            0xFFFF0000, // 红色
            0xFF2196F3, // 蓝色
            0xFF4CAF50, // 绿色
            0xFFFFEB3B, // 黄色
            0xFFFF9800, // 橙色
            0xFF9C27B0, // 紫色
            0xFF795548  // 棕色
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupListeners();
    }

    private void initViews() {
        doodleView = findViewById(R.id.doodleView);
        strokeSeekBar = findViewById(R.id.strokeSeekBar);
        eraserButton = findViewById(R.id.btnEraser);
        undoButton = findViewById(R.id.btnUndo);
        redoButton = findViewById(R.id.btnRedo);
        clearButton = findViewById(R.id.btnClear);
        saveButton = findViewById(R.id.btnSave);

        // 设置默认画笔
        doodleView.setColor(COLORS[0]);
        doodleView.setStrokeWidth(8f);

        // 设置画笔粗细滑块
        strokeSeekBar.setProgress(8);
        strokeSeekBar.setMax(50);
    }

    private void setupListeners() {
        // 绘制监听
        doodleView.setOnDrawingListener(new DoodleView.OnDrawingListener() {
            @Override
            public void onDrawStart() {
                updateButtonStates();
            }

            @Override
            public void onDrawEnd() {
                updateButtonStates();
            }

            @Override
            public void onClear() {
                updateButtonStates();
            }
        });

        // 粗细调节
        strokeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    doodleView.setStrokeWidth(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    /**
     * 颜色选择
     */
    public void onColorClick(View view) {
        int index = view.getId() - R.id.color0;
        if (index >= 0 && index < COLORS.length) {
            doodleView.setColor(COLORS[index]);
            doodleView.setEraser(false);
            updateEraserButton();
        }
    }

    /**
     * 橡皮擦
     */
    public void onEraserClick(View view) {
        doodleView.setEraser(!doodleView.isEraser());
        updateEraserButton();
    }

    /**
     * 撤销
     */
    public void onUndoClick(View view) {
        doodleView.undo();
        updateButtonStates();
    }

    /**
     * 重做
     */
    public void onRedoClick(View view) {
        doodleView.redo();
        updateButtonStates();
    }

    /**
     * 清空
     */
    public void onClearClick(View view) {
        doodleView.clear();
        updateButtonStates();
        Toast.makeText(this, "画布已清空", Toast.LENGTH_SHORT).show();
    }

    /**
     * 保存
     */
    public void onSaveClick(View view) {
        File file = new File(getExternalFilesDir(null), 
                "doodle_" + System.currentTimeMillis() + ".png");
        
        if (doodleView.saveToFile(file.getAbsolutePath(), Bitmap.CompressFormat.PNG)) {
            Toast.makeText(this, "已保存: " + file.getName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取画布 Bitmap
     */
    public void onGetBitmapClick(View view) {
        Bitmap bitmap = doodleView.getBitmap();
        if (bitmap != null) {
            Toast.makeText(this, 
                    String.format(Locale.getDefault(), "Bitmap: %dx%d", 
                            bitmap.getWidth(), bitmap.getHeight()), 
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 更新橡皮擦按钮状态
     */
    private void updateEraserButton() {
        if (doodleView.isEraser()) {
            eraserButton.setText("画笔");
            eraserButton.setSelected(true);
        } else {
            eraserButton.setText("橡皮擦");
            eraserButton.setSelected(false);
        }
    }

    /**
     * 更新按钮状态
     */
    private void updateButtonStates() {
        undoButton.setEnabled(doodleView.canUndo());
        redoButton.setEnabled(doodleView.canRedo());
    }
}
