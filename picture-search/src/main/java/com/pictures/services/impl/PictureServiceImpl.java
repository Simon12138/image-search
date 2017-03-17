package com.pictures.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.pictures.models.Picture;
import com.pictures.models.Tag;
import com.pictures.repositories.PictureRepository;
import com.pictures.services.PictureService;
import com.pictures.type.DatedPicture;
import com.pictures.type.TypeConstants;

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
	public void attachTag(Picture picture, Tag tag) {
		picture.getTags().add(tag);
		pictureRepo.save(picture);
	}

	@Override
	public Picture findPictureByName(String name) {
		return pictureRepo.findPictureByName(name);
	}

	@Override
	public List<Picture> findAll() {
		Sort sort = new Sort(Direction.DESC, "creationTime");
		return pictureRepo.findAll(sort);
	}

	@Override
	public Picture findPictureById(Long id) {
		return pictureRepo.findPictureById(id);
	}

	@Override
	public List<Picture> searchPictureByKey(String key) {
		return pictureRepo.searchPictureByKey(key);
	}

	@Override
	public List<DatedPicture> buildDatedPictures(List<Picture> pictures) {
		List<DatedPicture> datedPictures = new ArrayList<>();
		if(pictures == null || pictures.isEmpty()) {
			return datedPictures;
		}
		Long maxTime = pictures.get(0).getCreationTime().getTime();
		Long minTime = pictures.get(pictures.size() - 1).getCreationTime().getTime();
		ArrayList<Date> groupDates = new ArrayList<>();
		long groupSize = (maxTime - minTime) % TypeConstants.AN_HOUR == 0 ? 
				(maxTime - minTime) / TypeConstants.AN_HOUR : (maxTime - minTime) / TypeConstants.AN_HOUR + 1;
		if(groupSize == 0) {
			groupDates.add(new Date(minTime));
		} else {
			for(long i = 0; i < groupSize; i++) {
				groupDates.add(new Date(maxTime - i * TypeConstants.AN_HOUR));
			}
		}
		for(int i=0; i < groupDates.size(); i++) {
			DatedPicture datedPicture = new DatedPicture(groupDates.get(i), new ArrayList<>());
			for(Picture picture : pictures) {
				if(picture.getCreationTime().compareTo(new Date(groupDates.get(i).getTime() - TypeConstants.AN_HOUR)) > 0 && 
						picture.getCreationTime().compareTo(groupDates.get(i)) <= 0) {
					datedPicture.getPictures().add(picture);
				}
			}
			if(!datedPicture.getPictures().isEmpty()) {
				datedPictures.add(datedPicture);
			}
		}
		return datedPictures;
	}
}
