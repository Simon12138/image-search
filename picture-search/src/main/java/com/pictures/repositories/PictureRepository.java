package com.pictures.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pictures.models.Picture;


@Repository
public interface PictureRepository extends JpaRepository<Picture, Long>{
	@Query("select p from Picture p where p.path=?1")
	public Picture findPictureByName(String name);
	
	@Query("select p from Picture p where p.id=?1")
	public Picture findPictureById(Long id);
	
	@Query("select p FROM Picture p join p.tags tag where tag.description=?1 order by p.creationTime desc")
	public List<Picture> searchPictureByKey(String key);
}
