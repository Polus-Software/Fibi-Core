package com.polus.core.externalreviewer.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.polus.core.externalreviewer.pojo.CoiWithPerson;
import com.polus.core.externalreviewer.pojo.ExtReviewerAcademicArea;
import com.polus.core.externalreviewer.pojo.ExtReviewerAcademicRank;
import com.polus.core.externalreviewer.pojo.ExtReviewerAcademicSubArea;
import com.polus.core.externalreviewer.pojo.ExtReviewerAffiliation;
import com.polus.core.externalreviewer.pojo.ExtReviewerAttachment;
import com.polus.core.externalreviewer.pojo.ExtReviewerCira;
import com.polus.core.externalreviewer.pojo.ExtReviewerOriginality;
import com.polus.core.externalreviewer.pojo.ExtReviewerThoroughness;
import com.polus.core.externalreviewer.pojo.ExternalReviewer;
import com.polus.core.externalreviewer.pojo.ExternalReviewerAttachmentType;
import com.polus.core.externalreviewer.pojo.ExternalReviewerExt;
import com.polus.core.externalreviewer.pojo.ExternalReviewerRights;
import com.polus.core.externalreviewer.pojo.ExternalReviewerSpecialization;
import com.polus.core.externalreviewer.pojo.ReviewerRights;
import com.polus.core.pojo.Country;

public class ExternalReviewerVo {
	
	private ExternalReviewer extReviewer;
	
	private ExternalReviewerExt externalReviewerExt;
	
	private ExternalReviewerRights externalReviewerRight;

    private Integer extReviewerId;
	
	private String message;
	
	private Integer currentPage;
	
	private Integer pageNumber;
	
	private Integer totalExtReviewer;
	
	private String property1;
	
	private String property2;

	private String property3;

	private String property4;

	private String property5;
	
	private String property6;
	
	private String property7;
	
	private String property8;
	
	private String property9;
	
	private List<String> property10;
	
	private List<String> property11;
	
	private String property12;

	private List<String> property13;
	
	private List<String> property14;
	
	private List<String> property15;

	private List<ExternalReviewer> extReviewers;
	
	private List<ExternalReviewerSpecialization> externalReviewerSpecializations;
	
	private List<ExternalReviewerAttachmentType> externalReviewerAttachmentType;
	
	private List<ExtReviewerAcademicArea> extReviewerAcademicArea;
	
	private List<ExtReviewerAcademicRank> extReviewerAcademicRank;
	
	private List<ExtReviewerAffiliation> extReviewerAffiliation;
	
	private List<ExtReviewerCira> extReviewerCira;
	
	private List<ExtReviewerOriginality> extReviewerOriginality;
	
	private List<ReviewerRights> reviewerRights;
	
	private List<Country> country;
	
	private List<ExtReviewerThoroughness> extReviewerThoroughness;
	
	private List<ExtReviewerAttachment> extReviewerAttachments;
	
	private ExtReviewerAttachment extReviewerAttachment;
	
	private String reverse;
	
	private Map<String, String> sort = new HashMap<>();
	
	private Integer extReviewerAttachmentId;
	
	private String searchString;
	
	private List<ExtReviewerAcademicSubArea> extReviewerAcademicSubArea;

	private String specialismKeyword;

	private String affiliationInstitution;

	private List<CoiWithPerson> coiWithPersons;

	public ExternalReviewer getExtReviewer() {
		return extReviewer;
	}

	public void setExtReviewer(ExternalReviewer extReviewer) {
		this.extReviewer = extReviewer;
	}

	public Integer getExtReviewerId() {
		return extReviewerId;
	}

