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
	
	//   ����List���ֵ�ѡ��
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
	
	public File currentDirectory =null;// ��ǰĿ¼
	
	public boolean TCardExist;
	private ProgressDialog progressDialog;


//	private File currentDirectory = new File(filePath); 
	
	/** Called when the activity is first created. */
	/** Activity������ʱ���� */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//���ó���֧�֣������������Ĳ˵���
		getListView().setOnCreateContextMenuListener(this);
		
		TCardExist = judgeSDcard();
		if(TCardExist){
			SDpath = getSDPath();
			System.out.println(SDpath);
			//setContentView(R.layout.filemanager);//ò�������������ó�����Ӧ
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
	
	//�ж�sd���Ƿ���� 	
	public boolean judgeSDcard(){
		
		boolean sdCardExist = Environment.getExternalStorageState()   
        .equals(android.os.Environment.MEDIA_MOUNTED); 
		return sdCardExist;
	}
	
	//��ȡsd��·��  Environment 
	public String getSDPath(){ 
	       File sdDir = null; 
	         sdDir = Environment.getExternalStorageDirectory();//��ȡ��Ŀ¼ 

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
			/*getAbsolutePath �õ�һ���ļ��ľ���·��
			 * ���ҽ�activity���������ó�·��
			 */
			this.setTitle(aDirectory.getAbsolutePath());
		Log.e("open folder aDirectory.getAbsolutePath()", aDirectory.getAbsolutePath());
		
			this.currentDirectory = aDirectory;
			Log.e("Fill ·��",aDirectory.getPath());
			FillList(aDirectory.listFiles(),sortMethod);
	}
	
	
	/**Ѱ���ʺϵĳ�����ļ�*/

	  private void openFile(File f)
	  {
		System.out.println("openfile there");
	    Intent intent = new Intent();
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    intent.setAction(android.content.Intent.ACTION_VIEW);
	   
	   
	    String type = getMIMEType(f);
	    //�����ͼƬ
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
	    		//��ϵͳѰ����Ӧ�ĳ���
	    		System.out.println("other open method");
	    		startActivity(intent);
	    	}catch(Exception e){
	    	Toast.makeText(fileMng.this,  
                    "û�ҵ�������ļ���Ӧ�ó��򡭡�",  
                    Toast.LENGTH_SHORT).show(); 

	    }
	    }
	  }

	//����ļ������ͣ���ȡ��׺��
	  private String getMIMEType(File f)
	  {
	    String type="";
	    String fName=f.getName();
	    if(f.isDirectory()){
	    	type="folder";
	    	return type;
	    }
	    /* ȡ����չ�� */
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
	  
	  //���ͼ��
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
	   * Ϊ����ʾ�����Ի������ļ��зŵ�ǰ��
	   */
	  private void sortfolder(File[] files){
			for (File currentFile : files){
				// �鿴���ļ��л����ļ�
				Drawable currentIcon = null;
					String type="";
					type = getMIMEType(currentFile);//�ж��ļ�
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
	   * ������Ҫ��ʱ������
	   */
	  private void sortNo(File[] files){
		  for (File currentFile : files){
				// �鿴���ļ��л����ļ�
				Drawable currentIcon = null;
					String type="";
					type = getMIMEType(currentFile);//�ж��ļ�
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
	   * ����С����
	   */
	  private void sortBySize(File[] files){
		 
	  }
	  /*
	   * ����������
	   */
	  private void sortByName(File[] files){
		 
		  TreeSet<String> s = new TreeSet<String>(); //treeset���Զ�����
		  
		  for (File currentFile : files){
				// �鿴���ļ��л����ļ�
					String type="";
					type = getMIMEType(currentFile);//�ж��ļ�
					if(!type.equals("folder")){
						 s.add(currentFile.getName());        //s����ӵ�ǰ�ļ�������
					}
			}
		  /*
		   * ���õ�������������ӵ�directoryEntries��
		   */
		  Iterator it = s.iterator();
		  
		  while (it.hasNext()) {
			    Drawable currentIcon = null;
				String type="";
				String currentPath=this.currentDirectory.getAbsolutePath()+"/"+(String)it.next(); 
				File currentFile=new File(currentPath);
				type = getMIMEType(currentFile);//�ж��ļ�
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
	   * �û���������directoryEntries��������ʾ����  
	   */
	private void FillList(File[] files,String sortMethod) {
			
		this.directoryEntries.clear();//����б�
		
		String momentpath = GetCurDirectory();
		Log.e("GetCurDirectory momentpath",momentpath);
		Log.e("getSDPath sdpath",SDpath);
		// and the ".." == 'Up one level'
		//������Ǹ�Ŀ¼�������һ��Ŀ¼
		if(!momentpath.equals(SDpath))
		{
			this.directoryEntries.add(new IconifiedText(
					getString(R.string.up_one_level), 
					getResources().getDrawable(R.drawable.uponelevel),null));
		}
		
		//ע�⣬�Ҹĳ��ˣ������Ų������ǽ��ļ��з���ǰ��
		if(sortMethod.equals("no")){
			//������
			 sortfolder(files);
			sortNo(files);
		}
		else if(sortMethod.equals("bySize")){
			//����С����
			 sortfolder(files);
			 //���汾������sortBySize�ģ��Լ���ûʵ�֣�ֻ���Ȳ��������š������¡����Ҽ����뷽�������ȿ�������
			 sortNo(files);
			//sortBySize(files);
			}
		else if(sortMethod.equals("byName")){
			//���������Ѿ�ʵ��
			 sortfolder(files);
			sortByName(files);
		}
		else if(sortMethod.equals("byTime")){
			//��ʱ������������и������ģ�����
			 sortfolder(files);
			 //���汾�����������ģ��Լ���ûʵ�֣�ֻ���Ȳ��������š������¡����Ҽ����뷽�������ȿ�������
			 sortNo(files);
			
		}
		else if(sortMethod.equals("byStyle")){
			//����������������и������ģ�����
			 sortfolder(files);
			 //���汾�����������ģ��Լ���ûʵ�֣�ֻ���Ȳ��������š������¡����Ҽ����뷽�������ȿ�������
			 sortNo(files);
		}
		
//		Collections.sort(this.directoryEntries);//����
		

		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this);
		itla.setListItems(this.directoryEntries);		
		this.setListAdapter(itla);
	}

	
	//���������˵�     �����Ĳ˵�
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

	//�����Ĳ˵�ѡ��Ļص�����
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
	        .setTitle("��ʾ")
	        .setMessage("ȷ��ɾ��?")
	        .setIcon(R.drawable.alert_dialog_icon)
	        .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	        setResult(RESULT_OK);//ȷ����ť�¼�
	        delDir(del);	        
	        }
	        })
	        .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	         //ȡ����ť�¼�
	        }
	        })
	        .show();
            break;
        }
        case FILE_CUT:
        {
        	File judgeFolderFile=new File(this.currentDirectory.getAbsolutePath()+"/"+this.directoryEntries.get(info.position).getText());
        	if(judgeFolderFile.isDirectory()){
        		Log.e("�����ļ���",judgeFolderFile.toString());
            	cutFolderok = true;
        	}else{
        		Log.e("���е����ļ�",judgeFolderFile.toString());
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
        		Log.e("�����ļ���",judgeFolderFile.toString());
            	copyFolderok = true;
        	}else{
        		Log.e("���Ƶ����ļ�",judgeFolderFile.toString());
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

			   new AlertDialog.Builder(this).setTitle("������").setView(layout)
			     .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
					        setResult(RESULT_OK);//ȷ����ť�¼�
					        
					        NewName = (EditText) layout.findViewById(R.id.newname_edit);
							String newName = NewName.getText().toString();

							renameFile(filepath,oldName,newName);
							Log.e("filepath",filepath);
							Log.e("oldName",oldName);
							Log.e("newName",newName);
					        }
					        })
			     .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				        	
				        	setTitle("ȡ��");
					         //ȡ����ť�¼�
					        }
					        }).show();	
			 		
			break;
        }
        
        case FILE_PROPERTY:   
        	final String now= this.currentDirectory.getAbsolutePath()+"/"+this.directoryEntries.get(info.position).getText();
            File nowFile=new File(now);
            String nowSize;
           
            	
 	          if(nowFile.isDirectory()){
 	            	nowSize="�ļ���";
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
 	        .setTitle("����")
 	        .setMessage(now+"\n"+nowSize)
 	        .setIcon(R.drawable.alert_dialog_icon)
 	        .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
 	        public void onClick(DialogInterface dialog, int whichButton) {}}).show();
    		
        	break;
        	}
		return true;

    }
    
    //����б���Ŀ����Ӧ���ļ��еĻ����룬���ǵĻ�ѡ������
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
			// Refresh ˢ��
			Log.e("ѡ��Items", "ˢ��");
			this.EnterFolder(this.currentDirectory,sortBy);
		} else if(selectedFileString.equals(getString(R.string.up_one_level))){
			// ����һ��Ŀ¼
			Log.e("ѡ��Items", "�����ϼ�");
			this.upOneLevel();
		} else {

			File clickedFile = null;
					clickedFile = new File(this.currentDirectory.getAbsolutePath()
												+"/"+ this.directoryEntries.get(position).getText());
				
			if(clickedFile.getName().equals(".android_secure")){
				Toast.makeText(getApplicationContext(), "���ļ��оܾ�����",
					     Toast.LENGTH_SHORT).show();//����ļ��е�Ȩ��������ء������������롭��
	    	}
			else if(clickedFile.isDirectory())
			{
				Log.e("���ļ���",clickedFile.getName());
				this.EnterFolder(clickedFile,sortBy);
			}
			else
			{
				Log.e("���ļ�", clickedFile.getName());
				openFile(clickedFile);
			}
		
			
	}
	}
	
	//�õ���ǰĿ¼�ľ���·��
	public String GetCurDirectory(){	  
		  return this.currentDirectory.getAbsolutePath();
		}
	//�˵�������Ӧ
  @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	  menu.add(0,0,0,"�½��ļ���").setIcon(R.drawable.ic_menu_create);
		menu.add(0,1,0,"ճ��").setIcon(R.drawable.ic_menu_copy);
		menu.add(0,2,0,"����ʽ").setIcon(R.drawable.ic_menu_view_by);
		menu.add(0,3,0,"�˳�����").setIcon(R.drawable.ic_menu_go_to);
		return super.onCreateOptionsMenu(menu);
	}

  @Override
	public boolean onOptionsItemSelected(MenuItem item) {   
		  
		super.onOptionsItemSelected(item);
		
		switch(item.getItemId()){
		case 0://�½�
			//create_folder();
			LayoutInflater inflater = getLayoutInflater();
			   final View layout = inflater.inflate(R.layout.create_folder, null);

			   new AlertDialog.Builder(this).setTitle("�����ļ���").setView(layout)
			     .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
					        setResult(RESULT_OK);//ȷ����ť�¼�
					        
							FloderName = (EditText) layout.findViewById(R.id.folder_edit);
							String info = FloderName.getText().toString();
							System.out.println(info);
							

					        newFolder(info);
							
							
					        }
					        })
			     .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				        	
				        	setTitle("ȡ��");
					         //ȡ����ť�¼�
					        }
					        }).show();	
			 		
			break;

		case 1://ճ��
			if((copyyes==false)&&(cutyes==false))
			{
				Toast.makeText(getApplicationContext(),"û���ļ����ļ��н��й����ƻ�ճ��", Toast.LENGTH_SHORT).show();
			}
        	//��ʾProgressDialog
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
        	this.EnterFolder(this.currentDirectory,sortBy);//ˢ��
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
        		this.EnterFolder(this.currentDirectory,sortBy);//ˢ��
        		cutyes = false;
        		copyyes = false;
        		cutFolderok = false;
        	}
			}
        	break;
		case 2://�鿴ģʽ
			new AlertDialog.Builder(this).setTitle("����ʽ").setIcon(
				     android.R.drawable.ic_dialog_info).setSingleChoiceItems(
				     new String[] { "��ʱ������", "����С����", "����������","����������" }, 0,
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
				     }).setNegativeButton("����", null).show();
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
	

	//�½�һ���ļ��� 
	private void newFolder(String folderPath){
		Log.e("yangw","create new folder");
		 try{
		//	 File destDir = new File("/sdcard/test");//.createNewFile();
			 String filePath = folderPath;
			 File myFilePath = new File(this.currentDirectory.getAbsolutePath()+"/"+filePath); 
			 System.out.println("�½��ļ�������"+this.currentDirectory.getAbsolutePath()+"/"+filePath);
			 
			 if (!myFilePath.exists()) { 
			        myFilePath.mkdir(); 

		  }	else{   
			  
			  new AlertDialog.Builder(this) 
		    .setTitle("����ʧ��") 
		    .setMessage("�ļ��Ѿ�����")
		    .setPositiveButton("ȷ��",null)
		    .show();	  
		  }
		  
		  this.EnterFolder(this.currentDirectory,sortBy);//ˢ��
		  
		 }catch(Exception e){
			 System.out.println("�½��ļ��в�������");	 
			 return;
		 }
	}
	
	/*
	 * ɾ���ļ����ļ���
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
	    	   this.EnterFolder(this.currentDirectory,sortBy);//ˢ��
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
	           this.EnterFolder(this.currentDirectory,sortBy);//ˢ��
	       }   
	   }  
	   
  /** 
  * ���Ƶ����ļ� 
  * @param oldPath String ԭ�ļ�·�� �磺c:/fqf.txt 
  * @param newPath String ���ƺ�·�� �磺f:/fqf.txt 
  * @return boolean 
  */ 
	   public static void copyFile(String oldPath, String newPath) { 
		   try { 
		          int bytesum = 0; 
		          int byteread = 0; 
		          File oldfile = new File(oldPath); 
		          if (oldfile.exists()) { //�ļ�����ʱ 
		        	  InputStream inStream = new FileInputStream(oldPath); //����ԭ�ļ� 
		              FileOutputStream fs = new FileOutputStream(newPath); 
		              byte[] buffer = new byte[1444]; 
		              
		              while ( (byteread = inStream.read(buffer)) != -1) { 
		                  bytesum += byteread; //�ֽ��� �ļ���С 
		                  System.out.println(bytesum); 
		                  fs.write(buffer, 0, byteread); 
		              } 
		              inStream.close(); 
		          }
		   } 
		      catch (Exception e) { 
		          System.out.println("���Ƶ����ļ���������"); 
		          e.printStackTrace(); 
	   }
	   
	   } 
   
	   
  /** 
    * ���������ļ������� 
    * @param oldPath String ԭ�ļ�·�� �磺c:/fqf 
    * @param newPath String ���ƺ�·�� �磺f:/fqf/ff 
    * @return boolean 
    * mkdir() ֻ�����Ѿ����ڵ�Ŀ¼�д��������ļ��С�
    * mkdirs() �����ڲ����ڵ�Ŀ¼�д����ļ��С����磺a\\b,�ȿ��Դ����༶Ŀ¼�� 
    */ 
  public static void copyFolder(String oldPath, String newPath) { 

      try { 
          (new File(newPath)).mkdirs(); //����ļ��в����� �������ļ���   
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
              if(temp.isDirectory()){//��������ļ��� 
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
                  output.flush(); //��ջ���������
                  output.close(); 
                  input.close(); 
              }               
          } 
          
      }
      catch (Exception e) { 
          System.out.println("���������ļ������ݲ�������"); 
          e.printStackTrace(); 

      } 
      

  } 

  //�����ļ�
  public void moveFile(String oldPath, String newPath) { 
      copyFile(oldPath, newPath); 
      delDir(oldPath); 

  } 
  
  /** 
    * �ƶ��ļ���ָ��Ŀ¼ 
    */ 
  public void moveFolder(String oldPath, String newPath) { 
      copyFolder(oldPath, newPath); 
      delDir(oldPath); 

  } 

  /** *//**�ļ�������  
   * @param path �ļ�Ŀ¼  
   * @param oldname  ԭ�����ļ���  
   * @param newname ���ļ���  
   */  
  public void renameFile(String path,String oldname,String newname)
  {   
		Log.e("renameFile filepath",path);
		Log.e("renameFile oldName",oldname);
		Log.e("renameFile newName",newname);
      if(!oldname.equals(newname))
      {//�µ��ļ�������ǰ�ļ�����ͬʱ,���б�Ҫ����������   
          File oldfile=new File(path+"/"+oldname);   
          File newfile=new File(path+"/"+newname);   
          if(newfile.exists())//���ڸ�Ŀ¼���Ѿ���һ���ļ������ļ�����ͬ��������������   
              System.out.println(newname+"�Ѿ����ڣ�");   
          else{   
              oldfile.renameTo(newfile);   
              Log.e("oldfile.renameTo(newfile);","success");
              this.EnterFolder(this.currentDirectory,sortBy);//ˢ��
          }    
      }            
  }  

  		
}
	


