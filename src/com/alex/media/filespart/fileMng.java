package com.alex.media.filespart;

import java.io.File;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.alex.media.R;
import com.alex.media.ebookpart.textView;
import com.alex.media.pictruepart.picpartFlow;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class fileMng extends ListActivity {
	
	//   长按List出现的选项
	public static final int FILE_COPY = Menu.FIRST;
	public static final int FILE_CUT = Menu.FIRST + 1;
	public static final int FILE_RENAME = Menu.FIRST + 2;
	public static final int FILE_DELETE = Menu.FIRST + 3;
	public static final int FILE_PROPERTY = Menu.FIRST + 4;
	public static final int FILE_PASTE = Menu.FIRST + 5;
	
	private EditText FloderName;
	private EditText NewName;
	
	private boolean copyyes = false;
	private boolean copyFolderok = false; 
	private boolean cutyes = false; 
	private boolean cutFolderok = false; 
	
	private String oldpath;
	private String oldFolderName;
	
	String sortBy="no";
	static final int REQUEST_CODE = 1;
	
	private static String SDpath;
	
 //   protected static final int SUB_ACTIVITY_REQUEST_CODE = 1337;
	
	private List<IconifiedText> directoryEntries = new ArrayList<IconifiedText>();
	
	public File currentDirectory =null;// 当前目录
	
	public boolean TCardExist;
	private ProgressDialog progressDialog;


//	private File currentDirectory = new File(filePath); 
	
	/** Called when the activity is first created. */
	/** Activity被创建时调用 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//启用长按支持，弹出的上下文菜单在
		getListView().setOnCreateContextMenuListener(this);
		
		TCardExist = judgeSDcard();
		if(TCardExist){
			SDpath = getSDPath();
			System.out.println(SDpath);
			//setContentView(R.layout.filemanager);//貌似这样不能设置长按响应
			this.getListView().setBackgroundResource(R.drawable.bg3);
			currentDirectory=new File(SDpath);
			EnterFolder(currentDirectory,"no");
			 
			this.setSelection(0);
		}else
		{
			setContentView(R.layout.nosdcard);
		}
	//	this.setSelection(0);
	}
	
	//判断sd卡是否存在 	
	public boolean judgeSDcard(){
		
		boolean sdCardExist = Environment.getExternalStorageState()   
        .equals(android.os.Environment.MEDIA_MOUNTED); 
		return sdCardExist;
	}
	
	//获取sd卡路径  Environment 
	public String getSDPath(){ 
	       File sdDir = null; 
	         sdDir = Environment.getExternalStorageDirectory();//获取跟目录 

	       return sdDir.toString(); 
	} 

	
	/**
	 * This function browses up one level 
	 * according to the field: currentDirectory
	 */
	private void upOneLevel(){
		if(this.currentDirectory.getParent() != null)
			this.EnterFolder(this.currentDirectory.getParentFile(),sortBy);
	}
	
	
	private void EnterFolder(final File aDirectory,String sortMethod){
			/*getAbsolutePath 得到一个文件的绝对路径
			 * 并且将activity的名字设置成路径
			 */
			this.setTitle(aDirectory.getAbsolutePath());
		Log.e("open folder aDirectory.getAbsolutePath()", aDirectory.getAbsolutePath());
		
			this.currentDirectory = aDirectory;
			Log.e("Fill 路径",aDirectory.getPath());
			FillList(aDirectory.listFiles(),sortMethod);
	}
	
	
	/**寻找适合的程序打开文件*/

	  private void openFile(File f)
	  {
		System.out.println("openfile there");
	    Intent intent = new Intent();
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    intent.setAction(android.content.Intent.ACTION_VIEW);
	   
	   
	    String type = getMIMEType(f);
	    //如果是图片
	    if(type.equals("image/*")){
	    	System.out.println("jpg__>");
	    	Intent intent2=new Intent();
	    	intent2.putExtra("path", f.getAbsolutePath());
	    	intent2.putExtra("parentPath", f.getParentFile().getAbsolutePath().toString());
	    	intent2.putExtra("filename",f.getName());
	    	System.out.println("intent");
	    	intent2.setClass(fileMng.this, picpartFlow.class);
	    	System.out.println("setclass");
	    	fileMng.this.startActivity(intent2);
	    	System.out.println("start");
	    	}
	    /*else if(type.equals("mp3/*")){
	    	
	    }*/
	    else if(type.equals("apk/*")){
	    	intent.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");
	    	startActivity(intent);
	    }
	    else if(type.equals("txt/*")){
	    	Intent txtIntent=new Intent(fileMng.this,textView.class);
	    	txtIntent.putExtra("path", f.getAbsolutePath());
	    	startActivity(txtIntent);
	    }
	    else{
	    	intent.setDataAndType(Uri.fromFile(f),type);
	    	try {
	    		//让系统寻找相应的程序
	    		System.out.println("other open method");
	    		startActivity(intent);
	    	}catch(Exception e){
	    	Toast.makeText(fileMng.this,  
                    "没找到打开这个文件的应用程序……",  
                    Toast.LENGTH_SHORT).show(); 

	    }
	    }
	  }

	//获得文件的类型（读取后缀）
	  private String getMIMEType(File f)
	  {
	    String type="";
	    String fName=f.getName();
	    if(f.isDirectory()){
	    	type="folder";
	    	return type;
	    }
	    /* 取得扩展名 */
	    String end=fName.substring(fName.lastIndexOf(".")+1,fName.length()).toLowerCase();
	   
	   
	    if(end.equals("m4a")||end.equals("mid")||
	       end.equals("xmf")||end.equals("ogg")||end.equals("wav"))
	    {
	      type = "audio";
	    }
	    else if(end.equals("mp3")){
	    	type = "mp3";
	    }
	    else if(end.equals("3gp")||end.equals("mp4"))
	    {
	      type = "video";
	    }
	    else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||
	            end.equals("jpeg")||end.equals("bmp"))
	    {
	      type = "image";
	    }
	     else if(end.equals("apk")) 
        { 
           type = "apk"; 
        } 
	    else if(end.equals("txt")||end.equals("lrc"))
	    {
	      type = "txt";
	    }
	    else if(end.equals("xls")){
	    	type = "xls";
	    }
	    else if(end.equals("doc")){
	    	type="doc";
	    }
	    else if(end.equals("pdf")){
	    	type="pdf";
	    }
	    else if(end.equals("ppt")){
	    	type="ppt";
	    }
	    else if(end.equals("zip")||end.equals("rar")||end.equals("jar"))
	    {
	      type = "packed";
	    }
	    else
	    {
	      type="other";
	    }
	   
	    type += "/*";

	    return type;
	  }
	  
	  //获得图标
	  public Drawable getCurrentIcon(String type){
		  Drawable currentIcon = null;
		  if(type.equals("folder")){
					currentIcon = getResources().getDrawable(R.drawable.folder); 
		  }
		  else if(type.equals("image/*")){
				currentIcon = getResources().getDrawable(R.drawable.myfiles_file_image); 
			}
			else if(type.equals("video/*")){
				currentIcon = getResources().getDrawable(R.drawable.myfiles_file_video); 
			}
			else if(type.equals("packed/*")){
				currentIcon = getResources().getDrawable(R.drawable.myfiles_file_zip); 
			}
			else if((type.equals("audio/*"))||(type.equals("mp3/*"))){
				currentIcon = getResources().getDrawable(R.drawable.myfiles_file_music); 
			}
			else if(type.equals("txt/*")){
				currentIcon = getResources().getDrawable(R.drawable.text); 
			}
			else if(type.equals("doc/*")){
				currentIcon = getResources().getDrawable(R.drawable.myfiles_file_doc); 
			}
			else if(type.equals("pdf/*")){
				currentIcon = getResources().getDrawable(R.drawable.myfiles_file_pdf_thumb); 
			}
			else if(type.equals("xls/*")){
				currentIcon = getResources().getDrawable(R.drawable.myfiles_file_xls_thumb); 
			}
			else if(type.equals("ppt/*")){
				currentIcon = getResources().getDrawable(R.drawable.myfiles_file_ppt); 
			}
			else if(type.equals("apk/*")){
				currentIcon = getResources().getDrawable(R.drawable.myfiles_file_apk); 
			}
			else if(type.equals("other/*")){
				currentIcon = getResources().getDrawable(R.drawable.myfiles_file_others); 
			}
		  return currentIcon;
	  }
	  
	  /*
	   * 为了显示的人性化，将文件夹放到前面
	   */
	  private void sortfolder(File[] files){
			for (File currentFile : files){
				// 查看是文件夹还是文件
				Drawable currentIcon = null;
					String type="";
					type = getMIMEType(currentFile);//判断文件
					if(type.equals("folder")){
						currentIcon=getCurrentIcon(type);
						
					    int currentPathStringLenght = this.currentDirectory.getAbsolutePath().length();
							
						System.out.println("relative enter"+currentPathStringLenght);
							
						System.out.println("getparent notnull");
						this.directoryEntries.add(new IconifiedText(
									currentFile.getAbsolutePath().
									substring(currentPathStringLenght+1),
									currentIcon,null));
					}
			}
	  }
	  
	  /*
	   * 无排序要求时的排序
	   */
	  private void sortNo(File[] files){
		  for (File currentFile : files){
				// 查看是文件夹还是文件
				Drawable currentIcon = null;
					String type="";
					type = getMIMEType(currentFile);//判断文件
					if(!type.equals("folder")){
						currentIcon=getCurrentIcon(type);
						
					    int currentPathStringLenght = this.currentDirectory.getAbsolutePath().length();
							
						System.out.println("relative enter"+currentPathStringLenght);
							
						System.out.println("getparent notnull");
						this.directoryEntries.add(new IconifiedText(
									currentFile.getAbsolutePath().
									substring(currentPathStringLenght+1),
									currentIcon,null));
					}
			}
	  }
	 
	  /*
	   * 按大小排序
	   */
	  private void sortBySize(File[] files){
		 
	  }
	  /*
	   * 按名称排序
	   */
	  private void sortByName(File[] files){
		 
		  TreeSet<String> s = new TreeSet<String>(); //treeset可自动排序
		  
		  for (File currentFile : files){
				// 查看是文件夹还是文件
					String type="";
					type = getMIMEType(currentFile);//判断文件
					if(!type.equals("folder")){
						 s.add(currentFile.getName());        //s中添加当前文件的名字
					}
			}
		  /*
		   * 利用迭代器将名字添加到directoryEntries中
		   */
		  Iterator it = s.iterator();
		  
		  while (it.hasNext()) {
			    Drawable currentIcon = null;
				String type="";
				String currentPath=this.currentDirectory.getAbsolutePath()+"/"+(String)it.next(); 
				File currentFile=new File(currentPath);
				type = getMIMEType(currentFile);//判断文件
				if(!type.equals("folder")){
					currentIcon=getCurrentIcon(type);
					
				    int currentPathStringLenght = this.currentDirectory.getAbsolutePath().length();
						
					System.out.println("relative enter"+currentPathStringLenght);
						
					System.out.println("getparent notnull");
					this.directoryEntries.add(new IconifiedText(
								currentFile.getAbsolutePath().
								substring(currentPathStringLenght+1),
								currentIcon,null));
				}
		  }
	  }

	
	  /*
	   * 用获得数据填充directoryEntries，并且显示出来  
	   */
	private void FillList(File[] files,String sortMethod) {
			
		this.directoryEntries.clear();//清空列表
		
		String momentpath = GetCurDirectory();
		Log.e("GetCurDirectory momentpath",momentpath);
		Log.e("getSDPath sdpath",SDpath);
		// and the ".." == 'Up one level'
		//如果不是根目录则添加上一级目录
		if(!momentpath.equals(SDpath))
		{
			this.directoryEntries.add(new IconifiedText(
					getString(R.string.up_one_level), 
					getResources().getDrawable(R.drawable.uponelevel),null));
		}
		
		//注意，我改成了，不管排不排序都是将文件夹放在前面
		if(sortMethod.equals("no")){
			//不排序
			 sortfolder(files);
			sortNo(files);
		}
		else if(sortMethod.equals("bySize")){
			//按大小排序
			 sortfolder(files);
			 //下面本来该是sortBySize的，自己还没实现，只能先不排序用着……有事……我继续想方法，你先看看……
			 sortNo(files);
			//sortBySize(files);
			}
		else if(sortMethod.equals("byName")){
			//名字排序，已经实现
			 sortfolder(files);
			sortByName(files);
		}
		else if(sortMethod.equals("byTime")){
			//按时间排序，这个是有个函数的，你查查
			 sortfolder(files);
			 //下面本来该是其他的，自己还没实现，只能先不排序用着……有事……我继续想方法，你先看看……
			 sortNo(files);
			
		}
		else if(sortMethod.equals("byStyle")){
			//按类型排序，这个是有个函数的，你查查
			 sortfolder(files);
			 //下面本来该是其他的，自己还没实现，只能先不排序用着……有事……我继续想方法，你先看看……
			 sortNo(files);
		}
		
//		Collections.sort(this.directoryEntries);//排序
		

		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this);
		itla.setListItems(this.directoryEntries);		
		this.setListAdapter(itla);
	}

	
	//长按弹出菜单     上下文菜单
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        
        try {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            return;
        }

 //       Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
 //      if (cursor == null) {
 //           return;
 //       }

        menu.setHeaderTitle("Option");
      if(copyyes == false&&cutyes == false){
    	  
    	  menu.add(0, FILE_DELETE, 0, R.string.delete);
          menu.add(0, FILE_CUT, 0, R.string.cut);
          menu.add(0, FILE_COPY, 0, R.string.copy);    
          menu.add(0, FILE_RENAME, 0, R.string.rename);        
          menu.add(0, FILE_PROPERTY, 0, R.string.property);
    	  
      }else{
        	menu.add(0, FILE_DELETE, 0, R.string.delete);
            menu.add(0, FILE_CUT, 0, R.string.cut);
            menu.add(0, FILE_COPY, 0, R.string.copy);    
            menu.add(0, FILE_RENAME, 0, R.string.rename);        
            menu.add(0, FILE_PROPERTY, 0, R.string.property);
      }
    }
	


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK)
    	{
    		if(this.currentDirectory.getAbsolutePath().toString().equals("/mnt/sdcard"))
    	     {
    			finish();
//    			return  onKeyDown(keyCode,event);
    	     }
    	else
    		{
    		  this.EnterFolder(this.currentDirectory.getParentFile(),sortBy);
    		  
        	}
    		return true;
    	}else{
    		return super.onKeyDown(keyCode, event);
    	}
		
	}

	//上下文菜单选择的回调函数
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            return false;
        }
        switch (item.getItemId()) {
        case FILE_DELETE: 
        {
            final String del = this.currentDirectory.getAbsolutePath()+"/"+this.directoryEntries.get(info.position).getText();
         
            new AlertDialog.Builder(this)
	        .setTitle("提示")
	        .setMessage("确定删除?")
	        .setIcon(R.drawable.alert_dialog_icon)
	        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	        setResult(RESULT_OK);//确定按钮事件
	        delDir(del);	        
	        }
	        })
	        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	         //取消按钮事件
	        }
	        })
	        .show();
            break;
        }
        case FILE_CUT:
        {
        	File judgeFolderFile=new File(this.currentDirectory.getAbsolutePath()+"/"+this.directoryEntries.get(info.position).getText());
        	if(judgeFolderFile.isDirectory()){
        		Log.e("剪切文件夹",judgeFolderFile.toString());
            	cutFolderok = true;
        	}else{
        		Log.e("剪切单个文件",judgeFolderFile.toString());
        	}
        	oldpath = null; oldFolderName = null;
        	oldpath = this.currentDirectory.getAbsolutePath()+"/"+this.directoryEntries.get(info.position).getText(); 	
        	oldFolderName = this.directoryEntries.get(info.position).getText();
        	cutyes = true;
        }
        	break;
        case FILE_COPY:
        {
        	
        	File judgeFolderFile=new File(this.currentDirectory.getAbsolutePath()+"/"+this.directoryEntries.get(info.position).getText());
        	if(judgeFolderFile.isDirectory()){
        		Log.e("复制文件夹",judgeFolderFile.toString());
            	copyFolderok = true;
        	}else{
        		Log.e("复制单个文件",judgeFolderFile.toString());
        	}
         	oldpath = null; oldFolderName = null;
        	oldpath = this.currentDirectory.getAbsolutePath()+"/"+this.directoryEntries.get(info.position).getText(); 	
        	oldFolderName = this.directoryEntries.get(info.position).getText();
        	copyyes = true;
        	break;
        }
        case FILE_RENAME:
        {
        	final String oldName = this.directoryEntries.get(info.position).getText();
        	final String filepath = this.currentDirectory.getAbsolutePath();
			LayoutInflater inflater = getLayoutInflater();
			   final View layout = inflater.inflate(R.layout.rename, null);

			   new AlertDialog.Builder(this).setTitle("重命名").setView(layout)
			     .setPositiveButton("确定", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
					        setResult(RESULT_OK);//确定按钮事件
					        
					        NewName = (EditText) layout.findViewById(R.id.newname_edit);
							String newName = NewName.getText().toString();

							renameFile(filepath,oldName,newName);
							Log.e("filepath",filepath);
							Log.e("oldName",oldName);
							Log.e("newName",newName);
					        }
					        })
			     .setNegativeButton("取消", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				        	
				        	setTitle("取消");
					         //取消按钮事件
					        }
					        }).show();	
			 		
			break;
        }
        
        case FILE_PROPERTY:   
        	final String now= this.currentDirectory.getAbsolutePath()+"/"+this.directoryEntries.get(info.position).getText();
            File nowFile=new File(now);
            String nowSize;
           
            	
 	          if(nowFile.isDirectory()){
 	            	nowSize="文件夹";
 	            }else{
 	            	float usesize;
 	        		
 	 	           long filesize=nowFile.length();
 	 	           if(filesize>1048576){
 	 	        	   usesize=filesize/1048576;
 	 	        	   nowSize=usesize+" M";
 	 	           }
 	 	           if(filesize>1024){
 	 	        	   usesize=filesize/1024;
 	 	        	   nowSize=usesize+" K";
 	 	           }
 	 	           else {
 	 	        	   nowSize=filesize+" B";
 	 	           }
 	            }
 	          
             new AlertDialog.Builder(this)
 	        .setTitle("属性")
 	        .setMessage(now+"\n"+nowSize)
 	        .setIcon(R.drawable.alert_dialog_icon)
 	        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
 	        public void onClick(DialogInterface dialog, int whichButton) {}}).show();
    		
        	break;
        	}
		return true;

    }
    
    //点击列表项目的响应，文件夹的话进入，不是的话选择程序打开
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		System.out.println("file list clicked");
		super.onListItemClick(l, v, position, id);
		
		getListView().getSelectedItemId();
				
