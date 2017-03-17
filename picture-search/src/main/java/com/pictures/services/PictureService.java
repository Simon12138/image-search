package com.pictures.services;

import java.util.List;

import com.pictures.models.Picture;
import com.pictures.models.Tag;
import com.pictures.type.DatedPicture;

public interface PictureService {
	Picture create(Picture picture);
	void attachTag(Picture picture, Tag tag);
	Picture findPictureByName(String name);
	Picture findPictureById(Long id);
	List<Picture> findAll();
	List<Picture> searchPictureByKey(String key);
	List<DatedPicture> buildDatedPictures(List<Picture> pictures);
}
