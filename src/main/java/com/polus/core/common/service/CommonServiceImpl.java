package com.polus.core.common.service;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import javax.persistence.Column;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.polus.core.applicationexception.dto.ApplicationException;
import com.polus.core.auditreport.export.dao.ExportDao;
import com.polus.core.common.dao.CommonDao;
import com.polus.core.common.event.PdfHeaderFooterPageEvent;
import com.polus.core.constants.Constants;
import com.polus.core.general.pojo.WebSocketConfiguration;
import com.polus.core.notification.pojo.NotificationRecipient;
import com.polus.core.pojo.Country;
import com.polus.core.pojo.FileType;
import com.polus.core.pojo.LetterTemplateType;
import com.polus.core.pojo.LookupWindow;
import com.polus.core.pojo.ResearchTypeArea;
import com.polus.core.pojo.ResearchTypeSubArea;
import com.polus.core.util.Truth;
import com.polus.core.util.VmlDrawing;
import com.polus.core.vo.CommonVO;
import com.polus.core.vo.LookUp;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Transactional
@Service(value = "commonService")
public class CommonServiceImpl implements CommonService {

	protected static Logger logger = LogManager.getLogger(CommonServiceImpl.class.getName());

	protected static final String FISCAL_YEAR_MONTH_PARAMETER_NAME = "FISCAL_START_MONTH";
	protected static final String KC_GENERAL_NAMESPACE = "KC-GEN";
	protected static final String DOCUMENT_COMPONENT_NAME = "Document";
	protected static final String MONTH_KEY = "month";
	protected static final String YEAR_KEY = "year";
	private static final Integer PDF_WIDTH_PERCENTAGE = 95;

	@Autowired
	private CommonDao commonDao;

	@Value("${fibicore.notification.attachment.filepath}")
	private String notificationAttachmentFilePath;

	@Autowired
	private ElasticSyncOperation elasticSyncOperation;

	@Autowired
	private ExportDao exportDao;

	@Override
	public Long getNextSequenceNumber(String sequenceName) {
		return commonDao.getNextSequenceNumber(sequenceName);
	}

	@Override
	public boolean getParameterValueAsBoolean(String parameterName) {
		return commonDao.getParameterValueAsBoolean(parameterName);
	}

	@Override
	public Integer getCurrentFiscalYear() {
		return getCurrentFiscalData(null).get(YEAR_KEY);
	}

	@Override
	public Integer getCurrentFiscalMonthForDisplay() {
		return getCurrentFiscalData(null).get(MONTH_KEY) + 1;
	}

	protected Integer getFiscalYearMonth() {
		return commonDao.getParameter(FISCAL_YEAR_MONTH_PARAMETER_NAME);
	}

	private int findMonth(int startingMonth, int currentMonth) {
		/**
		 * We are building an array of integers. The array position number is the fiscal
		 * month position of the calendar month. The array values are the calendar
		 * months. So an example with a fiscal year starting in September would be as
		 * follows: YEAR[0] = Calendar.September Year[1] = Calendar.October Year[11 =
		 * Calendar.August
		 */
		int nextMonth = startingMonth;
		int[] YEAR = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
		for (int i = 0; i < 12; i++) {
			YEAR[i] = nextMonth;
			if (nextMonth == 11) {
				nextMonth = 0;
			} else {
				nextMonth++;
			}
		}
		for (int i : YEAR) {
			if (YEAR[i] == currentMonth) {
				return i;
			}
		}
		throw new IllegalArgumentException("Could not find the current month: " + currentMonth);
	}

