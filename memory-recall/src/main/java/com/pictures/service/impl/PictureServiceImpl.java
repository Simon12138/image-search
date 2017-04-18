package com.pictures.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.pictures.entity.Picture;
import com.pictures.repository.PictureRepository;
import com.pictures.service.LocationService;
import com.pictures.service.PictureService;


@Service
@Primary
public class PictureServiceImpl implements PictureService {
	
	@Autowired
	private PictureRepository pictureRepo;
	
	@Override
	public Picture create(Picture picture) {
		return pictureRepo.save(picture);
	}

	@Override
	public List<Picture> list() {
		return pictureRepo.findAll();
	}

	@Override
	public Picture findByName(String name) {
		return pictureRepo.findByName(name);
	}

	@Override
	public Picture findById(Long id) {
		return pictureRepo.findById(id);
	}

	@Override
	public Picture update(Picture picture) {
		return pictureRepo.saveAndFlush(picture);
	}
	
	public List<Picture> getPicturesByFaces(List<String> faceIds) {
		return pictureRepo.getPicturesByFaces(faceIds);
	}

	@Override
	public List<Picture> getPicturesByLocation(String locations) {
		return pictureRepo.getPicturesByLocation(locations);
	}

	@Override
	public List<Picture> findByIds(List<Long> ids) {
		return pictureRepo.findByIds(ids);
	}

	@Override
	public List<Picture> getPicturesByObject(String object) {
		return pictureRepo.getPicturesByObject(object);
	}

}
