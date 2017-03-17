package com.pictures.controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.pictures.models.PersonPredefined;
import com.pictures.models.Picture;
import com.pictures.models.Tag;
import com.pictures.models.enumration.TagType;
import com.pictures.projectoxford.face.FaceServiceClient;
import com.pictures.projectoxford.face.FaceServiceClient.FindSimilarMatchMode;
import com.pictures.projectoxford.face.contract.AddPersistedFaceResult;
import com.pictures.projectoxford.face.contract.Face;
import com.pictures.projectoxford.face.contract.FaceList;
import com.pictures.projectoxford.face.contract.FaceMetadata;
import com.pictures.projectoxford.face.contract.SimilarPersistedFace;
import com.pictures.projectoxford.face.rest.ClientException;
import com.pictures.projectoxford.vision.VisionServiceClient;
import com.pictures.projectoxford.vision.contract.AnalysisResult;
import com.pictures.response.HttpResponseBody;
import com.pictures.services.PersonPredefinedService;
import com.pictures.services.PictureService;
import com.pictures.services.TagService;
import com.pictures.type.ResponseCode;
import com.pictures.type.ResponseMessage;
import com.pictures.type.TypeConstants;


@Controller
@RequestMapping(value="/images")
public class ImageController {
	
	Logger logger = LoggerFactory.getLogger(ImageController.class);
	
	@Autowired
	private PictureService pictureService;
	
	@Autowired
	private TagService tagService;
	
	@Autowired
	private VisionServiceClient visionService;
	
	@Autowired
	private FaceServiceClient faceService;
	
