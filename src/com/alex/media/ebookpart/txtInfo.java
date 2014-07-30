package com.alex.media.ebookpart;

import java.io.Serializable;

public class txtInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id;
	private String txtName;
	private String txtSize;
	private String txtPath;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTxtName() {
		return txtName;
	}
	public void setTxtName(String txtName) {
		this.txtName = txtName;
	}
	public String getTxtSize() {
		return txtSize;
	}
	public void setTxtSize(String txtSize) {
		this.txtSize = txtSize;
	}
	public String getTxtPath() {
		return txtPath;
	}
	public void setTxtPath(String txtPath) {
		this.txtPath = txtPath;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public txtInfo(String id, String txtName, String txtSize, String txtPath) {
		super();
		this.id = id;
		this.txtName = txtName;
		this.txtSize = txtSize;
		this.txtPath = txtPath;
	}
	
	public txtInfo() {
		super();
	}
	
	@Override
	public String toString() {
		return "txtInfo [id=" + id + ", txtName=" + txtName + ", txtPath="
				+ txtPath + ", txtSize=" + txtSize + "]";
	}
	
	
	
	
}
