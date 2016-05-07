package org.lazysource.uberprogressview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * @author ishan
 * @since 07/05/16
 */
public class UberProgressView extends View {

    private static final String TAG = UberProgressView.class.getSimpleName();
    private static final String CENTER_CIRCLE_MAIN = "CENTER_CIRCLE_MAIN";
    private static final String UBER_PROGRESS_VIEW = "UBER_PROGRESS_VIEW";


    // Stationary Solid Circle Fields
    private float cXStationary;
    private float cYStationary;
    private float rStationary;
    private final Paint mPaintStationaryCircle;

    // Stationary Growing-Fading Circle Fields
    private float rStationaryGF;
    private Paint mPaintGrowingFadingCircle;

    // Orbiting Solid Circle Fields
    private float cXOrbiting;
    private float cYOrbiting;
    private float rOrbiting;
    private float orbitPathRadius;
    private final Paint mPaintOrbitingCircle1;
    private final Paint mPaintOrbitingCircle2;
    private final Paint mPaintOrbitingCircle3;
    private final Paint mPaintOrbitingCircle4;


    // Animation calculation fields
    private final float totalAnimationTime = 100;
    private float currentAnimationTime = 0;
    private float delta = 0;
    private float theta = 0;

    AccelerateDecelerateInterpolator accelerateDecelerateInterpolator;
    DecelerateInterpolator decelerateInterpolator;

    public UberProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPaintStationaryCircle = new Paint();
        this.mPaintStationaryCircle.setColor(Color.parseColor("#29B6F6"));
        this.mPaintGrowingFadingCircle = new Paint();
        this.mPaintGrowingFadingCircle.setColor(Color.parseColor("#29B6F6"));
        this.mPaintOrbitingCircle1 = new Paint();
        this.mPaintOrbitingCircle1.setColor(Color.parseColor("#29B6F6"));
        this.mPaintOrbitingCircle2 = new Paint();
        this.mPaintOrbitingCircle2.setColor(Color.parseColor("#29B6F6"));
        this.mPaintOrbitingCircle2.setAlpha(191);
        this.mPaintOrbitingCircle3 = new Paint();
        this.mPaintOrbitingCircle3.setColor(Color.parseColor("#29B6F6"));
        this.mPaintOrbitingCircle3.setAlpha(127);
        this.mPaintOrbitingCircle4 = new Paint();
        this.mPaintOrbitingCircle4.setColor(Color.parseColor("#29B6F6"));
        this.mPaintOrbitingCircle4.setAlpha(64);


        rStationary = 12f;
        rStationaryGF = 0f;
        rOrbiting = rStationary / 2;
        orbitPathRadius = 4 * rStationary;

        accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
        decelerateInterpolator = new DecelerateInterpolator(1.0f);
        animationCalculationsRunnable.run();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cXStationary = w / 2;
        cYStationary = h / 2;

        cXOrbiting = cXStationary;
        cYOrbiting = cYStationary - (orbitPathRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(cXStationary, cYStationary, rStationary, mPaintStationaryCircle);
        canvas.drawCircle(cXStationary, cYStationary, rStationaryGF, mPaintGrowingFadingCircle);

        rStationaryGF = rStationary * (4 * decelerateInterpolator.getInterpolation(delta));

        int alpha = 100 - (int)(delta * 100);
        mPaintGrowingFadingCircle.setAlpha(alpha);
        theta = (360 * delta) - 90;

        drawCircle(canvas, theta, mPaintOrbitingCircle1);


        float lagFactor1 = 15 - (delta * 5);
        float lagFactor2 = 30 - (delta * 10);
        float lagFactor3 = 45 - (delta * 15);

//        Log.e(TAG, "delta = " + delta + ", theta = " + theta
//                + ", lf1 = " + lagFactor1
//                + ", lf2 = " + lagFactor2
//                + ", lf3 = " + lagFactor3);
        if (theta > 15 && theta < 350) {
            drawCircle(canvas, theta - lagFactor1, mPaintOrbitingCircle2);

            drawCircle(canvas, theta - lagFactor2, mPaintOrbitingCircle3);

            drawCircle(canvas, theta - lagFactor3, mPaintOrbitingCircle4);
        }

    }

    private void drawCircle(Canvas canvas, float theta, Paint paint) {

        double thetaInRadians = Math.toRadians(theta);

        float oribitingCX = cXStationary + (orbitPathRadius * (float) Math.cos(thetaInRadians));
        float oribitingCY = cYStationary + (orbitPathRadius * (float) Math.sin(thetaInRadians));

        canvas.drawCircle(oribitingCX, oribitingCY, rOrbiting, paint);
    }

    final Runnable animationCalculationsRunnable = new Runnable() {
        @Override
        public void run() {

            if (currentAnimationTime >= 0) {
                currentAnimationTime += 5;
                delta = currentAnimationTime / totalAnimationTime;
                if (delta >= 1.0) {
                    currentAnimationTime = 0;
                    rStationaryGF = 0f;
                }
                delta = accelerateDecelerateInterpolator.getInterpolation(delta);
                postDelayed(this, 60);
                invalidate();
            }

        }
    };

    private static void printCoordinates(String coordinatesOf, float x, float y){
        Log.e(TAG, coordinatesOf + "(" + x + "," + y + ")");
    }

    private static void printSize(String sizeOf, int width, int height){
        Log.e(TAG, sizeOf + "(width = " + width + ", height = " + height + ")");
    }
}