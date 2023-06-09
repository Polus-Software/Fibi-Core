package com.polus.core.auditlog.report.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.auditlog.report.dao.AuditReportDao;
import com.polus.core.auditlog.report.vo.AuditReportParam;
import com.polus.core.auditlog.vo.AuditLogInfo;
import com.polus.core.common.dao.CommonDao;
import com.polus.core.common.service.CommonService;
import com.polus.core.common.service.DateTimeService;
import com.polus.core.constants.Constants;
import com.polus.core.person.dao.PersonDao;
import com.polus.core.roles.dao.AuthorizationServiceDao;
import com.polus.core.security.AuthenticatedUser;
import com.polus.core.vo.CommonVO;

@Transactional
@Service
public class AuditReportServiceImpl implements AuditReportService {

	@Autowired
	private AuthorizationServiceDao authorizationServiceDao;

	@Autowired
	private AuditReportQueryBuilder auditReportQueryBuilder;

	@Autowired
	private AuditReportDao auditReportDao;

	@Autowired
	private CommonService commonService;

	@Autowired
	private CommonDao commonDao;
	
	@Autowired
	private PersonDao personDao;

	@Autowired
	private DateTimeService dateTimeService;

	@Override
	public boolean isAuthorized(AuditReportParam auditReportParamVO) {
		boolean isAuthorized = false;
		List<String> rightList = null;
		auditReportParamVO.setRequesterPersonId(AuthenticatedUser.getLoginPersonId());
		rightList = authorizationServiceDao.allSystemLevelPermission(auditReportParamVO.getRequesterPersonId());
		if (rightList.contains("VIEW_AUDIT_LOG")) {
			isAuthorized = true;
		}
		return isAuthorized;
	}

	@Override
	public ResponseEntity<byte[]> generateReport(AuditReportParam auditReportParamVO) throws ParseException {
		StringBuilder stringBuilder = new StringBuilder();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
		if (!auditReportParamVO.getPersonIds().isEmpty()) {
			auditReportQueryBuilder.setPersonIds(auditReportParamVO.getPersonIds());
			String personNamesString = String.join("|", auditReportParamVO.getPersonNames());
			stringBuilder.append("Person Name : ");
			stringBuilder.append(personNamesString);
			stringBuilder.append("\n");
		}
		if(!auditReportParamVO.getModuleNames().isEmpty()) {
			auditReportQueryBuilder.setModules(auditReportParamVO.getModuleNames());
			String modulesString = String.join("|", auditReportParamVO.getModuleDescriptions());
			stringBuilder.append("Module(s) : ");
			stringBuilder.append(modulesString);
			stringBuilder.append("\n");
		}
		if (auditReportParamVO.getActionFrom() != null) {
			auditReportQueryBuilder.setActionFrom(LocalDate.parse(auditReportParamVO.getActionFrom(), formatter));
			stringBuilder.append("Action From : ");
			stringBuilder.append(auditReportParamVO.getActionFrom());
			stringBuilder.append("\n");
		} else {
			auditReportQueryBuilder.setActionFrom(LocalDate.parse("2000-01-01", formatter));
			stringBuilder.append("Action From : ");
			stringBuilder.append(LocalDate.parse("2000-01-01", formatter));
			stringBuilder.append("\n");
		}
		if (auditReportParamVO.getActionTo() != null) {
			auditReportQueryBuilder.setActionTo(LocalDate.parse(auditReportParamVO.getActionTo(), formatter));
			stringBuilder.append("Action To : ");
			stringBuilder.append(auditReportParamVO.getActionTo());
			stringBuilder.append("\n");
		} else {
			auditReportQueryBuilder.setActionTo(LocalDate.parse("2999-12-31", formatter));
			stringBuilder.append("Action To : ");
			stringBuilder.append(LocalDate.parse("2999-12-31", formatter));
			stringBuilder.append("\n");
		}
		auditReportParamVO.setCriteria(stringBuilder.toString());
		auditReportQueryBuilder.buildSQL();
		List<AuditLogInfo> auditLogInfoList = auditReportDao.fetchReport();
		auditReportQueryBuilder.clearDataMembers();
		return exportAuditLogDatas(auditLogInfoList, auditReportParamVO, stringBuilder);
	}

