package com.pictures.type;

import java.util.Date;
import java.util.List;

import com.pictures.models.Picture;

public class DatedPicture {
	private Date groupTime;
	
	private List<Picture> pictures;
	
	public DatedPicture(Date groupTime, List<Picture> pictures) {
		this.groupTime = groupTime;
		this.pictures = pictures;
	}

	public Date getGroupTime() {
		return groupTime;
	}

	public void setGroupTime(Date groupTime) {
		this.groupTime = groupTime;
	}

	public List<Picture> getPictures() {
		return pictures;
	}

	public void setPictures(List<Picture> pictures) {
		this.pictures = pictures;
	}
	
	
}