	protected Map<String, Integer> getCurrentFiscalData(Calendar calendar) {
		Map<String, Integer> data = new HashMap<String, Integer>();

		if (calendar == null) {
			calendar = Calendar.getInstance();
		}
		Integer fiscalStartMonth = getFiscalYearMonth();
		// assuming July is the fiscal start month
		if (calendar.get(Calendar.MONTH) == fiscalStartMonth.intValue()) {
			// July 1st, 2012, is the 1st month of FY 2013
			data.put(MONTH_KEY, findMonth(fiscalStartMonth, calendar.get(Calendar.MONTH)));
			if (fiscalStartMonth.equals(Calendar.JANUARY)) {
				data.put(YEAR_KEY, calendar.get(Calendar.YEAR));
			} else {
				data.put(YEAR_KEY, calendar.get(Calendar.YEAR) + 1);
			}
		} else if (calendar.get(Calendar.MONTH) > fiscalStartMonth.intValue()) {
			// August 1st 2012, is the second month of FY 2013
			data.put(MONTH_KEY, findMonth(fiscalStartMonth, calendar.get(Calendar.MONTH)));
			if (fiscalStartMonth.equals(Calendar.JANUARY)) {
				data.put(YEAR_KEY, calendar.get(Calendar.YEAR));
			} else {
				data.put(YEAR_KEY, calendar.get(Calendar.YEAR) + 1);
			}
		} else {
			// June 1st 2012, is the 12th month of FY 2012
			data.put(MONTH_KEY, findMonth(fiscalStartMonth, calendar.get(Calendar.MONTH)));
			data.put(YEAR_KEY, calendar.get(Calendar.YEAR));
		}
		return data;
	}

	@Override
	public String hash(Object valueToHide) throws GeneralSecurityException {
		if (valueToHide != null && !StringUtils.isEmpty(valueToHide.toString())) {
			try {
				MessageDigest md = MessageDigest.getInstance(Constants.HASH_ALGORITHM);
				return new String(Base64.encodeBase64(md.digest(valueToHide.toString().getBytes(Constants.CHARSET))),
						Constants.CHARSET);
			} catch (UnsupportedEncodingException arg2) {
				return "";
			}
		} else {
			return "";
		}
	}

	@Override
	public String render(String text, Map<String, String> replacementParameters) {
		for (String key : replacementParameters.keySet()) {
			text = StringUtils.replace(text, key, replacementParameters.get(key));
		}
		return text;
	}

	@Override
	public String replaceHtmlFromEditor(String htmlContent) {
		return htmlContent.toString().replaceAll("\\<.*?>", "");
	}

	@Override
	public void setNotificationRecipients(String recipient, String recipientType,
			Set<NotificationRecipient> dynamicEmailrecipients) {
		NotificationRecipient notificationRecipient = new NotificationRecipient();
		notificationRecipient.setRecipientPersonId(recipient);
		notificationRecipient.setRecipientType(recipientType);
		dynamicEmailrecipients.add(notificationRecipient);
	}

