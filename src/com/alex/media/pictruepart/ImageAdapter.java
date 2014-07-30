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
	 * 构造image的adapter
	 */
	public ImageAdapter(Context c, List<iamgeInfo> imageInfos) {
		System.out.println("iamgeadapter constructer");
		mContext = c;
		mImageInfos = imageInfos;//承接过来imageinfos
		
		mImages = new ImageView[mImageInfos.size()]; //imageView的数组
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
	 * 倒影的设置，bitmap不能倒置，利用matrix的preScale方法
	 */
	public boolean createReflectedImages() {
		System.out.println("flect start");
		final int reflectionGap = 4;
		/*
		 * 迭代读取imageInfos中的image对象
		 */
		for (Iterator iterator=mImageInfos.iterator();iterator.hasNext();)
		{
			iamgeInfo imageinfo=(iamgeInfo)iterator.next();
			System.out.println("itertor go next");
			
			//imagerelativeNow=imageinfo.getIamgePath().replace("/mnt/sdcard/","/");
		    System.out.println(imageinfo.getIamgePath());
		    		
		    //生成旧的bitmap（即对源图像大小上设置bitmap）
					Bitmap originalImage =  BitmapFactory.decodeFile(imageinfo.getIamgePath());
					System.out.println("decodefile com");
					int width = originalImage.getWidth();//旧bitmap的宽
					int height = originalImage.getHeight();//旧bitmap的高
					
					System.out.println("decodefile com "+width+""+height);
					
					Matrix matrix = new Matrix();//Matrix对象
					/*
					 * 此处是关键，使用matrix的preScale方法，将x轴不变，y倒置
					 */
					matrix.preScale(1, -1);
					/*
					 * 在倒置好的mtrix中设置出新的bitmap，宽度不变，高度置为1/2
					 */
					Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
							height / 2, width, height / 2, matrix, false);
					/*
					 * 合并大小的bitmap
					 */
					Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
							(height + height / 2), Config.ARGB_8888);
					//设置容器
					Canvas canvas = new Canvas(bitmapWithReflection);
					
					/*
					 * 先将旧的（正常的）bitmap放入
					 */
					canvas.drawBitmap(originalImage, 0, 0, null);

					Paint deafaultPaint = new Paint();
					
					/*
					 * 使用默认的paint对象画出矩形容器
					 * 参数 ：左上右底
					 * 底部带间距reflectionGap
					 */
					canvas.drawRect(0, height, width, height + reflectionGap,
							deafaultPaint);
					//将倒影的bitmap添加进去
					canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
					
					/*
					 * 使用LinearGradient在bitmapWithReflection上绘制阴影
					 * 不太准………………，这样太占资源………………
					 */
					Paint paint = new Paint();
					LinearGradient shader = new LinearGradient(0, originalImage
							.getHeight(), 0, bitmapWithReflection.getHeight()
							+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);

					paint.setShader(shader);

					paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));//刷新

					canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
							+ reflectionGap, paint);
					/*
					 * 图片加载bitmap
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
