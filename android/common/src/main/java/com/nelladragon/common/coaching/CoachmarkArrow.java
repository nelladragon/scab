// Copyright (C) 2015 Peter Robinson
package com.nelladragon.common.coaching;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;


import com.nelladragon.common.R;

import java.security.InvalidParameterException;

/**
 * Create curved arrows to be used for Coachmarks.
 */
public class CoachmarkArrow extends View {
    private static final String TAG = CoachmarkArrow.class.getSimpleName();

    public static final float NOT_USED = -1;

    Paint paint;

    PointF fromPoint = null;
    PointF midPoint = null;
    PointF toPoint = null;
    PointF arrow1Point = null;
    PointF arrow2Point = null;

    public CoachmarkArrow(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CoachmarkArrow, 0, 0);
        processPoints(a);

        float fromX = a.getFloat(R.styleable.CoachmarkArrow_fromx, NOT_USED);
        float fromY = a.getFloat(R.styleable.CoachmarkArrow_fromy, NOT_USED);
        float midX = a.getFloat(R.styleable.CoachmarkArrow_midx, NOT_USED);
        float midY = a.getFloat(R.styleable.CoachmarkArrow_midy, NOT_USED);
        float toX = a.getFloat(R.styleable.CoachmarkArrow_tox, NOT_USED);
        float toY = a.getFloat(R.styleable.CoachmarkArrow_toy, NOT_USED);
        float arrow1X = a.getFloat(R.styleable.CoachmarkArrow_arrow1x, NOT_USED);
        float arrow1Y = a.getFloat(R.styleable.CoachmarkArrow_arrow1y, NOT_USED);
        float arrow2X = a.getFloat(R.styleable.CoachmarkArrow_arrow2x, NOT_USED);
        float arrow2Y = a.getFloat(R.styleable.CoachmarkArrow_arrow2y, NOT_USED);

        if (fromX == NOT_USED || fromY == NOT_USED || toX == NOT_USED || toY == NOT_USED) {
            throw new InvalidParameterException("fromx, fromy, tox, toy must be specified");
        }

        this.fromPoint = new PointF(fromX, fromY);
        this.toPoint = new PointF(toX, toY);

        if (midX != NOT_USED && midY != NOT_USED) {
            this.midPoint = new PointF(midX, midY);
        }
        if (arrow1X != NOT_USED && arrow1Y != NOT_USED) {
            this.arrow1Point = new PointF(arrow1X, arrow1Y);
        }
        if (arrow2X != NOT_USED && arrow2Y != NOT_USED) {
            this.arrow2Point = new PointF(arrow2X, arrow2Y);
        }


        processPaint(a);

        a.recycle();
    }


    private void processPoints(TypedArray a) {
        float fromX = a.getFloat(R.styleable.CoachmarkArrow_fromx, NOT_USED);
        float fromY = a.getFloat(R.styleable.CoachmarkArrow_fromy, NOT_USED);
        float midX = a.getFloat(R.styleable.CoachmarkArrow_midx, NOT_USED);
        float midY = a.getFloat(R.styleable.CoachmarkArrow_midy, NOT_USED);
        float toX = a.getFloat(R.styleable.CoachmarkArrow_tox, NOT_USED);
        float toY = a.getFloat(R.styleable.CoachmarkArrow_toy, NOT_USED);
        float arrow1X = a.getFloat(R.styleable.CoachmarkArrow_tox, NOT_USED);
        float arrow1Y = a.getFloat(R.styleable.CoachmarkArrow_toy, NOT_USED);
        float arrow2X = a.getFloat(R.styleable.CoachmarkArrow_tox, NOT_USED);
        float arrow2Y = a.getFloat(R.styleable.CoachmarkArrow_toy, NOT_USED);

        // The exception below will only be thrown during development as it
        // will be caused by not all parameters being in the layout file.
        if (fromX == NOT_USED || fromY == NOT_USED || toX == NOT_USED || toY == NOT_USED) {
            throw new InvalidParameterException("fromx, fromy, tox, toy must be specified");
        }

        this.fromPoint = new PointF(fromX, fromY);
        this.toPoint = new PointF(toX, toY);

        if (midX != NOT_USED && midY != NOT_USED) {
            this.midPoint = new PointF(midX, midY);
        }
        if (arrow1X != NOT_USED && arrow1Y != NOT_USED && arrow2X != NOT_USED && arrow2Y != NOT_USED) {
            this.arrow1Point = new PointF(arrow1X, arrow1Y);
            this.arrow2Point = new PointF(arrow2X, arrow2Y);
        }
    }

    private void processPaint(TypedArray a) {
        paint = new Paint();

        paint.setColor(a.getColor(R.styleable.CoachmarkArrow_col, Color.WHITE));
        paint.setStrokeWidth(a.getDimensionPixelSize(R.styleable.CoachmarkArrow_linewidth, 0));
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // If there is a mid-point, draw a spline, otherwise just draw a line.
        if (this.midPoint != null) {
            float[] x = new float[3];
            float[] y = new float[3];
            if (fromPoint.x > toPoint.x) {
                x[0] = toPoint.x * width;
                x[1] = midPoint.x * width;
                x[2] = fromPoint.x * width;
                y[0] = toPoint.y * height;
                y[1] = midPoint.y * height;
                y[2] = fromPoint.y * height;
            } else {
                x[0] = fromPoint.x * width;
                x[1] = midPoint.x * width;
                x[2] = toPoint.x * width;
                y[0] = fromPoint.y * height;
                y[1] = midPoint.y * height;
                y[2] = toPoint.y * height;
            }

            int start = (int) x[0];
            int end = (int) x[2];

            int lastX = -1;
            int lastY = -1;
            Spline spline = Spline.createSpline(x, y);
            for (int aX = start; aX <= end; aX++) {
                int aY = (int) spline.interpolate(aX);
                if (lastX != -1) {
                    canvas.drawLine(lastX, lastY, aX, aY, paint);
                }
                lastX = aX;
                lastY = aY;
            }

        } else {
            canvas.drawLine(fromPoint.x * width, fromPoint.y * height, toPoint.x * width, toPoint.y * height, paint);
        }

        // Draw the arrow if needed.
        if (this.arrow1Point != null) {
            canvas.drawLine(arrow1Point.x * width, arrow1Point.y * height, toPoint.x * width, toPoint.y * height, paint);
            canvas.drawLine(arrow2Point.x * width, arrow2Point.y * height, toPoint.x * width, toPoint.y * height, paint);
        }
    }
}

