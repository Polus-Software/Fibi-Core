package com.polus.core.externalreviewer.dao;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.externalreviewer.pojo.CoiWithPerson;
import com.polus.core.externalreviewer.pojo.ExtReviewerAcademicArea;
import com.polus.core.externalreviewer.pojo.ExtReviewerAcademicRank;
import com.polus.core.externalreviewer.pojo.ExtReviewerAcademicSubArea;
import com.polus.core.externalreviewer.pojo.ExtReviewerAffiliation;
import com.polus.core.externalreviewer.pojo.ExtReviewerAttachment;
import com.polus.core.externalreviewer.pojo.ExtReviewerAttachmentFile;
import com.polus.core.externalreviewer.pojo.ExtReviewerCira;
import com.polus.core.externalreviewer.pojo.ExtReviewerOriginality;
import com.polus.core.externalreviewer.pojo.ExtReviewerThoroughness;
import com.polus.core.externalreviewer.pojo.ExternalReviewer;
import com.polus.core.externalreviewer.pojo.ExternalReviewerAttachmentType;
import com.polus.core.externalreviewer.pojo.ExternalReviewerExt;
import com.polus.core.externalreviewer.pojo.ExternalReviewerRights;
import com.polus.core.externalreviewer.pojo.ExternalReviewerSpecialization;
import com.polus.core.externalreviewer.pojo.ReviewerRights;
import com.polus.core.externalreviewer.pojo.SpecialismKeyword;
import com.polus.core.externalreviewer.vo.ExternalReviewerVo;
import com.polus.core.vo.CommonVO;

@Transactional
@Service
public interface ExternalReviewerDao {
	
	/**
	 * This method is used to save and update the ExternalReviewer details
	 * @param extReviewer
	 * @return updated extReviewer object
	 */
	public ExternalReviewer saveOrUpdateExtReviewer(ExternalReviewer extReviewer);
	
	/**
	 * This method is used to save and update the ExternalReviewerExt details
	 * @param externalReviewerExt
	 * @return updated externalReviewerExt object
	 */
	public ExternalReviewerExt saveOrUpdateExternalReviewerExt(ExternalReviewerExt externalReviewerExt);
	
	/**
	 * This method is used to save and update the ExternalReviewer Specialization details
	 * @param extReviewerSpecialization
	 * @return updated extReviewerSpecialization object
	 */
	public ExternalReviewerSpecialization saveOrUpdateExtReviewerSpecialization(ExternalReviewerSpecialization extReviewerSpecialization);
	
	/**
	 * This method is used to save and update the ExternalReviewer Rights details
	 * @param externalReviewerRight
	 * @return updated externalReviewerRight object
	 */
	public ExternalReviewerRights saveOrUpdateExternalReviewerRights(ExternalReviewerRights externalReviewerRight);

	/**
	 * This method is used to get details of ExternalReviewer based on id
	 * @param extReviewerId
	 * @return extReviewer
	 */
	public ExternalReviewer getExtReviewerDetailById(Integer extReviewerId);
    
	/**
	 * This method is used to get password
	 * @param extReviewerId
	 * @return password
	 */
	public String getexternalReviewerPassword(Integer externalReviewerId);

	/**
	 * This method is used to check user name is unique
	 * @param userName
	 * @return true/false
	 */
	public boolean checkUniqueUserName(String userName);

	/**
	 * This method is used for get all reviewer details
	 * @param ExternalReviewerVo
	 * @return ExternalReviewerVo
	 */
	public ExternalReviewerVo getAllExtReviewer(ExternalReviewerVo vo);
	
	/**
	 * This method is used for get ExternalReviewerExt details
	 * @param extReviewerId
	 * @return ExternalReviewerExt object
	 */
	public ExternalReviewerExt getExternalReviewerExts(Integer extReviewerId);
	
	/**
	 * This method is used for get External Reviewer Specialization details
	 * @param extReviewerId
	 * @return ExternalReviewerSpecialization object
	 */
	public List<ExternalReviewerSpecialization> getExternalReviewerSpecializations(Integer extReviewerId); 
	
	/**
	 * This method is used for get External Reviewer Right details
	 * @param extReviewerId
	 * @return ExternalReviewerRight object
	 */
	public ExternalReviewerRights getExternalReviewerRights(Integer extReviewerId);
	
	/**
	 * This method is used for save External reviewer Attachment
	 * @param externalReviewerAttachment
	 * @return externalReviewerAttachment object
	 */
	public ExtReviewerAttachment saveOrUpdateExtAttachment(ExtReviewerAttachment externalReviewerAttachment);
	
	/**
	 * This method is used to fetch ExternalReviewerAttachmentType lookup.
	 * @return it returns the ExternalReviewerAttachmentType.
	 */
	public List<ExternalReviewerAttachmentType> fetchExternalReviewerAttachmentTypes();
	
	/**
	 * This method is used for get all ExtReviewerAcademicArea lookup
	 */
	public List<ExtReviewerAcademicArea> fetchExtReviewerAcademicArea();
	
	/**
	 * This method is used for get all ExtReviewerAcademicRank lookup
	 */
	public List<ExtReviewerAcademicRank> fetchExtReviewerAcademicRank();
	
	/**
	 * This method is used for get all ExtReviewerAffilation lookup
	 */
	public List<ExtReviewerAffiliation> fetchExtReviewerAffilation();
	
