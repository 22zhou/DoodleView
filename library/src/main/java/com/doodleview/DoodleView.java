package com.doodleview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * DoodleView - Android 手绘涂鸦视图
 * 
 * 核心功能：
 * - 自由绘制
 * - 颜色/粗细调节
 * - 橡皮擦
 * - 撤销/重做
 * - 保存图片
 * 
 * @author 22zhou
 */
public class DoodleView extends View {
    
    // 默认配置
    private static final float DEFAULT_STROKE_WIDTH = 8f;
    private static final int DEFAULT_COLOR = Color.BLACK;
    
    // 绘制画笔
    private Paint drawPaint;
    private Paint canvasPaint;
    
    // 离屏画布
    private Bitmap drawBitmap;
    private Canvas drawCanvas;
    
    // 当前路径
    private Path currentPath;
    
    // 路径历史（支持撤销/重做）
    private List<DrawPath> undoPaths = new ArrayList<>();
    private List<DrawPath> redoPaths = new ArrayList<>();
    
    // 配置
    private int currentColor = DEFAULT_COLOR;
    private float strokeWidth = DEFAULT_STROKE_WIDTH;
    private boolean isEraser = false;
    
    // 监听器
    private OnDrawingListener listener;
    
    /**
     * 绘制路径数据结构
     */
    private static class DrawPath {
        Path path;
        int color;
        float strokeWidth;
        boolean isEraser;
        
        DrawPath(Path path, int color, float strokeWidth, boolean isEraser) {
            this.path = path;
            this.color = color;
            this.strokeWidth = strokeWidth;
            this.isEraser = isEraser;
        }
    }
    
    /**
     * 绘制监听器接口
     */
    public interface OnDrawingListener {
        void onDrawStart();
        void onDrawEnd();
        void onClear();
    }
    
    // 构造方法
    public DoodleView(Context context) {
        super(context);
        init();
    }
    
    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public DoodleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    /**
     * 初始化
     */
    private void init() {
        // 初始化绘制画笔
        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setDither(true);
        drawPaint.setColor(currentColor);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setStrokeWidth(strokeWidth);
        
        // 画布画笔
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        if (w > 0 && h > 0) {
            // 创建离屏 Bitmap
            drawBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(drawBitmap);
            
            // 填充白色背景
            drawCanvas.drawColor(Color.WHITE);
        }
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // 绘制离屏 Bitmap
        if (drawBitmap != null) {
            canvas.drawBitmap(drawBitmap, 0, 0, canvasPaint);
        }
        
        // 绘制当前路径
        canvas.drawPath(currentPath, drawPaint);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                break;
                
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                break;
                
            case MotionEvent.ACTION_UP:
                touchUp();
                break;
        }
        
