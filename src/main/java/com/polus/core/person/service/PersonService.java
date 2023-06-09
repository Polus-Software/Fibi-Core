package com.polus.core.person.service;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.person.pojo.Person;
import com.polus.core.person.vo.PersonSearchResult;
import com.polus.core.person.vo.PersonVO;

@Transactional
@Service
public interface PersonService {

	/**
	 * This method is used to fetch the person details using personId
	 * @param vo
	 * @return Object of PersonVO as string
	 */
	public ResponseEntity<String> getPersonDetailById(PersonVO vo);

	/**
	 * This method is used to get the similar person based on search string
	 * @param searchString
	 * @return list of personId and fullName 
	 */
	public List<PersonSearchResult> findPerson(String searchString);

	/**
	 * This method is used to save and update person details
	 * @param vo
	 * @return success message and created object as string
	 */
	public String saveOrUpdatePerson(PersonVO vo);

	/**
	 * This method is used to fetch all persons list
	 * @param vo
	 * @return list of all persons
	 */
	public String getAllPersons(PersonVO vo);
	
	public String savePersonFromFeed(Person person);

	/**
	 * This method is used to get Person BasedOnRoleAndRight
	 * @param vo
	 * @return list of person
	 */
	public Set<Person> getPersonBasedOnRoleAndRight();

	/**
	 * This method is used to save degree details of a person
	 * @param vo
	 * @return list of person degree.
	 */
	public String savePersonDegree(PersonVO vo);

	/**
	 * This method is used to get all degree details of a person
	 * @param vo
	 * @return list of person degree.
	 */
	public String getAllPersonDegree(PersonVO vo);

	/**
	 * This method is used to delete degree of a person based on personDegreeId.
	 * @param personDegreeId
	 */
	public String deletePersonDegree(Integer personDegreeId);

	/**
	 * This method is used to get all degree type lookup.
	 * @param  
	 * @return list of degree type.
	 */
	public String getDegreeType();

	/**
	 * This method is used for getPersonPrimaryInformation
	 * @param Person ID
	 * @return person basic information like id,email,full name, unit
	 */
	public Person getPersonPrimaryInformation(String personId);

}