	private ResponseEntity<byte[]> exportAuditLogDatas(List<AuditLogInfo> auditLogInfoList,
			AuditReportParam auditReportParamVO, StringBuilder criteriaList) {
		ResponseEntity<byte[]> attachmentData = null;
		try {
			CommonVO vo = new CommonVO();
			vo.setExportType("xlsx");
			commonDao.getCurrentTimestamp();
			vo.setDocumentHeading("Audit Log Report " + "(Generated by "
					+ personDao.getPersonPrimaryInformation(auditReportParamVO.getRequesterPersonId()).getFullName()
					+ " on " + dateTimeService.getCurrentSqlDate() + ")");
			XSSFWorkbook workbook = new XSSFWorkbook();
			List<Object[]> dashboardData = new ArrayList<>();
			for (AuditLogInfo auditLogInfo : auditLogInfoList) {
				Object[] object = new Object[10];
				object[0] = auditLogInfo.getLogId();
				object[1] = auditReportDao.fetchModuleDescriptionByModule(auditLogInfo.getModule());
				object[2] = auditLogInfo.getSubModule();
				object[3] = auditLogInfo.getModuleItemKey();
				object[4] = auditLogInfo.getActionPersonId();	
				object[5] = auditLogInfo.getUpdateUser();
				object[6] = personDao.getPersonPrimaryInformation(auditLogInfo.getActionPersonId()).getEmailAddress();
				if (auditLogInfo.getActionType().contains("I")) {
					object[7] = "Insert";
				} else if (auditLogInfo.getActionType().contains("U")) {
					object[7] = "Update";
				} else if (auditLogInfo.getActionType().contains("D")) {
					object[7] = "Delete";
				}
				object[8] = auditLogInfo.getChanges();
				DateFormat dateFormat = new SimpleDateFormat(Constants.TWELVE_HOUR_DATE_FORMAT);
				dateFormat.setTimeZone(TimeZone.getTimeZone(Constants.CRON_JOB_TIMEZONE));
				object[9] = dateFormat.format(auditLogInfo.getUpdateTimestamp());
				dashboardData.add(object);		
			}
			XSSFSheet sheet = workbook.createSheet("Audit Log Data");
			commonService.addDetailsInHeader(workbook, sheet);
			Object[] tableHeadingRow = { "Id#", "Module", "Sub Module", "Module Document ID",
					"Action Person's id", "Action Person's Name", "Action Person's email", "Action Type", "Changes/Module Details", "Action TimeStamp" };
			prepareExcelSheet(dashboardData, sheet, tableHeadingRow, workbook, vo, auditReportParamVO.getCriteria());
			attachmentData = commonService.getResponseEntityForDownload(vo, workbook);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attachmentData;
	}

	private void prepareExcelSheet(List<Object[]> dashboardData, XSSFSheet sheet, Object[] tableHeadingRow,
			XSSFWorkbook workbook, CommonVO vo, String criteria) {
		XSSFCellStyle tableBodyStyle = workbook.createCellStyle();
		String documentHeading = vo.getDocumentHeading();
		prepareExcelSheetHeader(sheet, tableHeadingRow, documentHeading, workbook, tableBodyStyle);
		XSSFCellStyle criteriaHeadStyle = (XSSFCellStyle) workbook.createCellStyle();
		XSSFFont tableHeadFont = (XSSFFont) workbook.createFont();
		tableHeadFont.setBold(true);
		tableHeadFont.setFontHeightInPoints((short) 10);
		criteriaHeadStyle.setFont(tableHeadFont);
		Row headerRow = sheet.createRow(1);
		Cell cellCriteriaHeader = headerRow.createCell(0);
		cellCriteriaHeader.setCellValue("Criteria Used In Report");
		cellCriteriaHeader.setCellStyle(criteriaHeadStyle);
		Row rowCriteria = sheet.createRow(2);
		Cell cellCriteria = rowCriteria.createCell(0);
		cellCriteria.setCellValue(criteria);
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		cellCriteria.setCellStyle(cellStyle);
		rowCriteria.setHeightInPoints(30);
		sheet.addMergedRegion(new CellRangeAddress(2, 5, 0, 20));
		XSSFFont tableBodyFont = workbook.createFont();
		tableBodyFont.setFontHeightInPoints((short) 10);
		cellStyle.setFont(tableBodyFont);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		int rowNumber = 7;
		sheet.setColumnWidth(8, 8000);
		for (Object[] objectArray : dashboardData) {
			Row row = sheet.createRow(rowNumber++);
			int cellNumber = 0;
			for (Object objectData : objectArray) {
				Cell cell = row.createCell(cellNumber++);
				cell.setCellStyle(tableBodyStyle);
				cell.setCellStyle(cellStyle);
				if (objectData instanceof String) {
					cell.setCellValue((String) objectData);
				} else if (objectData instanceof Integer)
					cell.setCellValue((Integer) objectData);
				else if (objectData instanceof BigInteger) {
					String stringValue = ((BigInteger) objectData).toString();
					cell.setCellValue((String) stringValue);
				} else if (objectData instanceof BigDecimal) {
					DecimalFormat decimalFormat = new DecimalFormat(Constants.NUMBER_FORMAT_WITH_DECIMAL);
					String stringValue = ((BigDecimal) objectData).toString();
					XSSFCellStyle headStyle = workbook.createCellStyle();
					headStyle.setAlignment(HorizontalAlignment.RIGHT);
					cell.setCellStyle(headStyle);
					cell.setCellValue(decimalFormat.format(new BigDecimal(stringValue)));
				} else if (objectData instanceof Date) {
					if (objectData != null) {
						Date date = (Date) objectData;
						String dateValue = commonService.convertDateFormatBasedOnTimeZone(date.getTime(),
								Constants.DEFAULT_DATE_FORMAT);
						cell.setCellValue((String) dateValue);
					}
				} else if (objectData == null) {
					cell.setCellValue((String) " ");
				}
			}
		}
		autoSizeColumns(workbook);
	}

	private XSSFWorkbook prepareExcelSheetHeader(XSSFSheet sheet, Object[] tableHeadingRow, String documentHeading,
			XSSFWorkbook workbook, XSSFCellStyle tableBodyStyle) {
		int headingCellNumber = 0;
		Row headerRow = sheet.createRow(0);
		Cell headingCell = headerRow.createCell(0);
		headingCell.setCellValue((String) documentHeading);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, tableHeadingRow.length - 1));
		XSSFFont headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 12);
		XSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setFont(headerFont);
		headingCell.setCellStyle(headerStyle);
		Row tableHeadRow = sheet.createRow(6);
		XSSFCellStyle tableHeadStyle = workbook.createCellStyle();
		tableHeadStyle.setBorderTop(BorderStyle.HAIR);
		tableHeadStyle.setBorderBottom(BorderStyle.HAIR);
		tableHeadStyle.setBorderLeft(BorderStyle.HAIR);
		tableHeadStyle.setBorderRight(BorderStyle.HAIR);
		tableHeadStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		tableHeadStyle.setAlignment(HorizontalAlignment.LEFT);
		tableHeadStyle.setWrapText(true);	
		XSSFFont tableHeadFont = workbook.createFont();
		tableHeadFont.setBold(true);
		tableHeadFont.setFontHeightInPoints((short) 10);
		tableHeadStyle.setFont(tableHeadFont);
		tableBodyStyle.setBorderTop(BorderStyle.HAIR);
		tableBodyStyle.setBorderBottom(BorderStyle.HAIR);
		tableBodyStyle.setBorderLeft(BorderStyle.HAIR);
		tableBodyStyle.setBorderRight(BorderStyle.HAIR);
		XSSFFont tableBodyFont = workbook.createFont();
		tableBodyFont.setFontHeightInPoints((short) 10);
		tableBodyStyle.setFont(tableBodyFont);
		for (Object heading : tableHeadingRow) {
			Cell cell = tableHeadRow.createCell(headingCellNumber++);
			cell.setCellValue((String) heading);
			cell.setCellStyle(tableHeadStyle);
			cell.setCellStyle(tableHeadStyle);
		}
		return workbook;
	}

	private void autoSizeColumns(XSSFWorkbook workbook) {
		int numberOfSheets = workbook.getNumberOfSheets();
		for (int i = 0; i < numberOfSheets; i++) {
			XSSFSheet sheet = workbook.getSheetAt(i);
			if (sheet.getPhysicalNumberOfRows() > 0) {
				Row row = sheet.getRow(6);
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					int columnIndex = cell.getColumnIndex();
					if(columnIndex==0||columnIndex==7||columnIndex==8) {
						continue;
					}
					sheet.autoSizeColumn(columnIndex);
				}
			}
		}
	}

}
