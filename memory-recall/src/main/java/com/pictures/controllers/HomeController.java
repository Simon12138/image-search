package com.pictures.controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.pictures.controllers.dto.QueryParam;
import com.pictures.entity.Avatar;
import com.pictures.entity.Location;
import com.pictures.entity.Picture;
import com.pictures.entity.PictureFace;
import com.pictures.entity.PictureObject;
import com.pictures.entity.RealObject;
import com.pictures.projectoxford.face.FaceServiceClient;
import com.pictures.projectoxford.face.contract.AddPersistedFaceResult;
import com.pictures.projectoxford.face.contract.Face;
import com.pictures.projectoxford.face.contract.FaceRectangle;
import com.pictures.projectoxford.face.contract.SimilarPersistedFace;
import com.pictures.projectoxford.face.rest.ClientException;
import com.pictures.projectoxford.vision.VisionServiceClient;
import com.pictures.projectoxford.vision.contract.AnalysisResult;
import com.pictures.projectoxford.vision.contract.Tag;
import com.pictures.service.AvatarService;
import com.pictures.service.LocationService;
import com.pictures.service.PictureService;
import com.pictures.service.RealObjectService;
import com.pictures.utils.ImageUtils;
import com.pictures.utils.SystemDataSet;

@Controller
@RequestMapping(value="/")
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private PictureService pictureService;
	
	@Autowired
	private FaceServiceClient faceClient;

	@Autowired
	private VisionServiceClient visionClient;

	@Autowired
	private AvatarService avatarService;
	
	@Autowired
	private RealObjectService objectService;
	
	@Autowired
	private LocationService locationService;
	
	private String[] features = { "tags" };
	
	private String[] details = {};
	
	
	@RequestMapping("/")
	public String index(Model model) {
		List<Avatar> avatars = avatarService.list();
		model.addAttribute("avatars", avatars);
		List<RealObject> objects = objectService.list();
		model.addAttribute("objects", objects);
		List<Location> locations = locationService.list();
		model.addAttribute("locations", locations);
		return "index";
	}
	
	@RequestMapping(value="/images/query", method = RequestMethod.POST)
	@ResponseBody
	public HttpEntity<Picture[]> query(@RequestBody QueryParam queryParam) {
		Picture[] pictures = pictureService.queryPicture(queryParam).toArray(new Picture[] {});
		return new HttpEntity<Picture[]>(pictures);
	}
	
	@RequestMapping(value="/images/download/{id}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<byte[]> downloadPicture(@PathVariable Long id) {
		Picture picture = pictureService.findById(id);
		byte[] result;
		try {
			InputStream in = new FileInputStream(SystemDataSet.PICTURE_SAVED_LOCATION + picture.getName());
			result = IOUtils.toByteArray(in);
			HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.IMAGE_JPEG);
		    headers.setContentLength(result.length);
		    return new HttpEntity<byte[]>(result, headers);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new HttpEntity<byte[]>(new byte[0]);
	}
	
	@RequestMapping(value="/avatars/download/{id}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<byte[]> downloadAvatar(@PathVariable Long id) {
		Avatar avatar = avatarService.findById(id);
		byte[] result;
		try {
			InputStream in = new FileInputStream(SystemDataSet.AVATAR_SAVED_LOCATION + avatar.getName());
			result = IOUtils.toByteArray(in);
			HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.IMAGE_JPEG);
		    headers.setContentLength(result.length);
		    return new HttpEntity<byte[]>(result, headers);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new HttpEntity<byte[]>(new byte[0]);
	}
	
	@RequestMapping(value="/objects/download/{id}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<byte[]> downloadObject(@PathVariable Long id) {
		RealObject object = objectService.findById(id);
		byte[] result;
		try {
			InputStream in = new FileInputStream(SystemDataSet.PICTURE_SAVED_LOCATION + object.getPictureName());
			result = IOUtils.toByteArray(in);
			HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.IMAGE_JPEG);
		    headers.setContentLength(result.length);
		    return new HttpEntity<byte[]>(result, headers);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new HttpEntity<byte[]>(new byte[0]);
	}
	
	@RequestMapping(value="/locations/download/{id}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<byte[]> downloadLocation(@PathVariable Long id) {
		Location location = locationService.findById(id);
		List<Picture> pictures = pictureService.getPicturesByLocation(location.getDescription());
		byte[] result;
		try {
			InputStream in = new FileInputStream(SystemDataSet.PICTURE_SAVED_LOCATION + pictures.get(0).getName());
			result = IOUtils.toByteArray(in);
			HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.IMAGE_JPEG);
		    headers.setContentLength(result.length);
		    return new HttpEntity<byte[]>(result, headers);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new HttpEntity<byte[]>(new byte[0]);
	}
	
	@RequestMapping(value="/avatars/{id}/images", method=RequestMethod.GET)
	@ResponseBody
	public HttpEntity<List<Picture>> getImagesUsingAvatar(@PathVariable Long id) {
		Avatar avatar = avatarService.findById(id);
		try {
			byte [] avatarImage = ImageUtils.loadImage(SystemDataSet.AVATAR_SAVED_LOCATION + avatar.getName());
			UUID faceId = faceClient.detect(avatarImage, true, false, null)[0].faceId;
			SimilarPersistedFace[] similarPersistedFaces = faceClient.findSimilar(faceId, SystemDataSet.FACE_LIST_ID, 1000);
			List<String> faceUUIDs = new ArrayList<>();
			for(SimilarPersistedFace face : similarPersistedFaces) {
				faceUUIDs.add(face.persistedFaceId.toString());
			}
//			List<String> faceUUIDs = Arrays.asList("af215745-e250-4a5d-9966-e89b7e3e7f73", "486c6531-6c81-46c6-ad8f-ab26901b5a0d", "d38900c7-ee53-4511-830d-29aa5f0027ec");
			List<Picture> pictures = pictureService.getPicturesByFaces(faceUUIDs);
			return new HttpEntity<List<Picture>>(pictures);
		} 
		catch (ClientException e) {
			logger.warn("Find similar faces failed: {0}", e);
		} catch (IOException e) {
			logger.warn("Find similar faces failed: {0}", e);
		}
		return new HttpEntity<List<Picture>>(new ArrayList<>());
	}
	
	@RequestMapping(value="/objects/{id}/images", method=RequestMethod.GET)
	@ResponseBody
	public HttpEntity<List<Picture>> getImagesUsingObject(@PathVariable Long id) {
		RealObject object = objectService.findById(id);
		List<Picture> pictures = pictureService.getPicturesByObject(object.getName());
		return new HttpEntity<List<Picture>>(pictures);
	}
	
	@RequestMapping(value="/locations/{id}/images", method=RequestMethod.GET)
	@ResponseBody
	public HttpEntity<List<Picture>> getImagesUsingLocation(@PathVariable Long id) {
		Location location = locationService.findById(id);
		List<Picture> pictures = pictureService.getPicturesByLocation(location.getDescription());
		return new HttpEntity<List<Picture>>(pictures);
	}
	
	@RequestMapping(value="/images/upload", method=RequestMethod.POST)
	@ResponseBody
	public void upload(@RequestParam("files") MultipartFile[] images) {
		for(MultipartFile image : images) {
			Picture picture = pictureService.findByName(image.getOriginalFilename());
			if(image.isEmpty() || picture != null) {
				continue;
			}
			// save image
			try {
				ImageUtils.saveImage(SystemDataSet.PICTURE_SAVED_LOCATION + image.getOriginalFilename(), image.getBytes());
			} catch (IOException e) {
				logger.warn("Save image failed {0}", e);
			}
			
			picture = new Picture();
			// Handle picture name
			picture.setName(image.getOriginalFilename());
			
			// Handle creation time
			handleCreationTime(picture);
			
			// Handle location
			try {
				handleLocation(picture, image);
			} catch (Exception e) {
				logger.warn("Get location information from picture failed: {0}", e);
			}
			
			// Handle face information
			try {
				handleFace(picture, image);
			} catch (Exception e) {
				logger.warn("Recognize faces from picture failed: {0}", e);
			}
			
			// Handle object information
			try {
				handleObject(picture, image);
			} catch (Exception e) {
				logger.warn("Recognize objects from picture failed: {0}", e);
			}
			
			pictureService.create(picture);
		}
		
		
	}
	
	private void handleCreationTime(Picture picture) {
		String name = picture.getName();
		String creationTime = name.substring(17, name.length() - 5);
		DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
		try {
			Date date = df.parse(creationTime);
			picture.setCreationTime(date);
		} catch (ParseException e) {
			logger.warn("Date format is wrong: {0}", e);
		}
	}
	
	private void handleLocation(Picture picture, MultipartFile image) throws Exception {
		Metadata metadata = ImageMetadataReader.readMetadata(image.getInputStream());
        Double latitude = null;
        Double longitude = null;
        for(Directory directory : metadata.getDirectories()) {
        	for(com.drew.metadata.Tag tag : directory.getTags()) {
        		String tagName = tag.getTagName();
        		String tagDesc = tag.getDescription();
        		if("GPS Latitude".equals(tagName)) {
        			if(!StringUtils.isEmpty(tagDesc)) {
        				latitude = convertLocationToDouble(tagDesc);
        			}
        		} else if("GPS Longitude".equals(tagName)) {
        			if(!StringUtils.isEmpty(tagDesc)) {
        				longitude = convertLocationToDouble(tagDesc);
        			}
        		}
        	}
        }
        if(latitude != null && longitude != null && !latitude.equals(SystemDataSet.ZERO) && !longitude.equals(SystemDataSet.ZERO)) {
        	picture.setLatitude(latitude);
        	picture.setLongitude(longitude);
        	LatLng location = new LatLng(latitude, longitude);
        	GeoApiContext context = new GeoApiContext().setApiKey(SystemDataSet.GOOGLE_MAP_SECRET_ID);
        	GeocodingResult[] addressResult = GeocodingApi.reverseGeocode(context, location).await();
        	String locationDesc = addressResult[0].formattedAddress;
        	Location lo = locationService.findByDescription(locationDesc);
        	if(lo == null) {
        		lo = new Location();
        		lo.setDescription(locationDesc);
        		locationService.create(lo);
        	}
        	picture.setLocation(locationDesc);
        }
	}
	
	private double convertLocationToDouble(String value) {
		double hour = Double.valueOf(value.split("°")[0].trim());
		double minute = Double.valueOf(value.split("°")[1].split("'")[0].trim());
		double second = Double.valueOf(value.split("°")[1].split("'")[1].split("\"")[0].trim());
		return hour + minute / 60 + second / 3600;
	}
	
	
	private void handleFace(Picture picture, MultipartFile image) throws Exception {
		// get faces from this picture
		Face[] facesInPicture = faceClient.detect(image.getBytes(), true, false, null);
		List<AddPersistedFaceResult> persistedFaceResults = new ArrayList<>();
		Map<AddPersistedFaceResult, Face> persistedFacesMap = new HashMap<>();
		try {
			faceClient.getFaceList(SystemDataSet.FACE_LIST_ID);
		} catch(Exception e) {
			faceClient.createFaceList(SystemDataSet.FACE_LIST_ID, SystemDataSet.FACE_LIST_NAME, null);
		}
		for (Face face : facesInPicture) {
			AddPersistedFaceResult persistedFace = faceClient.AddFaceToFaceList(SystemDataSet.FACE_LIST_ID, image.getBytes(),
					null, face.faceRectangle);
			persistedFaceResults.add(persistedFace);
			persistedFacesMap.put(persistedFace, face);
			PictureFace pictureFace = new PictureFace();
			pictureFace.setUuid(persistedFace.persistedFaceId.toString());
			picture.addFace(pictureFace);
		}

		List<Avatar> avatars = avatarService.list();
		if (avatars.isEmpty()) {
			for (AddPersistedFaceResult persistedFace : persistedFaceResults) {
				Avatar avatar = new Avatar();
				avatar.setName(persistedFace.persistedFaceId.toString() + picture.getName());
				avatar.setUuid(persistedFace.persistedFaceId.toString());
				avatarService.create(avatar);
				FaceRectangle rectangle = persistedFacesMap.get(persistedFace).faceRectangle;
				ImageUtils.cutPic(SystemDataSet.PICTURE_SAVED_LOCATION + picture.getName(),
						SystemDataSet.AVATAR_SAVED_LOCATION + avatar.getName(), rectangle.left, rectangle.top,
						rectangle.width, rectangle.height);
			}
		} else {
			UUID[] facesId = new UUID[avatars.size()];
			for (int i = 0; i < avatars.size(); i++) {
				facesId[i] = UUID.fromString(avatars.get(i).getUuid());
			}
			for (AddPersistedFaceResult persistedFace : persistedFaceResults) {
				SimilarPersistedFace[] similarFaces = faceClient
						.findSimilar(persistedFacesMap.get(persistedFace).faceId, SystemDataSet.FACE_LIST_ID, 2);
				if (similarFaces.length < 2) {
					Avatar avatar = new Avatar();
					avatar.setName(persistedFace.persistedFaceId.toString() + picture.getName());
					avatar.setUuid(persistedFace.persistedFaceId.toString());
					avatarService.create(avatar);
					FaceRectangle rectangle = persistedFacesMap.get(persistedFace).faceRectangle;
					ImageUtils.cutPic(SystemDataSet.PICTURE_SAVED_LOCATION + picture.getName(),
							SystemDataSet.AVATAR_SAVED_LOCATION + avatar.getName(), rectangle.left, rectangle.top,
							rectangle.width, rectangle.height);
				}
			}
		}
	}
	
	private void handleObject(Picture picture, MultipartFile image) throws Exception {
		AnalysisResult result = visionClient.analyzeImage(image.getBytes(), features, details);
		List<Tag> tagsInPicture = filterTags(result);
		// find the same name tag if confidence is larger than system,
		// replace; else do nothing
		for (Tag tag : tagsInPicture) {
			RealObject object = objectService.findByName(tag.getName());
			if (object == null) {
				object = new RealObject();
				object.setName(tag.getName());
				object.setConfidence(tag.getConfidence());
				object.setPictureName(picture.getName());
				objectService.create(object);
			} else if (Double.compare(tag.getConfidence(), object.getConfidence()) > 0) {
				object.setConfidence(tag.getConfidence());
				object.setPictureName(picture.getName());
				objectService.update(object);
			}
			PictureObject pictureObject = new PictureObject();
			pictureObject.setName(object.getName());
			picture.addObject(pictureObject);
		}
	}
	
	private List<Tag> filterTags(AnalysisResult result) {
		List<Tag> tags = new ArrayList<>();
		List<Tag> tagsInResult = result.getTags();
		for (Tag tag : tagsInResult) {
			if (!SystemDataSet.EXCLUDE_TAGS.contains(StringUtils.upperCase(tag.getName()))
					&& Double.compare(tag.getConfidence(), SystemDataSet.CONFIDENCE) >= 0) {
				tags.add(tag);
			}
		}
		return tags;
	}
}
