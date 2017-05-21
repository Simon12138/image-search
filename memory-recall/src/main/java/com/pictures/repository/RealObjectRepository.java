package com.pictures.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pictures.entity.RealObject;

@Repository
public interface RealObjectRepository extends JpaRepository<RealObject, Long> {
	
	@Query("select o from RealObject o where o.name=?1")
	public RealObject findByName(String name);
	
	@Query("select o from RealObject o where o.id=?1")
	public RealObject findById(Long id);
	
	@Query("select o from RealObject o")
	public List<RealObject> listUsingFilter();
	
}
