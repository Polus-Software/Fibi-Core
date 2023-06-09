package com.polus.core.rolodex.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.polus.core.pojo.Country;
import com.polus.core.rolodex.service.RolodexService;
import com.polus.core.rolodex.vo.RolodexSearchResult;
import com.polus.core.rolodex.vo.RolodexVO;
import com.polus.core.vo.CommonVO;

@RestController
public class RolodexController {

	protected static Logger logger = LogManager.getLogger(RolodexController.class.getName());

	@Autowired
	private RolodexService rolodexService;

	@PostMapping(value = "/saveOrUpdateRolodex")
	public String createRolodex(@RequestBody RolodexVO vo, HttpServletRequest request, HttpServletResponse response) {
		logger.info("Request for saveOrUpdateRolodex");
		return rolodexService.saveOrUpdateRolodex(vo);
	}

	@PostMapping(value = "/findRolodex")
	public List<RolodexSearchResult> getNext(@RequestBody CommonVO vo) {
		logger.info("Requesting for /findRolodex");
		logger.info("searchString : {}" , vo.getSearchString());
		return rolodexService.findRolodex(vo);
	}

	@PostMapping(value = "/getRolodexDetailById")
	public String getPersonDetailById(@RequestBody RolodexVO vo, HttpServletRequest request, HttpServletResponse response) {
		logger.info("Request for getRolodexDetailById");
		logger.info("rolodexId : {}" , vo.getRolodexId());
		return rolodexService.getRolodexDetailById(vo);
	}

	@PostMapping(value = "/getAllRolodexes")
	public String getAllRolodexes(@RequestBody RolodexVO vo, HttpServletRequest request, HttpServletResponse response) {
		logger.info("Request for getAllRolodexes");
		return rolodexService.getAllRolodexes(vo);
	}

//	@PostMapping(value = "/exportRolodexDatas")
//	public ResponseEntity<byte[]> exportRolodexDatas(HttpServletRequest request, @RequestBody CommonVO vo) throws Exception {
//		XSSFWorkbook workbook = dashboardService.getXSSFWorkbookForDashboard(vo);
//		return dashboardService.getResponseEntityForDownload(vo, workbook);
//	}

	@PostMapping(value = "/findCountry")
	public List<Country> findCountry(@RequestBody CommonVO vo, HttpServletRequest request, HttpServletResponse response) {
		logger.info("Requesting for /findCountry");
		logger.info("searchString : {}" , vo.getSearchString());
		return rolodexService.findCountryList(vo);
	}

}
