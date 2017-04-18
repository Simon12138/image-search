package com.pictures.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pictures.entity.Avatar;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {

	@Query("select a from Avatar a where a.id=?1")
	public Avatar findById(Long id);
	
}
