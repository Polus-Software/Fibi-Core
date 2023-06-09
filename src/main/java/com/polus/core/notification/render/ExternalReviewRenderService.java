package com.polus.core.notification.render;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.constants.Constants;
import com.polus.core.externalreviewer.dao.ExternalReviewerDao;
import com.polus.core.externalreviewer.pojo.ExternalReviewer;

@Transactional
@Service
public class ExternalReviewRenderService implements EmailRenderService {
	
	@Autowired
	private ExternalReviewerDao externalReviewerDao;
	
	@Value("${externalReview.url}")
	private String applicationURL;

	@Override
	public String getModuleType() {
		return String.valueOf(Constants.EXTERNAL_REVIEWER_MODULE_CODE);
	}
	
	@Override
	public Map<String, String> getPlaceHolderData(String extReviewerId) {
		ExternalReviewer extReviewer = externalReviewerDao.getExtReviewerDetailById(Integer.parseInt(extReviewerId));
		return externalReviewerPlaceholder(extReviewer);
	}
	
	private Map<String, String> externalReviewerPlaceholder(ExternalReviewer extReviewer) {
		Map<String, String> placeHolder = new HashMap<>();
		placeHolder.put("{FULL_NAME}", extReviewer.getPassportName() != null ? extReviewer.getPassportName() : "");
		placeHolder.put("{USER_NAME}", extReviewer.getPrincipalName() != null ? extReviewer.getPrincipalName() : "");
		placeHolder.put("{LOGIN_URL}", applicationURL != null ? applicationURL : "");
		return placeHolder;
	}

	@Override
	public String getSubModuleCode() {
		return String.valueOf(Constants.EXTERNAL_REVIEWER_SUB_MODULE_CODE);
	}

}
