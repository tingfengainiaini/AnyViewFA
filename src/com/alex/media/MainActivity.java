package com.alex.media;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;

import com.alex.media.R;
import com.alex.media.ebookpart.ebook;
import com.alex.media.filespart.fileMng;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity{
	
	AlertDialog menuDialog;// menu菜单Dialog
	private ListView listView1;
	private ListView listView2;
	GridView menuGrid, toolbarGrid;
	View menuView;
	private ScanSdReceiver scanSdReceiver = null;
	
	private DBHelper dbHelper = null;
	private int[] _ids;
	private String[]_titles;
	private String[] _path;
	Cursor cursor = null;
	private int[] music_id;

	/*-- Toolbar底部菜单选项下标--*/
	private final int TOOLBAR_MENU = 0;// 菜单
	private final int TOOLBAR_EXIT = 1;//退出
	
	/** 弹出的菜单1 **/
	int[] menu_image_array = {R.drawable.menu_open,R.drawable.menu_ebook,R.drawable.menu_music,
			R.drawable.menu_help,R.drawable.menu_about,R.drawable.menu_quit};
	String[] menu_name_array = {"打开","电子书","音乐","帮助","关于","退出"};
	
	
	/** 主界面list **/

	int[] menu_image_array2 = {R.drawable.menu_open,R.drawable.menu_ebook,R.drawable.menu_music,R.drawable.menu_help};
	String[] menu_name_array2 = {"文件管理","电子书","音乐","帮助"};
	
	/** 底部菜单 **/
	int[] menu_toolbar_image_array = {R.drawable.menu,R.drawable.menu_quit};
	String[] menu_toolbar_name_array ={"菜单","退出"};
	
	@Override
	protected void onStop() {
		super.onStop();
		if (dbHelper!=null){
			dbHelper.close();
			dbHelper = null;
		}
		if (cursor!=null){
			cursor.close();
			cursor = null;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		makemenu();//设置自定义的menu菜单
		makeToolbar();//设置底部菜单
		makelistview();//设置listview
		makeMusicRecent();//设置最近播放列表
	}
	
	
	/*返回键询问是否退出*/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			exitall();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/*
	 * 设置自定义的菜单
	 * 利用自定义view和alertdialog
	 */
	public void makemenu(){
		// 设置自定义menu菜单
		menuView = View.inflate(this, R.layout.gridview_menu, null);
		// 创建AlertDialog
		menuDialog = new AlertDialog.Builder(this).create();
		menuDialog.setView(menuView);
		menuDialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU)// 监听按键
					dialog.dismiss();
				return false;
			}
		});

		menuGrid = (GridView) menuView.findViewById(R.id.gridview);
		menuGrid.setAdapter(getMenuAdapter(menu_name_array, menu_image_array));
		
		/** 监听menu选项 **/
		menuGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case 0://打开
                    System.out.println("intent1");
					Intent intent=new Intent();
					intent.setClass(MainActivity.this, fileMng.class);
					System.out.println("intent2");
					MainActivity.this.startActivity(intent);
					break;
				case 1://电子书
					Intent intent2=new Intent();
					intent2.setClass(MainActivity.this,ebook.class);
					//finish();
					MainActivity.this.startActivity(intent2);
					
					break;
				case 2://听音乐
					Intent intent3=new Intent();
					intent3.setClass(MainActivity.this,TestMain.class);
					//finish();
					MainActivity.this.startActivity(intent3);
					break;
				case 3:// 帮助
					Toast.makeText(getApplicationContext(), "帮助",
						     Toast.LENGTH_SHORT).show();
					Intent intentHelp=new Intent(MainActivity.this,helpActivity.class);
					startActivity(intentHelp);
					break;
				case 4:// 关于
					Intent intentAbout=new Intent();
					intentAbout.setClass(MainActivity.this,About.class);
					MainActivity.this.startActivity(intentAbout);
					break;
				case 5:// 退出
					exitall();
					break;
				}
				
				
			}
		});
	}
	
	
	/*
	 * 创建底部菜单
	 */
	public void makeToolbar(){
		toolbarGrid = (GridView) findViewById(R.id.GridView_toolbar);
		toolbarGrid.setBackgroundResource(R.drawable.bottom);// 设置背景
		toolbarGrid.setNumColumns(2);// 设置每行列数
		toolbarGrid.setGravity(Gravity.CENTER);// 位置居中
		toolbarGrid.setVerticalSpacing(10);// 垂直间隔
		toolbarGrid.setHorizontalSpacing(80);// 水平间隔
		
		toolbarGrid.setAdapter(getMenuAdapter(menu_toolbar_name_array,
				menu_toolbar_image_array));// 设置菜单Adapter
		
		/** 监听底部菜单选项 **/
		toolbarGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case TOOLBAR_MENU://菜单
					menuDialog.show();
					break;
				case TOOLBAR_EXIT://退出
					exitall();
					break;
				}
			}
		});
	}
	
	
	public void makelistview(){
		/** ListView列表**/
		listView1 = (ListView) findViewById(R.id.ListView_catalog);
		listView1.setAdapter(getMenuAdapter(menu_name_array2, menu_image_array2));
		listView1.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				switch (arg2) {
				case 0://文件管理
					System.out.println("intent1");
					Intent intent=new Intent();
					intent.setClass(MainActivity.this, fileMng.class);
					System.out.println("intent2");
					MainActivity.this.startActivity(intent);
					break;
				case 1://电子书
					Intent intent4=new Intent();
					intent4.setClass(MainActivity.this,ebook.class);
					//finish();
					MainActivity.this.startActivity(intent4);
					break;
				case 2://听音乐
					Intent intent2=new Intent();
					intent2.setClass(MainActivity.this,TestMain.class);
					finish();
					MainActivity.this.startActivity(intent2);
					break;
				case 3://帮助
					Intent intentHelp=new Intent(MainActivity.this,helpActivity.class);
					startActivity(intentHelp);
					break;
				}
			}
			
		});
	}
	/*
	 * 最近播放的音乐
	 */
	public void makeMusicRecent(){
		System.out.println("makeMusicRecent");
		dbHelper = new DBHelper(this, "music.db", null, 2);
		cursor = dbHelper.queryRecently();
		cursor.moveToFirst();
		int num;
		if (cursor!=null){
			num = cursor.getCount();
			System.out.println("cursor num-->"+num);
		} else{
			TextView recentMusic=(TextView)findViewById(R.id.recentMusic);
			recentMusic.setText("最近播放的音乐（无）");
			
			System.out.println("cursor null-->");
			return;
		}
		String idString ="";
		if (num>=4){
			//大于4 只取出前4个
			for(int i=0;i<4;i++){
				music_id = new int[4];
				music_id[i]=cursor.getInt(cursor.getColumnIndex("music_id"));
				if (i<3){
					idString = idString+music_id[i]+",";
				} else{
					idString = idString+music_id[i];
				}
				cursor.moveToNext();
			} 
		}else if(num>0){
			for(int i=0;i<num;i++){
				music_id = new int[num];
				System.out.println("for music id-->"+num);
				music_id[i]=cursor.getInt(cursor.getColumnIndex("music_id"));
				System.out.println("for music id get-->"+music_id[i]);
				if (i<num-1){
					idString = idString+music_id[i]+",";
				} else{
					idString = idString+music_id[i];
					System.out.println("for music id get-->"+idString);
				}
				cursor.moveToNext();
				System.out.println("cursor move to next");
			}
		}
		if (cursor!=null){
			cursor.close();
			cursor=null;
			System.out.println("cursor null");
		}
		if (dbHelper!=null){
			dbHelper.close();
			dbHelper = null;
			System.out.println("dahelp null");
		}
		//listview = new ListView(this);
		Cursor c = this.getContentResolver()
		.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[]{MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.DURATION,
				MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.DATA},
				MediaStore.Audio.Media._ID + " in ("+ idString + ")", null, null);
		  c.moveToFirst();
		  
		    c.moveToFirst();
		    _ids = new int[c.getCount()];
		    _titles = new String[c.getCount()];
		    _path = new String[c.getCount()];
		    
		    List<HashMap<String,String>> list=new ArrayList<HashMap<String,String>>();
		    for(int i=0;i<c.getCount();i++){
		    	HashMap<String,String> map=new HashMap<String,String>();
		    	
		    	_ids[i] = c.getInt(3);
		    	_titles[i] = c.getString(0);
		    	
		    	map.put("txtName", _titles[i]);
				System.out.println("musicRecent name-->"+_titles[i]);
				String musicTime=toTime(c.getInt(1));
				map.put("txtSize",musicTime );
				System.out.println("setlist txtsize-->"+musicTime);
				
		    	_path[i] = c.getString(5).substring(4);
		    	
		    	list.add(map);
				System.out.println("list add__>>");
				
		    	c.moveToNext();
		    }
	      System.out.println("for get com");
	      listView2=(ListView)findViewById(R.id.ListView_recent);
	      System.out.println("findview");
	     
	      SimpleAdapter	simpleAdapter=new SimpleAdapter(this,list,R.layout.txtinfo,new String[]{"txtName","txtSize"},new int[]{R.id.txt_name,R.id.txt_size});
		System.out.println("adapter already");
	     
		listView2.setAdapter(simpleAdapter);
	      System.out.println("listview set adapter");
	      listView2.setOnItemClickListener(new ListItemClickListener());
	}
	
	/**
	 * 时间格式转换
	 * @param time
	 * @return
	 */
	public String toTime(int time) {

		time /= 1000;
		int minute = time / 60;
		int hour = minute / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}
	
	
    class ListItemClickListener implements OnItemClickListener{

    	@Override
    	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
    		// TODO Auto-generated method stub
    		Intent intent = new Intent(MainActivity.this,MusicActivity.class);
    		intent.putExtra("_ids", _ids);
    		intent.putExtra("_titles", _titles);
    		intent.putExtra("position", position);
    		intent.putExtra("paths", _path);
    		finish();
    		startActivity(intent);
    		//finish();
    	}
    	
    }
	@Override
	/**
	 * 创建MENU
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("menu");// 必须创建一项
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	/**
	 * 拦截MENU
	 */
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (menuDialog == null) {
			menuDialog = new AlertDialog.Builder(this).setView(menuView).show();
		} else {
			menuDialog.show();
		}
		return false;// 返回为true 则显示系统menu
	}
	
	/**
	 * 构造菜单Adapter
	 * 
	 * @param menuNameArray
	 *            名称
	 * @param imageResourceArray
	 *            图片
	 * @return SimpleAdapter
	 */
	private SimpleAdapter getMenuAdapter(String[] menuNameArray,
			int[] imageResourceArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemText", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter=null;
		
			simperAdapter = new SimpleAdapter(this, data,
					R.layout.item_menu, new String[] { "itemImage", "itemText" },
					new int[] { R.id.item_image, R.id.item_text });
		
		return simperAdapter;
	}	
	
	
	/**执行退出的函数**/
	public void exitall(){
		new AlertDialog.Builder(this).setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert)).setTitle("警告").setMessage("确定退程序？").setPositiveButton("确定",
				new AlertDialog.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (scanSdReceiver!=null)
			  				{
							unregisterReceiver(scanSdReceiver);
							}
						finish();
						Intent intent2 = new Intent();
						intent2.setAction("com.alex.media.MUSIC_SERVICE");
						stopService(intent2);
						}
			
			}).setNegativeButton("取消", new AlertDialog.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
			
		}).show();
	}
		
}
