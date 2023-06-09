package com.polus.core.adminportal.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.polus.core.adminportal.dto.SponsorHierarchyDto;
import com.polus.core.adminportal.pojo.InstituteLARate;
import com.polus.core.adminportal.pojo.InstituteRate;
import com.polus.core.adminportal.service.SponsorHierarchyService;
import com.polus.core.adminportal.service.UnitHierarchyService;
import com.polus.core.adminportal.vo.RateLaVO;
import com.polus.core.adminportal.vo.RateVO;
import com.polus.core.adminportal.vo.UnitHierarchyVO;
import com.polus.core.adminportal.vo.UnitVO;
import com.polus.core.common.dto.ResponseData;
import com.polus.core.constants.Constants;
import com.polus.core.vo.SearchResult;

@RestController
public class AdminPortalContoller {
	protected static Logger logger = LogManager.getLogger(AdminPortalContoller.class.getName());

	@Autowired
	@Qualifier(value = "unitHierarchyService")
	private UnitHierarchyService unitHierarchyService;

	@SuppressWarnings("unused")
	@Autowired
	private HibernateTemplate hibernateTemplate;

	@Autowired
	SponsorHierarchyService sponsorHierarchyService;

	@RequestMapping(value = "/getUnitHierarchy", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getUnitHierarchy(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Requesting for get unit hierarchy");
		 logger.info("Root unit number :" + Constants.ROOT_UNIT);
         return unitHierarchyService.getUnitHierarchy(Constants.ROOT_UNIT);

	}
	@RequestMapping(value = "/getUnitsList", method = RequestMethod.GET)
	public String getUnitsList(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Requesting for get unit hierarchy search result");
		return unitHierarchyService.getUnitsList();
	}
	@RequestMapping(value = "/getUnitDetail", method = RequestMethod.POST)
	public String getUnitDetails(@RequestBody UnitHierarchyVO vo, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("Requesting for get full details of a single unit");
		return unitHierarchyService.getUnitDetails(vo);
	}
	@RequestMapping(value = "/addNewUnit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String addNewUnit(@RequestBody UnitVO unitVO, HttpServletRequest request, HttpServletResponse response) {
		logger.info("unitObject :" + unitVO);
		logger.info("Requesting for add new unit");
		return unitHierarchyService.addNewUnit(unitVO);
	}
	@RequestMapping(value = "/addRate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String addRate(@RequestBody RateVO rateVO, HttpServletRequest request, HttpServletResponse response) {
		logger.info("addRate :" + "addRate API : " + rateVO);
		String campusFlag = rateVO.getCampusFlag();
		if (campusFlag != null && campusFlag.equalsIgnoreCase("BOTH")) {
			InstituteRate instituteRateOnCampus = rateVO.getInstituteRate();
			instituteRateOnCampus.setOnOffCampusFlag("N");
			rateVO.setInstituteRate(instituteRateOnCampus);
			unitHierarchyService.addInstituteRate(rateVO);
			InstituteRate instituteRateOffCampus = rateVO.getInstituteRate();
			instituteRateOffCampus.setId(null);
			instituteRateOffCampus.setOnOffCampusFlag("Y");
			rateVO.setInstituteRate(instituteRateOffCampus);
			return unitHierarchyService.addInstituteRate(rateVO);
		}
		return unitHierarchyService.addInstituteRate(rateVO);
	}
	@RequestMapping(value = "/getRates", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getRates(@RequestBody RateVO rateVo, HttpServletRequest request, HttpServletResponse response) {
		logger.info("Requesting for get rates");
		return unitHierarchyService.getRates(rateVo);
	}
	@RequestMapping(value = "/deleteRate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String deleteRate(@RequestBody RateVO rateVo, HttpServletRequest request, HttpServletResponse response) {
		logger.info("RateVO :" + rateVo);
		logger.info("Requesting for delete rates");
		return unitHierarchyService.deleteRate(rateVo);
	}
	@RequestMapping(value = "/deleteUnitAdministrator", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String deleteRate(@RequestBody UnitVO unitVO, HttpServletRequest request, HttpServletResponse response) {
		logger.info("UnitVO :" + unitVO);
		logger.info("Requesting for delete Unit Administrator");
		return unitHierarchyService.deleteUnitAdministrator(unitVO);
	}
	@RequestMapping(value = "/addLARate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String addLARate(@RequestBody RateLaVO rateLaVO, HttpServletRequest request, HttpServletResponse response) {
		logger.info("addLARates :" + "addLARate API : ");
		String campusFlag = rateLaVO.getCampusFlag();
		if (campusFlag != null && campusFlag.equalsIgnoreCase("BOTH")) {
			InstituteLARate instituteRateOnCampus = rateLaVO.getInstituteLARate();
			instituteRateOnCampus.setOnOffCampusFlag("N");
			rateLaVO.setInstituteLARate(instituteRateOnCampus);
			unitHierarchyService.addInstituteLARate(rateLaVO);
			InstituteLARate instituteRateOffCampus = rateLaVO.getInstituteLARate();
			instituteRateOffCampus.setOnOffCampusFlag("F");
			rateLaVO.setInstituteLARate(instituteRateOffCampus);
			return unitHierarchyService.addInstituteLARate(rateLaVO);
		}
		return unitHierarchyService.addInstituteLARate(rateLaVO);
	}
	@RequestMapping(value = "/getLARates", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getLARates(@RequestBody RateLaVO rateLaVO, HttpServletRequest request, HttpServletResponse response) {
		logger.info("Requesting for get LARates");
		return unitHierarchyService.getLARates(rateLaVO);
	}
	@RequestMapping(value = "/deleteLARate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String deleteLARate(@RequestBody RateLaVO rateLaVO, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("RateVO :" + rateLaVO);
		logger.info("Requesting for delete la rates");
		return unitHierarchyService.deleteLARate(rateLaVO);
	}

	@PostMapping("/sponsorHierarchy")
	public ResponseEntity<ResponseData> createSponsorHierarchy(@RequestBody SponsorHierarchyDto sponsorHierarchy) {
		return sponsorHierarchyService.createSponsorHierarchy(sponsorHierarchy);
	}

	@PostMapping( "/sponsorHierarchy/hierarchy")
	public ResponseEntity<ResponseData> getSponsorHierarchy(@RequestBody SearchResult searchResult) {
		return sponsorHierarchyService.getSponsorHierarchy(searchResult);
	}

	@PutMapping( "/sponsorHierarchy")
	public ResponseEntity<ResponseData> updateSponsorHierarchy(@RequestBody SponsorHierarchyDto sponsorHierarchy) {
		return sponsorHierarchyService.updateSponsorHierarchy(sponsorHierarchy);
	}

	@DeleteMapping("/sponsorHierarchy/{sponsorGroupId}")
	public ResponseEntity<ResponseData> deleteSponsorHierarchy(@PathVariable("sponsorGroupId") Integer sponsorGroupId) {
		return sponsorHierarchyService.deleteSponsorHierarchy(sponsorGroupId);
	}

	@PostMapping("/sponsorHierarchy/{rootGroupId}/sponsors")
	public ResponseEntity<Object> getNotAddedSponsorsInSH(@PathVariable("rootGroupId") Integer rootGroupId,
																@RequestBody Map<String, String> voObj) {
		return sponsorHierarchyService.getNotAddedSponsorsInSH(rootGroupId, voObj);
	}

	@GetMapping( "/sponsorHierarchies")
	public ResponseEntity<ResponseData> getSponsorHierarchies() {
		return sponsorHierarchyService.getSponsorHierarchies();
	}

	@PostMapping( "/sponsorHierarchy/sponsorGroups")
	public ResponseEntity<Object> getAllGroups(@RequestBody(required = false) Map<String, String> searchWord) {
		return sponsorHierarchyService.getAllSponsorHierarchyGroups(searchWord.get("searchString"));
	}
}
