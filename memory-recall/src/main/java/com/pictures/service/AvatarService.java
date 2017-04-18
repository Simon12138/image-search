package com.pictures.service;

import java.util.List;

import com.pictures.entity.Avatar;

public interface AvatarService {
	public Avatar create(Avatar avatar);
	
	public List<Avatar> list();
	
	public Avatar findById(Long id);
}
