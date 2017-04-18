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
import com.pictures.repository.RealObjectRepository;
import com.pictures.service.RealObjectService;

@Service
@Primary
public class RealObjectServiceImpl implements RealObjectService {

	@Autowired
	private RealObjectRepository objectRepo;
	
	@Override
	public RealObject create(RealObject object) {
		return objectRepo.save(object);
	}

	@Override
	public List<RealObject> list() {
		return objectRepo.findAll();
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

}
