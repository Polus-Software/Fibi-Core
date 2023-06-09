package com.polus.core.externalreviewer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polus.core.applicationexception.dto.ApplicationException;
import com.polus.core.common.dao.CommonDao;
import com.polus.core.common.service.CommonService;
import com.polus.core.constants.Constants;
import com.polus.core.externalreviewer.dao.ExternalReviewerDao;
import com.polus.core.externalreviewer.pojo.CoiWithPerson;
import com.polus.core.externalreviewer.pojo.ExtReviewerAffiliation;
import com.polus.core.externalreviewer.pojo.ExtReviewerAttachment;
import com.polus.core.externalreviewer.pojo.ExtReviewerAttachmentFile;
import com.polus.core.externalreviewer.pojo.ExternalReviewer;
import com.polus.core.externalreviewer.pojo.ExternalReviewerExt;
import com.polus.core.externalreviewer.pojo.ExternalReviewerRights;
import com.polus.core.externalreviewer.pojo.ExternalReviewerSpecialization;
import com.polus.core.externalreviewer.pojo.SpecialismKeyword;
import com.polus.core.externalreviewer.vo.ExternalReviewerVo;
import com.polus.core.notification.email.service.EmailService;
import com.polus.core.notification.email.vo.EmailServiceVO;
import com.polus.core.notification.pojo.NotificationRecipient;
import com.polus.core.person.dao.PersonDao;
import com.polus.core.security.AuthenticatedUser;
import com.polus.core.vo.CommonVO;

@Transactional
@Service(value = "externalReviewerService")
public class ExternalReviewerServiceImpl implements ExternalReviewerService {

	protected static Logger logger = LogManager.getLogger(ExternalReviewerServiceImpl.class.getName());

	@Autowired
	private CommonDao commonDao;

	@Autowired
	private ExternalReviewerDao externalReviewerDao;

	@Autowired
	private CommonService commonService;
	
	@Autowired
	private EmailService emailService;

	@Autowired
	@Qualifier(value = "personDao")
	private PersonDao personDao;

	@Override
	public String saveOrUpdateReviewerDetails(ExternalReviewerVo vo) {
		try {
			ExternalReviewer extReviewer = vo.getExtReviewer();
			String password = null;
			ExternalReviewerRights externalReviewerRight = new ExternalReviewerRights();
			if ((extReviewer.getExternalReviewerId() == null || (extReviewer.getExternalReviewerId() != null
					&& Boolean.TRUE.equals(extReviewer.getIsUsernameChange())))
					&& Boolean.TRUE.equals(externalReviewerDao.checkUniqueUserName(extReviewer.getPrincipalName()))) {
				vo.setMessage("Username already exists");
				vo.setExtReviewer(null);
				return commonDao.convertObjectToJSON(vo);
			}
			if (extReviewer.getExternalReviewerId() == null) {
				String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@*_";
				password = RandomStringUtils.random(15, characters);
				extReviewer.setPassword(commonService.hashBySha(password));
				externalReviewerRight.setReviewerRightId(Constants.REVIEWER);
			} else {
				extReviewer.setPassword(externalReviewerDao.getexternalReviewerPassword(extReviewer.getExternalReviewerId()));
			}
			extReviewer.setUpdateTimeStamp(commonDao.getCurrentTimestamp());
			extReviewer.setUpdateUser(AuthenticatedUser.getLoginUserName());
			externalReviewerDao.saveOrUpdateExtReviewer(extReviewer);
			if(externalReviewerRight.getReviewerRightId() != null) {
				 externalReviewerRight.setExternalReviewerId(extReviewer.getExternalReviewerId());
				 vo.setExternalReviewerRight(externalReviewerRight);
				 saveOrUpdateuserAccess(vo);
			}
			if (password != null) {
				sendExternalReviewerPasswordMail(extReviewer.getPrimaryEmail(),extReviewer.getExternalReviewerId(), password);
			}
			vo.setMessage("External reviewer details saved successfully");
		} catch (Exception e) {
			throw new ApplicationException("error in saveOrUpdateReviewerDetails", e, Constants.JAVA_ERROR);
		}
		return commonDao.convertObjectToJSON(vo);
	}

