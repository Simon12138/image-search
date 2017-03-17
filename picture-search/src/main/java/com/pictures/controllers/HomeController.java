package com.pictures.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.pictures.models.Picture;
import com.pictures.services.PictureService;
import com.pictures.type.DatedPicture;
import com.pictures.type.Person;

@Controller
@RequestMapping(value="/")
public class HomeController {
	
	@Autowired
	private PictureService pictureService;
	
	@RequestMapping("/")
	public String index(Model model) {
		List<Picture> pictures = pictureService.findAll();
		List<DatedPicture> datedPictures = new ArrayList<>();
		datedPictures = pictureService.buildDatedPictures(pictures);
		model.addAttribute("datedPictures", datedPictures);
		return "index";
	}
	
	@RequestMapping(value="/presetting/person", method=RequestMethod.POST)
	public String preSetting(@ModelAttribute(value="person") Person person) {
		return "presetting";
	}

}
