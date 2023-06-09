package com.polus.core.useractivitylog.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.common.dao.CommonDao;
import com.polus.core.common.service.CommonService;
import com.polus.core.person.pojo.Person;
import com.polus.core.pojo.Unit;
import com.polus.core.roles.dao.AuthorizationServiceDao;
import com.polus.core.useractivitylog.dao.UserActivityLogDao;
import com.polus.core.useractivitylog.pojo.PersonLoginDetail;
import com.polus.core.useractivitylog.vo.UserActivityLogVO;
import com.polus.core.vo.CommonVO;

@Transactional
@Service(value = "userActivityLogService")
public class UserActivityLogServiceImpl implements UserActivityLogService {

	protected static Logger logger = LogManager.getLogger(UserActivityLogServiceImpl.class.getName());
	private static final String TWELVE_HOUR_DATE_FORMAT = "dd/MM/yyyy hh:mm:ss aa";
	private static final String CRON_JOB_TIMEZONE = "Asia/Singapore";

	@Autowired
	private UserActivityLogDao userActivityLogDao;

	@Autowired
	private AuthorizationServiceDao authorizationServiceDao;

	@Autowired
	private CommonDao commonDao;

	@Autowired
	private CommonService commonService;

	@Override
	public String getUnitByPersonId(UserActivityLogVO vo, String personId) {
		List<Unit> unitLists = null;
		List<String> systemRights = authorizationServiceDao.allSystemLevelPermission(personId);
		if (systemRights != null && !systemRights.isEmpty()) {
			unitLists = userActivityLogDao.getUnitsListByPersonIdAndRights(vo.getPersonId(), systemRights);
		}
		return commonDao.convertObjectToJSON(unitLists);
	}

	@Override
	public String fetchPersonLoginDetails(UserActivityLogVO userActivityLogVO) {
		userActivityLogDao.getPersonLoginDetails(userActivityLogVO);
		if(userActivityLogVO.getPersonLoginDetails() != null && !userActivityLogVO.getPersonLoginDetails().isEmpty()) {
			getUnitOfPerson(userActivityLogVO.getPersonLoginDetails());
		}
		return commonDao.convertObjectToJSON(userActivityLogVO);
	}

	private void getUnitOfPerson(List<PersonLoginDetail> persons) {
		Set<String> personIds = persons.stream().map(PersonLoginDetail::getPersonId).collect(Collectors.toSet());
		if (!personIds.isEmpty()) {
			List<Person> personDetails = commonDao.getPersonDetailByPersonId(new ArrayList<>(personIds));
			Map<String, Unit> collect = personDetails.stream().collect(Collectors.toMap(person -> person.getPersonId(), person -> person.getUnit() != null ? person.getUnit() : new Unit()));
			persons.stream().filter(item -> item.getPersonId() != null).filter(item -> collect.containsKey(item.getPersonId())).forEach(item -> item.setUnit(collect.get(item.getPersonId())));
		}
	}

	@Override
	public ResponseEntity<byte[]> exportUserActivityDatas(UserActivityLogVO userActivityLogVO) {
		ResponseEntity<byte[]> attachmentData = null;
		try {
			CommonVO vo = new CommonVO();
			vo.setExportType("xlsx");
			vo.setDocumentHeading(userActivityLogVO.getDocumentHeading());
			XSSFWorkbook workbook = new XSSFWorkbook();
			List<Object[]> dashboardData = new ArrayList<>();
			userActivityLogVO = userActivityLogDao.getPersonLoginDetails(userActivityLogVO);
			if(userActivityLogVO.getPersonLoginDetails() != null && !userActivityLogVO.getPersonLoginDetails().isEmpty()){
				getUnitOfPerson(userActivityLogVO.getPersonLoginDetails());
				List<PersonLoginDetail> personLoginDetails = userActivityLogVO.getPersonLoginDetails();
				for (PersonLoginDetail personLoginDetail: personLoginDetails) {
					Object[] object = new Object[5];
					object[0] = personLoginDetail.getPersonId();
					object[1] = personLoginDetail.getFullName();
					object[2] = personLoginDetail.getUnit().getUnitName();
					object[3] = personLoginDetail.getLoginStatus();
					DateFormat dateFormat = new SimpleDateFormat(TWELVE_HOUR_DATE_FORMAT);
					dateFormat.setTimeZone(TimeZone.getTimeZone(CRON_JOB_TIMEZONE));
					object[4] = dateFormat.format(personLoginDetail.getUpdateTimestamp());
					dashboardData.add(object);
				}
			}
			XSSFSheet sheet = workbook.createSheet("User Details");
			commonService.addDetailsInHeader(workbook,sheet);
			Object[] tableHeadingRow = { "Id#", "Name", "Unit", "Activity" , "Date And Time" };
			commonService.prepareExcelSheet(dashboardData, sheet, tableHeadingRow, workbook, vo);
			attachmentData = commonService.getResponseEntityForDownload(vo, workbook);
		} catch (Exception e) {
			logger.error(e);
		}
		return attachmentData;
	}

	@Override
	public void savePersonLoginDetails(String personID, String fullName, String loginStatus, String userName) {
		PersonLoginDetail personLoginDetail = new PersonLoginDetail();
		personLoginDetail.setPersonId(personID);
		personLoginDetail.setLoginStatus(loginStatus);
		personLoginDetail.setFullName(fullName);
		personLoginDetail.setUpdateTimestamp(commonDao.getCurrentTimestamp());
		personLoginDetail.setUpdateUser(userName);
		userActivityLogDao.savePersonLoginDetail(personLoginDetail);
	}

}
