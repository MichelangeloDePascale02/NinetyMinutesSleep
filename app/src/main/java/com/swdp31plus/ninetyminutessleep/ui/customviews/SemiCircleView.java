package com.swdp31plus.ninetyminutessleep.ui.customviews;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.swdp31plus.ninetyminutessleep.R;

public class SemiCircleView extends View {

    private Paint paint;
    private RectF outerRect;
    private int progress;  // Valore della ProgressBar
    private float strokeWidth = 60;  // Spessore del semicerchio

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
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(android.R.attr.colorPrimary,typedValue,false);
        TypedArray arr = getContext().obtainStyledAttributes(typedValue.data, new int[]{
                android.R.attr.colorPrimary});
        int primaryColor = arr.getColor(0,-1);
        paint.setColor(primaryColor);  // Colore del semicerchio

        outerRect = new RectF();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();  // Richiama onDraw per ridisegnare la view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getWidth() / 2;
        int margin = 100;

        // Imposta i rettangoli del semicerchio
        outerRect.set(margin, margin, width - margin, height * 2 - margin);

        // Disegna il semicerchio cavo
        float startAngle = 180;
        float sweepAngle = (float) progress / 100 * 180;  // Scala il progresso a un angolo
        canvas.drawArc(outerRect, startAngle, sweepAngle, false, paint);
    }
}

