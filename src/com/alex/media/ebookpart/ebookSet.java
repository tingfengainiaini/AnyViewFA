package com.alex.media.ebookpart;


import com.alex.media.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ebookSet extends ListActivity{
	private final String[] array={
		"字体大小","字体颜色","字体间距","亮度调节"	
	};
	private SeekBar seekBar=null; 
	private String textSize="NoChange";
	private String textColor="NoChange";
	private String textScale="NoChange";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println(" set oncreate");
		setContentView(R.layout.ebookset);
		System.out.println(" set setcontentview");
		setList();
		System.out.println(" set setlist");
		updateToggles();
	} 
	//将列表加入数据
	void setList(){
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(ebookSet.this,R.layout.ebookset_list,array);
		ebookSet.this.getListView().setAdapter(adapter);
	}
	private void updateToggles() {
		// TODO Auto-generated method stub
		seekBar = (SeekBar) findViewById(R.id.MySeekBar);
		seekBar.setProgress((int) (android.provider.Settings.System.getInt(
				getContentResolver(),
				android.provider.Settings.System.SCREEN_BRIGHTNESS, 255) ));
		System.out.println("updateTooggles getProgress-->"+seekBar.getProgress());
		seekBar.setOnSeekBarChangeListener(seekListener);
		System.out.println("seekBar.setOnSeekBarChangeListener");
	}
	
	private OnSeekBarChangeListener seekListener = new OnSeekBarChangeListener() {

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (fromUser) {
				Integer tmpInt = seekBar.getProgress();
				System.out.println("from user before "+seekBar.getProgress());
				// 51 (seek scale) * 5 = 255 (max brightness)
				// Old way
				android.provider.Settings.System.putInt(getContentResolver(),
						android.provider.Settings.System.SCREEN_BRIGHTNESS,
						tmpInt); // 0-255
				tmpInt = Settings.System.getInt(getContentResolver(),
						Settings.System.SCREEN_BRIGHTNESS, -1);
				System.out.println("after putInt"+tmpInt);
				// Cupcake way..... sucks
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				// lp.screenBrightness = 1.0f;
				Float tmpFloat = (float)tmpInt / 255;
				System.out.println("float-->"+tmpFloat);
				if (0<= tmpInt && tmpInt <= 255) {
					lp.screenBrightness = tmpFloat;
					System.out.println("lp.screenBrightness-->"+lp.screenBrightness);
				}
				getWindow().setAttributes(lp);
				System.out.println("lp.screenBrightness setA");
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
		};
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		switch(position){
		case 0:
			new AlertDialog.Builder(ebookSet.this).setTitle("字体大小").setItems(R.array.item_textsize_dialog,
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String[] arrayClick=getResources().getStringArray(R.array.item_textsize_dialog);
							Toast.makeText(getApplicationContext(), arrayClick[which],
								     Toast.LENGTH_SHORT).show();
							textSize=arrayClick[which];
						}
					}).show();
			break;
		case 1:
			new AlertDialog.Builder(ebookSet.this).setTitle("字体颜色").setItems(R.array.item_textcolor_dialog,
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String[] arrayClick2=getResources().getStringArray(R.array.item_textcolor_dialog);
							Toast.makeText(getApplicationContext(), arrayClick2[which],
								     Toast.LENGTH_SHORT).show();
							textColor=arrayClick2[which];
						}
					}).show();
			break;
		case 2://字体间距
			new AlertDialog.Builder(ebookSet.this).setTitle("字体间距").setItems(R.array.item_textscale_dialog,
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String[] arrayClick3=getResources().getStringArray(R.array.item_textscale_dialog);
							Toast.makeText(getApplicationContext(), arrayClick3[which],
								     Toast.LENGTH_SHORT).show();
							textScale=arrayClick3[which];
						}
					}).show();
			break;
		case 3:
			break;
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		  if (keyCode == event.KEYCODE_BACK) {
			  Intent i = new Intent();   
	           
		        Bundle b = new Bundle();   
		        b.putString("TEXTSIZE", textSize);   
		        b.putString("TEXTCOLOR",textColor);
		        b.putString("TEXTSCALE",textScale);
		        i.putExtras(b);   
		        this.setResult(RESULT_OK, i);     
				finish();
			}
		return super.onKeyDown(keyCode, event);
	}
	
	
		
}
