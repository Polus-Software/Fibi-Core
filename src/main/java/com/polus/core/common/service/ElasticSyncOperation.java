package com.polus.core.common.service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.cloudwatch.model.ListMetricsRequest;
import com.amazonaws.services.cloudwatch.model.ListMetricsResult;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polus.core.applicationexception.dto.ApplicationException;
import com.polus.core.applicationexception.service.ApplicationExceptionService;
import com.polus.core.common.dto.AWSStatus;
import com.polus.core.common.dto.ElasticQueueRequest;
import com.polus.core.constants.Constants;
import com.polus.core.notification.email.service.EmailService;
import com.polus.core.notification.email.vo.EmailServiceVO;
import com.polus.core.notification.pojo.NotificationRecipient;

@Service(value = "elasticSyncOperation")
public class ElasticSyncOperation {

	protected static Logger logger = LogManager.getLogger(ElasticSyncOperation.class.getName());

	@Value("${fibicore.elasticsync.sqs.enable}")
	private boolean sqsEnabled;

	@Value("${fibicore.elasticsync.enable}")
	private boolean elasticSyncEnabled;

	@Value("${fibicore.elastic.sqs.queue.name}")
	private String queueName;

	@Value("${fibicore.elastic.sync.url}")
	private String elasticSyncUrl;

	@Value("${fibicore.spring.elastic.error.mail.receiver}")
	private String receiver;

	private ExecutorService executorService = Executors.newCachedThreadPool();

	@Autowired
	private EmailService emailService;

	@Autowired
	private CommonService commonService;

	@Autowired
	private ApplicationExceptionService applicationExceptionService;

	@Autowired
	private AWSStatus awsStatus;

	private static final String ELASTIC_SYNC_ERROR = "ELASTIC_FIBI";

	static BasicAWSCredentials bAWSc = new BasicAWSCredentials("AKIAQF5PEGHFL6MHFKEG", "Jq+pOXFAHYAMDw2ebJgn0hxrihwtCsb1jhKR78++");

	public static SQSConnection createConnection() throws JMSException {
		// Create the connection factory based on the config
	     // Create the connection factory based on the config       
		   SQSConnectionFactory connectionFactory = new SQSConnectionFactory(
	               new ProviderConfiguration(),
	               AmazonSQSClientBuilder.standard()
	                       .withRegion(Regions.AP_NORTHEAST_1)
	                       .withCredentials( new AWSStaticCredentialsProvider(bAWSc))
	               );
			SQSConnection connection = connectionFactory.createConnection();
			return connection;
	}

	public static Session getQueueSession() throws JMSException {
		Connection connection = createConnection();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        return session;
	}

