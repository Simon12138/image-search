package com.pictures.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pictures.entity.Picture;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> {
	
	@Query("select p from Picture p where p.name=?1")
	public Picture findByName(String name);
	
	@Query("select p from Picture p where p.id=?1")
	public Picture findById(Long id);
	
	@Query("select p from Picture p where p.id in ?1")
	public List<Picture> findByIds(List<Long> ids);
	
	@Query("select distinct p from Picture p join p.faces pf where pf.uuid in ?1 order by p.creationTime")
	public List<Picture> getPicturesByFaces(List<String> faceIds);
	
	@Query("select p from Picture p where p.location=?1")
	public List<Picture> getPicturesByLocation(String locations);
	
	@Query("select distinct p from Picture p join p.objects po where po.name=?1 order by p.creationTime")
	public List<Picture> getPicturesByObject(String object);
	
	@Query("select p from Picture p order by p.creationTime")
	public List<Picture> listPictureOrderByTime();

}
