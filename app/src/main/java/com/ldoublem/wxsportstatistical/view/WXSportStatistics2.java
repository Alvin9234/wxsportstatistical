package com.ldoublem.wxsportstatistical.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.ldoublem.wxsportstatistical.R;

import java.util.ArrayList;
import java.util.List;


public class WXSportStatistics2 extends View implements OnTouchListener {

    float mHigh = 0;//高度
    float Width = 0;//宽度
    List<Integer> listValue = new ArrayList<>();//点
    List<RectF> listRectF = new ArrayList<>();//每列对应的区域
    List<String> listDAY = new ArrayList<>();//底部日期
    int MaxValue = 0;
    float minAbsY;//最小y,显示在X轴,结合坐标轴，每个点的相应y值为minAbsY - 绝对高度
    float maxAbsY;//最大y,结合坐标轴，每个点的相应y值为文字高度 + CircleR

    private Paint paintBg;//背景
    private Paint paint;//折线,点
    private Paint paintText;//绘制文字，未选中
    private Paint paintText2;//绘制文字，选中

    private Path path;
    Shader mShader;//阴影
    int color_w = Color.argb(200, 255, 255, 255);

    int CircleR = 5;//点半径
    //4fd4d0
    int bottomH = 0;//距离底部高度，给文字显示用的

    int topH = 0;//距离顶部部高度，给选中后的文字显示用的

    Context context;

    //选中
    public int select = -1;
    public int selectbottom = -1;

    public WXSportStatistics2(Context context, AttributeSet attrs) {
        super(context, attrs);
        CircleR = DensityUtil.dip2px(context, 3);
        bottomH = DensityUtil.dip2px(context, 12);
        topH = DensityUtil.dip2px(context, 16);

        this.context = context;
        initView();
    }

    public WXSportStatistics2(Context context) {
        super(context);
        CircleR = DensityUtil.dip2px(context, 3);
        bottomH = DensityUtil.dip2px(context, 12);
        topH = DensityUtil.dip2px(context, 16);

        this.context = context;
        initView();
    }

    private void initView() {
        setOnTouchListener(this);

        mHigh = getHeight();
        Width = getWidth();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setColor(Color.rgb(255, 255, 255));
        paint.setStyle(Paint.Style.FILL);

        paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setStrokeWidth(4);
        paintText.setColor(getResources().getColor(R.color.textColot));
        paintText.setStyle(Paint.Style.FILL);
        paintText.setTextSize(16);

        paintText2 = new Paint();
        paintText2.setAntiAlias(true);
        paintText2.setStrokeWidth(4);
        paintText2.setColor(Color.WHITE);
        paintText2.setStyle(Paint.Style.FILL);
        paintText2.setTextSize(20);

        paintBg = new Paint();
        paintBg.setAntiAlias(true);
        paintBg.setStrokeWidth(0);
        paintBg.setColor(Color.rgb(255, 255, 255));
        paintBg.setStyle(Paint.Style.FILL);

        path = new Path();
        //线性渐变阴影
        mShader = new LinearGradient(0, 0, 0, getHeight(), new int[]{color_w,
                getResources().getColor(R.color.transparency)}, null, Shader.TileMode.CLAMP);
        paintBg.setShader(mShader);
        maxAbsY = (int) getTextHigh(paintText2)+CircleR;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float High = mHigh - bottomH;
        minAbsY = High - CircleR - 1;

        if (listValue != null && listValue.size() != 0 && listDAY != null && listDAY.size() != 0) {
            int valueSize = listValue.size();
            int daySize = listDAY.size();
            //绘制折线
            for (int i = 0; i < valueSize; i++) {
                float w = (float) (Width / valueSize * (i + 0.5));
                float h;
                if (MaxValue == 0) {
                    h = minAbsY;
                }else{
                    float absHeight = High * listValue.get(i) / MaxValue;
                    if(absHeight >= High){//最大值
                        h = maxAbsY;
                    }else {
                        if(minAbsY - absHeight<= maxAbsY){
                            h = Math.abs(minAbsY - absHeight) + maxAbsY;
                        }else{
                            h = minAbsY - absHeight;
                        }
                    }
                }

                if (i == 0) {
                    path.moveTo(w, h);
                } else if (i == valueSize - 1) {

                    path.lineTo(w, h);
                    path.lineTo(w, High);

                    path.lineTo((float) (Width / valueSize * 0.5), High);

                    path.lineTo((float) (Width / valueSize * 0.5), High - High * listValue.get(0) / MaxValue);

                    path.close();

                    canvas.drawPath(path, paintBg);

                } else {
                    path.lineTo(w, h);
                }

                if (i != valueSize - 1) {
                    float stopX = (float) (Width / valueSize * (i + 1.5));
                    float stopY;
                    if (MaxValue == 0){
                        stopY = minAbsY;
                    }else{
                        float stopAbsHeight = High * listValue.get(i + 1) / MaxValue;

                        if(stopAbsHeight >= High){//最大值
                            stopY = maxAbsY;
                        }else {
                            if(minAbsY - stopAbsHeight<= maxAbsY){
                                stopY = Math.abs(minAbsY - stopAbsHeight) + maxAbsY;
                            }else{
                                stopY = minAbsY - stopAbsHeight;
                            }
                        }
                    }
                    canvas.drawLine(w, h, stopX, stopY, paint);
                }
            }
            listRectF.clear();

            //绘制X轴文字
            for (int i = 0; i < daySize; i++) {
                float w = (float) (Width / valueSize * (i + 0.5));
                String day = listDAY.get(i);
                if (daySize == 7) {
                    paintText.setTextSize(DensityUtil.dip2px(getContext(), 10));
                    paintText2.setTextSize(DensityUtil.dip2px(getContext(), 10));
                    int width = getTextWidth(paintText, day);

                    if (selectbottom == i) {
                        canvas.drawText(day, w - width / 2, mHigh - 2, paintText2);
                        selectbottom = -1;
                    } else {
                        canvas.drawText(day, w - width / 2, mHigh - 2, paintText);
                    }
                } else if (daySize == 30) {
                    paintText.setTextSize(DensityUtil.dip2px(getContext(), 10));
                    paintText2.setTextSize(DensityUtil.dip2px(getContext(), 10));
                    if (i == 0 || i == 7 || i == 15 || i == 22 || i == 29) {

                        int width = getTextWidth(paintText, day);

                        if (selectbottom == i) {

                            if (i == 0) {
                                canvas.drawText(day, w, mHigh - 2, paintText2);

                            } else {
                                canvas.drawText(day, w - width / 2, mHigh - 2, paintText2);

                            }
                            selectbottom = -1;
                        } else {
                            if (i == 0) {
                                canvas.drawText(day, w, mHigh - 2, paintText);

                            } else {
                                canvas.drawText(day, w - width / 2, mHigh - 2, paintText);

                            }
                        }
                    }
                }
            }

            //绘制点
            for (int i = 0; i < valueSize; i++) {
                float w = (float) (Width / valueSize * (i + 0.5));
                float h;
                if (MaxValue == 0){
                    h = minAbsY;//最小值
                }else{
                    int value = listValue.get(i);
                    if(value<=0){
                        h = minAbsY;//最小值
                    }else{
                        float pointAbsY = High * listValue.get(i) / MaxValue;

                        if(pointAbsY >= High){//最大值
                            h = maxAbsY;
                        }else {
                            if(minAbsY - pointAbsY<= maxAbsY){
                                h = Math.abs(minAbsY - pointAbsY) + maxAbsY;
                            }else{
                                h = minAbsY - pointAbsY;
                            }
                        }
                    }
                }

                canvas.drawCircle(w, h, CircleR, paint);

                RectF f1 = new RectF();

                float wrf = (Width / valueSize / 2);

                f1.set(w - wrf, 0, w + wrf, getHeight());

                /*if (select == i) {//选中后，绘制顶文字
                    int width = getTextWidth(paintText2, listValue.get(i) + "");
                    int high = (int) getTextHigh(paintText2);

                    if (w - width / 2 < 0) {
                        canvas.drawText(listValue.get(i) + "", w, high, paintText2);
                    } else if (w + width / 2 > Width) {
                        canvas.drawText(listValue.get(i) + "", w - width, high, paintText2);
                    } else {
                        canvas.drawText(listValue.get(i) + "", w - width / 2, high, paintText2);
                    }
                    select = -1;
                }*/

                listRectF.add(f1);
            }
        }
    }

