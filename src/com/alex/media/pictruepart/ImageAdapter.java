package com.alex.media.pictruepart;

import java.util.Iterator;


import java.util.List;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	int index = 0;
	int mGalleryItemBackground;
	private Context mContext;
	private List<iamgeInfo> mImageInfos;
	private ImageView[] mImages;
	String imagerelativeNow;
	//private String[] imageRelative=null;
	
	/*
	 * ����image��adapter
	 */
	public ImageAdapter(Context c, List<iamgeInfo> imageInfos) {
		System.out.println("iamgeadapter constructer");
		mContext = c;
		mImageInfos = imageInfos;//�нӹ���imageinfos
		
		mImages = new ImageView[mImageInfos.size()]; //imageView������
		System.out.println("constructer com"+mImageInfos.size());
		//imageRelative=new String[mImageInfos.size()];
	}
/*private void getRelativePath(){
	for (Iterator iterator=mImageInfos.iterator();iterator.hasNext();){
		iamgeInfo imageinfo=(iamgeInfo)iterator.next();
		String imagerelativeNow=imageinfo.getIamgePath().replace("/mnt/sdcard/","/");
		
	}
}*/
	/*
	 * ��Ӱ�����ã�bitmap���ܵ��ã�����matrix��preScale����
	 */
	public boolean createReflectedImages() {
		System.out.println("flect start");
		final int reflectionGap = 4;
		/*
		 * ������ȡimageInfos�е�image����
		 */
		for (Iterator iterator=mImageInfos.iterator();iterator.hasNext();)
		{
			iamgeInfo imageinfo=(iamgeInfo)iterator.next();
			System.out.println("itertor go next");
			
			//imagerelativeNow=imageinfo.getIamgePath().replace("/mnt/sdcard/","/");
		    System.out.println(imageinfo.getIamgePath());
		    		
		    //���ɾɵ�bitmap������Դͼ���С������bitmap��
					Bitmap originalImage =  BitmapFactory.decodeFile(imageinfo.getIamgePath());
					System.out.println("decodefile com");
					int width = originalImage.getWidth();//��bitmap�Ŀ�
					int height = originalImage.getHeight();//��bitmap�ĸ�
					
					System.out.println("decodefile com "+width+""+height);
					
					Matrix matrix = new Matrix();//Matrix����
					/*
					 * �˴��ǹؼ���ʹ��matrix��preScale��������x�᲻�䣬y����
					 */
					matrix.preScale(1, -1);
					/*
					 * �ڵ��úõ�mtrix�����ó��µ�bitmap����Ȳ��䣬�߶���Ϊ1/2
					 */
					Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
							height / 2, width, height / 2, matrix, false);
					/*
					 * �ϲ���С��bitmap
					 */
					Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
							(height + height / 2), Config.ARGB_8888);
					//��������
					Canvas canvas = new Canvas(bitmapWithReflection);
					
					/*
					 * �Ƚ��ɵģ������ģ�bitmap����
					 */
					canvas.drawBitmap(originalImage, 0, 0, null);

					Paint deafaultPaint = new Paint();
					
					/*
					 * ʹ��Ĭ�ϵ�paint���󻭳���������
					 * ���� �������ҵ�
					 * �ײ������reflectionGap
					 */
					canvas.drawRect(0, height, width, height + reflectionGap,
							deafaultPaint);
					//����Ӱ��bitmap��ӽ�ȥ
					canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
					
					/*
					 * ʹ��LinearGradient��bitmapWithReflection�ϻ�����Ӱ
					 * ��̫׼������������������̫ռ��Դ������������
					 */
					Paint paint = new Paint();
					LinearGradient shader = new LinearGradient(0, originalImage
							.getHeight(), 0, bitmapWithReflection.getHeight()
							+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);

					paint.setShader(shader);

					paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));//ˢ��

					canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
							+ reflectionGap, paint);
					/*
					 * ͼƬ����bitmap
					 */
					ImageView imageView = new ImageView(mContext);
					imageView.setImageBitmap(bitmapWithReflection);
					imageView.setLayoutParams(new GalleryFlow.LayoutParams(180, 240));
//					imageView.setScaleType(ScaleType.MATRIX);
					mImages[index++] = imageView;
				
		}
		return true;
	}

	private Resources getResources() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCount() {
		return mImageInfos.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return mImages[position];
	}

	public float getScale(boolean focused, int offset) {
		return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
	}

}
