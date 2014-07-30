package com.alex.media.ebookpart;

import java.io.File;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.util.EncodingUtils;

import com.alex.media.R;
import com.alex.media.filespart.fileMng;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class textView extends Activity{
	byte[] data=null;
	private FileInputStream in=null;
	private TextView tv = null;
	private boolean fullScreen=false;
	private boolean ifGB=true;
	int[] menu_image_array = {R.drawable.menu_fullscreen,R.drawable.menu_set,
			R.drawable.menu_set_autohv,R.drawable.menu_bold,R.drawable.menu_return};
	String[] menu_name_array = {"ȫ��","����","��ת","������","����"};
	

	private String fileName;
	AlertDialog menuDialog;// menu�˵�Dialog
	GridView menuGrid, toolbarGrid;
	View menuView;
	
	private int times=0;
	/**the buffer size*/
	final static int mBUFFER = 16384;
	/**file*/
	private File mFile;
	/**offset*/
	//private int mOffset = 0;
	byte abyte0[] = null;
	int datapos=0;
	int timesTotal=0;
	int len=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("textView onCreate");
		setContentView(R.layout.txtview);
		
		fileName = this.getIntent().getStringExtra("path");
		System.out.println("filename get");
		tv = (TextView)findViewById(R.id.txtView);
		
		makemenu();//�����Զ����menu�˵�
		System.out.println("make menu");
		tv.setText("");
		//openFile(fileName);
		updateView(fileName, "GB2312");
		System.out.println("activity--->" + Thread.currentThread().getId());
		System.out.println("activityname--->" + Thread.currentThread().getName());
	}
 
    

	

   public void updateView( String fileName,final String encoding ){
	
	   data = openFile(fileName);
	    String display = EncodingUtils.getString(data, "GB2312");
		tv.append(display);
	        System.out.println("updateview start");
			
			System.out.println("get id");
			
			System.out.println("set shenglue");
			
					
			
			textView.this.setTitle(mFile.getName().toString());
			System.out.println("set title");
			//openFile(fileName);
		}

	
