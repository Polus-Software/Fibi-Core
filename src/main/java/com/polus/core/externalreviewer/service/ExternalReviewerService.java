package com.polus.core.externalreviewer.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.polus.core.externalreviewer.pojo.ExtReviewerAffiliation;
import com.polus.core.externalreviewer.pojo.SpecialismKeyword;
import com.polus.core.externalreviewer.vo.ExternalReviewerVo;
import com.polus.core.vo.CommonVO;

@Transactional
@Service
public interface ExternalReviewerService {

	/**
	 * This method is used to save and update ExtReviewer details
	 * 
	 * @param vo
	 * @return ExtReviewer details with message
	 */
	public String saveOrUpdateReviewerDetails(ExternalReviewerVo vo);
	
	/**
	 * This method is used to save and update AdditionalDetails details
	 * 
	 * @param vo
	 * @return ExtReviewer details with message
	 */
	public String saveOrUpdateAdditionalDetails(ExternalReviewerVo vo);

	/**
	 * This method is used to save and update userAccess details
	 * 
	 * @param vo
	 * @return ExtReviewer details with message
	 */
	public String saveOrUpdateuserAccess(ExternalReviewerVo vo);
	
	/**
	 * This method is used to save External ReviewerAttachment details
	 * 
	 * @param vo
	 * @return ExtReviewer details with message
	 */
	public String addExtReviewerAttachment(MultipartFile[] files,  String formDataJSON);
	/**
	 * This method is used to get ExtReviewerDetail based on id
	 * 
	 * @param vo
	 * @return ExtReviewer details
	 */
	public String getExtReviewerDetailById(ExternalReviewerVo vo);

	/**
	 * This method is used to get all external Reviewer Details 
	 * 
	 * @param vo
	 * @return ExternalReviewerVo
	 */
	public String getAllExtReviewers(ExternalReviewerVo vo);

	/**
	 * This method is used to get all external Reviewer Details 
	 * 
	 * @param 
	 * @return All lookup data
	 */
	public String getAllExtReviewersLookup();

	/**
	 * This method is used to  downloadExternalReviewerAttachment
	 * 
	 * @param  externalReviewerAttachmentId
	 * @return file
	 */
	public ResponseEntity<byte[]> downloadExternalReviewerAttachment(Integer externalReviewerAttachmentId);

	/**
	 * This method is used to  deleteExtReviewerAttachment
	 * @param  externalReviewerAttachmentId
	 */
	public String deleteExtReviewerAttachment(Integer extReviewerAttachmentId);
    
	/**
	 * This method is used to  updateExtAttachment
	 * @param  ExternalReviewerVo
	 */
	public String updateExtAttachment(ExternalReviewerVo vo);

	/**
	 * This methos is used for findSpecialismKeywords
	 * @param vo
	 * @return
	 */
	public List<SpecialismKeyword> findSpecialismKeywords(CommonVO vo);

	/**
	 * This methos is used for resetExternalReviewerPassword
	 * @param externalReviewerId
	 * @return
	 */
	public ResponseEntity<String> resetExternalReviewerPassword(Integer externalReviewerId);

	/**
	 * @param vo
	 * @return
	 */
	public String addSpecialismKeyword(ExternalReviewerVo vo);

	/**
	 * This method is used for enpoint search of AffilationInstitution
	 * @param vo
	 * @return list of ExtReviewerAffilation
	 */
	public List<ExtReviewerAffiliation> findAffilationInstitution(CommonVO vo);

	/**
	 * This method is used for adding new values in AffiliationInstitution
	 * @param vo
	 * @return AffiliationInstitution
	 */
	public String addAffiliationInstitution(ExternalReviewerVo vo);

}
