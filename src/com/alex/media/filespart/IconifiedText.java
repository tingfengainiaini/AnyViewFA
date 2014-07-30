
package com.alex.media.filespart;
import java.sql.Date;

import android.graphics.drawable.Drawable;

//Comparable�ӿڵ�����һ��Collection(���ϣ����ǿ�������    ����Ĺ�����󷽷�compareTo(Object o) ����������
public class IconifiedText implements Comparable<IconifiedText>{
    
	private String mText = "";
	private Drawable mIcon;
	private Date mdate;

	private boolean mSelectable = true;

	public IconifiedText(String text, Drawable bullet, Date date) {
		mIcon = bullet;
		mText = text;
		mdate = date;
	}
	
	public boolean isSelectable() {
		return mSelectable;
	}
	
	public void setSelectable(boolean selectable) {
		mSelectable = selectable;
	}
	
	public String getText() {
		return mText;
	}
	
	public void setText(String text) {
		mText = text;
	}
	
	public void setIcon(Drawable icon) {
		mIcon = icon;
	}
	
	public Drawable getIcon() {
		return mIcon;
	}
	
	public void setDate(Date date){
		mdate = date;
	}

	public Date getDate(){
		return mdate;
	}
	

	
	public int compareTo(IconifiedText other) {
		if(this.mText != null)
			return this.mText.compareTo(other.getText()); 
		else 
			throw new IllegalArgumentException();
	}
}
