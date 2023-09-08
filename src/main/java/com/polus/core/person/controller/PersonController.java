package com.polus.core.person.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.polus.core.common.dto.ElasticQueueRequest;
import com.polus.core.common.service.CommonService;
import com.polus.core.common.service.ElasticSyncOperation;
import com.polus.core.person.pojo.Person;
import com.polus.core.person.service.PersonService;
import com.polus.core.person.vo.PersonSearchResult;
import com.polus.core.person.vo.PersonVO;
import com.polus.core.pojo.Country;

@RestController
public class PersonController {

	protected static Logger logger = LogManager.getLogger(PersonController.class.getName());

	@Autowired
	public PersonService personService;
	
	@Autowired
	public CommonService commonService;

	@Autowired
	private ElasticQueueRequest elasticQueueRequest;

	@Autowired
	private ElasticSyncOperation elasticSyncOperation;

//	@Autowired
//	@Qualifier(value = "dashboardService")
//	private DashboardService dashboardService;

	@PostMapping(value = "/getPersonDetailById", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> getPersonDetailById(@RequestBody PersonVO vo, HttpServletRequest request, HttpServletResponse response) {
		logger.info("Request for getPersonDetailById");
		return personService.getPersonDetailById(vo);
	}

	@GetMapping(value = "/findPersons")
	public List<PersonSearchResult> getNext(HttpServletRequest request, HttpServletResponse response, @RequestParam("searchString") String searchString) {
		logger.info("Requesting for findPersons");
		logger.info("searchString : {}", searchString);
		return personService.findPerson(searchString);
	}

	@PostMapping(value = "/saveOrUpdatePerson", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String saveOrUpdatePerson(@RequestBody PersonVO vo, HttpServletRequest request, HttpServletResponse response) {
		logger.info("Request for saveOrUpdatePerson");
		String personResponse = personService.saveOrUpdatePerson(vo);
		elasticSyncOperation.initiateSyncForElasticQueueRequestList(elasticQueueRequest);
		return personResponse;
	}

	@PostMapping(value = "/getAllPersons", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getAllPersons(@RequestBody PersonVO vo, HttpServletRequest request, HttpServletResponse response) {
		logger.info("Request for getAllPersons");
		return personService.getAllPersons(vo);
	}

//	@PostMapping(value = "/exportPersonDatas")
//	public ResponseEntity<byte[]> exportPersonDatas(HttpServletRequest request, @RequestBody CommonVO vo) throws Exception {
//		XSSFWorkbook workbook = dashboardService.getXSSFWorkbookForDashboard(vo);
//		return dashboardService.getResponseEntityForDownload(vo, workbook);
//	}
	
	public String savePersonFromFeed( Person person) {
		return personService.savePersonFromFeed(person);
	}

	@GetMapping(value = "/getCountryLookUp", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Country> getCountryLookUp(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Requesting for getCountryLookUp");
		return commonService.getCountryLookUp();
	}

	@PostMapping(value = "/savePersonDegree", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String saveOrUpdatePersonDegree(@RequestBody PersonVO vo, HttpServletRequest request, HttpServletResponse response) {
		logger.info("Requesting for savePersonDegree");
		return personService.savePersonDegree(vo);
	}

	@PostMapping(value = "/getAllPersonDegree", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getAllExtReviewers(@RequestBody PersonVO vo, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("Request for getAllPersonDegree");
		return personService.getAllPersonDegree(vo);
	}

	@DeleteMapping(value = "/deletePersonDegree/{personDegreeId}")
	public String deleteExtReviewerAttachment(@PathVariable(value = "personDegreeId", required = true) final Integer personDegreeId) {
		logger.info("Requesting for deletePersonDegree");
		return personService.deletePersonDegree(personDegreeId);
	}
	
	@GetMapping(value = "/getDegreeType")
	public String fetchRequiredParams(HttpServletRequest request) {
		logger.info("Requesting for getDegreeType");
		return personService.getDegreeType();
	}

	@PostMapping(value = "/getPersonPrimaryInformation")
	public Person getPersonPrimaryInformation(@RequestBody PersonVO vo, HttpServletRequest request) {
		logger.info("Requesting for getPersonPrimaryInformation");
		return personService.getPersonPrimaryInformation(vo.getPersonId());
	}
}
