package com.pictures.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.pictures.controllers.dto.QueryParam;
import com.pictures.entity.Avatar;
import com.pictures.entity.Picture;
import com.pictures.repository.PictureRepository;
import com.pictures.service.AvatarService;
import com.pictures.service.LocationService;
import com.pictures.service.PictureService;
import com.pictures.service.RealObjectService;


@Service
@Primary
public class PictureServiceImpl implements PictureService {
	
	@Autowired
	private PictureRepository pictureRepo;
	
	@Autowired
	private AvatarService avatarService;
	
	@Autowired
	private RealObjectService objectService;
	
	@Autowired
	private LocationService locationService;
	
	@Autowired
	private EntityManager em;
	
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

	@SuppressWarnings("unchecked")
	@Override
	public List<Picture> queryPicture(QueryParam param) {
		String faceUUID = param.getFaceId() == null ? null : avatarService.findById(param.getFaceId()).getUuid();
		String objectName = param.getObjectId() == null ? null : objectService.findById(param.getObjectId()).getName();
		String location = param.getLocationId() == null ? null : locationService.findById(param.getLocationId()).getDescription();
		
		Query query = em.createQuery("select distinct p from Picture p left join p.faces pf left join p.objects po where "
				+ "(:faceUUID is null or pf.uuid=:faceUUID) and "
				+ "(:objectName is null or po.name=:objectName) and "
				+ "(:location is null or p.location=:location)");
		query.setParameter("faceUUID", faceUUID);
		query.setParameter("objectName", objectName);
		query.setParameter("location", location);
		List<Picture> pictures = query.getResultList();
		return pictures;
	}

}
