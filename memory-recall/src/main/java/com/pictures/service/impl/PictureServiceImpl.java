package com.pictures.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
import com.pictures.entity.PictureObject;
import com.pictures.entity.RealObject;
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
	
	@SuppressWarnings("unchecked")
	public List<Picture> getPicturesByFaces(List<String> faceIds) {
		Query query = em.createQuery("select distinct p from Picture p join p.faces pf where pf.uuid in :faceIds order by p.creationTime");
		if(faceIds.isEmpty()) {
			query.setParameter("faceIds", "''");
		} else {
			query.setParameter("faceIds", faceIds);
		}
		return query.getResultList();
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
	public List<Picture> queryPictureMoreOneCue(QueryParam param) throws ClientException, IOException {
//		Double startHour = param.getStartHour() == null ? SystemDataSet.DEFAULT_START_HOUR : param.getStartHour();
//		Double endHour = param.getEndHour() == null ? SystemDataSet.DEFAULT_END_HOUR : param.getEndHour();
//		List<String> faceUUIDs = new ArrayList<>();
//		if(param.getFaceId() != null) {
//			Avatar avatar = avatarService.findById(param.getFaceId());
//			try {
//				byte [] avatarImage = ImageUtils.loadImage(SystemDataSet.AVATAR_SAVED_LOCATION + avatar.getName());
//				UUID faceId = faceClient.detect(avatarImage, true, false, null)[0].faceId;
//				SimilarPersistedFace[] similarPersistedFaces = faceClient.findSimilar(faceId, SystemDataSet.FACE_LIST_ID, 1000);
//				for(SimilarPersistedFace face : similarPersistedFaces) {
//					if(Double.compare(face.confidence, SystemDataSet.SIMILAR_FACE_CONFIDENCE_QUERY_PROCESS) > 0)
//						faceUUIDs.add(face.persistedFaceId.toString());
//				}
//			} catch (ClientException e) {
//				logger.warn("Find similar faces failed: {0}", e);
//			} catch (IOException e) {
//				logger.warn("Find similar faces failed: {0}", e);
//			}
//		}
//		String objectName = param.getObjectId() == null ? null : objectService.findById(param.getObjectId()).getName();
//		String location = param.getLocationId() == null ? null : locationService.findById(param.getLocationId()).getDescription();
//		
//		Query query = em.createQuery("select distinct p from Picture p left join p.faces pf left join p.objects po where "
//				+ "(:faceId is null or pf.uuid in (:faceUUIDs)) and "
//				+ "(:objectName is null or po.name=:objectName) and "
//				+ "(:location is null or p.location=:location) and "
//				+ "(p.creationHour>=:startHour and p.creationHour<:endHour)");
//		query.setParameter("faceId", param.getFaceId());
//		if(faceUUIDs.isEmpty()) {
//			query.setParameter("faceUUIDs", "''");
//		} else {
//			query.setParameter("faceUUIDs", faceUUIDs);
//		}
//		query.setParameter("objectName", objectName);
//		query.setParameter("location", location);
//		query.setParameter("startHour", startHour);
//		query.setParameter("endHour", endHour);
		List<Picture> pictures = new ArrayList<>();
		
		StringBuffer sqlBuffer = new StringBuffer("select distinct p from Picture p left join p.faces pf left join p.objects po");
		if((param.getTimes() != null && !param.getTimes().isEmpty()) || 
				(param.getLocationIds() != null && !param.getLocationIds().isEmpty()) ||
				(param.getFaceIds() != null && !param.getFaceIds().isEmpty())) {
			sqlBuffer.append(" where ");
		}
		// append time
		if(param.getTimes() != null && !param.getTimes().isEmpty()) {
			sqlBuffer.append("(");
			Iterator<Integer> itTime = param.getTimes().iterator();
			while(itTime.hasNext()) {
				Integer time = itTime.next();
				Double startTime = time - 0.5;
				Double endTime = time + 0.5;
				sqlBuffer.append("(p.creationHour>=" + startTime + " and p.creationHour<" + endTime + ")");
				if(itTime.hasNext()) {
					sqlBuffer.append(" or ");
				}
			}
			sqlBuffer.append(")");
		}
		
		
		// append object
		List<String> objects = new ArrayList<>();
		if(param.getObjectIds() != null && !param.getObjectIds().isEmpty()) {
			Iterator<Long> itObject = param.getObjectIds().iterator();
			while(itObject.hasNext()) {
				Long objectId = itObject.next();
				String objectName = objectService.findById(objectId).getName();
				objects.add(objectName);
			}
		}
		
		// append location
		if(param.getLocationIds() != null && !param.getLocationIds().isEmpty()) {
			if(sqlBuffer.indexOf("creationHour") != -1) {
				sqlBuffer.append(" and ");
			}
			sqlBuffer.append("(");
			Iterator<Long> itLocation = param.getLocationIds().iterator();
			while(itLocation.hasNext()) {
				Long locationId = itLocation.next();
				String location = locationService.findById(locationId).getDescription();
				sqlBuffer.append("(p.location='" + location + "')");
				if(itLocation.hasNext()) {
					sqlBuffer.append(" or ");
				}
			}
			sqlBuffer.append(")");
		}
		
		// append face
		if(param.getFaceIds() != null && !param.getFaceIds().isEmpty()) {
			if(sqlBuffer.indexOf("creationHour") != -1 || sqlBuffer.indexOf("p.location") != -1) {
				sqlBuffer.append(" and ");
			}
			sqlBuffer.append("(");
			Iterator<Long> itFace = param.getFaceIds().iterator();
			while(itFace.hasNext()) {
				StringBuffer faceUUIDs = new StringBuffer("(");
				Long faceId = itFace.next();
				Avatar avatar = avatarService.findById(faceId);
				byte [] avatarImage = ImageUtils.loadImage(SystemDataSet.AVATAR_SAVED_LOCATION + avatar.getName());
				UUID faceUUID = faceClient.detect(avatarImage, true, false, null)[0].faceId;
				SimilarPersistedFace[] similarPersistedFaces = faceClient.findSimilar(faceUUID, SystemDataSet.FACE_LIST_ID, 1000);
				for(SimilarPersistedFace face : similarPersistedFaces) {
					if(Double.compare(face.confidence, SystemDataSet.SIMILAR_FACE_CONFIDENCE_QUERY_PROCESS) > 0)
						faceUUIDs.append("'" + face.persistedFaceId.toString() + "',");
				}
				faceUUIDs.deleteCharAt(faceUUIDs.length() - 1);
				faceUUIDs.append(")");
				if(faceUUIDs.length() > 1) {
					sqlBuffer.append("(pf.uuid in " + faceUUIDs.toString() + ")");
				} else {
					sqlBuffer.append("(1=2)");
				}
				if(itFace.hasNext()) {
					sqlBuffer.append(" or ");
				}
			}
			sqlBuffer.append(")");
		}
		Query query = em.createQuery(sqlBuffer.toString());
		pictures = query.getResultList();
		
		Iterator<Picture> itPicture = pictures.iterator();
		while(itPicture.hasNext()) {
			Picture picture = itPicture.next();
			if(!containsObjects(picture, objects)) {
				itPicture.remove();
			}
		}
		return pictures;
	}
	
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<Picture> queryPictureMoreOneCue(QueryParam param) throws ClientException, IOException {
//		Double startHour = param.getStartHour() == null ? SystemDataSet.DEFAULT_START_HOUR : param.getStartHour();
//		Double endHour = param.getEndHour() == null ? SystemDataSet.DEFAULT_END_HOUR : param.getEndHour();
//		List<String> faceUUIDs = new ArrayList<>();
//		if(param.getFaceId() != null) {
//			Avatar avatar = avatarService.findById(param.getFaceId());
//			try {
//				byte [] avatarImage = ImageUtils.loadImage(SystemDataSet.AVATAR_SAVED_LOCATION + avatar.getName());
//				UUID faceId = faceClient.detect(avatarImage, true, false, null)[0].faceId;
//				SimilarPersistedFace[] similarPersistedFaces = faceClient.findSimilar(faceId, SystemDataSet.FACE_LIST_ID, 1000);
//				for(SimilarPersistedFace face : similarPersistedFaces) {
//					if(Double.compare(face.confidence, SystemDataSet.SIMILAR_FACE_CONFIDENCE_QUERY_PROCESS) > 0)
//						faceUUIDs.add(face.persistedFaceId.toString());
//				}
//			} catch (ClientException e) {
//				logger.warn("Find similar faces failed: {0}", e);
//			} catch (IOException e) {
//				logger.warn("Find similar faces failed: {0}", e);
//			}
//		}
//		String objectName = param.getObjectId() == null ? null : objectService.findById(param.getObjectId()).getName();
//		String location = param.getLocationId() == null ? null : locationService.findById(param.getLocationId()).getDescription();
//		
//		Query query = em.createQuery("select distinct p from Picture p left join p.faces pf left join p.objects po where "
//				+ "(:faceId is null or pf.uuid in (:faceUUIDs)) and "
//				+ "(:objectName is null or po.name=:objectName) and "
//				+ "(:location is null or p.location=:location) and "
//				+ "(p.creationHour>=:startHour and p.creationHour<:endHour)");
//		query.setParameter("faceId", param.getFaceId());
//		if(faceUUIDs.isEmpty()) {
//			query.setParameter("faceUUIDs", "''");
//		} else {
//			query.setParameter("faceUUIDs", faceUUIDs);
//		}
//		query.setParameter("objectName", objectName);
//		query.setParameter("location", location);
//		query.setParameter("startHour", startHour);
//		query.setParameter("endHour", endHour);
//		List<Picture> pictures = query.getResultList();
//		return pictures;
//		
//	}
	
	private boolean containsObjects(Picture picture, List<String> objects) {
		int count = 0;
		List<PictureObject> objectsInPicture = picture.getObjects();
		for(String object : objects) {
			boolean exist = false;
			for(PictureObject objectInPicture : objectsInPicture) {
				if(object.equals(objectInPicture.getName())) {
					exist = true;
				}
			}
			if(exist) {
				count++;
			}
		}
		return count == objects.size();
	}

}
