package com.polus.core.useractivitylog.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.polus.core.security.AuthenticatedUser;
import com.polus.core.useractivitylog.service.UserActivityLogService;
import com.polus.core.useractivitylog.vo.UserActivityLogVO;

@RestController
public class UserActivityLogController {

	protected static Logger logger = LogManager.getLogger(UserActivityLogController.class.getName());

	@Autowired
	@Qualifier(value = "userActivityLogService")
	private UserActivityLogService userActivityLogService;

	@PostMapping(value = "/getPersonLoginDetails")
	public String getPersonLoginDetails(@RequestBody UserActivityLogVO vo, HttpServletRequest request) {
		logger.info("Requesting for getPersonLoginDetails");
		return userActivityLogService.fetchPersonLoginDetails(vo);
	}

	@PostMapping(value = "/exportUserActivityDatas")
	public ResponseEntity<byte[]> exportUserActivityDatas(HttpServletRequest request, @RequestBody UserActivityLogVO vo)
			throws Exception {
		logger.info("Requesting for exportUserActivityDatas");
		return userActivityLogService.exportUserActivityDatas(vo);
	}

	@PostMapping(value = "/getHomeUnitDetails")
	public String getHomeUnitDetails(@RequestBody UserActivityLogVO vo, HttpServletRequest request) {
		logger.info("Requesting for getHomeUnitDetails");
		logger.info("Login personId: {}", AuthenticatedUser.getLoginPersonId());
		return userActivityLogService.getUnitByPersonId(vo, AuthenticatedUser.getLoginPersonId());
	}

}
