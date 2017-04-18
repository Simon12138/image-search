package com.pictures.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pictures.entity.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
	
	@Query("select l from Location l where l.description=?1")
	public Location findByDescription(String desc);
	
	@Query("select l from Location l where l.id=?1")
	public Location findById(Long id);
}
