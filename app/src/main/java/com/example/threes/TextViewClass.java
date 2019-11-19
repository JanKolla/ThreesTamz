package com.example.threes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;



@SuppressLint("AppCompatCustomView")
public class TextViewClass extends TextView {
    public TextViewClass(Context context) {
        super(context);
    }

    public TextViewClass(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewClass(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int r =getMeasuredWidth();
        setMeasuredDimension(r,r);
    }

    public void setTileText(int val){
        if(val<100){
            setTextSize(40);
        }else if(val<1000){
            setTextSize(30);
        }else{
            setTextSize(20);
        }
        if(val>2){
            setTextColor(Color.BLACK);
        } else{
            setTextColor(Color.WHITE);
        }
        GradientDrawable gradientDrawable=(GradientDrawable)this.getBackground();
        gradientDrawable.setColor(Game.getGame().getColor(val));
        setBackground(gradientDrawable);
        if(val==0){
            setText(" ") ;
        }else{
            setText(""+val);
        }
    }
}
