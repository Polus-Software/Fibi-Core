package com.polus.core.auditreport.export.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.auditreport.export.dao.ExportDao;
import com.polus.core.auditreport.pojo.AuditReportType;
import com.polus.core.auditreport.repository.AuditReportTypeRepository;
import com.polus.core.common.service.CommonService;
import com.polus.core.vo.CommonVO;

@Transactional
@Service(value = "exportService")
public class ExportServiceImpl implements ExportService {

	protected static Logger logger = LogManager.getLogger(ExportServiceImpl.class.getName());

	@Autowired
	private ExportDao exportDao;

	@Autowired
	private AuditReportTypeRepository auditReportTypeRepository;

	@Autowired
	private CommonService commonService;

	@Override
	public XSSFWorkbook getXSSFWorkbookForAdministrativeReport(CommonVO vo) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		AuditReportType auditReportType = auditReportTypeRepository.findAuditReportTypeByReportType(vo.getType());
		vo.setDocumentHeading(auditReportType != null ? auditReportType.getReportName() : "Audit Report");
		XSSFSheet sheet = workbook.createSheet(vo.getDocumentHeading());
		commonService.addDetailsInHeader(workbook, sheet);
		List<Object[]> reportData = exportDao.administrativeDetails(vo);
		Object[] headers = reportData.get(0);
		reportData.remove(0);
		commonService.prepareExcelSheet(reportData, sheet, headers, workbook, vo);
		return workbook;
	}

}
