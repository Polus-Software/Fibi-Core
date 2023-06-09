package com.polus.core.auditreport.export.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.polus.core.vo.CommonVO;

@Service
public interface ExportService {

	public XSSFWorkbook getXSSFWorkbookForAdministrativeReport(CommonVO vo);

}
