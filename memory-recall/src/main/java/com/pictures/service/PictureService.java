package com.pictures.service;

import java.util.List;

import com.pictures.entity.Picture;

public interface PictureService {
	
	public Picture create(Picture picture);
	
	public List<Picture> list();
	
	public Picture findByName(String name);
	
	public Picture findById(Long id);
	
	public List<Picture> findByIds(List<Long> ids);
	
	public Picture update(Picture picture);
	
	public List<Picture> getPicturesByFaces(List<String> faceIds);
	
	public List<Picture> getPicturesByLocation(String locations);
	
	public List<Picture> getPicturesByObject(String object);
	
}
