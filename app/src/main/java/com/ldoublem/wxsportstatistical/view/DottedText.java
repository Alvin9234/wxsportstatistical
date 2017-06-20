package com.ldoublem.wxsportstatistical.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DottedText extends View{
	private Paint paintTextLast;//绘制文字，最后一个
	float selectPointValueHeight;//选中的点对应的Y轴动态坐标
	int paintAlpha = 0;//选中的点对应的Y轴动态坐标
	List<Integer> listValue = new ArrayList<>();//点
	float mHigh = 0;//高度
	float Width = 0;//宽度
	int CircleR = 5;//点半径
	//4fd4d0
	int bottomH = 0;//距离底部高度，给文字显示用的

	int topH = 0;//距离顶部部高度，给选中后的文字显示用的
	float minAbsY;//最小y,显示在X轴,结合坐标轴，每个点的相应y值为minAbsY - 绝对高度
	private float High;
	//选中
	public int select = -1;
	List<RectF> listRectF = new ArrayList<>();//每列对应的区域

	public DottedText(Context context, AttributeSet attrs) {
		super(context, attrs);
		CircleR = DensityUtil.dip2px(context, 3);
		bottomH = DensityUtil.dip2px(context, 12);
		topH = DensityUtil.dip2px(context, 16);
		initView();
	}

	public DottedText(Context context) {
		super(context);
		CircleR = DensityUtil.dip2px(context, 3);
		bottomH = DensityUtil.dip2px(context, 12);
		topH = DensityUtil.dip2px(context, 16);
		initView();
	}

	private void initView(){
		mHigh = getHeight();
		Width = getWidth();

		paintTextLast = new Paint();
		paintTextLast.setAntiAlias(true);
		paintTextLast.setStrokeWidth(4);
		paintTextLast.setColor(Color.WHITE);
		paintTextLast.setStyle(Paint.Style.FILL);
		paintTextLast.setAlpha(paintAlpha);
		paintTextLast.setTextSize(DensityUtil.dip2px(getContext(), 10));

		selectPointValueHeight = mHigh-2-getTextHigh(paintTextLast);

		High = mHigh - bottomH;
		minAbsY = High - CircleR - 1;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (listValue != null && listValue.size() != 0){
			int valueSize = listValue.size();
			//绘制点
			for (int i = 0; i < valueSize; i++) {
				float w = (float) (Width / valueSize * (i + 0.5));

				RectF f1 = new RectF();

				float wrf = (Width / valueSize / 2);

				f1.set(w - wrf, 0, w + wrf, getHeight());

				if (select == i) {//选中后，绘制顶文字
					int width = getTextWidth(paintTextLast, listValue.get(i) + "");
					int high = (int) getTextHigh(paintTextLast);

					if (w - width / 2 < 0) {
						if(selectPointValueHeight<=high){
							canvas.drawText(listValue.get(i) + "", w, high, paintTextLast);
							select = -1;
						}else{
							drawSelectText(listValue.get(i) + "",w,canvas);
						}
					} else if (w + width / 2 > Width) {
						if(selectPointValueHeight<=high){
							canvas.drawText(listValue.get(i) + "", w - width, high, paintTextLast);
							select = -1;
						}else{
							drawSelectText(listValue.get(i) + "",w - width,canvas);
						}
					} else {
						if(selectPointValueHeight<=high){
							canvas.drawText(listValue.get(i) + "", w - width / 2, high, paintTextLast);
							select = -1;
						}else{
							drawSelectText(listValue.get(i) + "",w - width / 2,canvas);
						}
					}
				}

				listRectF.add(f1);
			}
		}
	}

	public void setValue(final List<Integer> listValue){
		this.listValue.clear();
		this.listValue.addAll(listValue);
		initView();
		invalidate();
	}

	public void onSelectItem(int item){
		this.select = item;
		initView();
		invalidate();
	}

	public void drawSelectText(String text,float x,Canvas canvas){
		paintTextLast.setAlpha(paintAlpha);
		canvas.drawText(text, x, selectPointValueHeight, paintTextLast);
		selectPointValueHeight = selectPointValueHeight - 20;
		paintAlpha = paintAlpha+15;
		if(paintAlpha>=255){
			paintAlpha = 255;
		}
		invalidate();
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
