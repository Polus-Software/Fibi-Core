package com.polus.core.sftpconfiguration;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.general.pojo.SftpConfigurationData;
import com.polus.core.vo.EmailContent;

@Transactional
@Service
public interface SftpConfigurationService {

	/**
	 * 
	 * @param parameterName
	 * @return
	 */
	public SftpConfigurationData getSFTPConfigurationValue(String parameterName);

	/**
	 * 
	 * @param parameterName
	 * @return
	 */
	public String getSftpConfigurationValueAsString(String parameterName);

	/**
	 * This method used for checking sftp connection
	 * 
	 * @param emailContent
	 * @param valueToHide  - Password to encrypt
	 */
	public Boolean checkSFTPConnection(EmailContent emailContent);

}
