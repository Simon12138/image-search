package com.pictures.controllers.dto;

import java.util.List;

/*
 * This parameter type is using for query images:
 * faceId: Avatar image id
 * objectId:Object image id
 * locationId: Location image id
 */

public class QueryParam {
	
	private List<Long> faceIds;
	
	private List<Long> objectIds;
	
	private List<Long> locationIds;
	
	private List<Integer> times;

	public List<Long> getFaceIds() {
		return faceIds;
	}

	public void setFaceIds(List<Long> faceIds) {
		this.faceIds = faceIds;
	}

	public List<Long> getObjectIds() {
		return objectIds;
	}

	public void setObjectIds(List<Long> objectIds) {
		this.objectIds = objectIds;
	}

	public List<Long> getLocationIds() {
		return locationIds;
	}

	public void setLocationIds(List<Long> locationIds) {
		this.locationIds = locationIds;
	}

	public List<Integer> getTimes() {
		return times;
	}

	public void setTimes(List<Integer> times) {
		this.times = times;
	}  
}
