package com.clem.ipoca1.core.view.CornerView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;

import com.clem.ipoca1.core.R;


/**
 * Created by zengxianzi on 2016/5/31.
 */
public class CornerTextView extends android.support.v7.widget.AppCompatTextView {

    float radius = 0.0f;

    public void setTop_left_radius(float top_left_radius) {
        this.top_left_radius = top_left_radius;
        renderView();
    }

    public void setTop_right_radius(float top_right_radius) {
        this.top_right_radius = top_right_radius;
        renderView();
    }

    public void setBottom_right_radius(float bottom_right_radius) {
        this.bottom_right_radius = bottom_right_radius;
        renderView();
    }

    public void setBottom_left_radius(float bottom_left_radius) {
        this.bottom_left_radius = bottom_left_radius;
        renderView();
    }

    public void setBg(int bg) {
        this.bg = bg;
        renderView();
    }

    float top_left_radius = 0.0f;
    float top_right_radius = 0.0f;
    float bottom_right_radius = 0.0f;
    float bottom_left_radius = 0.0f;
    float border = 0.0f;
    int borderColor = 0;
    int bg = 0;

    public CornerTextView(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public CornerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public CornerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CornerView, defStyleAttr, defStyleAttr);
        radius = a.getDimension(R.styleable.CornerView_radius, 0);
        top_left_radius = a.getDimension(R.styleable.CornerView_top_left_radius, 0);
        top_right_radius = a.getDimension(R.styleable.CornerView_top_right_radius, 0);
        bottom_right_radius = a.getDimension(R.styleable.CornerView_bottom_right_radius, 0);
        bottom_left_radius = a.getDimension(R.styleable.CornerView_bottom_left_radius, 0);
        border = a.getDimension(R.styleable.CornerView_border, 0);
        bg = a.getColor(R.styleable.CornerView_backgroundColor, 0);
        borderColor = a.getColor(R.styleable.CornerView_borderColor,bg);
        setGravity(Gravity.CENTER);
        renderView();
        a.recycle();
    }

    @Override
    public void setBackgroundColor(int color) {
        bg = color;
        renderView();
    }

    public void setRadius(float radius){
        this.radius = radius;
        renderView();
    }

    public void setBorder(float border){
        this.border = border;
        renderView();
    }

    public void setBorderColor(int borderColor){
        this.borderColor = borderColor;
        renderView();
    }

    private void renderView(){

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(bg);
        gd.setStroke((int) border, borderColor);
        if (radius != 0) {
            gd.setCornerRadius(radius);
        }else{
            float[] r = new float[]{top_left_radius,top_left_radius,top_right_radius,top_right_radius,
                    bottom_right_radius,bottom_right_radius,bottom_left_radius,bottom_left_radius};
            gd.setCornerRadii(r);
        }
        setBackgroundDrawable(gd);
        invalidate();
    }

}
