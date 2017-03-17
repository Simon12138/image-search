package com.pictures.services;

import java.util.UUID;

import com.pictures.models.PersonPredefined;

public interface PersonPredefinedService {
	PersonPredefined create(PersonPredefined person);
	public PersonPredefined findPersonByPersonId(UUID personId);
	public PersonPredefined findPersonByName(String name);
}