	public void setExtReviewerId(Integer extReviewerId) {
		this.extReviewerId = extReviewerId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getTotalExtReviewer() {
		return totalExtReviewer;
	}

	public void setTotalExtReviewer(Integer totalExtReviewer) {
		this.totalExtReviewer = totalExtReviewer;
	}

	public String getProperty1() {
		return property1;
	}

	public void setProperty1(String property1) {
		this.property1 = property1;
	}

	public String getProperty2() {
		return property2;
	}

	public void setProperty2(String property2) {
		this.property2 = property2;
	}

	public String getProperty3() {
		return property3;
	}

	public void setProperty3(String property3) {
		this.property3 = property3;
	}

	public String getProperty4() {
		return property4;
	}

	public void setProperty4(String property4) {
		this.property4 = property4;
	}

	public String getProperty6() {
		return property6;
	}

	public void setProperty6(String property6) {
		this.property6 = property6;
	}

	public List<ExternalReviewer> getExtReviewers() {
		return extReviewers;
	}

	public void setExtReviewers(List<ExternalReviewer> extReviewers) {
		this.extReviewers = extReviewers;
	}

	public ExternalReviewerExt getExternalReviewerExt() {
		return externalReviewerExt;
	}

	public void setExternalReviewerExt(ExternalReviewerExt externalReviewerExt) {
		this.externalReviewerExt = externalReviewerExt;
	}

	public ExternalReviewerRights getExternalReviewerRight() {
		return externalReviewerRight;
	}

	public void setExternalReviewerRight(ExternalReviewerRights externalReviewerRight) {
		this.externalReviewerRight = externalReviewerRight;
	}

	public List<ExternalReviewerSpecialization> getExternalReviewerSpecializations() {
		return externalReviewerSpecializations;
	}

	public void setExternalReviewerSpecializations(List<ExternalReviewerSpecialization> externalReviewerSpecializations) {
		this.externalReviewerSpecializations = externalReviewerSpecializations;
	}

	public String getProperty9() {
		return property9;
	}

	public void setProperty9(String property9) {
		this.property9 = property9;
	}

	public List<String> getProperty10() {
		return property10;
	}

	public void setProperty10(List<String> property10) {
		this.property10 = property10;
	}

	public List<String> getProperty11() {
		return property11;
	}

	public void setProperty11(List<String> property11) {
		this.property11 = property11;
	}

	public String getProperty12() {
		return property12;
	}

	public void setProperty12(String property12) {
		this.property12 = property12;
	}

	public String getProperty5() {
		return property5;
	}

	public void setProperty5(String property5) {
		this.property5 = property5;
	}

	public String getProperty7() {
		return property7;
	}

	public void setProperty7(String property7) {
		this.property7 = property7;
	}

	public String getProperty8() {
		return property8;
	}

	public void setProperty8(String property8) {
		this.property8 = property8;
	}

	public String getReverse() {
		return reverse;
	}

	public void setReverse(String reverse) {
		this.reverse = reverse;
	}

	public Map<String, String> getSort() {
		return sort;
	}

	public void setSort(Map<String, String> sort) {
		this.sort = sort;
	}

	public List<ExternalReviewerAttachmentType> getExternalReviewerAttachmentType() {
		return externalReviewerAttachmentType;
	}

	public void setExternalReviewerAttachmentType(List<ExternalReviewerAttachmentType> externalReviewerAttachmentType) {
		this.externalReviewerAttachmentType = externalReviewerAttachmentType;
	}

	public List<ExtReviewerAcademicArea> getExtReviewerAcademicArea() {
		return extReviewerAcademicArea;
	}

	public void setExtReviewerAcademicArea(List<ExtReviewerAcademicArea> extReviewerAcademicArea) {
		this.extReviewerAcademicArea = extReviewerAcademicArea;
	}

	public List<ExtReviewerAcademicRank> getExtReviewerAcademicRank() {
		return extReviewerAcademicRank;
	}

	public void setExtReviewerAcademicRank(List<ExtReviewerAcademicRank> extReviewerAcademicRank) {
		this.extReviewerAcademicRank = extReviewerAcademicRank;
	}

	public List<ExtReviewerCira> getExtReviewerCira() {
		return extReviewerCira;
	}

	public void setExtReviewerCira(List<ExtReviewerCira> extReviewerCira) {
		this.extReviewerCira = extReviewerCira;
	}

	public List<ExtReviewerOriginality> getExtReviewerOriginality() {
		return extReviewerOriginality;
	}

	public void setExtReviewerOriginality(List<ExtReviewerOriginality> extReviewerOriginality) {
		this.extReviewerOriginality = extReviewerOriginality;
	}

	public List<ReviewerRights> getReviewerRights() {
		return reviewerRights;
	}

	public void setReviewerRights(List<ReviewerRights> reviewerRights) {
		this.reviewerRights = reviewerRights;
	}

	public List<ExtReviewerThoroughness> getExtReviewerThoroughness() {
		return extReviewerThoroughness;
	}

	public void setExtReviewerThoroughness(List<ExtReviewerThoroughness> extReviewerThoroughness) {
		this.extReviewerThoroughness = extReviewerThoroughness;
	}

	public List<Country> getCountry() {
		return country;
	}

	public void setCountry(List<Country> country) {
		this.country = country;
	}

	public List<ExtReviewerAttachment> getExtReviewerAttachments() {
		return extReviewerAttachments;
	}

	public void setExtReviewerAttachments(List<ExtReviewerAttachment> extReviewerAttachments) {
		this.extReviewerAttachments = extReviewerAttachments;
	}

	public Integer getExtReviewerAttachmentId() {
		return extReviewerAttachmentId;
	}

	public void setExtReviewerAttachmentId(Integer extReviewerAttachmentId) {
		this.extReviewerAttachmentId = extReviewerAttachmentId;
	}

	public ExtReviewerAttachment getExtReviewerAttachment() {
		return extReviewerAttachment;
	}

	public void setExtReviewerAttachment(ExtReviewerAttachment extReviewerAttachment) {
		this.extReviewerAttachment = extReviewerAttachment;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public List<ExtReviewerAcademicSubArea> getExtReviewerAcademicSubArea() {
		return extReviewerAcademicSubArea;
	}

	public void setExtReviewerAcademicSubArea(List<ExtReviewerAcademicSubArea> extReviewerAcademicSubArea) {
		this.extReviewerAcademicSubArea = extReviewerAcademicSubArea;
	}

	public String getSpecialismKeyword() {
		return specialismKeyword;
	}

	public void setSpecialismKeyword(String specialismKeyword) {
		this.specialismKeyword = specialismKeyword;
	}

	public List<String> getProperty13() {
		return property13;
	}

	public void setProperty13(List<String> property13) {
		this.property13 = property13;
	}

	public List<String> getProperty14() {
		return property14;
	}

	public void setProperty14(List<String> property14) {
		this.property14 = property14;
	}

	public String getAffiliationInstitution() {
		return affiliationInstitution;
	}

	public void setAffiliationInstitution(String affiliationInstitution) {
		this.affiliationInstitution = affiliationInstitution;
	}

	public List<CoiWithPerson> getCoiWithPersons() {
		return coiWithPersons;
	}

	public void setCoiWithPersons(List<CoiWithPerson> coiWithPersons) {
		this.coiWithPersons = coiWithPersons;
	}

	public List<ExtReviewerAffiliation> getExtReviewerAffiliation() {
		return extReviewerAffiliation;
	}

	public void setExtReviewerAffiliation(List<ExtReviewerAffiliation> extReviewerAffiliation) {
		this.extReviewerAffiliation = extReviewerAffiliation;
	}

	public List<String> getProperty15() {
		return property15;
	}

	public void setProperty15(List<String> property15) {
		this.property15 = property15;
	}

}