	/**
	 * This method is used for get all fetchExtReviewerCira lookup
	 */
	public List<ExtReviewerCira> fetchExtReviewerCira();
	
	/**
	 * This method is used for get all ExtReviewerOriginality lookup
	 */
	public List<ExtReviewerOriginality> fetchExtReviewerOriginality();
	
	/**
	 * This method is used for get all ExtReviewerThoroughness lookup
	 */
	public List<ExtReviewerThoroughness> fetchExtReviewerThoroughness();
	
	/**
	 * This method is used for get all ReviewerRights lookup
	 */
	public List<ReviewerRights> fetchReviewerRights();

	/**
	 * This method is used for save FileData
	 * @param fileData
	 * @return externalReviewerAttachmentFile object
	 */
	public ExtReviewerAttachmentFile saveFileData(ExtReviewerAttachmentFile fileData);

	/**
	 * This method is used for get External Reviewer Attachment using Id
	 * @param externalReviewerAttachmentId
	 * @return ExtReviewerAttachment object
	 */
	public ExtReviewerAttachment getExtReviewerAttachmentById(Integer externalReviewerAttachmentId);

	/**
	 * This method is used for get FileData using Id
	 * @param fileDataId
	 * @return ExtReviewerAttachmentFile object
	 */
	public ExtReviewerAttachmentFile getFileDataById(String fileDataId);

	/**
	 * This method is used for deleteExtReviewerAttachment
	 * @param ExtReviewerAttachment object
	 */
	public void deleteExtReviewerAttachment(ExtReviewerAttachment extReviewerAttachment);

	/**
	 * This method is used for fetch ExtReviewerAttachment list
	 * @param externalReviewerId
	 * @return ExtReviewerAttachmentFile list
	 */
	public List<ExtReviewerAttachment> fetchExtReviewerAttachment(Integer externalReviewerId);

	/**
	 * This method is used for deleteFileData
	 * @param fileData
	 */
	public void deleteFileData(ExtReviewerAttachmentFile fileData);

	/**
	 * This method is used for update External review attachment
	 * @param externalReviewerAttachmentId
	 */
	public void updateExtAttachment(String description, Integer externalReviewerAttachmentId);

	/**
	 * This method is used for deleting External reviewer specialization
	 * @param extReviewerId
	 */
	public void deleteExtReviewerSpecialization(Integer extReviewerId);

	/**
	 * 
	 * @param vo
	 * @return
	 */
	public List<SpecialismKeyword> findSpecialismKeywords(CommonVO vo);

	/**
	 * This method is used for updateExternalReviewerPassword
	 * @param password
	 * @param externalReviewerId
	 */
	public void updateExternalReviewerPassword(String password, Integer externalReviewerId);

	/**
	 * This method is used for getEmailAddress of external reviewer
	 * @param externalReviewerId
	 * @return
	 */
	public String getEmailAddress(Integer externalReviewerId);

	/**
	 * @return
	 */
	public List<ExtReviewerAcademicSubArea> fetchExtReviewerAcademicSubArea();

	/**
	 * @param specialismKeyword
	 * @return
	 */
	public Boolean isKeywordExist(String specialismKeyword);

	/**
	 * @param specialismKeyword
	 * @return
	 */
	public SpecialismKeyword saveOrUpdateSpecialismKeyword(SpecialismKeyword specialismKeyword);

	/**
	 * This method is used for get max id of SpecialismKeyword
	 * @return id 
	 */
	public String getMaxSpecialismKeyword();

	/**
	 * This method is enpoint search of findAffilationInstitution
	 * @param vo
	 * @return list of affilationInstitution
	 */
	public List<ExtReviewerAffiliation> findAffilationInstitution(CommonVO vo);

	/**
	 * This method is used for adding new values in AffilationInstitution
	 * @param extReviewerAffilation
	 * @return ExtReviewerAffilation
	 */
	public ExtReviewerAffiliation saveOrUpdateAffilationInstitution(ExtReviewerAffiliation extReviewerAffilation);

	/**
	 * This method is used for get max id of AffilationInstitution
	 * @return AffilationInstitutionCode
	 */
	public String getMaxAffilationInstitution();

	/**
	 * This method is used for saving values in CoiWithPerson
	 * @param coiWithPerson
	 * @return CoiWithPerson
	 */
	public CoiWithPerson saveOrUpdateCoiWithPerson(CoiWithPerson coiWithPersonMapping);

	/**
	 * This method is used for deleting CoiWithPerson values
	 * @param coiWithPersonId
	 */
	public void deleteCoiWithPerson(Integer coiWithPersonId);

	/**
	 * This method is used for get CoiWithPerson details using externalReviewerId
	 * @param externalReviewerId
	 * @return CoiWithPerson
	 */
	public List<CoiWithPerson> getCoiWithPerson(Integer externalReviewerId);

	public Boolean isAffilationInstitutionExist(String affilationInstitution);

	/**
	 * This method is used for Deactivate the User Status After Agreement Expiry Date
	 * @param affilationInstitution
	 * @return
	 */
	public void externalReviewerExpire();

	/**
	* This method used for check any external reviewer has the specified email
	* @param primaryEmail
	* @return
	*/
	public boolean checkUniqueEmailAddress(String primaryEmail);


}
