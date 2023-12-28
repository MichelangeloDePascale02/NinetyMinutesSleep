package com.swdp31plus.ninetyminutessleep.ui.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.concurrent.TimeUnit;

public class SemiCircleView extends View {

    private Paint foregroundPaint;
    private RectF foregroundRect;
    private Paint backgroundPaint;
    private RectF backgroundRect;
    private int progress;  // Valore della ProgressBar
    private long millis;
    private float strokeWidth = 40;  // Spessore del semicerchio
    private SharedPreferences preferences;

    public SemiCircleView(Context context) {
        super(context);
        init();
    }

    public SemiCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SemiCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        foregroundPaint = new Paint();
        foregroundPaint.setAntiAlias(true);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(strokeWidth);

        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);

        TypedValue typedValue = new TypedValue();
        TypedArray arr;
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(android.R.attr.colorPrimary,typedValue,false);
        arr = getContext().obtainStyledAttributes(typedValue.data, new int[]{
                android.R.attr.colorPrimary});
        int primaryColor = arr.getColor(0,-1);
        arr.recycle();

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            backgroundPaint.setColor(Color.DKGRAY);
        } else {
            backgroundPaint.setColor(Color.LTGRAY);
        }

        foregroundPaint.setColor(primaryColor);

        foregroundRect = new RectF();
        backgroundRect = new RectF();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();  // Richiama onDraw per ridisegnare la view
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getWidth() / 2;
        int margin = 100;

        // Imposta i rettangoli del semicerchio
        backgroundRect.set(margin, margin, width - margin, height * 2 - margin);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        foregroundRect.set(margin, margin, width - margin, height * 2 - margin);
        foregroundPaint.setStyle(Paint.Style.STROKE);

        // Disegna il semicerchio cavo
        float startAngle = 180;
        float sweepAngle = (float) progress / 100 * 180;  // Scala il progresso a un angolo
        canvas.drawArc(backgroundRect, startAngle, 180, false, backgroundPaint);
        canvas.drawArc(foregroundRect, startAngle, sweepAngle, false, foregroundPaint);

        @SuppressLint("DefaultLocale") String ms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        foregroundPaint.setStyle(Paint.Style.FILL);
        foregroundPaint.setTextSize(150);  // Regola la dimensione del testo come desiderato
        foregroundPaint.setTextAlign(Paint.Align.CENTER);

        // Calcola le coordinate per il centro della vista
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2 + 260;

        // Disegna il testo al centro
        canvas.drawText(ms, centerX, centerY, foregroundPaint);
    }
}