    Animation popup_enter_bottom;
    Animation popup_out_bottom;
    SelectItem mSelectItem;
    int vid = 0;

    //设置数据
    public void setValue(final List<Integer> listValue, final boolean anim,
                         final List<String> listDay, SelectItem mSelectItem, int vid) {
        this.mSelectItem = mSelectItem;
        this.vid = vid;
        this.listDAY = new ArrayList<>();
        ;
        this.listDAY.addAll(listDay);
        if (this.listValue != null && this.listValue.size() != 0 && anim) {

            popup_out_bottom = AnimationUtils.loadAnimation(getContext(), R.anim.sacle_bottom_out);
            startAnimation(popup_out_bottom);

            popup_out_bottom.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation arg0) {
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    setVisibility(View.INVISIBLE);
                    play(listValue, anim, 10);
                }
            });

        } else {
            play(listValue, anim, 600);
        }
    }

    private void play(final List<Integer> listValue, boolean anim, int time) {
        this.listValue = new ArrayList<>();
        this.listValue.addAll(listValue);
        MaxValue = 0;
        //计算最大值
        post(new Runnable() {

            @Override
            public void run() {
                for (int a : listValue) {
                    if (a > MaxValue)
                        MaxValue = a;
                }
                initView();
                invalidate();
            }
        });

        //执行动画
        if (anim) {
            setVisibility(View.INVISIBLE);
            popup_enter_bottom = AnimationUtils.loadAnimation(getContext(), R.anim.sacle_bottom_in);
            popup_enter_bottom.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation arg0) {
                    setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                }
            });
            postDelayed(new Runnable() {

                @Override
                public void run() {
                    startAnimation(popup_enter_bottom);
                }
            }, time);

        }

    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {
            float x = event.getX();
            float y = event.getY();

            for (int i = 0; i < listRectF.size(); i++) {
                if (listRectF.get(i).contains(x, y)) {

                    if (mSelectItem != null) {
                        select = i;
                        selectbottom = i;
                        mSelectItem.onSelectItem(this.vid, i);
                    }

                    break;
                }

            }
        }
        return true;
    }

    public void ShowView() {
        setValue(this.listValue, false, this.listDAY, mSelectItem, this.vid);
    }

    public interface SelectItem {
        void onSelectItem(int vid, int item);
    }

    //获取文字宽度
    public int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    //获取文字高度
    public static float getTextHigh(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

}