	@SuppressWarnings("resource")
	@Override
	public MultipartFile uploadMedia(String file, String name, Integer remaining, Integer length, String timestamp,
			String userId, String contentType) {
		try {
			new File("uploads").mkdir();
			FileOutputStream fileOutputStream = new FileOutputStream(
					"uploads\\" + userId + '_' + timestamp + '_' + name.toString(), true);
			if (length != null && remaining != null && (length > 0 && remaining <= 0)) {
				fileOutputStream.write(java.util.Base64.getDecoder().decode(file.getBytes()));
				fileOutputStream.close();
				String fileName = name;
				String originalFileName = name;
				File attachment = new File("uploads\\" + userId + '_' + timestamp + '_' + name.toString());
				FileInputStream input = new FileInputStream(attachment);
//				MultipartFile result = new MockMultipart(fileName, originalFileName, contentType,
//						IOUtils.toByteArray(input));
				input.close();
				cleanDirectory(new File("uploads"), attachment);
				return null;
			}
			fileOutputStream.write(java.util.Base64.getDecoder().decode(file.getBytes()));
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	void cleanDirectory(File dir, File fi) {
		for (File file : dir.listFiles()) {
			if (!file.getName().equals(fi.getName())) {
				try {
					Files.deleteIfExists(Paths.get(file.getAbsolutePath()));
				} catch (IOException e) {
					return;
				}
			}

		}
	}

	@Override
	public String getLookUpDatas(String lookUpTableName, String lookUpTableColumnName) {
		List<LookUp> lookUpDatas = commonDao.getLookUpDatas(lookUpTableName, lookUpTableColumnName);
		return commonDao.convertObjectToJSON(lookUpDatas);
	}

	@Override
	public String convertDateFormatBasedOnTimeZone(Long dateValue, String dateFormat) {
		Date date = new Date(dateValue);
		String formattedDate = new SimpleDateFormat(dateFormat).format(commonDao.adjustTimezone(date));
		return formattedDate;
	}

	@Override
	public void setNotificationRecipientsforNonEmployees(String emailAddress, String recipientType,
			Set<NotificationRecipient> dynamicEmailrecipients) {
		NotificationRecipient notificationRecipient = new NotificationRecipient();
		notificationRecipient.setEmailAddress(emailAddress);
		notificationRecipient.setRecipientType(recipientType);
		dynamicEmailrecipients.add(notificationRecipient);
	}

	@Override
	public File createfileInUploads(XSSFWorkbook workbook, String fileName) {
		File report = null;
		try {
			File directory = new File(notificationAttachmentFilePath + File.separator + "NotificationAttatchments");
			directory.mkdir();
			String fileNameWithPath = File.separator + fileName;
			report = new File(directory, fileNameWithPath);
			FileOutputStream outputStream = new FileOutputStream(report);
			workbook.write(outputStream);
			workbook.close();
			outputStream.close();
		} catch (Exception e) {
			logger.error("error in createfileInUploads {}", e.getMessage());
		}
		return report;
	}

	@Override
	public Object getValueBasedOnColumnName(Object entityObject, String referanceColumn) {
		Object value = null;
		try {
			Class<? extends Object> className = entityObject.getClass();
			for (Field field : className.getDeclaredFields()) {
				Column col = field.getAnnotation(Column.class);
				if (col != null && referanceColumn.equals(col.name())) {
					value = invokeGetter(entityObject, field.getName());
				}
			}
		} catch (Exception e) {
			logger.error("Error occuerd in invokeGetter : {}", e.getMessage());
		}
		return value;
	}

	public Object invokeGetter(Object entityObject, Object fieldName) {
		PropertyDescriptor pd;
		try {
			pd = new PropertyDescriptor((String) fieldName, entityObject.getClass());
			return pd.getReadMethod().invoke(entityObject);
		} catch (Exception e) {
			logger.error("Error occuerd in invokeGetter : {}", e.getMessage());
			return null;
		}
	}

	@Override
	public Integer addFilesToZipFolder(Integer index, String fileName, ZipOutputStream zos) throws IOException {
		try {
			zos.putNextEntry(new ZipEntry(fileName));
		} catch (ZipException e) {
			if (e.getMessage().contains("duplicate entry")) {
				zos.putNextEntry(new ZipEntry(chageDupliacteName(fileName, ++index)));
			}
		}
		return index;
	}

	private String chageDupliacteName(String fileName, Integer index) {
		int extIndex = fileName.lastIndexOf('.');
		return new StringBuilder().append(fileName.substring(0, extIndex)).append("(").append(index).append(")")
				.append(fileName.substring(extIndex)).toString();
	}

	@Override
	public Claims getLoginPersonDetailFromJWT(HttpServletRequest request) {
		return Jwts.parser().setSigningKey(Constants.SECRET).parseClaimsJws(getJwtFromRequest(request)).getBody();
	}

	public String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader(Constants.HEADER_STRING);
		if (org.springframework.util.StringUtils.hasText(bearerToken)
				&& bearerToken.startsWith(Constants.TOKEN_PREFIX)) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}

    @Override
    public List<ResearchTypeArea> findResearchTypeArea(CommonVO vo) {
        return commonDao.findResearchTypeArea(vo);
    }

    @Override
    public List<ResearchTypeSubArea> findResearchTypeSubArea(CommonVO vo) {
        return commonDao.findResearchTypeSubArea(vo);
    }

	@Override
	public void addDetailsInHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		try {
			Header header;
			InputStream is;
			byte[] bytes;
			int pictureIdx;
			header = sheet.getHeader();
			header.setLeft("&G");
			header.setCenter("&K000000&12" + Constants.REPORT_HEADER);
			Footer footer;
			footer = sheet.getFooter();
			footer.setCenter(Constants.REPORT_FOOTER);
			Resource resource = new ClassPathResource("logo.png");
			is = resource.getInputStream();
			bytes = IOUtils.toByteArray(is);
			pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			is.close();
			sheet.setMargin(org.apache.poi.ss.usermodel.Sheet.TopMargin, 1);
			// create header picture from picture data of this workbook
			createPictureForHeader(sheet, pictureIdx, "logo", 1, "LH", sheet.getSheetName());
		} catch (Exception e) {
			logger.info("Error Occured in createPictureForHeader : {}", e.getMessage());
		}
	}

