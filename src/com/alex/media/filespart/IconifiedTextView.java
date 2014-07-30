package com.alex.media.filespart;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IconifiedTextView extends LinearLayout {
	
	private TextView mText;
	private ImageView mIcon;
	
	public IconifiedTextView(Context context, IconifiedText aIconifiedText) {
		super(context);

		/* First Icon and the Text to the right (horizontal),
		 * not above and below (vertical) */
		//设置页面方向  水平
		this.setOrientation(HORIZONTAL);

		mIcon = new ImageView(context);
		mIcon.setImageDrawable(aIconifiedText.getIcon());
		
		mIcon.setMaxHeight(40);
		// left, top, right, bottom
		mIcon.setPadding(0, 0, 5, 0); // 5px to the right
		
		/* At first, add the Icon to ourself
		 * (! we are extending LinearLayout) */
		addView(mIcon,  new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		mText = new TextView(context);
		mText.setText(aIconifiedText.getText());
		
		mText.setTextSize(20);//设置字体大小
		/* Now the text (after the icon) */
		addView(mText, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}

	public void setText(String words) {
		mText.setText(words);
	}
	
	public void setIcon(Drawable bullet) {
		mIcon.setImageDrawable(bullet);
	}
}