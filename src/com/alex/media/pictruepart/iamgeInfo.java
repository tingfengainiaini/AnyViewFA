package com.alex.media.pictruepart;

import java.io.Serializable;

public class iamgeInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private String imageid;
	private String iamgeName;
	private String imageSize;
	private String iamgePath;
	
	public String getImageid() {
		return imageid;
	}
	public void setImageid(String imageid) {
		this.imageid = imageid;
	}
	public String getIamgeName() {
		return iamgeName;
	}
	public void setIamgeName(String iamgeName) {
		this.iamgeName = iamgeName;
	}
	public String getImageSize() {
		return imageSize;
	}
	public void setImageSize(String imageSize) {
		this.imageSize = imageSize;
	}
	public String getIamgePath() {
		return iamgePath;
	}
	public void setIamgePath(String iamgePath) {
		this.iamgePath = iamgePath;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public String toString() {
		return "iamgeInfo [iamgeName=" + iamgeName + ", iamgePath=" + iamgePath
				+ ", imageSize=" + imageSize + ", imageid=" + imageid + "]";
	}
	public iamgeInfo(String imageid, String iamgeName, String imageSize,
			String iamgePath) {
		super();
		this.imageid = imageid;
		this.iamgeName = iamgeName;
		this.imageSize = imageSize;
		this.iamgePath = iamgePath;
	}
	public iamgeInfo() {
		super();
	}
	
	
}