//		int selectionRowID = (int) this.getSelectedItemId();
//		int selectionRowID = (int) getListView().getSelectedItemId();
		
		String selectedFileString = this.directoryEntries.get(position).getText();
										
		Log.e("onListItemClick Item named",selectedFileString);
	
		
		
		if (selectedFileString.equals(getString(R.string.current_dir))) {
			// Refresh 刷新
			Log.e("选择Items", "刷新");
			this.EnterFolder(this.currentDirectory,sortBy);
		} else if(selectedFileString.equals(getString(R.string.up_one_level))){
			// 到上一阶目录
			Log.e("选择Items", "返回上级");
			this.upOneLevel();
		} else {

			File clickedFile = null;
					clickedFile = new File(this.currentDirectory.getAbsolutePath()
												+"/"+ this.directoryEntries.get(position).getText());
				
			if(clickedFile.getName().equals(".android_secure")){
				Toast.makeText(getApplicationContext(), "此文件夹拒绝进入",
					     Toast.LENGTH_SHORT).show();//这个文件夹的权限问题很重…………不进入……
	    	}
			else if(clickedFile.isDirectory())
			{
				Log.e("打开文件夹",clickedFile.getName());
				this.EnterFolder(clickedFile,sortBy);
			}
			else
			{
				Log.e("打开文件", clickedFile.getName());
				openFile(clickedFile);
			}
		
			
	}
	}
	
	//得到当前目录的绝对路径
	public String GetCurDirectory(){	  
		  return this.currentDirectory.getAbsolutePath();
		}
	//菜单键的响应
  @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	  menu.add(0,0,0,"新建文件夹").setIcon(R.drawable.ic_menu_create);
		menu.add(0,1,0,"粘贴").setIcon(R.drawable.ic_menu_copy);
		menu.add(0,2,0,"排序方式").setIcon(R.drawable.ic_menu_view_by);
		menu.add(0,3,0,"退出管理").setIcon(R.drawable.ic_menu_go_to);
		return super.onCreateOptionsMenu(menu);
	}

  @Override
	public boolean onOptionsItemSelected(MenuItem item) {   
		  
		super.onOptionsItemSelected(item);
		
		switch(item.getItemId()){
		case 0://新建
			//create_folder();
			LayoutInflater inflater = getLayoutInflater();
			   final View layout = inflater.inflate(R.layout.create_folder, null);

			   new AlertDialog.Builder(this).setTitle("创建文件夹").setView(layout)
			     .setPositiveButton("确定", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
					        setResult(RESULT_OK);//确定按钮事件
					        
							FloderName = (EditText) layout.findViewById(R.id.folder_edit);
							String info = FloderName.getText().toString();
							System.out.println(info);
							

					        newFolder(info);
							
							
					        }
					        })
			     .setNegativeButton("取消", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				        	
				        	setTitle("取消");
					         //取消按钮事件
					        }
					        }).show();	
			 		
			break;

		case 1://粘贴
			if((copyyes==false)&&(cutyes==false))
			{
				Toast.makeText(getApplicationContext(),"没有文件或文件夹进行过复制或粘贴", Toast.LENGTH_SHORT).show();
			}
        	//显示ProgressDialog
			else{
			progressDialog = ProgressDialog.show(fileMng.this, "working...", "Please wait...", true);
        	if(copyyes == true){
        	if(copyFolderok == true){
        	String newpath = this.currentDirectory.getAbsolutePath()+"/"+oldFolderName; 
        	copyFolder(oldpath,newpath);
        	}
        	else{
        		
        		String newpath = this.currentDirectory.getAbsolutePath()+"/"+oldFolderName; 
        		copyFile(oldpath,newpath);        
        	}
        	progressDialog.dismiss();
        	this.EnterFolder(this.currentDirectory,sortBy);//刷新
        	copyyes = false;
        	cutyes = false;
        	copyFolderok = false;
        	}
        	else{
        		if(cutFolderok == true){
        			String newpath = this.currentDirectory.getAbsolutePath()+"/"+oldFolderName; 
                	moveFolder(oldpath,newpath);
        		}else{
        			String newpath = this.currentDirectory.getAbsolutePath()+"/"+oldFolderName; 
            		moveFile(oldpath,newpath);
        		}
        		progressDialog.dismiss();
        		this.EnterFolder(this.currentDirectory,sortBy);//刷新
        		cutyes = false;
        		copyyes = false;
        		cutFolderok = false;
        	}
			}
        	break;
		case 2://查看模式
			new AlertDialog.Builder(this).setTitle("排序方式").setIcon(
				     android.R.drawable.ic_dialog_info).setSingleChoiceItems(
				     new String[] { "按时间排序", "按大小排序", "按类型排序","按名称排序" }, 0,
				     new DialogInterface.OnClickListener() {
				      public void onClick(DialogInterface dialog, int which) {
				    	switch(which)  {
				          case 0:
				    	      sortBy="byTime";
				    		  break;
				    	  case 1:
				    		  sortBy="bySize";
				    		  Toast.makeText(getApplicationContext(), "bySize",
									     Toast.LENGTH_SHORT).show();
				    			  break;
				    	  case 2:
				    		  sortBy="byStyle";
			    			  break;
				    	  case 3:
				    		  sortBy="byName";
			    			  break;
				    	}
				    	EnterFolder(currentDirectory,sortBy);
				      }
				     }).setNegativeButton("返回", null).show();
			//sortList(sortBy);
			break;

		case 3:
			finish();
			break;
		default:
			break;
		}
	  
	    return true;   
	  
	} 
	

	//新建一个文件夹 
	private void newFolder(String folderPath){
		Log.e("yangw","create new folder");
		 try{
		//	 File destDir = new File("/sdcard/test");//.createNewFile();
			 String filePath = folderPath;
			 File myFilePath = new File(this.currentDirectory.getAbsolutePath()+"/"+filePath); 
			 System.out.println("新建文件夹名称"+this.currentDirectory.getAbsolutePath()+"/"+filePath);
			 
			 if (!myFilePath.exists()) { 
			        myFilePath.mkdir(); 

		  }	else{   
			  
			  new AlertDialog.Builder(this) 
		    .setTitle("创建失败") 
		    .setMessage("文件已经存在")
		    .setPositiveButton("确定",null)
		    .show();	  
		  }
		  
		  this.EnterFolder(this.currentDirectory,sortBy);//刷新
		  
		 }catch(Exception e){
			 System.out.println("新建文件夹操作出错");	 
			 return;
		 }
	}
	
	/*
	 * 删除文件、文件夹
	 * */
	   public void delDir(String path){   
	       File dir=new File(path);   
	       if (!dir.exists()) { 
	    	   return; 
	       }
	       if(dir.isFile())
	       {
	    	   String filePath = path; 
	    	   filePath = filePath.toString(); 
	    	   File delPath = new File(filePath); 
	    	   delPath.delete(); 
	    	   this.EnterFolder(this.currentDirectory,sortBy);//刷新
	       }
	       if(dir.isDirectory()){   
	           File[] tmp=dir.listFiles();   
	           for(int i=0;i<tmp.length;i++){   
	               if(tmp[i].isDirectory()){   
	                   delDir(path+"/"+tmp[i].getName());   
	               }   
	               else{   
	                   tmp[i].delete();   
	               }   
	           }   
	           dir.delete();   
	           this.EnterFolder(this.currentDirectory,sortBy);//刷新
	       }   
	   }  
	   
  /** 
  * 复制单个文件 
  * @param oldPath String 原文件路径 如：c:/fqf.txt 
  * @param newPath String 复制后路径 如：f:/fqf.txt 
  * @return boolean 
  */ 
	   public static void copyFile(String oldPath, String newPath) { 
		   try { 
		          int bytesum = 0; 
		          int byteread = 0; 
		          File oldfile = new File(oldPath); 
		          if (oldfile.exists()) { //文件存在时 
		        	  InputStream inStream = new FileInputStream(oldPath); //读入原文件 
		              FileOutputStream fs = new FileOutputStream(newPath); 
		              byte[] buffer = new byte[1444]; 
		              
		              while ( (byteread = inStream.read(buffer)) != -1) { 
		                  bytesum += byteread; //字节数 文件大小 
		                  System.out.println(bytesum); 
		                  fs.write(buffer, 0, byteread); 
		              } 
		              inStream.close(); 
		          }
		   } 
		      catch (Exception e) { 
		          System.out.println("复制单个文件操作出错"); 
		          e.printStackTrace(); 
	   }
	   
	   } 
   
	   
  /** 
    * 复制整个文件夹内容 
    * @param oldPath String 原文件路径 如：c:/fqf 
    * @param newPath String 复制后路径 如：f:/fqf/ff 
    * @return boolean 
    * mkdir() 只能在已经存在的目录中创建创建文件夹。
    * mkdirs() 可以在不存在的目录中创建文件夹。诸如：a\\b,既可以创建多级目录。 
    */ 
  public static void copyFolder(String oldPath, String newPath) { 

      try { 
          (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹   
          File a=new File(oldPath); 
          String[] file=a.list(); 
          File temp=null; 
          for (int i = 0; i < file.length; i++) { 
              if(oldPath.endsWith(File.separator)){ 
                  temp=new File(oldPath+file[i]); 
              } 
              else{ 
                  temp=new File(oldPath+File.separator+file[i]); 
              } 
              if(temp.isDirectory()){//如果是子文件夹 
                  copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]); 
              }
              if(temp.isFile()){ 
                  FileInputStream input = new FileInputStream(temp); 
                  FileOutputStream output = new FileOutputStream(newPath + "/" + 
                          (temp.getName()).toString()); 
                  byte[] b = new byte[1024 * 5]; 
                  int len; 
                  while ( (len = input.read(b)) != -1) { 
                      output.write(b, 0, len); 
                  } 
                  output.flush(); //清空缓冲区数据
                  output.close(); 
                  input.close(); 
              }               
          } 
          
      }
      catch (Exception e) { 
          System.out.println("复制整个文件夹内容操作出错"); 
          e.printStackTrace(); 

      } 
      

  } 

  //剪切文件
  public void moveFile(String oldPath, String newPath) { 
      copyFile(oldPath, newPath); 
      delDir(oldPath); 

  } 
  
  /** 
    * 移动文件到指定目录 
    */ 
  public void moveFolder(String oldPath, String newPath) { 
      copyFolder(oldPath, newPath); 
      delDir(oldPath); 

  } 

  /** *//**文件重命名  
   * @param path 文件目录  
   * @param oldname  原来的文件名  
   * @param newname 新文件名  
   */  
  public void renameFile(String path,String oldname,String newname)
  {   
		Log.e("renameFile filepath",path);
		Log.e("renameFile oldName",oldname);
		Log.e("renameFile newName",newname);
      if(!oldname.equals(newname))
      {//新的文件名和以前文件名不同时,才有必要进行重命名   
          File oldfile=new File(path+"/"+oldname);   
          File newfile=new File(path+"/"+newname);   
          if(newfile.exists())//若在该目录下已经有一个文件和新文件名相同，则不允许重命名   
              System.out.println(newname+"已经存在！");   
          else{   
              oldfile.renameTo(newfile);   
              Log.e("oldfile.renameTo(newfile);","success");
              this.EnterFolder(this.currentDirectory,sortBy);//刷新
          }    
      }            
  }  

  		
}
	


