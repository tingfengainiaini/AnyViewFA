package com.alex.media.ebookpart;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.alex.media.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class localList extends ListActivity{
	 String currentDir = null;
	 SimpleAdapter simpleAdapter=null;
		private int num;
	 private List<txtInfo> txtInfos=new ArrayList<txtInfo>();	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localebook);
		System.out.println("localList oncreate"); 
        currentDir = Environment.getExternalStorageDirectory().toString();//��ȡ��Ŀ¼ 
        System.out.println("sddir"+currentDir);
        setList();
        System.out.println("setList already");
      
        //���ó���֧�֣������������Ĳ˵���
		getListView().setOnCreateContextMenuListener(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo info) {
		menu.setHeaderTitle("Option");
		 menu.add(0, 1, 0, "��");
         menu.add(0, 2, 0, "ɾ��");
         final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) info;
			num = menuInfo.position;
	}

	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("���Ҫɾ����")
		.setPositiveButton("��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {					
				deleteMusicFile(num);//��sdcard��ɾ��,����ɾ�����ֵĺ���
				
				setList();						//���»���б�����ʾ������
				simpleAdapter.notifyDataSetChanged();		//�����б�UI
			}
		})
		.setNegativeButton("��", null);
		AlertDialog ad = builder.create();
		ad.show();
		return true;
	}
	
	/*��sdcard��ɾ��*/
	private void deleteMusicFile(int position){
		File file = new File(txtInfos.get(position).getTxtPath());
		file.delete();
	}
	/*
	 * �����б�
	 */
	void setList(){
		//this.getListView().clearChoices();
		getFiles(currentDir);
		List<HashMap<String,String>> list=new ArrayList<HashMap<String,String>>();
		for (Iterator iterator=txtInfos.iterator();iterator.hasNext();){
			txtInfo txtinfo=(txtInfo)iterator.next();
			HashMap<String,String> map=new HashMap<String,String>();
			
			map.put("txtName", txtinfo.getTxtName());
			System.out.println("setlist txtname-->"+txtinfo.getTxtName());
			
			map.put("txtSize", txtinfo.getTxtSize());
			System.out.println("setlist txtsize-->"+txtinfo.getTxtSize());
			
			list.add(map);
			System.out.println("list add__>>");
		}
		System.out.println("hashmap already");
		simpleAdapter=new SimpleAdapter(this,list,R.layout.txtinfo,new String[]{"txtName","txtSize"},new int[]{R.id.txt_name,R.id.txt_size});
		System.out.println("adapter already");
		setListAdapter(simpleAdapter);
		}
	/*
	 * ������е�txt�ļ�
	 */
	public void getFiles(String currentDir){
		 int i;
		    File currentFile=new File(currentDir);
		    File[] files = currentFile.listFiles();
		    for(i=0;i<files.length;i++){
		    	System.out.println("for-->"+i+"");
		    	if(files[i].getName().equals(".android_secure")){
		    		continue;//����ļ��е�Ȩ��������ء������������롭��
		    	}
		    	else if(files[i].isDirectory()){
		    		currentDir=files[i].getPath().toString();
		    		System.out.println(i+"-->"+currentDir);
		    		getFiles(files[i].getPath().toString());
		    	}
		    	else if(files[i].getName().endsWith(".txt")){
		    		System.out.println(files[i].getName());
		    		txtInfo txtInfoNow=new txtInfo();
		    		float usesize;
		           long filesize=files[i].length();
		           if(filesize>1048576){
		        	   usesize=filesize/1048576;
		        	   txtInfoNow.setTxtSize(usesize+" M");
		           }
		           if(filesize>1024){
		        	   usesize=filesize/1024;
		        	   txtInfoNow.setTxtSize(usesize+" K");
		           }
		           else {
		        	   txtInfoNow.setTxtSize(filesize+" B");
		           }
		            txtInfoNow.setTxtPath(files[i].getPath().toString());
		            txtInfoNow.setTxtName(files[i].getName().toString());
		            txtInfos.add(txtInfoNow);
		            }
		    }
		    System.out.println("for circle quit");
		    //return 
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		txtInfo txtInfoOn=txtInfos.get(position);
		Intent i=new Intent();
		i.putExtra("path", txtInfoOn.getTxtPath());
		i.setClass(localList.this, textView.class);
		localList.this.startActivity(i);
	}
	
	
}
