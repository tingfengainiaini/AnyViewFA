package com.alex.media.pictruepart;

import java.util.Observable;

public class ZoomState extends Observable {

    private float mZoom;
    private float mPanX;
    private float mPanY;

    public float getPanX() {
        return mPanX;
    }

    public float getPanY() {
        return mPanY;
    }

    public float getZoom() {
        return mZoom;
    }

    public void setPanX(float panX) {
        if (panX != mPanX) {
            mPanX = panX;
            setChanged();
        }
    }

    public void setPanY(float panY) {
        if (panY != mPanY) {
            mPanY = panY;
            //通知oberserver此改变事件
            setChanged();
        }
    }

    public void setZoom(float zoom) {
        if (zoom != mZoom) {
            mZoom = zoom;
            setChanged();
        }
    }
    
    public float getZoomX(float aspectQuotient) {
    	//返回较小的值
        return Math.min(mZoom, mZoom * aspectQuotient);
    }

    public float getZoomY(float aspectQuotient) {
        return Math.min(mZoom, mZoom / aspectQuotient);
    }
}
