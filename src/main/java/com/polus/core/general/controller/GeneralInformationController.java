package com.polus.core.general.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.polus.core.applicationexception.service.ApplicationExceptionService;
import com.polus.core.applicationexception.vo.ApplicationExceptionVO;
import com.polus.core.common.dao.CommonDao;
import com.polus.core.common.service.CommonService;
import com.polus.core.general.service.GeneralInformationService;
import com.polus.core.pojo.ResearchTypeArea;
import com.polus.core.pojo.ResearchTypeSubArea;
import com.polus.core.pojo.Unit;
import com.polus.core.vo.CommonVO;
import com.polus.core.vo.SponsorMaintenanceVO;

@RestController
public class GeneralInformationController {

	protected static Logger logger = LogManager.getLogger(GeneralInformationController.class.getName());

	@Autowired
	@Qualifier(value = "commonDao")
	private CommonDao commonDao;

	@Autowired
	@Qualifier(value = "commonService")
	private CommonService commonService;

	@Autowired
	@Qualifier(value = "generalInformationService")
	private GeneralInformationService generalInformationService;
	
	@Autowired
	private ApplicationExceptionService applicationExceptionService;

	@PostMapping(value = "/getUnitName")
	public String requestResearchSummaryData(@RequestBody CommonVO vo, HttpServletRequest request) throws Exception {
		logger.info("Requesting for getUnitName");
		logger.info("unitNumber : {} ", vo.getUnitNumber());
		return commonDao.convertObjectToJSON(commonDao.getUnitName(vo.getUnitNumber()));
	}

	@PostMapping(value = "/maintainSponsor")
	public String maintainSponsor(@RequestBody SponsorMaintenanceVO vo, HttpServletRequest request) throws Exception {
		logger.info("Requesting for maintainSponsor");
		logger.info("sponsorCode: {} ", vo.getSponsorCode());
		return commonDao.convertObjectToJSON(generalInformationService.fetchSponsorData(vo.getSponsorCode()));
	}

	@PostMapping(value = "/createNewSponsor")
	public String createNewSponsor(HttpServletRequest request) throws Exception {
		logger.info("Requesting for createNewSponsor");
		return commonDao.convertObjectToJSON(generalInformationService.createNewSponsor());
	}

	@PostMapping(value = "/saveSponsor")
	public String saveSponsor(@RequestBody SponsorMaintenanceVO vo, HttpServletRequest request) throws Exception {
		logger.info("Requesting for saveSponsor");
		return commonDao.convertObjectToJSON(generalInformationService.saveSponsor(vo));
	}

	@PostMapping(value = "/getPersonMonthySalaryForJobCode")
	public String getPersonMonthySalaryForJobCode(@RequestBody CommonVO vo, HttpServletRequest request) throws Exception {
		logger.info("Requesting for getPersonMonthySalaryForJobCode");
		logger.info("personId: {}", vo.getPersonId());
		logger.info("jobCode: {}", vo.getJobCode());
		return commonDao.convertObjectToJSON(generalInformationService.getMonthlySalaryForPerson(vo.getPersonId(), vo.getJobCode()));
	}

	@GetMapping(value = "/syncPersonRole", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String syncPersonRole(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Requesting for syncPersonRole");
		return generalInformationService.syncPersonRole();
	}

	@PostMapping(value = "/getSponsorName")
	public String getSponsorName(@RequestBody CommonVO vo, HttpServletRequest request) throws Exception {
		logger.info("Requesting for getSponsorName");
		logger.info("sponsorCode : {}" , vo.getSponsorCode());
		return commonDao.convertObjectToJSON(commonDao.getSponsorName(vo.getSponsorCode()));
	}

	@PostMapping(value = "/getAllSponsors")
	public String getAllSponsors(@RequestBody CommonVO vo, HttpServletRequest request) throws Exception {
		logger.info("Requesting for getAllSponsors");
		return generalInformationService.getAllSponsors(vo);
	}

	@PostMapping(value = "/getLookUpDatas")
	public String getLookUpDatas(@Valid @RequestBody CommonVO vo, HttpServletRequest request) throws Exception {
		logger.info("Requesting for getLookUpDatas");
		logger.info("lookUpTable : {}", vo.getLookUpTableName());
		logger.info("lookUpCode : {}", vo.getLookUpTableColumnName());
		return commonService.getLookUpDatas(vo.getLookUpTableName(), vo.getLookUpTableColumnName());
	}

	@PostMapping(value = "/fetchHelpTexts")
	public String fetchHelpTexts(@RequestBody CommonVO vo, HttpServletRequest request, HttpServletResponse response) {
		logger.info("Requesting for fetchHelpTexts");
		return generalInformationService.fetchHelpText(vo);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return errors;
	}

	@PostMapping(value = "/findResearchTypeArea", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<ResearchTypeArea> findResearchTypeArea(@RequestBody CommonVO vo) {
		logger.info("Requesting for Research Area");
		logger.info("searchString : {}" , vo.getSearchString());
		return commonService.findResearchTypeArea(vo);
	}

	@PostMapping(value = "/findResearchTypeSubArea", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<ResearchTypeSubArea> findResearchTypeSubArea(@RequestBody CommonVO vo) {
		logger.info("Requesting for Research Sub Area");
		logger.info("searchString : {}" , vo.getSearchString());
		logger.info("researchAreaCode : {}" , vo.getResearchTypeAreaCode());
		return commonService.findResearchTypeSubArea(vo);
	}

	@GetMapping(value = {"/getModulesConfiguration","/getModulesConfiguration/{moduleCode}"})
	public String getModulesConfiguration(@PathVariable(value = "moduleCode", required = false) final String moduleCode) {
		logger.info("Request for getModulesConfiguration");
		return generalInformationService.getModulesConfiguration(moduleCode);
	}

	@PostMapping( "/letterTemplate")
	public ResponseEntity<Object> getAllLetterTemplateTypes(@RequestBody CommonVO vo) {
		return commonService.getAllLetterTemplateTypes(vo);
	}

	@GetMapping(value = "/getLogstashStatus")
	public String getLogstashStatus() throws IOException {
		logger.info("Requesting for getLogstashStatus");
		return commonService.getLogstashStatus();
	}

	@GetMapping(value = "/bulkLogstashSync/{indexName}")
	public String callTheShell(@PathVariable(value = "indexName", required = true) final String indexName) throws IOException {
		logger.info("Requesting for bulkLogstashSync");
		return commonService.bulkLogstashSync(indexName);
	}

	@GetMapping(value = "/queueMatrixDetails")
	public Map<String, Map<String, List<Datapoint>>> queueMatrixDetails() {
	return commonService.queueMatrixDetails();
	}

	@PostMapping(value = "/elasticErrorDetails", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getElasticErrorDetails(HttpServletRequest request, HttpServletResponse response,
            @RequestBody ApplicationExceptionVO applicationExceptionVO) throws Exception {
        return applicationExceptionService.getElasticErrorDetails(applicationExceptionVO);
    }

	@PostMapping(value = "/getAllUnits")
	public List<Unit> getAllUnits(@RequestBody CommonVO vo, HttpServletRequest request, HttpServletResponse response) {
		logger.info("Requesting for getting all units");
		logger.info("searchString : {}", vo.getSearchString());
		return commonService.getAllUnits(vo.getSearchString());
	}
}
