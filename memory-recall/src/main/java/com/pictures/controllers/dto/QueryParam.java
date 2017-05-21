package com.pictures.controllers.dto;

import java.util.Date;

/*
 * This parameter type is using for query images:
 * faceId: Avatar image id
 * objectId:Object image id
 * locationId: Location image id
 */

public class QueryParam {
	
	private Long faceId;
	
	private Long objectId;
	
	private Long locationId;
	
	private Integer startHour;
	
	private Integer endHour;

	public Long getFaceId() {
		return faceId;
	}

	public void setFaceId(Long faceId) {
		this.faceId = faceId;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public Integer getStartHour() {
		return startHour;
	}

	public void setStartHour(Integer startHour) {
		this.startHour = startHour;
	}

	public Integer getEndHour() {
		return endHour;
	}

	public void setEndHour(Integer endHour) {
		this.endHour = endHour;
	}
	
}