	private void createPictureForHeader(XSSFSheet sheet, int pictureIdx, String pictureTitle, int vmlIdx,
			String headerPos, String sheetName) {
		try {
			OPCPackage opcpackage = sheet.getWorkbook().getPackage();
			// creating /xl/drawings/vmlDrawing1.vml
			String partSheetName = new StringBuilder("/xl/drawings/vmlDrawing").append(vmlIdx)
					.append(sheetName.replaceAll("\\s", "")).append(".vml").toString();
			PackagePartName partname = PackagingURIHelper.createPartName(partSheetName);
			PackagePart part = opcpackage.createPart(partname,
					"application/vnd.openxmlformats-officedocument.vmlDrawing");
			// creating new VmlDrawing
			VmlDrawing vmldrawing = new VmlDrawing(part);
			// creating the relation to the picture in
			// /xl/drawings/_rels/vmlDrawing1.vml.rels
			XSSFPictureData picData = sheet.getWorkbook().getAllPictures().get(pictureIdx);
			String rIdPic = vmldrawing.addRelation(null, XSSFRelation.IMAGES, picData).getRelationship().getId();
			// get image dimension
			ByteArrayInputStream is = new ByteArrayInputStream(picData.getData());
			// setting the image width = 3cm and height = 1.5 cm in pixels
			java.awt.Dimension imageDimension = new java.awt.Dimension(162, 56);
			is.close();
			// updating the VmlDrawing
			vmldrawing.setRelationIdPic(rIdPic);
			vmldrawing.setPictureTitle(pictureTitle);
			vmldrawing.setImageDimension(imageDimension);
			vmldrawing.setHeaderPosition(headerPos);
			// creating the relation to /xl/drawings/vmlDrawing1.xml in
			String rIdExtLink = sheet.getWorkbook().getSheet(sheetName)
					.addRelation(null, XSSFRelation.VML_DRAWINGS, vmldrawing).getRelationship().getId();
			sheet.getWorkbook().getSheet(sheetName).getCTWorksheet().addNewLegacyDrawingHF().setId(rIdExtLink);
		} catch (Exception e) {
			logger.info("Error Occured in createPictureForHeader : {}", e.getMessage());
		}
	}

	@Override
	public PdfWriter addPdfHeaderAndFooter(PdfWriter writer) {
		PdfHeaderFooterPageEvent event = new PdfHeaderFooterPageEvent(Constants.HEADER_Y_POSITION,
				Constants.IMAGE_X_POSITION, Constants.IMAGE_Y_POSITION);
		writer.setPageEvent(event);
		return writer;
	}

	@Override
	public List<LookUp> getAllLookUpWindowDetails() {
		List<LookupWindow> lookupWindows = commonDao.getAllLookUpWindows();
		return setLookUpDatas(lookupWindows);
	}

	private List<LookUp> setLookUpDatas(List<LookupWindow> lookupWindows) {
		List<LookUp> lookups = new ArrayList<>();
		lookupWindows.forEach(lookupWindow -> {
			LookUp lookup = new LookUp();
			if (lookupWindow.getDataTypeCode().equals("9")) {
				List<String> userLookups = commonDao.getUserDeffinedLookup();
				userLookups.forEach(userLookup -> {
					LookUp userDefinedLookup = new LookUp();
					userDefinedLookup.setCode(lookupWindow.getTableName() + "#" + userLookup);
					userDefinedLookup.setDescription(userLookup);
					userDefinedLookup.setDataType(lookupWindow.getDataTypeCode());
					lookups.add(userDefinedLookup);
				});
			} else if (lookupWindow.getDataTypeCode().equals("8")) {
				lookup.setCode(lookupWindow.getTableName() + "#" + lookupWindow.getColumnName());
				lookup.setDescription(lookupWindow.getDescription());
				lookup.setDataType(lookupWindow.getDataTypeCode());
				lookups.add(lookup);
			} else {
				lookup.setCode(lookupWindow.getColumnName());
				lookup.setDescription(lookupWindow.getDescription());
				lookup.setDataType(lookupWindow.getDataTypeCode());
				lookups.add(lookup);
			}
		});
		return lookups;
	}

