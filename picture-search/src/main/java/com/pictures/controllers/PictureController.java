package com.pictures.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pictures.models.Picture;
import com.pictures.models.Tag;
import com.pictures.response.HttpResponseBody;
import com.pictures.services.PictureService;
import com.pictures.type.DatedPicture;
import com.pictures.type.ResponseCode;
import com.pictures.type.ResponseMessage;

@Controller
@RequestMapping(value="/pictures")
public class PictureController {
	
	@Autowired
	private PictureService pictureService;
	
	@RequestMapping(value="/search/{key}", method=RequestMethod.POST)
	@ResponseBody
	public HttpResponseBody search(Model model, @PathVariable(value="key") String key) {
		HttpResponseBody message = new HttpResponseBody();
		List<Picture> pictures = pictureService.searchPictureByKey(key);
		List<DatedPicture> datedPictures = pictureService.buildDatedPictures(pictures);
		message.setCode(ResponseCode.SUCCESS);
		message.setContent(datedPictures);
		message.setMessage(ResponseMessage.P001007);
		model.addAttribute("datedPictures", datedPictures);
		return message;
	}
	
	@RequestMapping(value="/{id}/tags", method=RequestMethod.POST)
	@ResponseBody
	public HttpResponseBody getPictureTags(@PathVariable(value="id") Long id) {
		HttpResponseBody message = new HttpResponseBody();
		Picture picture = pictureService.findPictureById(id);
		List<Tag> tags = picture.getTags();
		message.setContent(tags);
		message.setCode(ResponseCode.SUCCESS);
		message.setMessage(ResponseMessage.P001007);
		return message;
	}

}
