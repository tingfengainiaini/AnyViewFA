package com.alex.media.pictruepart;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class SimpleZoomListener implements View.OnTouchListener {

    public enum ControlType {
        PAN, ZOOM
    }

    private ControlType mControlType = ControlType.PAN;

    private ZoomState mState;

    private float mX;
    private float mY;
    private float mGap;

    public void setZoomState(ZoomState state) {
        mState = state;
    }

    public void setControlType(ControlType controlType) {
        mControlType = controlType;
    }

    /*
     * 设置拖动屏幕的响应
     * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
     */
    public boolean onTouch(View v, MotionEvent event) {
        final int action = event.getAction();
        int pointCount = event.getPointerCount();//获得触摸的焦点个数
        
        //单点触摸
        if(pointCount == 1){
        	final float x = event.getX();//获得此点的位置
            final float y = event.getY();
            switch (action) {
            	//取得按下的点的位置，作为焦点
                case MotionEvent.ACTION_DOWN:
                    mX = x;
                    mY = y;
                    break;
                
                case MotionEvent.ACTION_MOVE: {
                	//拖动的位置变化
                    final float dx = (x - mX) / v.getWidth();
                    final float dy = (y - mY) / v.getHeight();
                    mState.setPanX(mState.getPanX() - dx);
                    mState.setPanY(mState.getPanY() - dy);
                    mState.notifyObservers();
                    mX = x;
                    mY = y;
                    break;
                }
            }
        }
        
        //多点触摸
        if(pointCount == 2){
        	final float x0 = event.getX(event.getPointerId(0));
            final float y0 = event.getY(event.getPointerId(0));
            
            final float x1 = event.getX(event.getPointerId(1));
            final float y1 = event.getY(event.getPointerId(1));
            
            final float gap = getGap(x0, x1, y0, y1);
            	switch (action) {
                case MotionEvent.ACTION_POINTER_2_DOWN:
                case MotionEvent.ACTION_POINTER_1_DOWN:
                	mGap = gap;
                    break;
                case MotionEvent.ACTION_POINTER_1_UP:
                	mX = x1;
                	mY = y1;
                	break;
                case MotionEvent.ACTION_POINTER_2_UP:
                	mX = x0;
                	mY = y0;
                    break;
                case MotionEvent.ACTION_MOVE: {
                    final float dgap = (gap - mGap)/ mGap;
                    System.out.println(String.valueOf(dgap));
                    Log.d("Gap", String.valueOf((float)Math.pow(20, dgap)));
                    float z=mState.getZoom() * (float)Math.pow(5, dgap);
                    //控制多点触控放大的限度
                    if((0.25f<=z)&&(z<=4.0f)){
                    	mState.setZoom(z);
                        mState.notifyObservers();
                        mGap = gap;
                    }
                    
                    break;
                }
            	}
        }
        	
        return true;
    }
    
    private float getGap(float x0, float x1, float y0, float y1){
    	return (float)Math.pow(Math.pow((x0-x1), 2)+Math.pow((y0-y1), 2), 0.5);
    }

}