public byte[] openFile(String fileName){
		try { 
			System.out.println("file open start");
			mFile = new File(fileName);
			in = new FileInputStream(mFile);
			int len = (int)mFile.length();
			
			byte abyte0[] = null;
			//long l = 0L;
			//l = mFile.length();//�õ��ļ��Ĵ�С
			System.out.println("file length"+len);
			
			abyte0 = new byte[len];// new a byte array
			if (mBUFFER > len) { // read all data
				try {
					System.out.println("read all data");
					in.read(abyte0);
					//mOffset += len;
					//String display = EncodingUtils.getString(abyte0, "GB2312");
					
					//tv = (TextView)findViewById(R.id.txtView);
					//tv.append(display);
					return abyte0;
				} catch (IOException ex1) {
					tv.setText("error");
					//return null;
				}
			} else { // read the data by paragraph
				int datapos = 0;
				int times = len / mBUFFER;// calculate the buffer count
				for (int i = 0; i < times; i++) {
					byte[] buffer = new byte[mBUFFER];
					try {
						in.read(buffer);
						
						//copy the data to a byte array
						System.arraycopy(buffer, 0, abyte0, datapos, mBUFFER);
						//String display = EncodingUtils.getString(buffer, "GB2312");
						//tv = (TextView)findViewById(R.id.txtView);
						//tv.append(display);
						datapos += mBUFFER;
						//mOffset += mBUFFER;
					} catch (IOException ex2) {
						tv.setText("error");
						return null;
					}
				}
				int rest = len - datapos;
				//If you read the provisions of the length of the actual reading is greater than the length of the
				//go on read data
				byte[] buffer = new byte[rest];
				try {
					in.read(buffer);
					System.arraycopy(buffer, 0, abyte0, datapos, rest);
					//String display = EncodingUtils.getString(buffer, "GB2312");
					//tv = (TextView)findViewById(R.id.txtView);
					//tv.append(display);
					//mOffset += rest;
				} catch (IOException ex3) {
				}
				buffer = null;
			}
			System.gc();
			return abyte0;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	@Override
	/**
	 * ����MENU
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("menu");// ���봴��һ��
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	/**
	 * ����MENU
	 */
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (menuDialog == null) {
			menuDialog = new AlertDialog.Builder(this).setView(menuView).show();
		} else {
			menuDialog.show();
		}
		return false;// ����Ϊtrue ����ʾϵͳmenu
	}
	/*
	 * �����Զ���Ĳ˵�
	 * �����Զ���view��alertdialog
	 */
	public void makemenu(){
		// �����Զ���menu�˵�
		System.out.println("menu onCreate");
		menuView = View.inflate(this, R.layout.gridview_menu, null);
		// ����AlertDialog
		menuDialog = new AlertDialog.Builder(this).create();
		menuDialog.setView(menuView);
		System.out.println("menu onCreate setcontentview");
		menuDialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU)// ��������
					dialog.dismiss();
				return false;
			}
		});

		menuGrid = (GridView) menuView.findViewById(R.id.gridview);
		menuGrid.setAdapter(getMenuAdapter(menu_name_array, menu_image_array));
		System.out.println("menu setadapter");
		/** ����menuѡ�� **/
		menuGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case 0://ȫ��
					System.out.println("ebook fullscreen");
					int val = getWindow().getAttributes().flags;
			        // ȫ�� 66816 - ��ȫ�� 65792
			        if(val != 66816){//��ȫ��
			            getWindow().setFlags(
			                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
			                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
			        }else{//ȡ��ȫ��
			            getWindow().clearFlags(
			                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
			        }

					break;
				case 1://����
					Intent i = new Intent(textView.this, ebookSet.class);   
			        startActivityForResult(i,1);  
					System.out.println("ebook set");
					
					break;
				case 2:// ��ת
					int screenState=textView.this.getRequestedOrientation();
					if(screenState==-1){
						Toast.makeText(getApplicationContext(), "�޷�������Ļ״̬",
							     Toast.LENGTH_SHORT).show();
					}
					else if(screenState==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					}
					else if(screenState==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					}
					System.out.println("ebook autohv");
					break;
				case 3://�����л�
					if(!ifGB){
						String display = EncodingUtils.getString(data,"GB2312" );
						//tv = (TextView)findViewById(R.id.txtView);
						tv.setText(display);
						ifGB=true;
						//textView.this.setTitle(mFile.getName().toString());
					}else{
						String display = EncodingUtils.getString(data, "utf-8");
						//tv = (TextView)findViewById(R.id.txtView);
						tv.setText(display);
						ifGB=false;
						//textView.this.setTitle(mFile.getName().toString());
					}
					
					System.out.println("set text bold");
					break;
				case 4:// ����
					menuDialog.dismiss();
					break;
				}
				
				
			}
		});
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 switch (resultCode){   
	        case RESULT_OK:   
	            Bundle b = data.getExtras();   
	            String txtSize = b.getString("TEXTSIZE");   
	            String txtColor=b.getString("TEXTCOLOR");
	            String txtScale=b.getString("TEXTSCALE");
	            setTxtScale(txtScale);
	            setTxtSize(txtSize);
	            setTxtColor(txtColor);
	        }   
		super.onActivityResult(requestCode, resultCode, data);
	}

   public void setTxtSize(String txtSize){
	   if(txtSize.equals("��С")){
		   tv.setTextSize(12);
	   }else if(txtSize.equals("С")){
		   tv.setTextSize(17);
	   }else if(txtSize.equals("�е�")){
		   tv.setTextSize(22);
	   }else if(txtSize.equals("��")){
		   tv.setTextSize(27);
	   }else if(txtSize.equals("����")){
		   tv.setTextSize(32);
		   //tv.setBackgroundResource(this.getResources().getDrawable(R.drawable.bg3));
	   }
   }
   
   public void setTxtColor(String txtColor){
	   if(txtColor.equals("��ɫ")){
		  tv.setTextColor(android.graphics.Color.BLACK);
	   }else if(txtColor.equals("��ɫ")){
		  tv.setTextColor(android.graphics.Color.GRAY);
	   }else if(txtColor.equals("��ɫ")){
		  tv.setTextColor(android.graphics.Color.WHITE); 
	   }else if(txtColor.equals("��ɫ")){
		  tv.setTextColor(android.graphics.Color.RED);
	   }else if(txtColor.equals("��ɫ")){
		   tv.setTextColor(android.graphics.Color.YELLOW);
	   }
   }
   
   private void setTxtScale(String txtScale){
	   if(txtScale.equals("С")){
		   tv.setTextScaleX((float) 0.7);
	   }else if(txtScale.equals("����")){
		   tv.setTextScaleX(1);
	   }else if(txtScale.equals("��")){
		   tv.setTextScaleX(2);
	   }
   }
	/**
	 * ����˵�Adapter
	 * 
	 * @param menuNameArray
	 *            ����
	 * @param imageResourceArray
	 *            ͼƬ
	 * @return SimpleAdapter
	 */
	private SimpleAdapter getMenuAdapter(String[] menuNameArray,
			int[] imageResourceArray) {
		System.out.println("menu setadapter oncreate");
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemText", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.item_menu, new String[] { "itemImage", "itemText" },
				new int[] { R.id.item_image, R.id.item_text });
		return simperAdapter;
	}	
}
