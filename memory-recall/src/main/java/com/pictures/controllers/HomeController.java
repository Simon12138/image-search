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
		// load avatars for index page and show them
		List<Avatar> avatars = avatarService.list();
		model.addAttribute("avatars", avatars);
		// load object images for index page and show them
		List<RealObject> objects = objectService.list();
		model.addAttribute("objects", objects);
		// load location images for index page and show them
		List<Location> locations = locationService.list();
		model.addAttribute("locations", locations);
		// jump to index page
		return "index";
	}
	
	@RequestMapping(value="/images/query", method = RequestMethod.POST)
	@ResponseBody
	public HttpEntity<Picture[]> query(@RequestBody QueryParam queryParam) {
		// Start to query images using faceId, objectId and locationId
		Picture[] pictures = pictureService.queryPicture(queryParam).toArray(new Picture[] {});
		return new HttpEntity<Picture[]>(pictures);
	}
	
	@RequestMapping(value="/images/download/{id}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<byte[]> downloadPicture(@PathVariable Long id) {
		// load picture from database using picture id
		Picture picture = pictureService.findById(id);
		byte[] result;
		try {
			// load picture from pictures folder and convert to stream
			InputStream in = new FileInputStream(SystemDataSet.PICTURE_SAVED_LOCATION + picture.getName());
			result = IOUtils.toByteArray(in);
			// set response header for the request of downloading image
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
		// load avatar from database using avatar id
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
		// load object from database using object id
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
		// load location from database using location id
		Location location = locationService.findById(id);
		// get pictures which related with this location from database 
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
			logger.error("File {0} cannot be found.{1}", pictures.get(0).getName(), e);
		} catch (IOException e) {
			logger.error("Download location image failed: {0}", e);
		}
		return new HttpEntity<byte[]>(new byte[0]);
	}
	
	@RequestMapping(value="/avatars/{id}/images", method=RequestMethod.GET)
	@ResponseBody
	public HttpEntity<List<Picture>> getImagesUsingAvatar(@PathVariable Long id) {
		// load avatar from database using avatar id
		Avatar avatar = avatarService.findById(id);
		try {
			// load avatar images from folder
			byte [] avatarImage = ImageUtils.loadImage(SystemDataSet.AVATAR_SAVED_LOCATION + avatar.getName());
			// detect the avatar image and get the face UUID
			Face[] faces = faceClient.detect(avatarImage, true, false, null);
			UUID faceId = null;
			if(faces.length != 0) {
				faceId = faceClient.detect(avatarImage, true, false, null)[0].faceId;
			}
			// Using the face UUID find the similar faces in our face list and get the similar face UUIS
			SimilarPersistedFace[] similarPersistedFaces = faceId == null ? new SimilarPersistedFace[0] : faceClient.findSimilar(faceId, SystemDataSet.FACE_LIST_ID, 1000);
			List<String> faceUUIDs = new ArrayList<>();
			for(SimilarPersistedFace face : similarPersistedFaces) {
				if(Double.compare(face.confidence, SystemDataSet.SIMILAR_FACE_CONFIDENCE_QUERY_PROCESS) > 0) {
					faceUUIDs.add(face.persistedFaceId.toString());
				}
			}
			// load pictures using this similar faces UUID
			List<Picture> pictures = pictureService.getPicturesByFaces(faceUUIDs);
			return new HttpEntity<List<Picture>>(pictures);
		} catch (ClientException e) {
			logger.warn("Find similar faces failed: {0}", e);
		} catch (IOException e) {
			logger.warn("Find similar faces failed: {0}", e);
		}
		return new HttpEntity<List<Picture>>(new ArrayList<>());
	}
	
	@RequestMapping(value="/objects/{id}/images", method=RequestMethod.GET)
	@ResponseBody
	public HttpEntity<List<Picture>> getImagesUsingObject(@PathVariable Long id) {
		// load object from database using object id
		RealObject object = objectService.findById(id);
		// load pictures which related with this object using this object name
		List<Picture> pictures = pictureService.getPicturesByObject(object.getName());
		return new HttpEntity<List<Picture>>(pictures);
	}
	
	@RequestMapping(value="/locations/{id}/images", method=RequestMethod.GET)
	@ResponseBody
	public HttpEntity<List<Picture>> getImagesUsingLocation(@PathVariable Long id) {
		// load location from database using location id
		Location location = locationService.findById(id);
		// load pictures which related with this location using this location description
		List<Picture> pictures = pictureService.getPicturesByLocation(location.getDescription());
		return new HttpEntity<List<Picture>>(pictures);
	}
	
	@RequestMapping(value="/images/upload", method=RequestMethod.POST)
	@ResponseBody
	public void upload(@RequestParam("files") MultipartFile[] images) {
		for(MultipartFile image : images) {
			// load picture from database using picture name
			Picture picture = pictureService.findByName(image.getOriginalFilename());
			// if picture is existed in database, then ignore this picture
			if(image.isEmpty() || picture != null) {
				continue;
			}
			// save picture into folder
			try {
				ImageUtils.saveImage(SystemDataSet.PICTURE_SAVED_LOCATION + image.getOriginalFilename(), image.getBytes());
			} catch (IOException e) {
				logger.warn("Save image failed {0}", e);
			}
			// create a picture instance
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
			// save picture into database
			pictureService.create(picture);
		}
		
		
	}
	
	@SuppressWarnings("deprecation")
	private void handleCreationTime(Picture picture) {
		// Picture name formatter: XXXXXXXXXXXXXXXXX20170318_142151X.JPG
		String name = picture.getName();
		// Get the format time from picture name
		String creationTime = name.substring(17, name.length() - 5);
		DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
		try {
			// Get the time from the format time
			Date date = df.parse(creationTime);
			// set creation time for the picture
			picture.setCreationTime(date);
			picture.setCreationHour(date.getMinutes() >= 30 ? date.getHours() + 0.5 : date.getHours());
		} catch (ParseException e) {
			logger.warn("Date format is wrong: {0}", e);
		}
	}
	
	private void handleLocation(Picture picture, MultipartFile image) throws Exception {
		// get image meta data
		Metadata metadata = ImageMetadataReader.readMetadata(image.getInputStream());
		// Get the latitude and longitude fron the meta data
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
        // if the latitude and the longitude are both null or zero, ignore this picture's location information
        if(latitude != null && longitude != null && !latitude.equals(SystemDataSet.ZERO) && !longitude.equals(SystemDataSet.ZERO)) {
        	picture.setLatitude(latitude);
        	picture.setLongitude(longitude);
        	// get location description via google map API
        	LatLng location = new LatLng(latitude, longitude);
        	GeoApiContext context = new GeoApiContext().setApiKey(SystemDataSet.GOOGLE_MAP_SECRET_ID);
        	GeocodingResult[] addressResult = GeocodingApi.reverseGeocode(context, location).await();
        	// Get the first result
        	String locationDesc = addressResult[0].formattedAddress;
        	// load the location from database via location description
        	Location lo = locationService.findByDescription(locationDesc);
        	// if the location has been existed, do not create location 
        	if(lo == null) {
        		// if the location is not existed, should create a new location
        		lo = new Location();
        		lo.setDescription(locationDesc);
        		locationService.create(lo);
        	}
        	picture.setLocation(locationDesc);
        }
	}
	
	
	// Convert String location information to double for google API
	private double convertLocationToDouble(String value) {
		double hour = Double.valueOf(value.split("°")[0].trim());
		double minute = Double.valueOf(value.split("°")[1].split("'")[0].trim());
		double second = Double.valueOf(value.split("°")[1].split("'")[1].split("\"")[0].trim());
		return hour + minute / 60 + second / 3600;
	}
	
	
	private void handleFace(Picture picture, MultipartFile image) throws Exception {
		// detect faces from this picture
		Face[] facesInPicture = faceClient.detect(image.getBytes(), true, false, null);
		// record the faces which we added to the face list
		List<AddPersistedFaceResult> persistedFaceResults = new ArrayList<>();
		// record the relationships between face UUID and the faces which we added to the face list
		Map<AddPersistedFaceResult, Face> persistedFacesMap = new HashMap<>();
		// Check the face whether existed, if throw a exception, we should create a new face list.
		try {
			faceClient.getFaceList(SystemDataSet.FACE_LIST_ID);
		} catch(Exception e) {
			faceClient.createFaceList(SystemDataSet.FACE_LIST_ID, SystemDataSet.FACE_LIST_NAME, null);
		}
		// Add the persisted faces to the picture instance
		for (Face face : facesInPicture) {
			// add the face which we detected from the picture into the face list
			AddPersistedFaceResult persistedFace = faceClient.AddFaceToFaceList(SystemDataSet.FACE_LIST_ID, image.getBytes(),
					null, face.faceRectangle);
			// record the face into memory
			persistedFaceResults.add(persistedFace);
			persistedFacesMap.put(persistedFace, face);
			// create a face instance for the picture, and build the relationships for them
			PictureFace pictureFace = new PictureFace();
			pictureFace.setUuid(persistedFace.persistedFaceId.toString());
			picture.addFace(pictureFace);
		}

		// load all the avatars information from database
		List<Avatar> avatars = avatarService.list();
		// if there are no avatars in the system, we should add the faces as avatars directly 
		// else we need to check the face whether a new face in the system
		if (avatars.isEmpty()) {
			for (AddPersistedFaceResult persistedFace : persistedFaceResults) {
				// create a new avatar instance, and set value
				Avatar avatar = new Avatar();
				avatar.setName(persistedFace.persistedFaceId.toString() + picture.getName());
				avatar.setUuid(persistedFace.persistedFaceId.toString());
				// save the avatar into database
				avatarService.create(avatar);
				// Get the face area in the picture
				FaceRectangle rectangle = persistedFacesMap.get(persistedFace).faceRectangle;
				// Cut the face from the picture and save it into folder
				ImageUtils.cutPic(SystemDataSet.PICTURE_SAVED_LOCATION + picture.getName(),
						SystemDataSet.AVATAR_SAVED_LOCATION + avatar.getName(), rectangle.left, rectangle.top,
						rectangle.width, rectangle.height);
			}
		} else {
			// Build a list for the UUID of the avatars
			UUID[] facesId = new UUID[avatars.size()];
			for (int i = 0; i < avatars.size(); i++) {
				facesId[i] = UUID.fromString(avatars.get(i).getUuid());
			}
			for (AddPersistedFaceResult persistedFace : persistedFaceResults) {
				// find similar faces in the face list for the face which in the new picture
				SimilarPersistedFace[] similarFaces = faceClient
						.findSimilar(persistedFacesMap.get(persistedFace).faceId, SystemDataSet.FACE_LIST_ID, 2);
				// if the faces in the new picture is a new face, we need to create a avatar for this face
				if (similarFacesNotSoStrong(similarFaces)) {
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
	
	// if the length of the result is smaller than 2, it shows that the face is a new face
	// or if the result existed more than two face's similar confidence larger than 0.8, it shows that the face is not a new face
	private boolean similarFacesNotSoStrong(SimilarPersistedFace[] similarFaces) {
		if(similarFaces.length < 2) {
			return true;
		}
		for(int i = 1; i < similarFaces.length; i++) {
			if(Double.compare(similarFaces[i].confidence, SystemDataSet.SIMILAR_FACE_CONFIDENCE_IMPORT_PROCESS) >= 0) {
				return false;
			}
		}
		return true;
	}
	
	private void handleObject(Picture picture, MultipartFile image) throws Exception {
		// Analysis the picture via vision API
		AnalysisResult result = visionClient.analyzeImage(image.getBytes(), features, details);
		// Using filter to except some tags
		List<Tag> tagsInPicture = filterTags(result);
		// find the same name tag if confidence is larger than system,
		// replace; else do nothing
		for (Tag tag : tagsInPicture) {
			// load object from database via object name: tag name
			RealObject object = objectService.findByName(tag.getName());
			// if the object is not existed, create it and build the relationships with picture
			// else if the confidence is larger than the confidence of the object which already in the system, we need to replace the object picture
			if (object == null) {
				object = new RealObject();
				object.setName(tag.getName());
				object.setConfidence(tag.getConfidence());
				object.setPictureName(picture.getName());
				object.setParentTagsNumber(tagsInPicture.size());
				String objectImageUrl = objectService.searchObjectImage(tag.getName());
				object.setBingImageUrl(objectImageUrl);
				objectService.create(object);
			} else if (tagsInPicture.size() < object.getParentTagsNumber() || Double.compare(tag.getConfidence(), object.getConfidence()) > 0) {
				object.setConfidence(tag.getConfidence());
				object.setPictureName(picture.getName());
				object.setParentTagsNumber(tagsInPicture.size());
				objectService.update(object);
			}
			PictureObject pictureObject = new PictureObject();
			pictureObject.setName(object.getName());
			picture.addObject(pictureObject);
		}
	}
	
	// filter some tags which we do not want to show in the database and the page
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
