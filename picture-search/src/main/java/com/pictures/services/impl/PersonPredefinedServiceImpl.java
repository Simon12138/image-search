package com.pictures.services.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.pictures.models.PersonPredefined;
import com.pictures.repositories.PersonPredefinedRepository;
import com.pictures.services.PersonPredefinedService;

@Service
@Primary
public class PersonPredefinedServiceImpl implements PersonPredefinedService {

	@Autowired
	private PersonPredefinedRepository personRepository;
	
	@Override
	public PersonPredefined create(PersonPredefined person) {
		return personRepository.save(person);
	}
	
	@Override
	public PersonPredefined findPersonByPersonId(UUID personId){
		return personRepository.findPersonByPersonId(personId);
	}
	
	@Override
	public PersonPredefined findPersonByName(String name) {
		return personRepository.findPersonByName(name);
	}

}
