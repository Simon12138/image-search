package com.pictures.service;

import java.io.IOException;
import java.util.List;

import com.pictures.controllers.dto.QueryParam;
import com.pictures.entity.Picture;
import com.pictures.projectoxford.face.rest.ClientException;

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
	
	public List<Picture> queryPictureMoreOneCue(QueryParam param) throws ClientException, IOException;
	
//	public List<Picture> queryPicture(QueryParam param);
}
