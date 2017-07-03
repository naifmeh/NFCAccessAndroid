package com.example.lasse.nfcinterface.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.example.lasse.nfcinterface.R;

import java.util.ArrayList;

/**
 * Created by lasse on 28/06/17.
 */

public class PulsatingView extends RelativeLayout{
    private static final int DEFAULT_PULSE_COUNT=4;
    private static final int DEFAULT_DURATION_TIME=2000;
    private static final float DEFAULT_SCALE = 3.0f;
    private static final int DEFAULT_FILL_TYPE=0;

    private int pulseColor;
    private float pulseStrokeWidth;
    private float pulseRadius;
    private int pulseDurationTime;
    private int pulseAmount;
    private int pulseDelay;
    private float pulseScale;
    private int pulseType;
    private Paint paint;
    private boolean animationRunning=false;
    private AnimatorSet animatorSet;
    private ArrayList<Animator> animatorList;
    private LayoutParams pulseParams;
    private ArrayList<PulseView> pulseViewList = new ArrayList<PulseView>();


    public PulsatingView(Context context) {
        super(context);
    }

    public PulsatingView(Context context, AttributeSet attrs) {
        super(context,attrs);
        init(context,attrs);
    }
    public PulsatingView(Context context,AttributeSet attrs,int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        init(context,attrs);
    }

    private void init(final Context context,AttributeSet attrs) {
        if(isInEditMode()) {
            return;
        }

        if(attrs == null) {
            throw new IllegalArgumentException(getResources().getString(R.string.errorAttrSet));

        }
        final TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.PulsatingView);
        pulseColor = typedArray.getColor(R.styleable.PulsatingView_ps_color, ContextCompat.getColor(getContext(),R.color.colorAccent));
        pulseStrokeWidth=typedArray.getDimension(R.styleable.PulsatingView_ps_strokeWidth, getResources().getDimension(R.dimen.pulseStrokeWidth));
        pulseRadius = typedArray.getDimension(R.styleable.PulsatingView_ps_radius,getResources().getDimension(R.dimen.pulseRadius));
        pulseDurationTime = typedArray.getInt(R.styleable.PulsatingView_ps_duration,DEFAULT_DURATION_TIME);
        pulseAmount = typedArray.getInt(R.styleable.PulsatingView_ps_rippleAmount,DEFAULT_PULSE_COUNT);
        pulseScale = typedArray.getFloat(R.styleable.PulsatingView_ps_scale,DEFAULT_SCALE);
        pulseType = typedArray.getInt(R.styleable.PulsatingView_ps_type,DEFAULT_FILL_TYPE);
        typedArray.recycle();

        pulseDelay = pulseDurationTime/pulseAmount;

        paint = new Paint();
        paint.setAntiAlias(true);
        if(pulseType==DEFAULT_FILL_TYPE) {
            pulseStrokeWidth = 0;
            paint.setStyle(Paint.Style.FILL);
        } else {
            paint.setStyle(Paint.Style.STROKE);
        }
        paint.setColor(pulseColor);

        pulseParams = new LayoutParams((int)(2*(pulseRadius+pulseStrokeWidth)),(int)(2*(pulseRadius+pulseStrokeWidth)));
        pulseParams.addRule(CENTER_IN_PARENT,TRUE);

        animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorList
                 = new ArrayList<Animator>();
        for(int i=0;i<pulseAmount;i++) {
            PulseView pulseView = new PulseView(getContext());
            addView(pulseView,pulseParams);
            pulseViewList.add(pulseView);
            final ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(pulseView,"ScaleX",1.0f,pulseScale);
            scaleXAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            scaleXAnimator.setRepeatMode(ObjectAnimator.RESTART);
            scaleXAnimator.setStartDelay(i*pulseDelay);
            scaleXAnimator.setDuration(pulseDurationTime);
            animatorList.add(scaleXAnimator);

            final ObjectAnimator scaleYanimator = ObjectAnimator.ofFloat(pulseView,"ScaleY",1.0f,pulseScale);
            scaleYanimator.setRepeatCount(ObjectAnimator.INFINITE);
            scaleYanimator.setRepeatMode(ObjectAnimator.RESTART);
            scaleYanimator.setStartDelay(i*pulseDelay);
            scaleYanimator.setDuration(pulseDurationTime);
            animatorList.add(scaleYanimator);

            final ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(pulseView,"Alpha",1.0f,0f);
            alphaAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            alphaAnimator.setRepeatMode(ObjectAnimator.RESTART);
            alphaAnimator.setStartDelay(i*pulseDelay);
            alphaAnimator.setDuration(pulseDurationTime);
            animatorList.add(alphaAnimator);

        }
        animatorSet.playTogether(animatorList);
    }

    public void startPulseAnimation() {
        if(!isPulseAnimationRunning()) {
            for(PulseView pulseview:pulseViewList) {
                pulseview.setVisibility(VISIBLE);
            }
            animatorSet.start();
            animationRunning=true;
        }
    }
    public void setBcgColor(int color) {
        pulseColor = color;
    }

    public void stopPulseAnimation() {
        if(isPulseAnimationRunning()) {
            animatorSet.end();
            animationRunning=false;
        }

    }

    public boolean isPulseAnimationRunning() {
        return animationRunning;
    }

    private class PulseView extends View {
        public PulseView(Context context) {
            super(context);
            this.setVisibility(View.INVISIBLE);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            int radius = (Math.min(getWidth(),getHeight()))/2;
            canvas.drawCircle(radius,radius,radius-pulseStrokeWidth,paint);

        }
    }


}
