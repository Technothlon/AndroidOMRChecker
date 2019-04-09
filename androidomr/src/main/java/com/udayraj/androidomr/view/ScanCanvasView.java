package com.udayraj.androidomr.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Draws an array of shapes on a canvas
 */
public class ScanCanvasView extends View {

    private final ArrayList<ScanShape> shapes = new ArrayList<>();
    private Rect canvasRect;
    private final int statusbarHeight;
    public Bitmap cameraBitmap;
    private boolean cameraBitmapSet=false;
    public ScanCanvasView(Context context) {
        super(context);
        Resources res = context.getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        final int id = res.getIdentifier("status_bar_height","dimen","android");
        if(id>0)
            statusbarHeight =  res.getDimensionPixelSize(id);
        else
            statusbarHeight = (int) Math.ceil((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 24 : 25) * metrics.density);
        canvasRect = new Rect(0, 0, width, height);
    }

    public class ScanShape {
        private final Shape mShape;
        private final Paint mPaint;
        private final Paint mBorder;

        public ScanShape(Shape shape, Paint paint, Paint border) {
            mShape = shape;
            mPaint = paint;
            mBorder = border;
            mBorder.setStyle(Paint.Style.STROKE);
        }

        public void draw(Canvas canvas) {
            mShape.draw(canvas, mPaint);

            if (mBorder != null) {
                mShape.draw(canvas, mBorder);
            }
        }

        public Shape getShape() {
            return mShape;
        }
    }
    public void unsetCameraBitmap() {
        cameraBitmapSet = false;
    }
    public void setCameraBitmap(Bitmap bm) {
        this.cameraBitmap = bm;
        cameraBitmapSet = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw the frame.
        super.onDraw(canvas);
        if(cameraBitmapSet) {
//            if(cameraBitmap.getWidth() != canvasRect.width() || cameraBitmap.getHeight() != canvasRect.height())
//                cameraBitmap = ScanUtils.resizeToScreenContentSize(cameraBitmap, canvasRect.width(),canvasRect.height());
            canvas.drawBitmap(cameraBitmap, null, canvasRect, null);
        }
        else{
            // allocations per draw cycle.
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            int paddingRight = getPaddingRight();
            int paddingBottom = getPaddingBottom();

            int contentWidth = getWidth() - paddingLeft - paddingRight;
            int contentHeight = getHeight() - paddingTop - paddingBottom;

            for (ScanShape s : shapes) {
                s.getShape().resize(contentWidth, contentHeight);
                s.draw(canvas);
            }
        }

    }

    public void addShape(Shape shape, Paint paint, Paint border) {
        ScanShape scanShape = new ScanShape(shape, paint, border);
        shapes.add(scanShape);
    }

    public void clear() {
        shapes.clear();
    }
}