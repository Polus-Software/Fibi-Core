package com.polus.core.sftpconfiguration;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.polus.core.constants.Constants;
import com.polus.core.general.pojo.SftpConfigurationData;
import com.polus.core.vo.EmailContent;

@Transactional
@Service(value = "sftpConfigurationSerive")
public class SftpConfigurationServiceImpl implements SftpConfigurationService {

	protected static Logger logger = LogManager.getLogger(SftpConfigurationServiceImpl.class.getName());

	@Autowired
	private HibernateTemplate hibernateTemplate;

	@Override
	public SftpConfigurationData getSFTPConfigurationValue(String parameterName) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<SftpConfigurationData> query = builder.createQuery(SftpConfigurationData.class);
		Root<SftpConfigurationData> sftpConfigurationData = query.from(SftpConfigurationData.class);
		Predicate predicateConfigurationKey = builder.equal(sftpConfigurationData.get("configurationKey"), parameterName);
		query.where(builder.and(predicateConfigurationKey));
		return session.createQuery(query).uniqueResult();
	}
	
	@Override
	public String getSftpConfigurationValueAsString(String parameterName) {
		SftpConfigurationData getSftpConfiguration = getSFTPConfigurationValue(parameterName);
		return getSftpConfiguration != null ? getSftpConfiguration.getConfigurationValue() : null;
	}

	@Override
	public Boolean checkSFTPConnection(EmailContent emailContent) {
		boolean connectionStatus = true;
		com.jcraft.jsch.Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		try {
			String SFTPHOST = getSftpConfigurationValueAsString(Constants.SFTP_HOST);
			int SFTPPORT = Integer
					.parseInt(getSftpConfigurationValueAsString(Constants.SFTP_PORT));
			String SFTPUSER = getSftpConfigurationValueAsString(Constants.SFTP_USER);
			logger.info("preparing the host information for sftp connection check.");
			JSch jsch = new JSch();
			session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
			session.setPassword(getSftpConfigurationValueAsString(Constants.SFTP_PASSWORD));
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			logger.info("Host connected.");
			channel = session.openChannel("sftp");
			channel.connect();
			if (!channel.isConnected()) {
				connectionStatus = false;
				emailContent.getError().append("SFTP connection failed ").append("<br/>");
			}
		} catch (Exception ex) {
			logger.info("SFTP connection failed");
			connectionStatus = false;
			logger.info("Exception while trying to check the sftp connection {}", ex.getMessage());
			emailContent.getError().append("Exception while trying to check the sftp connection {} : ").append(ex)
					.append("<br/>");
		} finally {
			logger.info("Initiating SFTP Host final disconnection");
			if (channelSftp != null)
				channelSftp.exit();
			logger.info("sftp Channel exited.");
			if (channel != null)
				channel.disconnect();
			logger.info("Channel disconnected.");
			if (session != null)
				session.disconnect();
			logger.info("Host Session disconnected.");
		}
		return connectionStatus;
	}
}