	private void sendExternalReviewerPasswordMail(String emailAddress, Integer externalReviewerId, String password) {
		logger.info("Requesting for send ExternalUser Password Mail");
		Set<NotificationRecipient> dynamicEmailrecipients = new HashSet<>();
		EmailServiceVO emailServiceVO = new EmailServiceVO();
		emailServiceVO.setNotificationTypeId(Constants.EXTERNAL_REVIEWER_MAIL);
		emailServiceVO.setModuleCode(Constants.EXTERNAL_REVIEWER_MODULE_CODE);
		emailServiceVO.setSubModuleCode(Constants.EXTERNAL_REVIEWER_SUB_MODULE_CODE);
		emailServiceVO.setModuleItemKey(externalReviewerId.toString());
		commonService.setNotificationRecipientsforNonEmployees(emailAddress,Constants.NOTIFICATION_RECIPIENT_TYPE_TO, dynamicEmailrecipients);
		emailServiceVO.setRecipients(dynamicEmailrecipients);
		emailServiceVO.setPlaceHolder(externalReviewerPlaceholder(password));
		emailService.sendEmail(emailServiceVO);
	}

	private Map<String, String> externalReviewerPlaceholder(String password) {
		Map<String, String> placeHolder = new HashMap<>();
		placeHolder.put("{PASSWORD}", password != null ? password : "");
		return placeHolder;
	}

	@Override
	public String saveOrUpdateAdditionalDetails(ExternalReviewerVo vo) {
		ExternalReviewerExt externalReviewerExt = vo.getExternalReviewerExt();
		List<ExternalReviewerSpecialization> externalReviewerSpecializations = vo.getExternalReviewerSpecializations();
		List<CoiWithPerson> coiWithPersons = vo.getCoiWithPersons();
		String updateUserName = AuthenticatedUser.getLoginUserName();
		try {
			if (externalReviewerExt != null) {
				externalReviewerExt.setUpdateTimeStamp(commonDao.getCurrentTimestamp());
				externalReviewerExt.setUpdateUser(updateUserName);
				externalReviewerDao.saveOrUpdateExternalReviewerExt(externalReviewerExt);
				vo.setExternalReviewerExt(externalReviewerExt);
			}
			if (externalReviewerSpecializations != null) {
				externalReviewerSpecializations.forEach(extReviewerSpecialization -> {
					if ("I".equals(extReviewerSpecialization.getActionType())) {
						extReviewerSpecialization.setUpdateTimeStamp(commonDao.getCurrentTimestamp());
						extReviewerSpecialization.setUpdateUser(updateUserName);
						externalReviewerDao.saveOrUpdateExtReviewerSpecialization(extReviewerSpecialization);
					} else if ("D".equals(extReviewerSpecialization.getActionType())) {
						externalReviewerDao.deleteExtReviewerSpecialization(extReviewerSpecialization.getExtReviewerSpecializationId());
					}
				});
			}
			if (coiWithPersons != null) {
				coiWithPersons.forEach(coiWithPerson -> {
					if ("I".equals(coiWithPerson.getActionType())) {
						coiWithPerson.setUpdateTimeStamp(commonDao.getCurrentTimestamp());
						coiWithPerson.setUpdateUser(updateUserName);
						externalReviewerDao.saveOrUpdateCoiWithPerson(coiWithPerson);
					} else if ("D".equals(coiWithPerson.getActionType())) {
						externalReviewerDao.deleteCoiWithPerson(coiWithPerson.getCoiWithPersonId());
					}
				});
			}
			vo.setMessage("External additional details saved successfully");
		} catch (Exception e) {
			throw new ApplicationException("error in saveOrUpdateAdditionalDetails", e, Constants.JAVA_ERROR);
		}
		return commonDao.convertObjectToJSON(vo);
	}

	@Override
	public String saveOrUpdateuserAccess(ExternalReviewerVo vo) {
		try {
			ExternalReviewerRights externalReviewerRight = vo.getExternalReviewerRight();
			externalReviewerRight.setUpdateTimeStamp(commonDao.getCurrentTimestamp());
			externalReviewerRight.setUpdateUser(AuthenticatedUser.getLoginUserName());
			externalReviewerDao.saveOrUpdateExternalReviewerRights(externalReviewerRight);
			vo.setMessage("External User Access details saved successfully");
		} catch (Exception e) {
			throw new ApplicationException("error in saveOrUpdateuserAccess", e, Constants.JAVA_ERROR);
		}
		return commonDao.convertObjectToJSON(vo);
	}

