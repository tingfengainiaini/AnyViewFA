package com.alex.media.pictruepart;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;

public class GalleryFlow extends Gallery {

    private Camera mCamera = new Camera();
    private int mMaxRotationAngle = 60;//最大转角
    private int mMaxZoom = -120;//另一侧-120度
    private int mCoveflowCenter;//中轴

    public GalleryFlow(Context context) {
            super(context);
            this.setStaticTransformationsEnabled(true);
    }

    public GalleryFlow(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.setStaticTransformationsEnabled(true);
    }

    public GalleryFlow(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            this.setStaticTransformationsEnabled(true);
    }

    public int getMaxRotationAngle() {
            return mMaxRotationAngle;
    }

    public void setMaxRotationAngle(int maxRotationAngle) {
            mMaxRotationAngle = maxRotationAngle;
    }

    public int getMaxZoom() {
            return mMaxZoom;
    }

    public void setMaxZoom(int maxZoom) {
            mMaxZoom = maxZoom;
    }

    private int getCenterOfCoverflow() {
            return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2
                            + getPaddingLeft();
    }

    private static int getCenterOfView(View view) {
            return view.getLeft() + view.getWidth() / 2;
    }

    //中心图片处理
    protected boolean getChildStaticTransformation(View child, Transformation t) {

            final int childCenter = getCenterOfView(child);//中心位置
            final int childWidth = child.getWidth();//中心图片宽度
            int rotationAngle = 0;//角度为0

            t.clear();
            //设置animation
            t.setTransformationType(Transformation.TYPE_MATRIX);
            
            if (childCenter == mCoveflowCenter) {
            	   //图片中心为旋转中心
                    transformImageBitmap((ImageView) child, t, 0);
            } else {
            	/*
            	 * 设置角度
            	 * （旋转中心位置-图片中心位置）/图片宽度*最大旋转角
            	 */
                    rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle);
                    if (Math.abs(rotationAngle) > mMaxRotationAngle) {
                    	//超过最大旋转角，设置最大旋转角
                            rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle
                                            : mMaxRotationAngle;
                    }
                    //切换图片
                    transformImageBitmap((ImageView) child, t, rotationAngle);
            }

            return true;
    }

    /*
     * 更改大小
     * (non-Javadoc)
     * @see android.view.View#onSizeChanged(int, int, int, int)
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mCoveflowCenter = getCenterOfCoverflow();
            super.onSizeChanged(w, h, oldw, oldh);
    }

    
    /*
     * 滑动时转换bitmap，更改中心图片（childCenter）
     */
    private void transformImageBitmap(ImageView child, Transformation t,
                    int rotationAngle) {
            mCamera.save();
            final Matrix imageMatrix = t.getMatrix();//获得当前matrix
            final int imageHeight = child.getLayoutParams().height;
            final int imageWidth = child.getLayoutParams().width;
            final int rotation = Math.abs(rotationAngle);//取得滑动中的图片的角度绝对值

            // 如果在Y轴上移动，则图片上下移动；X轴上对应图片左右移动。
            //视角转换
            mCamera.translate(0.0f, 0.0f, 100.0f);

            /*
             *  旋转到最大旋转交的时候切换
             */
            if (rotation < mMaxRotationAngle) {
                    float zoomAmount = (float) (mMaxZoom + (rotation * 1.5));
                    mCamera.translate(0.0f, 0.0f, zoomAmount);
            }

            // 在Y轴上旋转，对应图片竖向向里翻转。
            // 如果在X轴上旋转，则对应图片横向向里翻转。
            mCamera.rotateY(rotationAngle);
            mCamera.getMatrix(imageMatrix);
            
            //变换图片
            imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
            imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
            mCamera.restore();//视角重置
    }
}
