package com.polus.core.externalreviewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.polus.core.externalreviewer.pojo.ExtReviewerAffiliation;
import com.polus.core.externalreviewer.pojo.SpecialismKeyword;
import com.polus.core.externalreviewer.service.ExternalReviewerService;
import com.polus.core.externalreviewer.vo.ExternalReviewerVo;
import com.polus.core.security.AuthenticatedUser;
import com.polus.core.vo.CommonVO;

@RestController
public class ExternalReviewerController {
	
	protected static Logger logger = LogManager.getLogger(ExternalReviewerController.class.getName());
	
	@Autowired
	public ExternalReviewerService externalReviewerService;

	@PostMapping(value = "/saveOrUpdateExtReviewer")
	public String saveOrUpdateExtReviewer(@RequestBody ExternalReviewerVo vo) {
		logger.info("Request for saveOrUpdateExtReviewer");
		return externalReviewerService.saveOrUpdateReviewerDetails(vo);
	}
	
	@PostMapping(value = "/saveOrUpdateAdditionalDetails")
	public String saveOrUpdateAdditionalDetails(@RequestBody ExternalReviewerVo vo) {
		logger.info("Request for saveOrUpdateAdditionalDetails");
		return externalReviewerService.saveOrUpdateAdditionalDetails(vo);
	}
	
	@PostMapping(value = "/saveOrUpdateuserAccess")
	public String saveOrUpdateuserAccess(@RequestBody ExternalReviewerVo vo) {
		logger.info("Request for saveOrUpdateuserAccess");
		return externalReviewerService.saveOrUpdateuserAccess(vo);
	}
	
	@PostMapping(value = "/addExtReviewerAttachment")
	public String addExtReviewerAttachment(@RequestParam(value = "files", required = false) MultipartFile[] files, @RequestParam("formDataJson") String formDataJson) {
		logger.info("Requesting for addExtReviewerAttachment");
		return externalReviewerService.addExtReviewerAttachment(files, formDataJson);
	}
	
	@PostMapping(value = "/getExtReviewerDetailById")
	public String getPersonDetailById(@RequestBody ExternalReviewerVo vo) {
		logger.info("Request for getExtReviewerDetailById");
		return externalReviewerService.getExtReviewerDetailById(vo);
	}

	@PostMapping(value = "/getAllExtReviewers")
	public String getAllExtReviewers(@RequestBody ExternalReviewerVo vo) {
		logger.info("Request for getAllExtReviewers");
		return externalReviewerService.getAllExtReviewers(vo);
	}	
	
	@DeleteMapping(value = "/deleteExtReviewerAttachment/{extReviewerAttachmentId}")
	public String deleteExtReviewerAttachment(@PathVariable(value = "extReviewerAttachmentId", required = true) final Integer extReviewerAttachmentId) {
		logger.info("Requesting for deleteExtReviewerAttachment");
		return externalReviewerService.deleteExtReviewerAttachment(extReviewerAttachmentId);
	}
	
	@GetMapping(value = "/getAllExtReviewersLookup")
	public String fetchRequiredParams(HttpServletRequest request) {
		logger.info("Requesting for getAllExtReviewersLookup");
		return externalReviewerService.getAllExtReviewersLookup();
	}
	
	@GetMapping(value = "/downloadExternalReviewerAttachment")
	public ResponseEntity<byte[]> downloadExternalReviewerAttachment(HttpServletResponse response, @RequestHeader("externalReviewerAttachmentId") Integer externalReviewerAttachmentId) {
		logger.info("Requesting for downloadExternalReviewerAttachment");
		logger.info("externalReviewerAttachmentId : {}", externalReviewerAttachmentId);
		return externalReviewerService.downloadExternalReviewerAttachment(externalReviewerAttachmentId);
	}

	@PostMapping(value = "/updateExtAttachment")
    public String updateAttachmentDetails(@RequestBody ExternalReviewerVo vo) {
		logger.info("Requesting for updateExtAttachment");
		return externalReviewerService.updateExtAttachment(vo);
	}

	@PostMapping(value = "/findSpecialismKeywords")
	public List<SpecialismKeyword> findSpecialismKeywords(@RequestBody CommonVO vo) {
		logger.info("Requesting for findSpecialismKeywords");
		return externalReviewerService.findSpecialismKeywords(vo);
	}

	@PatchMapping(value = "/resetExternalReviewerPassword/{externalReviewerId}")
	public ResponseEntity<String> resetExternalReviewerPassword(@PathVariable ("externalReviewerId") Integer externalReviewerId) {
		logger.info("Request for Reset External Reviewer Password");
		logger.info("Login Person Id : {}", AuthenticatedUser.getLoginPersonId());
		return externalReviewerService.resetExternalReviewerPassword(externalReviewerId);
	}

	@PostMapping(value = "/addSpecialismKeyword")
	public String addScienceKeyword(@RequestBody ExternalReviewerVo vo) {
		logger.info("Requesting for Adding SpecialismKeyword");
		logger.info("SpecialismKeyword : {}", vo.getSpecialismKeyword());
		return externalReviewerService.addSpecialismKeyword(vo);
	}

	@PostMapping(value = "/findAffiliationInstitution")
	public List<ExtReviewerAffiliation> findAffilationInstitution(@RequestBody CommonVO vo) {
		logger.info("Requesting for findAffiliationInstitution");
		return externalReviewerService.findAffilationInstitution(vo);
	}

	@PostMapping(value = "/addAffiliationInstitution")
	public String addAffiliationInstitution(@RequestBody ExternalReviewerVo vo) {
		logger.info("Requesting for Adding addAffiliationInstitution");
		logger.info("AffiliationInstitution : {}", vo.getAffiliationInstitution());
		return externalReviewerService.addAffiliationInstitution(vo);
	}
}