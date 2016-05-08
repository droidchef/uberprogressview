package org.lazysource.uberprogressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * @author ishan
 * @since 07/05/16
 */
public class UberProgressView extends View {

    private static final String TAG = UberProgressView.class.getSimpleName();
    private static final int MAX_FADING_CIRCLE_ALPHA = 60;
    private static final float TRAILING_FUNCTION_CHANGE_THRESHOLD = 0.90f;
    private float cXStationary;
    private float cYStationary;

    private float rStationary;
    private float rStationaryGF = 0f;
    private float rOrbiting;

    private float orbitPathDistanceFromCenter;

    private final Paint mPaintStationaryCircle = new Paint();
    private Paint mPaintGrowingFadingCircle = new Paint();
    private final Paint mPaintOrbitingCircle1 = new Paint();
    private final Paint mPaintOrbitingCircle2 = new Paint();
    private final Paint mPaintOrbitingCircle3 = new Paint();
    private final Paint mPaintOrbitingCircle4 = new Paint();

    private int stationaryCircleColor;
    private int fadingCircleColor;
    private int oribitingCircleColor;

    // Animation calculation fields
    private float totalAnimationTime;
    private float currentAnimationTime = 0;
    private float delta = 0;
    private float theta = 0;

    private int fadingCircleAlpha = 100;

    float movementFactor1, movementFactor2, movementFactor3;

    AccelerateDecelerateInterpolator accelerateDecelerateInterpolator;
    DecelerateInterpolator decelerateInterpolator;

    public UberProgressView(Context context) {
        super(context);
        init();
    }

    public UberProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public UberProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        animationCalculationsRunnable.run();
    }

    private void init(Context context, AttributeSet attributeSet) {

        final TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.UberProgressView,
                0, 0
        );

        try {

            stationaryCircleColor = typedArray.getColor(R.styleable.UberProgressView_stationary_circle_color, Color.parseColor("#29B6F6"));
            fadingCircleColor = typedArray.getColor(R.styleable.UberProgressView_fading_circle_color, Color.parseColor("#29B6F6"));
            oribitingCircleColor = typedArray.getColor(R.styleable.UberProgressView_orbiting_circle_color, Color.parseColor("#29B6F6"));
            rStationary = typedArray.getDimension(R.styleable.UberProgressView_stationary_circle_radius, 12f);
            float orbitingCircleRadius = typedArray.getDimension(R.styleable.UberProgressView_orbiting_circle_radius, 6f);
            // In order to make sure the orbiting circles are at least 75% the
            // size of the stationary circle this check is in place.
            if (orbitingCircleRadius > (rStationary / 3)) {
                rOrbiting = rStationary / 2;
            } else {
                rOrbiting = orbitingCircleRadius;
            }
            // This controls the speed of the animation where total animation
            // time is inversely proportional to the speed of the animation.
            totalAnimationTime = typedArray.getInteger(R.styleable.UberProgressView_animation_time, 100);

        } finally {
            typedArray.recycle();
        }

        setupColorPallets();

        setupInitialValuesForAnimation();
    }

    private void init() {

        stationaryCircleColor = Color.parseColor("#29B6F6");
        fadingCircleColor = Color.parseColor("#29B6F6");
        oribitingCircleColor = Color.parseColor("#29B6F6");
        rStationary = 12f;
        rOrbiting = rStationary / 2;
        totalAnimationTime = 100;

        setupColorPallets();

        setupInitialValuesForAnimation();
    }

    private void setupInterpolators() {
        accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
        decelerateInterpolator = new DecelerateInterpolator(1.0f);
    }

    private void setupInitialValuesForAnimation() {
        orbitPathDistanceFromCenter = 4 * rStationary;
        setupInterpolators();
    }

    private void setupColorPallets() {
        mPaintGrowingFadingCircle.setColor(fadingCircleColor);
        mPaintGrowingFadingCircle.setAntiAlias(true);
        mPaintStationaryCircle.setColor(stationaryCircleColor);
        mPaintStationaryCircle.setAntiAlias(true);
        mPaintOrbitingCircle1.setColor(oribitingCircleColor);
        mPaintOrbitingCircle1.setAntiAlias(true);
        mPaintOrbitingCircle2.setColor(oribitingCircleColor);
        mPaintOrbitingCircle2.setAlpha(191);
        mPaintOrbitingCircle2.setAntiAlias(true);
        mPaintOrbitingCircle3.setColor(oribitingCircleColor);
        mPaintOrbitingCircle3.setAlpha(127);
        mPaintOrbitingCircle3.setAntiAlias(true);
        mPaintOrbitingCircle4.setColor(oribitingCircleColor);
        mPaintOrbitingCircle4.setAlpha(64);
        mPaintOrbitingCircle4.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cXStationary = w / 2;
        cYStationary = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // This draws the stationary circle in the center of the view.
        canvas.drawCircle(cXStationary, cYStationary, rStationary, mPaintStationaryCircle);

        // This is the lighter circle that grows bigger in size over time and fades away.
        canvas.drawCircle(cXStationary, cYStationary, rStationaryGF, mPaintGrowingFadingCircle);

        mPaintGrowingFadingCircle.setAlpha(fadingCircleAlpha);

        drawCircle(canvas, theta, mPaintOrbitingCircle1);

        if (theta > 30 && theta <= 360) {
            drawCircle(canvas, theta - movementFactor1, mPaintOrbitingCircle2);
            drawCircle(canvas, theta - movementFactor2, mPaintOrbitingCircle3);
            drawCircle(canvas, theta - movementFactor3, mPaintOrbitingCircle4);
        }

    }

    private void drawCircle(Canvas canvas, float theta, Paint paint) {

        double thetaInRadians = Math.toRadians(theta);

        float oribitingCX = cXStationary + (orbitPathDistanceFromCenter * (float) Math.cos(thetaInRadians));
        float oribitingCY = cYStationary + (orbitPathDistanceFromCenter * (float) Math.sin(thetaInRadians));

        canvas.drawCircle(oribitingCX, oribitingCY, rOrbiting, paint);
    }

    private float getLagFactor(float K) {
        // LF = K - (delta * (K / 3))
        return (K - (delta * ( K / 3)));
    }

    private float getTrailFactor(float K) {
        // LF = ((4 * K * delta) - (3 * K)) / 2
        return ((4 * K * delta) - (3 * K)) / 16;
    }

    final Runnable animationCalculationsRunnable = new Runnable() {
        @Override
        public void run() {

            if (currentAnimationTime >= 0) {
                currentAnimationTime += 5;
                delta = accelerateDecelerateInterpolator.getInterpolation(currentAnimationTime/totalAnimationTime);
                rStationaryGF = rStationary * (4 * decelerateInterpolator.getInterpolation(delta));
                if (delta >= 1.0) {
                    currentAnimationTime = 0;
                    rStationaryGF = 0f;
                }
                fadingCircleAlpha = MAX_FADING_CIRCLE_ALPHA - (int)(delta * MAX_FADING_CIRCLE_ALPHA);
                theta = (360 * delta) - 90;
                if (delta < TRAILING_FUNCTION_CHANGE_THRESHOLD) {
                    movementFactor1 = getLagFactor(15);
                    movementFactor2 = getLagFactor(30);
                    movementFactor3 = getLagFactor(45);
                } else {
                    movementFactor1 = getTrailFactor(15);
                    movementFactor2 = getTrailFactor(30);
                    movementFactor3 = getTrailFactor(45);
                }
                postDelayed(this, 60);
                invalidate();
            }

        }
    };

}
