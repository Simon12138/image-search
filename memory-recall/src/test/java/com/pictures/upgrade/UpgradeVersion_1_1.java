package com.pictures.upgrade;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pictures.entity.Picture;
import com.pictures.entity.RealObject;
import com.pictures.service.PictureService;
import com.pictures.service.RealObjectService;



public class UpgradeVersion_1_1 extends UpgradeBase {
	
	@Autowired
	private PictureService pictureService;
	
	@Autowired
	private RealObjectService objectService;
	
	@Test
	public void upgrade() {
		List<Picture> pictures = pictureService.list();
		for(Picture picture : pictures) {
			Double creationHour = picture.getCreationTime().getMinutes() >= 30 ?
					picture.getCreationTime().getHours() + 0.5 : picture.getCreationTime().getHours();
			picture.setCreationHour(creationHour);
			pictureService.update(picture);
		}
		
		List<RealObject> realObjects = objectService.list();
		for(RealObject realObject : realObjects) {
			realObject.setParentTagsNumber(10);
			objectService.update(realObject);
		}
	}
}
