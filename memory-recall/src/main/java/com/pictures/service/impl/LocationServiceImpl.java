package com.pictures.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

import org.springframework.stereotype.Service;

import com.pictures.entity.Location;
import com.pictures.repository.LocationRepository;
import com.pictures.service.LocationService;

@Service
@Primary
public class LocationServiceImpl implements LocationService {
	
	@Autowired
	private LocationRepository locationRepo;

	@Override
	public Location create(Location location) {
		return locationRepo.save(location);
	}

	@Override
	public List<Location> list() {
		return locationRepo.findAll();
	}

	@Override
	public Location findByDescription(String desc) {
		return locationRepo.findByDescription(desc);
	}

	@Override
	public Location findById(Long id) {
		return locationRepo.findById(id);
	}

}
