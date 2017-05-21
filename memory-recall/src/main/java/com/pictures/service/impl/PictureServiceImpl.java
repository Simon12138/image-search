package com.pictures.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.pictures.controllers.dto.QueryParam;
import com.pictures.entity.Avatar;
import com.pictures.entity.Picture;
import com.pictures.projectoxford.face.FaceServiceClient;
import com.pictures.projectoxford.face.contract.SimilarPersistedFace;
import com.pictures.projectoxford.face.rest.ClientException;
import com.pictures.repository.PictureRepository;
import com.pictures.service.AvatarService;
import com.pictures.service.LocationService;
import com.pictures.service.PictureService;
import com.pictures.service.RealObjectService;
import com.pictures.utils.ImageUtils;
import com.pictures.utils.SystemDataSet;


@Service
@Primary
public class PictureServiceImpl implements PictureService {
	
	private static final Logger logger = LoggerFactory.getLogger(PictureServiceImpl.class);
	
	@Autowired
	private PictureRepository pictureRepo;
	
	@Autowired
	private AvatarService avatarService;
	
	@Autowired
	private RealObjectService objectService;
	
	@Autowired
	private LocationService locationService;
	
	@Autowired
	private FaceServiceClient faceClient;
	
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
		Double startHour = param.getStartHour() == null ? SystemDataSet.DEFAULT_START_HOUR : param.getStartHour();
		Double endHour = param.getEndHour() == null ? SystemDataSet.DEFAULT_END_HOUR : param.getEndHour();
		List<String> faceUUIDs = new ArrayList<>();
		if(param.getFaceId() != null) {
			Avatar avatar = avatarService.findById(param.getFaceId());
			try {
				byte [] avatarImage = ImageUtils.loadImage(SystemDataSet.AVATAR_SAVED_LOCATION + avatar.getName());
				UUID faceId = faceClient.detect(avatarImage, true, false, null)[0].faceId;
				SimilarPersistedFace[] similarPersistedFaces = faceClient.findSimilar(faceId, SystemDataSet.FACE_LIST_ID, 1000);
				for(SimilarPersistedFace face : similarPersistedFaces) {
					if(Double.compare(face.confidence, SystemDataSet.SIMILAR_FACE_CONFIDENCE_QUERY_PROCESS) > 0)
						faceUUIDs.add(face.persistedFaceId.toString());
				}
			} catch (ClientException e) {
				logger.warn("Find similar faces failed: {0}", e);
			} catch (IOException e) {
				logger.warn("Find similar faces failed: {0}", e);
			}
		}
		String objectName = param.getObjectId() == null ? null : objectService.findById(param.getObjectId()).getName();
		String location = param.getLocationId() == null ? null : locationService.findById(param.getLocationId()).getDescription();
		
		Query query = em.createQuery("select distinct p from Picture p left join p.faces pf left join p.objects po where "
				+ "(:faceId is null or pf.uuid in (:faceUUIDs)) and "
				+ "(:objectName is null or po.name=:objectName) and "
				+ "(:location is null or p.location=:location) and "
				+ "(p.creationHour>=:startHour and p.creationHour<:endHour)");
		query.setParameter("faceId", param.getFaceId());
		if(faceUUIDs.isEmpty()) {
			query.setParameter("faceUUIDs", "''");
		} else {
			query.setParameter("faceUUIDs", faceUUIDs);
		}
		query.setParameter("objectName", objectName);
		query.setParameter("location", location);
		query.setParameter("startHour", startHour);
		query.setParameter("endHour", endHour);
		List<Picture> pictures = query.getResultList();
		return pictures;
	}

}
