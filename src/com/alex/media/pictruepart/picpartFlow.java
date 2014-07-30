package com.alex.media.pictruepart;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.alex.media.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class picpartFlow extends Activity {
	private String filepath=null;
	private String filename=null;
	private String parentFilePath=null;
	private List<iamgeInfo> imageInfos=new ArrayList<iamgeInfo>();
	
	public void onCreate(Bundle savedInstanceState) {
		System.out.println("picpartFlow oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_gallery);
        //��ȡ��Ϣ
        Intent intent=this.getIntent();
		filepath=intent.getStringExtra("path");
		System.out.println(filepath);
		filename=intent.getStringExtra("filename");
		System.out.println(filename);
		parentFilePath=intent.getStringExtra("parentPath");
		
		//�����ϸ�activity�е����ͼƬ����Ϊ��һ��
		iamgeInfo iamgeInfoNow1=new iamgeInfo();
		iamgeInfoNow1.setIamgeName(filename);
		iamgeInfoNow1.setIamgePath(filepath);
		imageInfos.add(iamgeInfoNow1);
		//������ʾ
		setAdapter();
		}
	/*
	 * �˴����жϵ�ǰ������ļ����е�����ͼƬ�����Ҽ��뵽imageinfos������
	 */
	private void setAdapter(){
		int i;
		 File imageFile=new File(parentFilePath);
		 if(imageFile.isDirectory()){
			 File[] files = imageFile.listFiles();
			 for(i=0;i<files.length;i++){
				 //�����ļ��У�����jpg��β���Ҳ��ǵ�����Ǹ���ѭ���������
				 if((!files[i].isDirectory())&&(files[i].getName().endsWith(".jpg"))&&(!files[i].getName().toString().equals(filename))){
					    System.out.println(files[i].getName());
			    		iamgeInfo iamgeInfoNow=new iamgeInfo();
			    		
			    		iamgeInfoNow.setIamgeName(files[i].getName().toString());
			    		System.out.println(iamgeInfoNow.getIamgeName());
			    		
			    		iamgeInfoNow.setIamgePath(files[i].getPath().toString());
			    		System.out.println(iamgeInfoNow.getIamgePath());
			    		
			    		imageInfos.add(iamgeInfoNow);//���
			    		System.out.println("imageInfos add");
				 }
				 
			 }
		 }
		 /*Integer[] images = { R.drawable.img0001, R.drawable.img0030,
	                R.drawable.img0100, R.drawable.img0130, R.drawable.img0200,
	                R.drawable.img0230, R.drawable.img0300, R.drawable.img0330,
	                R.drawable.img0354 };*/
		    System.out.println("for quit");
	        ImageAdapter adapter = new ImageAdapter(this, imageInfos);//����adapter
	        System.out.println("new imageadapter");
	       
	        adapter.createReflectedImages();//���õ�Ӱ
	        System.out.println("create reflect");
	        
	        GalleryFlow galleryFlow = (GalleryFlow) findViewById(R.id.Gallery01);
	        galleryFlow.setAdapter(adapter);
	        //gallery���õ������
	        galleryFlow.setOnItemClickListener(new OnItemClickListener(){

		
	        	@Override
		public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					
				Intent picviewIntent=new Intent(picpartFlow.this,picpart.class);
				picviewIntent.putExtra("path", imageInfos.get(position).getIamgePath());
				picviewIntent.putExtra("filename", imageInfos.get(position).getIamgeName());
				startActivity(picviewIntent);
			}
	        	
	    });
	}
}