	@Autowired
	private PersonPredefinedService personService;
	
	
	@RequestMapping(value="/download/{pictureId}", method=RequestMethod.GET)
	@ResponseBody
	public HttpEntity<byte[]> downloadImage(@PathVariable Long pictureId) {
		Picture picture = pictureService.findPictureById(pictureId);
		byte[] result;
		try {
			InputStream in = new FileInputStream(TypeConstants.PICTURE_SAVED_LOCATION + picture.getPath());
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
	
	@RequestMapping(value="/person/create", method=RequestMethod.POST)
	@ResponseBody
	public HttpResponseBody createPerson(@RequestParam(value="image") MultipartFile image, @RequestParam(value="name") String name) {
		HttpResponseBody message = new HttpResponseBody();
		
		try {
			try {
				faceService.getFaceList(TypeConstants.FACE_LIST_ID);
				logger.info("Faces List already exist.", TypeConstants.FACE_LIST_ID);
			} catch(ClientException e) {
				if(e.getMessage().contains("is not found")) {
					logger.info("Start to Create a person group: {0}", TypeConstants.FACE_LIST_ID);
					faceService.createFaceList(TypeConstants.FACE_LIST_ID, TypeConstants.FACE_LIST_NAME, "");
				}
			}
			PersonPredefined person = personService.findPersonByName(name);
			if(person == null) {
				logger.info("Create a person into this person group.");
				Face[] faces = faceService.detect(image.getInputStream(), true, false, null);
				if(faces.length != 1) {
					logger.warn("You cannot upload a picture which contains more than one face.");
					message.setCode(ResponseCode.OPERATION_ERROR);
					message.setMessage(ResponseMessage.P001005);
					return message;
				}
				
				logger.info("Start to add face to face list.");
				AddPersistedFaceResult faceResult = faceService.AddFaceToFaceList(TypeConstants.FACE_LIST_ID, image.getInputStream(), name, faces[0].faceRectangle);
				person = new PersonPredefined();
				person.setPersonId(faceResult.persistedFaceId);
				person.setName(name);
				personService.create(person);
				logger.info("Create Person Succeffully.");
			} else {
				logger.warn("You have already defined this person.");
				message.setCode(ResponseCode.OPERATION_ERROR);
				message.setMessage(ResponseMessage.P001006);
				return message;
			}
		} catch (IOException | ClientException e) {
			logger.warn("Predefine person failed. Please check the internet.");
			message.setCode(ResponseCode.INTERNAL_ERROR);
			message.setMessage(ResponseMessage.P001004);
			return message;
		}
		
		message.setCode(ResponseCode.SUCCESS);
		message.setMessage(ResponseMessage.P001003);
		return message;
	}
	
	@RequestMapping(value="/upload", method=RequestMethod.POST) 
	@ResponseBody
	public HttpResponseBody uploadImages(@RequestParam("files") MultipartFile[] images) {
		HttpResponseBody message = new HttpResponseBody();
		
		// Combine the original file name
		String uploadedFileName = Arrays.stream(images).map(x -> x.getOriginalFilename())
                .filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(", "));
		
		// if the original name combined is empty
        if (StringUtils.isEmpty(uploadedFileName)) {
            logger.info("You have not chose any files.");
            message.setCode(ResponseCode.OPERATION_ERROR);
            message.setMessage(ResponseMessage.P001001);
            return message;
        }

        message = saveUploadedFiles(Arrays.asList(images));
		return message;
	}
	
	private HttpResponseBody saveUploadedFiles(List<MultipartFile> images) {
		HttpResponseBody message = new HttpResponseBody();
        for (MultipartFile image : images) {
        	Picture picture = pictureService.findPictureByName(image.getOriginalFilename());
            if (image.isEmpty() || picture != null) {
                continue;
            }
    		
    		try {
    			// save picture into server
                byte[] bytes = image.getBytes();
                Path path = Paths.get(TypeConstants.PICTURE_SAVED_LOCATION + image.getOriginalFilename());
                Files.write(path, bytes);
                
    			// Get create time from image
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String timeString = image.getOriginalFilename().substring(0, image.getOriginalFilename().indexOf("."));
        		Date creationTime = dateFormat.parse(timeString);
        		
				List<Tag> tags = getAddtionalInfo(image);
				picture = new Picture();
				picture.setPath(image.getOriginalFilename());
				picture.setCreationTime(creationTime);
				picture.setTags(tags);
				pictureService.create(picture);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Import failed while getting addtional information.", e.getMessage());
				message.setCode(ResponseCode.INTERNAL_ERROR);
				message.setMessage(ResponseMessage.P001002);
				return message;
			}
        }
        message.setContent(images.size());
        message.setCode(ResponseCode.SUCCESS);
		message.setMessage(ResponseMessage.P001000);
		return message;
    }
	
	private List<Tag> getAddtionalInfo(MultipartFile image) throws Exception {
		List<Tag> tags = new ArrayList<>();
		
		// Get WHAT tags and SCREEN tags
		String[] features = {"tags", "categories"};
		String[] details = {};
		List<String> screen = Arrays.asList("laptop", "pad");
		AnalysisResult result = visionService.analyzeImage(image.getInputStream(), features, details);
		for(com.pictures.projectoxford.vision.contract.Tag tag : result.getTags()) {
			if(tag.getConfidence() - TypeConstants.CONFIDENCE < 0) {
				continue;
			}
			Tag pictureTag = tagService.findTagByDesc(tag.getName().toLowerCase());
			if(pictureTag == null) {
				pictureTag = new Tag();
				pictureTag.setType(screen.contains(tag.getName()) ? TagType.SCREEN : TagType.WHAT);
				pictureTag.setDescription(tag.getName());
				tagService.create(pictureTag);
			}
			tags.add(pictureTag);
		}
		
		// Get WHERE tags
		Metadata metadata = ImageMetadataReader.readMetadata(image.getInputStream());
        Double latitude = null;
        Double longitude = null;
        for(Directory directory : metadata.getDirectories()) {
        	for(com.drew.metadata.Tag tag : directory.getTags()) {
        		String tagName = tag.getTagName();
        		String tagDesc = tag.getDescription();
        		if("GPS Latitude".equals(tagName)) {
        			if(StringUtils.isEmpty(tagDesc)) {
        				latitude = Double.valueOf(tagDesc);
        			}
        		} else if("GPS Longitude".equals(tagName)) {
        			if(StringUtils.isEmpty(tagDesc)) {
        				longitude = Double.valueOf(tagDesc);
        			}
        		}
        	}
        }
        if(latitude != null && longitude != null) {
        	LatLng location = new LatLng(latitude, longitude);
        	GeoApiContext context = new GeoApiContext().setApiKey(TypeConstants.GOOGLE_MAP_SECRET_ID);
        	GeocodingResult[] addressResult = GeocodingApi.reverseGeocode(context, location).await();
        	Tag pictureTag = tagService.findTagByDesc(addressResult[0].formattedAddress);
			if(pictureTag == null) {
				pictureTag = new Tag();
				pictureTag.setType(TagType.WHERE);
				pictureTag.setDescription(addressResult[0].formattedAddress);
				tagService.create(pictureTag);
			}
			tags.add(pictureTag);
        }
        
		// Get WHO tags
        Face[] faces =  faceService.detect(image.getInputStream(), true, false, null);
        Map<UUID, String> nameMaps = buildNamesMap();
        for(Face face : faces) {
        	SimilarPersistedFace[] faceResults = faceService.findSimilar(face.faceId, TypeConstants.FACE_LIST_ID, 100, FindSimilarMatchMode.matchFace);
        	Tag whoTag = tagService.findTagByDesc(nameMaps.get(faceResults[0].persistedFaceId));
        	if(whoTag == null) {
        		whoTag = new Tag();
        		whoTag.setType(TagType.WHO);
        		whoTag.setDescription(nameMaps.get(faceResults[0].persistedFaceId));
        		tagService.create(whoTag);
        	}
        	tags.add(whoTag);
        }
		
		return tags;
	}
	
	private Map<UUID, String> buildNamesMap() {
		Map<UUID, String> names = new HashMap<>();
		try {
			FaceList faceList = faceService.getFaceList(TypeConstants.FACE_LIST_ID);
			for(FaceMetadata face : faceList.persistedFaces) {
				names.put(face.persistedFaceId, face.userData == null ? "" : face.userData);
			}
		} catch (ClientException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return names;
	}
}
