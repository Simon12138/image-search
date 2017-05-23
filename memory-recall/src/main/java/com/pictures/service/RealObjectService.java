package com.pictures.service;

import java.util.List;

import com.pictures.entity.RealObject;

public interface RealObjectService {
	public RealObject create(RealObject object);
	
	public List<RealObject> list();
	
	public List<RealObject> listAll();
	
	public RealObject findByName(String name);
	
	public RealObject findById(Long id);
	
	public RealObject update(RealObject object);
	
	public List<RealObject> listObjects();
	
	public String searchObjectImage(String objectName);
}