	@Override
	public String getSponsorFormatBySponsorDetail(String sponsorCode, String sponsorName, String sponsorAcronym) {
		String sponsorFormat = Constants.SPONSOR_FORMAT;
		if (sponsorCode != null) {
			sponsorFormat = sponsorFormat.replace("SPONSOR_CODE", sponsorCode);
		} else {
			sponsorFormat = sponsorFormat.replace("SPONSOR_CODE", "");
		}
		if (sponsorName != null) {
			sponsorFormat = sponsorFormat.replace("SPONSOR_NAME", sponsorName);
		} else {
			sponsorFormat = sponsorFormat.replace("SPONSOR_NAME", "");
		}
		if (sponsorAcronym != null) {
			sponsorFormat = sponsorFormat.replace("SPONSOR_ACRONYM", sponsorAcronym);
		} else {
			sponsorFormat = sponsorFormat.replace("(SPONSOR_ACRONYM)", "");
		}
		return sponsorFormat;
	}

	@Override
	public boolean getWebSocketConfigurationValue(String configKey) {
		try {
			WebSocketConfiguration socketConfiguration = commonDao.getWebSocketConfigurationValue(configKey);
			String value = socketConfiguration != null ? socketConfiguration.getConfigurationValue() : null;
			if (value == null) {
				logger.info("web socket config value is not set or empty. key is : {}", configKey);
				value = "false";
			}
			return Truth.strToBooleanIgnoreCase(value);
		} catch (Exception e) {
			logger.error("error occured in getWebSocketConfigurationValue for key : {}", configKey);
			return false;
		}
	}

	@Override
	public List<Country> getCountryLookUp() {
		return commonDao.getCountryLookUp();
	}

	@Override
	public String getUnitFormatByUnitDetail(String unitNumber, String unitName) {
		String unitFormat = Constants.UNIT_FORMAT;
		if (unitNumber != null) {
			unitFormat = unitFormat.replace("UNIT_NUMBER", unitNumber);
		} else {
			unitFormat = unitFormat.replace("UNIT_NUMBER", "");
		}
		if (unitName != null) {
			unitFormat = unitFormat.replace("UNIT_NAME", unitName);
		} else {
			unitFormat = unitFormat.replace("UNIT_NAME", "");
		}
		return unitFormat;
	}

	@Override
	public ResponseEntity<Object> getAllLetterTemplateTypes(CommonVO vo) {
		List<LetterTemplateType> letterTemplateTypes = new ArrayList<>();
		commonDao.fetchAllLetterTemplateTypes(vo.getSearchString()).forEach(obj -> {
			Object[] tempObj = (Object[]) obj;
			LetterTemplateType letterTemplateType = new LetterTemplateType();
			letterTemplateType.setLetterTemplateTypeCode((String) tempObj[0]);
			letterTemplateType.setFileName((String) tempObj[1]);
			letterTemplateType.setContentType((String) tempObj[2]);
			letterTemplateType.setPrintFileType((String) tempObj[3]);
			letterTemplateTypes.add(letterTemplateType);
		});
		return new ResponseEntity<>(letterTemplateTypes, HttpStatus.OK);
	}

	@Override
	public String hashBySha(Object valueToHide) throws GeneralSecurityException {
		if (valueToHide != null && !StringUtils.isEmpty(valueToHide.toString())) {
			try {
				MessageDigest md = MessageDigest.getInstance(Constants.HASH_ALGORITHM_SHA);
				return new String(Base64.encodeBase64(md.digest(valueToHide.toString().getBytes(Constants.CHARSET))),
						Constants.CHARSET);
			} catch (UnsupportedEncodingException arg2) {
				return "";
			}
		} else {
			return "";
		}
	}

