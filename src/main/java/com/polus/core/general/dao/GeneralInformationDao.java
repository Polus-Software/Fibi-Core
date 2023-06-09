package com.polus.core.general.dao;

import java.math.BigDecimal;
import java.util.List;

import com.polus.core.general.pojo.DynamicModuleConfig;
import com.polus.core.general.pojo.DynamicSectionConfig;
import com.polus.core.general.pojo.HelpText;
import com.polus.core.pojo.Sponsor;
import com.polus.core.pojo.SponsorType;
import com.polus.core.vo.CommonVO;

public interface GeneralInformationDao {

	public Sponsor fetchSponsorData(String sponsorCode);

	public Integer insertSponsorData(Sponsor sponsor);

	public void updateSponsorData(Sponsor sponsor);

	public void deleteSponsorData(Sponsor sponsor);

	public List<SponsorType> fetchSponsorTypeLookup();

	public String nextSponsorCode();

	public BigDecimal getMonthlySalary(String personId);

	public BigDecimal getMonthySalaryForJobCode(String jobCode);

	public String syncPersonRole();

	public CommonVO getAllSponsors(CommonVO vo);

	public boolean evaluateRule(Integer moduleCode, Integer subModuleCode, String moduleItemKey, Integer ruleId, String logginPersonId, String updateUser, String subModuleItemKey);

	public HelpText gethelpText(Integer moduleCode, Integer sectionTypeCode);

	public List<HelpText> gethelpTextBasedOnHelpTextId(Integer parentHelpTextId);
	
	public List<DynamicSectionConfig> getSectionConfiguration(String moduleCode);

	public List<DynamicModuleConfig> getModuleConfiguration();

}
