package com.alex.media.pictruepart;

import com.alex.media.R;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ZoomControls;

public class picpart extends Activity {
	/** Called when the activity is first created. */
	private ImageZoomView mZoomView;
	private ZoomState mZoomState;//状态
	private Bitmap mBitmap;
	private SimpleZoomListener mZoomListener;
	private ProgressBar progressBar;//加载的进度
	
	private float z;//控制放大缩小的限制
	
	//开启新的控制加载图片
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progressBar.setVisibility(View.GONE);
			mZoomView.setImage(mBitmap);
			mZoomState = new ZoomState();
			mZoomView.setZoomState(mZoomState);
			mZoomListener = new SimpleZoomListener();
			mZoomListener.setZoomState(mZoomState);
			//开启拖动事件监听
			mZoomView.setOnTouchListener(mZoomListener);
			//复位zoomstate
			resetZoomState();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		System.out.println("picpart start");
		
		//获取文件的路径以及名字
		Intent intent=this.getIntent();
		final String filepath=intent.getStringExtra("path");
		System.out.println(filepath);
		String filename=intent.getStringExtra("filename");
		System.out.println(filename);
		
		setContentView(R.layout.activity_image);
		System.out.println("setContentView");
		this.setTitle(filepath);//将activity名字设置成路径加文件名
		
		mZoomView = (ImageZoomView) findViewById(R.id.zoomView);
		progressBar = (ProgressBar) findViewById(R.id.progress_large);
		progressBar.setVisibility(View.VISIBLE);
		System.out.println("setprogressbarvisible");
		
		//开启新的线程加载
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("thread");
				//decodeFile函数默认的是sdcard的相对路径
				String filepath1=filepath.replace("/mnt/sdcard/","/");
				System.out.println("pick not already-->"+filepath);
				System.out.println("pick already-->"+filepath1);
				System.out.println(filepath1);
				mBitmap = BitmapFactory.decodeFile(filepath);
				handler.sendEmptyMessage(0);
			}
		});
		thread.start();
		
		System.out.println("thread already start");
		final ZoomControls zoomCtrl = (ZoomControls) findViewById(R.id.zoomCtrl);
		System.out.println("zoomCtrl");
		
		//放大按钮的响应
		zoomCtrl.setOnZoomInClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("zoomCtrl onclick");
				z = mZoomState.getZoom() + 0.25f;
				/*
				 * 改变mzoom，执行setChanged()，通知观察模式
				 *最小缩小到四分之一，最大四倍
				 */
				if(z<=4.0f){
					mZoomState.setZoom(z);
					zoomCtrl.setIsZoomOutEnabled(true);//将“缩小”激活
					mZoomState.notifyObservers();
				}else{
					//放大四倍不能继续放大
					Toast.makeText(getApplicationContext(), "警告：不能继续放大……",
						     Toast.LENGTH_SHORT).show();
					zoomCtrl.setIsZoomInEnabled(false);
				}
				
			}
		});
		
		//缩小按钮的响应
		zoomCtrl.setOnZoomOutClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				float z = mZoomState.getZoom() - 0.25f;
				
				if(z>=0.25f){
					mZoomState.setZoom(z);
					zoomCtrl.setIsZoomInEnabled(true);//将“放大“激活
					mZoomState.notifyObservers();
				}else{
					//最多缩小到四分之一，不能继续缩小
					Toast.makeText(getApplicationContext(), "警告：不能继续缩小……",
						     Toast.LENGTH_SHORT).show();
					zoomCtrl.setIsZoomOutEnabled(false);
				}
				
			}
		});
	}
    
	//当activity消失时回收bitmap的资源
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBitmap != null)
			mBitmap.recycle();
		// mZoomView.setOnTouchListener(null);
		// mZoomState.deleteObservers();
	}
   //恢复zoom状态
	private void resetZoomState() {
		mZoomState.setPanX(0.5f);
		mZoomState.setPanY(0.5f);
		mZoomState.setZoom(1f);
		mZoomState.notifyObservers();
	}
}