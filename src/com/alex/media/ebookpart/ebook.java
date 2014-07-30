package com.alex.media.ebookpart;

import com.alex.media.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;



public class ebook extends TabActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ebook);
		
		TabHost tabHost=getTabHost();
		Intent localIntent=new Intent();
		localIntent.setClass(ebook.this,localList.class);
		TabHost.TabSpec localSpec=tabHost.newTabSpec("local");
		localSpec.setIndicator("±æµÿ Èø‚", getResources().getDrawable(android.R.drawable.btn_star));
		localSpec.setContent(localIntent);
		System.out.println("before add tab ");
		tabHost.addTab(localSpec);
	}
	
}
