package com.pictures.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pictures.models.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
	// SELECT p FROM Post p LEFT JOIN FETCH p.author ORDER BY p.date DESC
	@Query("SELECT t FROM Tag t WHERE t.description=?1")
	Tag findTagByDesc(String description);
}
