package com.zpone.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Created by user on 2017/12/5.
 * Power by cly
 */

public class MyProgressBar extends ProgressBar {
        String text;
        Paint mPaint;

        public MyProgressBar(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
            initText();
        }

        public MyProgressBar(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            // TODO Auto-generated constructor stub
            initText();
        }


        public MyProgressBar(Context context, AttributeSet attrs) {
            super(context, attrs);
            // TODO Auto-generated constructor stub
            initText();
        }

        @Override
        public synchronized void setProgress(int progress) {
            // TODO Auto-generated method stub
            setText(progress);
            super.setProgress(progress);

        }

        @Override
        protected synchronized void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.onDraw(canvas);
            //this.setText();
            Rect rect = new Rect();
            this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
            int x = (getWidth() / 2) - rect.centerX();
            int y = (getHeight() / 2) - rect.centerY();
            canvas.drawText(this.text, x, y, this.mPaint);
        }

        //初始化，画笔
        private void initText(){
            mPaint = new Paint();
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(40);
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            mPaint.setAntiAlias(true);


        }

        private void setText(){
            setText(this.getProgress());
        }

        //设置文字内容
        private void setText(int progress){
            this.text = String.valueOf(progress);
        }


    }