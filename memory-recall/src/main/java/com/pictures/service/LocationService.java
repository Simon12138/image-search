package com.pictures.service;

import java.util.List;

import com.pictures.entity.Location;

public interface LocationService {
	public Location create(Location location);
	
	public List<Location> list();
	
	public Location findByDescription(String desc);
	
	public Location findById(Long id);
}
