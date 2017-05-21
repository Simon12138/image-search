package com.pictures.controllers.dto;

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
	
	private Double startHour;
	
	private Double endHour;

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

	public Double getStartHour() {
		return startHour;
	}

	public void setStartHour(Double startHour) {
		this.startHour = startHour;
	}

	public Double getEndHour() {
		return endHour;
	}

	public void setEndHour(Double endHour) {
		this.endHour = endHour;
	}
	
}
