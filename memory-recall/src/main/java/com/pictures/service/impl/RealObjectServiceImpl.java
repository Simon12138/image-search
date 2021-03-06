package com.pictures.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.pictures.entity.RealObject;
import com.pictures.projectoxford.bing.BingImageSearchClient;
import com.pictures.projectoxford.bing.BingImageSearchException;
import com.pictures.projectoxford.bing.model.BingImageSearchRequest;
import com.pictures.projectoxford.bing.model.BingImageSearchResponse;
import com.pictures.repository.RealObjectRepository;
import com.pictures.service.RealObjectService;
import com.pictures.utils.SystemDataSet;

@Service
@Primary
public class RealObjectServiceImpl implements RealObjectService {

	@Autowired
	private RealObjectRepository objectRepo;
	
	@Autowired
	private BingImageSearchClient searchClient;
	
	@Override
	public RealObject create(RealObject object) {
		return objectRepo.save(object);
	}

	@Override
	public List<RealObject> list() {
		return objectRepo.listUsingFilter(SystemDataSet.REAL_OBJECT_CONFIDENCE_TO_SHOW);
	}

	@Override
	public RealObject findByName(String name) {
		return objectRepo.findByName(name);
	}

	@Override
	public RealObject update(RealObject object) {
		return objectRepo.saveAndFlush(object);
	}

	@Override
	public List<RealObject> listObjects() {
		List<RealObject> objects = objectRepo.findAll();
		Map<String, RealObject> objectsMap = new HashMap<>();
		for(RealObject object : objects) {
			if(objectsMap.containsKey(object.getPictureName())) {
				RealObject obj = objectsMap.get(object.getPictureName());
				if(Double.compare(object.getConfidence(), obj.getConfidence()) > 0) {
					objectsMap.put(object.getPictureName(), object);
				}
			} else {
				objectsMap.put(object.getPictureName(), object);
			}
		}
		List<RealObject> results = new ArrayList<>();
		Set<Entry<String, RealObject>> entrySet = objectsMap.entrySet();
		for(Entry<String, RealObject> entry : entrySet) {
			results.add(entry.getValue());
		}
		return results;
	}

	@Override
	public RealObject findById(Long id) {
		return objectRepo.findById(id);
	}

	@Override
	public List<RealObject> listAll() {
		return objectRepo.findAll();
	}
	
	
	public String searchObjectImage(String objectName) {
		BingImageSearchRequest request = new BingImageSearchRequest();
		request.keyword.set(objectName)
			.offset.set(0).count.set(2).mkt.set("en_US").safeSearch.set("Strict").size.set("Large");
		try {
			BingImageSearchResponse response = searchClient.searchImage(request);
			return response.value.get(0).thumbnailUrl;
		} catch (BingImageSearchException e) {
			e.printStackTrace();
		}
		return "";
	}

}
