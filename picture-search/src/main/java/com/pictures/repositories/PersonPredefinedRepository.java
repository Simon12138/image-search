package com.pictures.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pictures.models.PersonPredefined;

@Repository
public interface PersonPredefinedRepository extends JpaRepository<PersonPredefined, Long> {
	@Query("select p from PersonPredefined p where p.personId=?1")
	public PersonPredefined findPersonByPersonId(UUID personId);
	
	@Query("select p from PersonPredefined p where p.name=?1")
	public PersonPredefined findPersonByName(String name);
}
