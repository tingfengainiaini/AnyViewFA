package com.alex.media;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class helpActivity extends Activity {
    /** Called when the activity is first created. */
	private TextView helpText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        helpText = (TextView)findViewById(R.id.helpText);
        helpText.setText(get(R.string.help1)+"\n"+"\n"+get(R.string.help2)+"\n"+"\n"+get(R.string.help3)+"\n"+"\n"+get(R.string.help4));
    }
    public String get(int id){
    	return getResources().getString(id);
    }
}