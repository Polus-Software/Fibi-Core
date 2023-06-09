package com.polus.core.auditreport.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.polus.core.auditreport.export.service.ExportService;
import com.polus.core.auditreport.service.AuditReportService;
import com.polus.core.common.service.CommonService;
import com.polus.core.vo.CommonVO;

@RestController
public class AuditReportController {

	protected static Logger logger = LogManager.getLogger(AuditReportController.class.getName());

	@Autowired
	@Qualifier(value = "exportService")
	private ExportService exportService;

	@Autowired
	@Qualifier(value = "auditReportService")
	private AuditReportService auditReportService;

	@Autowired
	@Qualifier(value = "commonService")
	private CommonService commonService;

	@PostMapping("/exportAuditReport")
	public ResponseEntity<byte[]> exportAuditReport(@RequestBody CommonVO vo) throws Exception {
		logger.info("Request for Administrative Report type {} ", vo.getType());
		vo.setExportType("xlsx");
		XSSFWorkbook workbook = exportService.getXSSFWorkbookForAdministrativeReport(vo);
		return commonService.getResponseEntityForDownload(vo, workbook);
	}

	@GetMapping("/auditReportTypes")
	public String getAuditReportTypes() {
		return auditReportService.getAuditReportTypes();
	}

}
