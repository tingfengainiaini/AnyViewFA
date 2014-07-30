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
    private int mMaxRotationAngle = 60;//���ת��
    private int mMaxZoom = -120;//��һ��-120��
    private int mCoveflowCenter;//����

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

    //����ͼƬ����
    protected boolean getChildStaticTransformation(View child, Transformation t) {

            final int childCenter = getCenterOfView(child);//����λ��
            final int childWidth = child.getWidth();//����ͼƬ���
            int rotationAngle = 0;//�Ƕ�Ϊ0

            t.clear();
            //����animation
            t.setTransformationType(Transformation.TYPE_MATRIX);
            
            if (childCenter == mCoveflowCenter) {
            	   //ͼƬ����Ϊ��ת����
                    transformImageBitmap((ImageView) child, t, 0);
            } else {
            	/*
            	 * ���ýǶ�
            	 * ����ת����λ��-ͼƬ����λ�ã�/ͼƬ���*�����ת��
            	 */
                    rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle);
                    if (Math.abs(rotationAngle) > mMaxRotationAngle) {
                    	//���������ת�ǣ����������ת��
                            rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle
                                            : mMaxRotationAngle;
                    }
                    //�л�ͼƬ
                    transformImageBitmap((ImageView) child, t, rotationAngle);
            }

            return true;
    }

    /*
     * ���Ĵ�С
     * (non-Javadoc)
     * @see android.view.View#onSizeChanged(int, int, int, int)
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mCoveflowCenter = getCenterOfCoverflow();
            super.onSizeChanged(w, h, oldw, oldh);
    }

    
    /*
     * ����ʱת��bitmap����������ͼƬ��childCenter��
     */
    private void transformImageBitmap(ImageView child, Transformation t,
                    int rotationAngle) {
            mCamera.save();
            final Matrix imageMatrix = t.getMatrix();//��õ�ǰmatrix
            final int imageHeight = child.getLayoutParams().height;
            final int imageWidth = child.getLayoutParams().width;
            final int rotation = Math.abs(rotationAngle);//ȡ�û����е�ͼƬ�ĽǶȾ���ֵ

            // �����Y�����ƶ�����ͼƬ�����ƶ���X���϶�ӦͼƬ�����ƶ���
            //�ӽ�ת��
            mCamera.translate(0.0f, 0.0f, 100.0f);

            /*
             *  ��ת�������ת����ʱ���л�
             */
            if (rotation < mMaxRotationAngle) {
                    float zoomAmount = (float) (mMaxZoom + (rotation * 1.5));
                    mCamera.translate(0.0f, 0.0f, zoomAmount);
            }

            // ��Y������ת����ӦͼƬ�������﷭ת��
            // �����X������ת�����ӦͼƬ�������﷭ת��
            mCamera.rotateY(rotationAngle);
            mCamera.getMatrix(imageMatrix);
            
            //�任ͼƬ
            imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
            imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
            mCamera.restore();//�ӽ�����
    }
}
