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
	private ZoomState mZoomState;//״̬
	private Bitmap mBitmap;
	private SimpleZoomListener mZoomListener;
	private ProgressBar progressBar;//���صĽ���
	
	private float z;//���ƷŴ���С������
	
	//�����µĿ��Ƽ���ͼƬ
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progressBar.setVisibility(View.GONE);
			mZoomView.setImage(mBitmap);
			mZoomState = new ZoomState();
			mZoomView.setZoomState(mZoomState);
			mZoomListener = new SimpleZoomListener();
			mZoomListener.setZoomState(mZoomState);
			//�����϶��¼�����
			mZoomView.setOnTouchListener(mZoomListener);
			//��λzoomstate
			resetZoomState();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		System.out.println("picpart start");
		
		//��ȡ�ļ���·���Լ�����
		Intent intent=this.getIntent();
		final String filepath=intent.getStringExtra("path");
		System.out.println(filepath);
		String filename=intent.getStringExtra("filename");
		System.out.println(filename);
		
		setContentView(R.layout.activity_image);
		System.out.println("setContentView");
		this.setTitle(filepath);//��activity�������ó�·�����ļ���
		
		mZoomView = (ImageZoomView) findViewById(R.id.zoomView);
		progressBar = (ProgressBar) findViewById(R.id.progress_large);
		progressBar.setVisibility(View.VISIBLE);
		System.out.println("setprogressbarvisible");
		
		//�����µ��̼߳���
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("thread");
				//decodeFile����Ĭ�ϵ���sdcard�����·��
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
		
		//�Ŵ�ť����Ӧ
		zoomCtrl.setOnZoomInClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("zoomCtrl onclick");
				z = mZoomState.getZoom() + 0.25f;
				/*
				 * �ı�mzoom��ִ��setChanged()��֪ͨ�۲�ģʽ
				 *��С��С���ķ�֮һ������ı�
				 */
				if(z<=4.0f){
					mZoomState.setZoom(z);
					zoomCtrl.setIsZoomOutEnabled(true);//������С������
					mZoomState.notifyObservers();
				}else{
					//�Ŵ��ı����ܼ����Ŵ�
					Toast.makeText(getApplicationContext(), "���棺���ܼ����Ŵ󡭡�",
						     Toast.LENGTH_SHORT).show();
					zoomCtrl.setIsZoomInEnabled(false);
				}
				
			}
		});
		
		//��С��ť����Ӧ
		zoomCtrl.setOnZoomOutClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				float z = mZoomState.getZoom() - 0.25f;
				
				if(z>=0.25f){
					mZoomState.setZoom(z);
					zoomCtrl.setIsZoomInEnabled(true);//�����Ŵ󡰼���
					mZoomState.notifyObservers();
				}else{
					//�����С���ķ�֮һ�����ܼ�����С
					Toast.makeText(getApplicationContext(), "���棺���ܼ�����С����",
						     Toast.LENGTH_SHORT).show();
					zoomCtrl.setIsZoomOutEnabled(false);
				}
				
			}
		});
	}
    
	//��activity��ʧʱ����bitmap����Դ
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBitmap != null)
			mBitmap.recycle();
		// mZoomView.setOnTouchListener(null);
		// mZoomState.deleteObservers();
	}
   //�ָ�zoom״̬
	private void resetZoomState() {
		mZoomState.setPanX(0.5f);
		mZoomState.setPanY(0.5f);
		mZoomState.setZoom(1f);
		mZoomState.notifyObservers();
	}
}