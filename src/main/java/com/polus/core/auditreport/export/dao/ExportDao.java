package com.polus.core.auditreport.export.dao;

import java.util.List;

import org.springframework.stereotype.Service;

import com.polus.core.vo.CommonVO;

@Service
public interface ExportDao {

	public List<Object[]> getExportDataOfPersonForDownload(CommonVO vo, List<Object[]> personData);

	public List<Object[]> getDataOfRolodexForDownload(CommonVO vo, List<Object[]> rolodexData);

	public List<Object[]> administrativeDetails(CommonVO vo);

}
