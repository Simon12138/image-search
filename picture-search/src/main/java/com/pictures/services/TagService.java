package com.pictures.services;

import com.pictures.models.Tag;

public interface TagService {
	
	Tag create(Tag tag);
	
	Tag findTagByDesc(String desc);
}
