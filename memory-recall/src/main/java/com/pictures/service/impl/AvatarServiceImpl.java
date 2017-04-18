package com.pictures.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.pictures.entity.Avatar;
import com.pictures.repository.AvatarRepository;
import com.pictures.service.AvatarService;

@Service
@Primary
public class AvatarServiceImpl implements AvatarService {
	
	@Autowired
	private AvatarRepository avatarRepo;

	@Override
	public Avatar create(Avatar avatar) {
		return avatarRepo.save(avatar);
	}

	@Override
	public List<Avatar> list() {
		return avatarRepo.findAll();
	}

	@Override
	public Avatar findById(Long id) {
		return avatarRepo.findById(id);
	}

}
