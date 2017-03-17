package com.pictures.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.pictures.models.Tag;
import com.pictures.repositories.TagRepository;
import com.pictures.services.TagService;

@Service
@Primary
public class TagServiceImpl implements TagService {
	
	@Autowired
	private TagRepository tagRepo;

	@Override
	public Tag create(Tag tag) {
		return this.tagRepo.save(tag);
	}

	@Override
	public Tag findTagByDesc(String desc) {
		return this.tagRepo.findTagByDesc(desc);
	}
	
}
