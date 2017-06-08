// Copyright (C) 2015 Peter Robinson
package com.nelladragon.common.thankyou;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.nelladragon.common.crypto.Rand;

/**
 * Displays fireworks.
 */
public class FireworksView extends ImageView {
	private static final String TAG = FireworksView.class.getSimpleName();
	
    private Paint narrowLinePaint;

    int latestW, latestH;
    public static final int NUM_POINTS = 10;
    private static final int TARGETS_SCALE = 2;

    public int numTargets = 2;

    int[] targetX, targetY, colors;
    int[][] targetPointsX, targetPointsY, colorsPoints;


    // This constructor might not be used...
    public FireworksView(Context c) {
    	super(c);
    	setup();
    }

    // This constructor is called as a result of the XML layout.
    public FireworksView(Context c, AttributeSet attr) {
    	super(c, attr);
    	setup();
    }
    
    private void setup() {
    	setScaleType(ScaleType.FIT_XY);
        setBackgroundColor(Color.BLACK);

        this.narrowLinePaint = new Paint();
        this.narrowLinePaint.setAntiAlias(true);
        this.narrowLinePaint.setDither(true);
        this.narrowLinePaint.setStyle(Paint.Style.STROKE);
        this.narrowLinePaint.setStrokeJoin(Paint.Join.ROUND);
        this.narrowLinePaint.setStrokeCap(Paint.Cap.ROUND);
        this.narrowLinePaint.setStrokeWidth(5);    	
    }
    

    public void setAmount(int amount) {
        this.numTargets = amount * TARGETS_SCALE;
    }

    public void fireAgain() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // Recalculate the fireworks.
                calculateFireWorks();
                // Request the UI thread redraw the view.
                postInvalidate();
            }
        });

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.latestW = w;
        this.latestH = h;

        determineFireWorkParameters();
        calculateFireWorks();


        //invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawFireWorks(canvas);
    }


    int targetRangeMinX;
    int targetRangeMaxX;
    int targetRangeMinY;
    int targetRangeMaxY;
    int fireworkRadius;
    private static final float relativeRadius = 0.1f;


    public void determineFireWorkParameters() {
        int shortestSide = (this.latestH > this.latestW) ? this.latestW : this.latestH;
        this.fireworkRadius = (int) (shortestSide * relativeRadius);
        this.targetRangeMinX = this.fireworkRadius;
        this.targetRangeMaxX = this.latestW - this.fireworkRadius;
        this.targetRangeMinY  = this.fireworkRadius;
        this.targetRangeMaxY = this.latestH - this.fireworkRadius;
    }


    public void calculateFireWorks() {
        this.targetX = new int[this.numTargets];
        this.targetY = new int[this.numTargets];
        this.colors = new int[this.numTargets];

        this.targetPointsX = new int[this.numTargets][NUM_POINTS];
        this.targetPointsY = new int[this.numTargets][NUM_POINTS];
        this.colorsPoints = new int[this.numTargets][NUM_POINTS];

        for (int i = 0; i < numTargets; i++) {
            calculateOneFireWork(i);
        }
    }

    public void calculateOneFireWork(int fireworkNum) {
        int color = getRandomVisibleColor();
        int targetX = (Rand.generateRandomPositiveInt() % (this.targetRangeMaxX - this.targetRangeMinX)) + this.targetRangeMinX;
        int targetY = (Rand.generateRandomPositiveInt() % (this.targetRangeMaxY - this.targetRangeMinY)) + this.targetRangeMinY;

        this.colors[fireworkNum] = color;
        this.targetX[fireworkNum] = targetX;
        this.targetY[fireworkNum] = targetY;

        for (int i= 0; i < NUM_POINTS; i++) {
            int angle = Rand.generateRandomPositiveInt() % 360;
            double randians = Math.toRadians(angle);
            this.targetPointsX[fireworkNum][i] = (int) (this.fireworkRadius * Math.sin(randians)) + targetX;
            this.targetPointsY[fireworkNum][i] = (int) (this.fireworkRadius * Math.cos(randians)) + targetY;
            this.colorsPoints[fireworkNum][i] = getRandomVisibleColor();
        }
    }

    private void drawFireWorks(Canvas canvas) {
        for (int i = 0; i < numTargets; i++) {
            drawOneFireWork(canvas, i);
        }
    }

    private void drawOneFireWork(Canvas canvas, int fireworkNum) {
        this.narrowLinePaint.setColor(this.colors[fireworkNum]);
        canvas.drawLine(0, this.latestH, this.targetX[fireworkNum], this.targetY[fireworkNum], this.narrowLinePaint);

        for (int i= 0; i < NUM_POINTS; i++) {
            this.narrowLinePaint.setColor(this.colorsPoints[fireworkNum][i]);
            canvas.drawLine(this.targetX[fireworkNum], this.targetY[fireworkNum],
                    this.targetPointsX[fireworkNum][i], this.targetPointsY[fireworkNum][i], this.narrowLinePaint);
        }
    }


    public static final String SAVE_NUM = "NUM";

    @Override
    public Parcelable onSaveInstanceState() {

        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt(SAVE_NUM, this.numTargets);

        // ... save everything
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            this.numTargets = bundle.getInt(SAVE_NUM);

            // ... load everything
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }


    public static int getRandomVisibleColor() {
        int color = Rand.generateRandomPositiveInt();
        color &= 0xffffff;
        color |= 0xff404040;
        return color;
    }
}