	@Override
	public void checkFileFormat(MultipartFile[] files, String fileType) {
		StringBuffer notSupportedAttachments = new StringBuffer();
		List<FileType> acceptedExtensions = commonDao.getAcceptedExtensions(fileType);
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File file = new File(files[i].getOriginalFilename());
				String fileName = file.getName();
				if (!(acceptedExtensions.get(0).getExtension().contains(FilenameUtils.getExtension(fileName)))) {
					notSupportedAttachments.append(fileName + " :File format not supported");
					notSupportedAttachments.append("\n");
				}
			}
			if (notSupportedAttachments.length() > 0) {
				throw new ApplicationException(notSupportedAttachments.toString(), "File not supported",
						HttpStatus.BAD_REQUEST);
			}
		}
	}

	@Override
	public void checkFileFormat(MultipartFile file, String string) {
		List<FileType> acceptedExtensions = commonDao.getAcceptedExtensions("General");
		if (file != null) {
			String fileName = file.getName();
			if (!(acceptedExtensions.get(0).getExtension().contains(FilenameUtils.getExtension(fileName)))) {
				throw new ApplicationException(fileName + " : File format not supported", "File not supported",
						HttpStatus.BAD_REQUEST);
			}
		}
	}

    @Override
	public String getLogstashStatus() throws IOException {
    	return elasticSyncOperation.getLogstashStatus();
	}

    @Override
	public String bulkLogstashSync(String indexName) {
		elasticSyncOperation.sendSyncRequest(null, "BULKSYNC", indexName);
		return commonDao.convertObjectToJSON("Success");
	}

	@Override
	public Map<String, Map<String, List<Datapoint>>> queueMatrixDetails() {
		return elasticSyncOperation.queueMatrixDetails();
	}

	@Override
	public ResponseEntity<byte[]> setAttachmentContent(String fileName, byte[] data) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/octet-stream"));
		headers.setContentDispositionFormData(fileName, fileName);
		headers.setContentLength(data.length);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		headers.setPragma("public");
		return new ResponseEntity<>(data, headers, HttpStatus.OK);
	}

	@Override
	public XSSFWorkbook getXSSFWorkbookForExport(CommonVO vo) throws Exception {
		logger.info("--------- getXSSFWorkbookForExport ---------");
		XSSFWorkbook workbook = new XSSFWorkbook();
		List<Object[]> dashboardData = new ArrayList<>();
		String requestType = vo.getTabIndex();
		String tabName = vo.getTabName();
		logger.info("requestType : {}", requestType);
		logger.info("tabName : {}", tabName);
		if (requestType.equals("PERSON")) {
			dashboardData = exportDao.getExportDataOfPersonForDownload(vo, dashboardData);
			XSSFSheet sheet = workbook.createSheet("Person Details");
			addDetailsInHeader(workbook, sheet);
			Object[] tableHeadingRow = { "Id#", "firstName" };
			prepareExcelSheet(dashboardData, sheet, tableHeadingRow, workbook, vo);
		} else if (requestType.equals("ROLODEX")) {
			dashboardData = exportDao.getDataOfRolodexForDownload(vo, dashboardData);
			XSSFSheet sheet = workbook.createSheet("Rolodex Details");
			addDetailsInHeader(workbook, sheet);
			Object[] tableHeadingRow = { "Id#", "Title" };
			prepareExcelSheet(dashboardData, sheet, tableHeadingRow, workbook, vo);
		}
		return workbook;
	}

	@Override
	public void prepareExcelSheet(List<Object[]> data, XSSFSheet sheet, Object[] tableHeadingRow,
			XSSFWorkbook workbook, CommonVO vo) {
		XSSFCellStyle tableBodyStyle = workbook.createCellStyle();
		String documentHeading = vo.getDocumentHeading();
		logger.info("documentHeading : {}", documentHeading);
		prepareExcelSheetHeader(sheet, tableHeadingRow, documentHeading, workbook, tableBodyStyle);
		int rowNumber = 2;
		for (Object[] objectArray : data) {
			Row row = sheet.createRow(rowNumber++);
			int cellNumber = 0;
			for (Object objectData : objectArray) {
				Cell cell = row.createCell(cellNumber++);
				cell.setCellStyle(tableBodyStyle);
				if (objectData instanceof String)
					cell.setCellValue((String) objectData);
				else if (objectData instanceof Integer)
					cell.setCellValue((Integer) objectData);
				else if (objectData instanceof BigInteger) {
					String stringValue = ((BigInteger) objectData).toString();
					cell.setCellValue((String) stringValue);
				} else if (objectData instanceof BigDecimal) {
					DecimalFormat decimalFormat = new DecimalFormat(Constants.NUMBER_FORMAT_WITH_DECIMAL);
					String stringValue = ((BigDecimal) objectData).toString();
					XSSFCellStyle headStyle = workbook.createCellStyle();
					headStyle.setAlignment(HorizontalAlignment.RIGHT);
					cell.setCellStyle(headStyle);
					cell.setCellValue(decimalFormat.format(new BigDecimal(stringValue)));
				} else if (objectData instanceof Date) {
					if (objectData != null) {
						Date date = (Date) objectData;
						String dateValue = convertDateFormatBasedOnTimeZone(date.getTime(), Constants.DEFAULT_DATE_FORMAT);
						cell.setCellValue((String) dateValue);
					}
				} else if (objectData == null) {
					cell.setCellValue((String) " ");
				}
			}
		}
		// Adjust size of table columns according to length of table data.
		autoSizeColumns(workbook);
	}

	private XSSFWorkbook prepareExcelSheetHeader(XSSFSheet sheet, Object[] tableHeadingRow, String documentHeading,
			XSSFWorkbook workbook, XSSFCellStyle tableBodyStyle) {
		int headingCellNumber = 0;
		Row headerRow = sheet.createRow(0);
		Cell headingCell = headerRow.createCell(0);
		headingCell.setCellValue((String) documentHeading);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, tableHeadingRow.length - 1));
		XSSFFont headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 15);
		XSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setFont(headerFont);
		headingCell.setCellStyle(headerStyle);
		// Table head style and font creation code.
		Row tableHeadRow = sheet.createRow(1);
		XSSFCellStyle tableHeadStyle = workbook.createCellStyle();
		tableHeadStyle.setBorderTop(BorderStyle.HAIR);
		tableHeadStyle.setBorderBottom(BorderStyle.HAIR);
		tableHeadStyle.setBorderLeft(BorderStyle.HAIR);
		tableHeadStyle.setBorderRight(BorderStyle.HAIR);
		XSSFFont tableHeadFont = workbook.createFont();
		tableHeadFont.setBold(true);
		tableHeadFont.setFontHeightInPoints((short) 12);
		tableHeadStyle.setFont(tableHeadFont);
		// Table body style and font creation code.
		tableBodyStyle.setBorderTop(BorderStyle.HAIR);
		tableBodyStyle.setBorderBottom(BorderStyle.HAIR);
		tableBodyStyle.setBorderLeft(BorderStyle.HAIR);
		tableBodyStyle.setBorderRight(BorderStyle.HAIR);
		XSSFFont tableBodyFont = workbook.createFont();
		tableBodyFont.setFontHeightInPoints((short) 12);
		tableBodyStyle.setFont(tableBodyFont);
		// Set table head data to each column.
		for (Object heading : tableHeadingRow) {
			Cell cell = tableHeadRow.createCell(headingCellNumber++);
			cell.setCellValue((String) heading);
			cell.setCellStyle(tableHeadStyle);
		}
		return workbook;
	}

	private void autoSizeColumns(XSSFWorkbook workbook) {
		int numberOfSheets = workbook.getNumberOfSheets();
		for (int i = 0; i < numberOfSheets; i++) {
			XSSFSheet sheet = workbook.getSheetAt(i);
			if (sheet.getPhysicalNumberOfRows() > 0) {
				Row row = sheet.getRow(1);
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					int columnIndex = cell.getColumnIndex();
					sheet.autoSizeColumn(columnIndex);
				}
			}
		}
	}

	@Override
	public ResponseEntity<byte[]> getResponseEntityForDownload(CommonVO vo, XSSFWorkbook workbook) throws Exception {
		logger.info("--------- getResponseEntityForDownload ---------");
		byte[] byteArray = null;
		String exportType = vo.getExportType();
		String documentHeading = vo.getDocumentHeading();
		logger.info("exportType : {}", exportType);
		logger.info("documentHeading : {}", documentHeading);
		if (exportType.equals("pdf")) {
			byteArray = generatePDFFileByteArray(documentHeading, workbook);
		} else {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			workbook.write(bos);
			byteArray = bos.toByteArray();
		}
		return getResponseEntity(byteArray);
	}

	@SuppressWarnings("incomplete-switch")
	public byte[] generatePDFFileByteArray(String documentHeading, XSSFWorkbook workbook) {
		byte[] byteArray = null;
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			if (workbook.getNumberOfSheets() != 0) {
				XSSFSheet worksheet = workbook.getSheetAt(0);
				Iterator<Row> rowIterator = worksheet.iterator();
				Document document = new Document();
				document.setPageSize(PageSize.A4.rotate());
				document.setMargins(40, 40, 80, 40);
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				addPdfHeaderAndFooter(writer);
				document.open();
				Font pdfTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
				Paragraph paragraph = new Paragraph(documentHeading, pdfTitleFont);
				paragraph.setAlignment(Element.ALIGN_CENTER);
				document.add(paragraph);
				document.add(Chunk.NEWLINE);
				int columnCount = getColumnsCount(worksheet);
				PdfPTable table = new PdfPTable(columnCount);
				table.setWidthPercentage(PDF_WIDTH_PERCENTAGE);
				PdfPCell table_cell;
				Font tableHeadingFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
				Font tableBodyFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();
					int rowIndex = row.getRowNum();
					Iterator<Cell> cellIterator = row.cellIterator();
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						switch (cell.getCellType()) {
						case STRING:
							if (rowIndex == 0) {
							} else if (rowIndex == 1) {
								table_cell = new PdfPCell(new Phrase(cell.getStringCellValue(), tableHeadingFont));
								table.addCell(table_cell);
							} else {
								table_cell = new PdfPCell(new Phrase(cell.getStringCellValue(), tableBodyFont));
								table.addCell(table_cell);
							}
							break;
						case NUMERIC:
							Double cellValueInDouble = cell.getNumericCellValue();
							Integer cellValueInInteger = cellValueInDouble.intValue();
							String cellValueInString = Integer.toString(cellValueInInteger);
							table_cell = new PdfPCell(new Phrase(cellValueInString, tableBodyFont));
							table.addCell(table_cell);
							break;
						}
					}
				}
				document.add(table);
				document.close();
			}
			byteArray = byteArrayOutputStream.toByteArray();
		} catch (Exception e) {
			logger.error("Error in method generatePDFFileByteArray", e);
		}
		return byteArray;
	}

	private int getColumnsCount(XSSFSheet xssfSheet) {
		int columnCount = 0;
		Iterator<Row> rowIterator = xssfSheet.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			List<Cell> cells = new ArrayList<>();
			Iterator<Cell> cellIterator = row.cellIterator();
			while (cellIterator.hasNext()) {
				cells.add(cellIterator.next());
			}
			for (int cellIndex = cells.size(); cellIndex >= 1; cellIndex--) {
				Cell cell = cells.get(cellIndex - 1);
				if (cell.toString().trim().isEmpty()) {
					cells.remove(cellIndex - 1);
				} else {
					columnCount = cells.size() > columnCount ? cells.size() : columnCount;
					break;
				}
			}
		}
		return columnCount;
	}

	private ResponseEntity<byte[]> getResponseEntity(byte[] bytes) {
		ResponseEntity<byte[]> attachmentData = null;
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType("application/octet-stream"));
			headers.setContentLength(bytes.length);
			headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
			headers.setPragma("public");
			attachmentData = new ResponseEntity<>(bytes, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error in method getResponseEntity", e);
		}
		return attachmentData;
	}

}