	public void sendSyncRequest(String moduleItemKey, String actionType, String indexName) {
		ServletRequestAttributes sra = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes());
		HttpServletRequest request =  sra.getRequest();
		try {
			if (!elasticSyncEnabled) {
				logger.info("Elastic sync operation is not enabled");
				return;
			}
			ElasticQueueRequest elasticQueueRequest = new ElasticQueueRequest(moduleItemKey, actionType, indexName);
			String elasticSyncRequest = convertObjectToJSON(elasticQueueRequest);
			if (sqsEnabled) {
				logger.info("Elastic sync operation with sqs is enabled");
				Session session = getQueueSession();
				TextMessage message = session.createTextMessage(elasticSyncRequest);
				MessageProducer producer = session.createProducer(session.createQueue(queueName));
				producer.send(message);
				System.err.println("message sending message: " + message);
				session.close();
			} else {
				logger.info("Elastic sync operation with REST is enabled");
				if ("BULKSYNC".equals(elasticQueueRequest.getActionType())) {
					try {
						syncElasticIndex(elasticSyncRequest);
					} catch (Exception e) {
						throw new ApplicationException("Error occured in elastic bulk sync", e, ELASTIC_SYNC_ERROR);
					}
				} else {
					executorService.execute(() -> syncElasticIndex(elasticSyncRequest));
				}
			}
			awsStatus.setStatus("NO_ERROR");
		} catch ( JMSException | AmazonSQSException e) {
			if (awsStatus.getStatus().equals("NO_ERROR")) {
				awsStatus.setStatus("ERROR");
				emailTheError();
			}
			applicationExceptionService.saveErrorDetails( new ApplicationException("JMSException occured while calling the elaticSync application", e, ELASTIC_SYNC_ERROR), request);
		} catch (ApplicationException e) {
			throw e;
		}
		catch (Exception e) {
			e.printStackTrace();
			applicationExceptionService.saveErrorDetails( new ApplicationException("Error occured while calling the elaticSync application", e, ELASTIC_SYNC_ERROR), request);
		}
	}

	private void emailTheError() {
		EmailServiceVO emailServiceVO = new EmailServiceVO();
		emailServiceVO.setBody("hi , got some issues in AWS");
		emailServiceVO.setSubject("ERROR : Issues occurs in AWS");
		Set<NotificationRecipient> dynamicEmailRecipients = new HashSet<>();
		commonService.setNotificationRecipientsforNonEmployees(receiver, Constants.NOTIFICATION_RECIPIENT_TYPE_TO, dynamicEmailRecipients);
		emailServiceVO.setRecipients(dynamicEmailRecipients);
		emailService.sendEmail(emailServiceVO);
	}

	private void syncElasticIndex(String elasticSyncRequest) {
		RestTemplate restTemplate = new RestTemplate();
		String postUrl = elasticSyncUrl + "elasticSync";
		restTemplate.postForObject(postUrl, elasticSyncRequest, String.class);
	}

	public static String convertObjectToJSON(Object object) {
		String response = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			response = mapper.writeValueAsString(object);
		} catch (Exception e) {
			logger.error("Error occured in convertObjectToJSON : {}", e.getMessage());
		}
		return response;
	}

	public Map<String, Map<String, List<Datapoint>>> queueMatrixDetails() {
		Map<String, List<Datapoint>> statDetails = new HashMap<>();
		Map<String, Map<String, List<Datapoint>>> queueStat = new HashMap<>();
		try {
			 AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_1)
					    .withCredentials(new AWSStaticCredentialsProvider(bAWSc)).build();

			ListMetricsRequest request = new ListMetricsRequest()
			        .withNamespace("AWS/SQS");

			boolean done = false;
			while(!done) {
			    ListMetricsResult response = cw.listMetrics(request);
			    for(Metric metric : response.getMetrics()) {
			        final GetMetricStatisticsRequest request1 = request(
		                    "", 24, metric.getDimensions(), metric.getMetricName());
		            final GetMetricStatisticsResult result = cw
		                    .getMetricStatistics(request1);
		            statDetails.put(metric.getMetricName(), result.getDatapoints());
		            if (queueStat.containsKey(metric.getDimensions().get(0).getValue())) {
		            	queueStat.get(metric.getDimensions().get(0).getValue()).put(metric.getMetricName(), result.getDatapoints());
		            } else {
		            	statDetails = new HashMap<>();
		            	statDetails.put(
			                    metric.getMetricName(), result.getDatapoints());
			            queueStat.put(metric.getDimensions().get(0).getValue(), statDetails);
		            }
			    }
			    request.setNextToken(response.getNextToken());
			    if(response.getNextToken() == null) {
			        done = true;
			    } 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return queueStat;
	}

	private static GetMetricStatisticsRequest request(
            final String funcationValue, int hours, List<Dimension> dimensions, String metrixName) {
        final long twentyFourHrs = 1000 * 60 * 60l * hours;
        final int oneHour = 60 * 60 * hours;
        return new GetMetricStatisticsRequest()
                .withStartTime(new Date(new Date().getTime() - twentyFourHrs))
                .withNamespace("AWS/SQS")
                .withPeriod(oneHour)
                .withDimensions(
                		dimensions)
                .withMetricName(metrixName)
                .withStatistics("Sum").withEndTime(new Date());
    }

	/**
	 * This method is used to call sendSyncRequest after null checking all params 
	 * * @param elasticQueueRequest - ElasticQueueRequest object
	 */
	public void initiateSyncForElasticQueueRequest(String moduleItemKey, String actionType, String indexName) {
		if (moduleItemKey != null && actionType != null && indexName != null) {
			sendSyncRequest(moduleItemKey, actionType, indexName);
		}
	}

	/**
	 * This method is used to send multiple sync requests
	 * In such cases, the multiple sync requests will be added up in the elasticQueueRequestList within the ElasticQueueRequest object
	 * Example scenario: when a proposal has been awarded, we need to update proposal elastic(proposal status update) as well as IP elastic (inserting IP)
	 * @param elasticQueueRequest - ElasticQueueRequest object
	 */
	public void initiateSyncForElasticQueueRequestList(ElasticQueueRequest elasticQueueRequest) {
		elasticQueueRequest.getElasticQueueRequestList().forEach(request -> 
			initiateSyncForElasticQueueRequest(request.getModuleItemKey(), request.getActionType(), request.getIndexName())
		);
	}

	/**
	 * This method id used to add sync requests to the elasticQueueRequestList within the ElasticQueueRequest object
	 * which will be used in the cases for sending multiple sync requests (refer method above)
	 * @param elasticQueueRequest
	 * @param moduleItemKey
	 * @param actionType
	 * @param indexName
	 * @return elasticQueueRequest -  elasticQueueRequest object with the multiple requests added in the list within
	 */
	public ElasticQueueRequest addElasticQueueRequestToList(ElasticQueueRequest elasticQueueRequest, String moduleItemKey, String actionType, String indexName) {
		ElasticQueueRequest tempElasticQueueRequest = new ElasticQueueRequest();
		BeanUtils.copyProperties(elasticQueueRequest, tempElasticQueueRequest);
		tempElasticQueueRequest.getElasticQueueRequestList().add(new ElasticQueueRequest(moduleItemKey, actionType, indexName));
		return tempElasticQueueRequest;
	}

	public String getLogstashStatus() {
		RestTemplate restTemplate = new RestTemplate();
		String postUrl = elasticSyncUrl + "getLogstashStatus";
		return restTemplate.postForObject(postUrl, null, String.class);
	}
}
