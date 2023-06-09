package com.polus.core.persontraining.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.polus.core.person.vo.PersonVO;
import com.polus.core.vo.CommonVO;

@Service
public interface PersonTrainingService {

	/**
	 * @param vo
	 * @return updated person training
	 */
	public String saveOrUpdatePersonTraining(PersonVO vo);

	/**
	 * @param vo
	 * @return training list
	 */
	public String getTrainingDashboard(PersonVO vo);

	/**
	 * @param vo
	 * @return updated comments
	 */
	public String saveOrUpdateTrainingComments(PersonVO vo);

	/**
	 * @param trainingCommentId
	 * @return boolean
	 */
	public String deleteTrainingComments(Integer trainingCommentId);

	/**
	 * @param trainingId
	 * @return training details
	 */
	public String getPersonTrainingDetails(Integer trainingId);

	/**
	 * @param vo
	 * @return vo
	 */
	public String loadTrainingList(CommonVO vo);

	/**
	 * @param files
	 * @param formDataJson
	 * @return vo
	 */
	public String saveOrUpdateTrainingAttachment(MultipartFile[] files, String formDataJson);

	/**
	 * @param parseInt
	 * @return blob
	 */
	public ResponseEntity<byte[]> downloadtrainingAttachment(int parseInt);

	/**
	 * @param attachmentId
	 * @return boolean
	 */
	public String deleteTrainingAttachment(Integer attachmentId);

	/**
	 * @param personTrainingId
	 * @return boolean
	 */
	public String deletePersonTraining(Integer personTrainingId);

	/**
	 * This method is used to fetch all training data
	 * @param vo
	 * @return list of training datas
	 */
	public String getAllTrainings(PersonVO vo);
}