        invalidate();
        return true;
    }
    
    /**
     * 开始触摸
     */
    private void touchStart(float x, float y) {
        currentPath = new Path();
        currentPath.moveTo(x, y);
        
        updatePaint();
        
        if (listener != null) {
            listener.onDrawStart();
        }
    }
    
    /**
     * 移动
     */
    private void touchMove(float x, float y) {
        currentPath.lineTo(x, y);
    }
    
    /**
     * 抬起
     */
    private void touchUp() {
        currentPath.lineTo(currentPath.getWidth(), currentPath.getHeight());
        
        // 保存路径到历史
        DrawPath drawPath = new DrawPath(currentPath, currentColor, strokeWidth, isEraser);
        undoPaths.add(drawPath);
        
        // 绘制到离屏画布
        if (drawCanvas != null) {
            drawCanvas.drawPath(currentPath, drawPaint);
        }
        
        // 清空重做栈
        redoPaths.clear();
        
        // 创建新路径
        currentPath = new Path();
        
        if (listener != null) {
            listener.onDrawEnd();
        }
    }
    
    /**
     * 更新画笔配置
     */
    private void updatePaint() {
        drawPaint.setColor(currentColor);
        drawPaint.setStrokeWidth(strokeWidth);
        
        if (isEraser) {
            // 橡皮擦模式 - 使用Clear模式
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            drawPaint.setXfermode(null);
        }
    }
    
    // ==================== Public API ====================
    
    /**
     * 设置画笔颜色
     */
    public void setColor(int color) {
        this.currentColor = color;
        this.isEraser = false;
        updatePaint();
    }
    
    /**
     * 获取当前画笔颜色
     */
    public int getColor() {
        return currentColor;
    }
    
    /**
     * 设置画笔粗细
     */
    public void setStrokeWidth(float width) {
        this.strokeWidth = width;
        updatePaint();
    }
    
    /**
     * 获取当前画笔粗细
     */
    public float getStrokeWidth() {
        return strokeWidth;
    }
    
    /**
     * 设置橡皮擦模式
     */
    public void setEraser(boolean isEraser) {
        this.isEraser = isEraser;
        updatePaint();
    }
    
    /**
     * 是否处于橡皮擦模式
     */
    public boolean isEraser() {
        return isEraser;
    }
    
    /**
     * 撤销
     */
    public void undo() {
        if (canUndo()) {
            // 获取最后一个路径
            DrawPath lastPath = undoPaths.remove(undoPaths.size() - 1);
            
            // 保存到重做栈
            redoPaths.add(lastPath);
            
            // 重新绘制所有路径
            redrawPaths();
        }
    }
    
    /**
     * 重做
     */
    public void redo() {
        if (canRedo()) {
            // 获取最后一个重做路径
            DrawPath redoPath = redoPaths.remove(redoPaths.size() - 1);
            
            // 保存到撤销栈
            undoPaths.add(redoPath);
            
            // 重新绘制所有路径
            redrawPaths();
        }
    }
    
    /**
     * 是否可以撤销
     */
    public boolean canUndo() {
        return !undoPaths.isEmpty();
    }
    
    /**
     * 是否可以重做
     */
    public boolean canRedo() {
        return !redoPaths.isEmpty();
    }
    
    /**
     * 清空画布
     */
    public void clear() {
        undoPaths.clear();
        redoPaths.clear();
        
        if (drawCanvas != null && drawBitmap != null) {
            drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            drawCanvas.drawColor(Color.WHITE);
        }
        
        currentPath = new Path();
        invalidate();
        
        if (listener != null) {
            listener.onClear();
        }
    }
    
    /**
     * 重新绘制所有路径
     */
    private void redrawPaths() {
        if (drawCanvas != null && drawBitmap != null) {
            // 清空画布
            drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            drawCanvas.drawColor(Color.WHITE);
            
            // 临时保存当前画笔状态
            Paint tempPaint = new Paint(drawPaint);
            
            // 重绘所有路径
            for (DrawPath drawPath : undoPaths) {
                Paint pathPaint = createPaint(drawPath);
                drawCanvas.drawPath(drawPath.path, pathPaint);
            }
            
            invalidate();
        }
    }
    
    /**
     * 创建指定配置的画笔
     */
    private Paint createPaint(DrawPath drawPath) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(drawPath.color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(drawPath.strokeWidth);
        
        if (drawPath.isEraser) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        
        return paint;
    }
    
    /**
     * 获取当前画布内容为 Bitmap
     */
    public Bitmap getBitmap() {
        if (drawBitmap != null) {
            return drawBitmap.copy(Bitmap.Config.ARGB_8888, false);
        }
        return null;
    }
    
    /**
     * 保存到文件
     * 
     * @param filePath 文件路径
     * @param format 图片格式 (Bitmap.CompressFormat.PNG 或 JPEG)
     * @return 是否保存成功
     */
    public boolean saveToFile(String filePath, Bitmap.CompressFormat format) {
        if (drawBitmap == null) {
            return false;
        }
        
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        
        try {
            FileOutputStream fos = new FileOutputStream(file);
            drawBitmap.compress(format, 100, fos);
            fos.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 保存到文件（默认 PNG 格式）
     */
    public boolean saveToFile(String filePath) {
        return saveToFile(filePath, Bitmap.CompressFormat.PNG);
    }
    
    /**
     * 设置绘制监听器
     */
    public void setOnDrawingListener(OnDrawingListener listener) {
        this.listener = listener;
    }
}