	@Override
	public String addExtReviewerAttachment(MultipartFile[] files, String formDataJSON) {
		commonService.checkFileFormat(files, "General");
		ExternalReviewerVo vo = new ExternalReviewerVo();
		 List<ExtReviewerAttachment> attachmentsList = new ArrayList<>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			vo = mapper.readValue(formDataJSON, ExternalReviewerVo.class);
			List<ExtReviewerAttachment> extAttachments = vo.getExtReviewerAttachments();
			 if (files != null && files.length > 0 && extAttachments != null) {
	                for (int i = 0; i < files.length; i++) {
	                	ExtReviewerAttachment extReviewerAttachment = extAttachments.get(i);
	                	ExtReviewerAttachmentFile fileData = new ExtReviewerAttachmentFile();
	                	extReviewerAttachment.setMimeType(files[i].getContentType());
	                	fileData.setAttachment(files[i].getBytes());
	                	fileData = externalReviewerDao.saveFileData(fileData);
	                	extReviewerAttachment.setFileDataId(fileData.getFileDataId());
						extReviewerAttachment.setUpdateTimestamp(commonDao.getCurrentTimestamp());
						extReviewerAttachment.setUpdateUser(AuthenticatedUser.getLoginUserName());
						externalReviewerDao.saveOrUpdateExtAttachment(extReviewerAttachment);
						attachmentsList.add(extReviewerAttachment);
	                }
	            }
		} catch (Exception e) {
			throw new ApplicationException("error in addExtReviewerAttachment", e, Constants.JAVA_ERROR);
		}
		return commonDao.convertObjectToJSON(attachmentsList);
	}

	@Override
	public String deleteExtReviewerAttachment(Integer extReviewerAttachmentId) {
		try {
			ExtReviewerAttachment extReviewerAttachment = externalReviewerDao.getExtReviewerAttachmentById(extReviewerAttachmentId);
			if (extReviewerAttachment != null) {
				if (extReviewerAttachment.getFileDataId() != null) {
					externalReviewerDao.deleteFileData(externalReviewerDao.getFileDataById(extReviewerAttachment.getFileDataId()));
				}
				externalReviewerDao.deleteExtReviewerAttachment(extReviewerAttachment);
			}
			return commonDao.convertObjectToJSON("deleted successfully");
		} catch (Exception e) {
			throw new ApplicationException("error in deleteExtReviewerAttachment", e, Constants.JAVA_ERROR);
		}
	}

	@Override
	public String getExtReviewerDetailById(ExternalReviewerVo vo) {
		Integer extReviewerId = vo.getExtReviewerId();
		vo.setExtReviewer(externalReviewerDao.getExtReviewerDetailById(extReviewerId));
		ExternalReviewerExt externalReviewerExt = externalReviewerDao.getExternalReviewerExts(extReviewerId);
		vo.setExternalReviewerExt(externalReviewerExt);
		vo.setExternalReviewerRight(externalReviewerDao.getExternalReviewerRights(extReviewerId));
		List<ExternalReviewerSpecialization> externalReviewerSpecializations = externalReviewerDao.getExternalReviewerSpecializations(extReviewerId);
		vo.setExternalReviewerSpecializations(externalReviewerSpecializations);
		vo.setCoiWithPersons(externalReviewerDao.getCoiWithPerson(extReviewerId));
		vo.setExtReviewerAttachments(externalReviewerDao.fetchExtReviewerAttachment(extReviewerId));
		return commonDao.convertObjectToJSON(vo);
	}

	@Override
	public String getAllExtReviewers(ExternalReviewerVo vo) {
		return commonDao.convertObjectToJSON(externalReviewerDao.getAllExtReviewer(vo));
	}

	@Override
	public String getAllExtReviewersLookup() {
		ExternalReviewerVo vo = new ExternalReviewerVo();
		vo.setExternalReviewerAttachmentType(externalReviewerDao.fetchExternalReviewerAttachmentTypes());
		vo.setExtReviewerAcademicArea(externalReviewerDao.fetchExtReviewerAcademicArea());
		vo.setExtReviewerAcademicSubArea(externalReviewerDao.fetchExtReviewerAcademicSubArea());
		vo.setExtReviewerAcademicRank(externalReviewerDao.fetchExtReviewerAcademicRank());
		vo.setExtReviewerAffiliation(externalReviewerDao.fetchExtReviewerAffilation());
		vo.setExtReviewerCira(externalReviewerDao.fetchExtReviewerCira());
		vo.setExtReviewerOriginality(externalReviewerDao.fetchExtReviewerOriginality());
		vo.setReviewerRights(externalReviewerDao.fetchReviewerRights());
		vo.setExtReviewerThoroughness(externalReviewerDao.fetchExtReviewerThoroughness());
		return commonDao.convertObjectToJSON(vo);
	}

	@Override
	public ResponseEntity<byte[]> downloadExternalReviewerAttachment(Integer externalReviewerAttachmentId) {
		ExtReviewerAttachment attachment = externalReviewerDao.getExtReviewerAttachmentById(externalReviewerAttachmentId);
		ResponseEntity<byte[]> attachmentData = null;
		try {
			ExtReviewerAttachmentFile fileData = externalReviewerDao.getFileDataById(attachment.getFileDataId());
			attachmentData = commonService.setAttachmentContent(attachment.getFileName(), fileData.getAttachment());
			return attachmentData;
		} catch (Exception e) {
			throw new ApplicationException("error in downloadExternalReviewerAttachment", e, Constants.JAVA_ERROR);
		}
	}

	@Override
	public String updateExtAttachment(ExternalReviewerVo vo) {
		try {
			externalReviewerDao.updateExtAttachment(vo.getExtReviewerAttachment().getDescription(),vo.getExtReviewerAttachment().getExternalReviewerAttachmentId());
			return commonDao.convertObjectToJSON(vo.getExtReviewerAttachment());
		} catch (Exception e) {
			throw new ApplicationException("error in updateExternalReviewerAttachment", e, Constants.JAVA_ERROR);
		}
	}
	
	@Override
	public List<SpecialismKeyword> findSpecialismKeywords(CommonVO vo) {
		return externalReviewerDao.findSpecialismKeywords(vo);
	}
	
	@Override
	public ResponseEntity<String> resetExternalReviewerPassword(Integer externalReviewId) {
		try {
			String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@*_";
			String password = RandomStringUtils.random(15, characters);
			externalReviewerDao.updateExternalReviewerPassword(commonService.hashBySha(password),externalReviewId);
			sendExternalReviewerPasswordMail(externalReviewerDao.getEmailAddress(externalReviewId),externalReviewId, password);
			return new ResponseEntity<>("successfully reset Password", HttpStatus.OK);
		}  catch (Exception e) {
			throw new ApplicationException("error in resetExternalReviewerPassword", e, Constants.JAVA_ERROR);
		}
	}

	@Override
	public String addSpecialismKeyword(ExternalReviewerVo vo) {
		SpecialismKeyword specialismKeyword = new SpecialismKeyword();
		specialismKeyword.setDescription(vo.getSpecialismKeyword());
		specialismKeyword.setUpdateUser(AuthenticatedUser.getLoginUserName());
		specialismKeyword.setIsActive(Boolean.TRUE);
		if (Boolean.FALSE.equals(externalReviewerDao.isKeywordExist(vo.getSpecialismKeyword()))) {
			specialismKeyword.setCode(externalReviewerDao.getMaxSpecialismKeyword());
			specialismKeyword.setUpdateTimestamp(commonDao.getCurrentTimestamp());
			return commonDao.convertObjectToJSON(externalReviewerDao.saveOrUpdateSpecialismKeyword(specialismKeyword));
		}
		return commonDao.convertObjectToJSON(specialismKeyword);
	}

	@Override
	public  List<ExtReviewerAffiliation> findAffilationInstitution(CommonVO vo) {
		return externalReviewerDao.findAffilationInstitution(vo);
	}
	
	@Override
	public String addAffiliationInstitution(ExternalReviewerVo vo) {
		ExtReviewerAffiliation affiliationInstitution = new ExtReviewerAffiliation();
		if (Boolean.FALSE.equals(externalReviewerDao.isAffilationInstitutionExist(vo.getAffiliationInstitution()))) { 
			String affiliationInstitutionCode = externalReviewerDao.getMaxAffilationInstitution();
			affiliationInstitution.setDescription(vo.getAffiliationInstitution());
			affiliationInstitution.setUpdateUser(AuthenticatedUser.getLoginUserName());
			affiliationInstitution.setAffiliationInstitutionCode(affiliationInstitutionCode);
			affiliationInstitution.setUpdateTimestamp(commonDao.getCurrentTimestamp());
		    externalReviewerDao.saveOrUpdateAffilationInstitution(affiliationInstitution);
		}
		return commonDao.convertObjectToJSON(affiliationInstitution);
	}

	@Scheduled(cron = "${external.reviewer.schedule}", zone = Constants.CRON_JOB_TIMEZONE)
	public void updateExternalReviewerStaus() {
		logger.info("update external reviewers when the agreement expiry date is over {}",  commonDao.getCurrentTimestamp());
		externalReviewerDao.externalReviewerExpire();
	}
}
