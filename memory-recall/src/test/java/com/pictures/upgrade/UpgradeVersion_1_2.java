package com.pictures.upgrade;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pictures.entity.RealObject;
import com.pictures.projectoxford.bing.BingImageSearchClient;
import com.pictures.projectoxford.bing.BingImageSearchException;
import com.pictures.projectoxford.bing.model.BingImageSearchRequest;
import com.pictures.projectoxford.bing.model.BingImageSearchResponse;
import com.pictures.projectoxford.bing.model.Image;
import com.pictures.service.RealObjectService;

public class UpgradeVersion_1_2 extends UpgradeBase {
	
	@Autowired
	private BingImageSearchClient searchClient;
	
	@Autowired
	private RealObjectService objectService;
	
	@Test
	public void upgrade() throws BingImageSearchException {
		List<RealObject> objects = objectService.listAll();
		for(RealObject object : objects) {
			BingImageSearchRequest request = new BingImageSearchRequest();
			request.keyword.set(object.getName())
				.offset.set(0).count.set(2).mkt.set("en_US").safeSearch.set("Strict").size.set("Large");
			BingImageSearchResponse response = searchClient.searchImage(request);
			object.setBingImageUrl(response.value.get(0).thumbnailUrl);
			objectService.update(object);
			for(Image image : response.value) {
				System.out.println(object.getName() + "----------" +image.thumbnailUrl);
			}
		}
	}
}
