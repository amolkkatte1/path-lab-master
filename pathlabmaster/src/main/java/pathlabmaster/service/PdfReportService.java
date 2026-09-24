package pathlabmaster.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import pathlabmaster.dao.BillMasterRepository;
import pathlabmaster.dao.ClientConfigRepository;
import pathlabmaster.dao.DoctorMasterRepository;
import pathlabmaster.dao.MdDoctorMasterRepository;
import pathlabmaster.dao.PatientMasterRepository;
import pathlabmaster.dao.ReportMasterRepository;

import pathlabmaster.pojo.BillMaster;
import pathlabmaster.pojo.ClientConfig;
import pathlabmaster.pojo.DoctorMaster;
import pathlabmaster.pojo.MdDoctorMaster;
import pathlabmaster.pojo.ParameterDetails;
import pathlabmaster.pojo.PatientMaster;
import pathlabmaster.pojo.PdfResponse;
import pathlabmaster.pojo.ReportMaster;
import pathlabmaster.utility.Constants;

@Service
public class PdfReportService {

	@Autowired
	private ReportMasterRepository reportMasterRepo;

	@Autowired
	private BillMasterRepository billMasterRepo;

	@Autowired
	private PatientMasterRepository patientMasterRepo;

	@Autowired
	private DoctorMasterRepository doctorMasterRepo;

	@Autowired
	private ClientConfigRepository clientConfigRepo;
	@Autowired
	private QrCodeService qrCodeService;
	@Autowired
	private MdDoctorMasterRepository mdDoctorRepo;
	// ============================================================
	// GENERATE PDF
	// ============================================================

	public byte[] generatePdf(String fromDate, String toDate, Long labId, String firstName, String lastName,
			Long patientId, String doctorName, Long doctorId) throws IOException {

		List<ReportMaster> reportMasterList = reportMasterRepo.filterReports(labId, fromDate, toDate, patientId);

		List<BillMaster> billMasters = billMasterRepo.filterBills(labId, fromDate, toDate);

		List<PatientMaster> patientMasterList = patientMasterRepo.filterPatients(labId, fromDate, toDate, firstName,
				lastName, patientId, doctorName, doctorId);

		List<DoctorMaster> doctorList = doctorMasterRepo.findByLabId(labId);

		if (reportMasterList == null) {
			reportMasterList = List.of();
		}

		if (billMasters == null) {
			billMasters = List.of();
		}

		if (patientMasterList == null) {
			patientMasterList = List.of();
		}

		if (doctorList == null) {
			doctorList = List.of();
		}

		Map<Long, DoctorMaster> doctorMap = new HashMap<>();

		for (DoctorMaster doctor : doctorList) {

			if (doctor != null && doctor.getDoctorId() != null) {

				doctorMap.putIfAbsent(doctor.getDoctorId(), doctor);
			}
		}

		Map<Long, BillMaster> billMap = new HashMap<>();

		for (BillMaster bill : billMasters) {

			if (bill != null && bill.getPatientId() != null) {

				billMap.putIfAbsent(bill.getPatientId(), bill);
			}
		}

		Map<Long, ReportMaster> reportMap = new HashMap<>();

		for (ReportMaster report : reportMasterList) {

			if (report != null && report.getPatientId() != null) {

				reportMap.putIfAbsent(report.getPatientId(), report);
			}
		}

		String labName = "";

		if (!patientMasterList.isEmpty()) {

			labName = patientMasterList.get(0).getLabName();
		}

		if (labName == null || labName.isBlank()) {

			labName = "Lab Report";
		}

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

			Document document = new Document(PageSize.A4, 15, 15, 15, 15);

			PdfWriter.getInstance(document, outputStream);

			document.open();

			Font labNameFont = new Font(Font.HELVETICA, 14, Font.BOLD);

			Font dateFont = new Font(Font.HELVETICA, 8, Font.NORMAL);

			Font headerFont = new Font(Font.HELVETICA, 5.5f, Font.BOLD);

			Font dataFont = new Font(Font.HELVETICA, 5.5f, Font.NORMAL);

			Font totalFont = new Font(Font.HELVETICA, 6, Font.BOLD);

			Paragraph labParagraph = new Paragraph(labName, labNameFont);

			labParagraph.setAlignment(Element.ALIGN_CENTER);

			labParagraph.setSpacingAfter(3);

			document.add(labParagraph);

			String dateRange = "From Date: " + (fromDate != null ? fromDate : "") + "    To Date: "
					+ (toDate != null ? toDate : "");

			Paragraph dateParagraph = new Paragraph(dateRange, dateFont);

			dateParagraph.setAlignment(Element.ALIGN_CENTER);

			dateParagraph.setSpacingAfter(6);

			document.add(dateParagraph);

			PdfPTable table = new PdfPTable(10);

			table.setWidthPercentage(100);

			table.setWidths(new float[] { 4f, 8f, 10f, 11f, 22f, 13f, 7f, 8f, 9f, 8f });

			String[] headers = { "Sr No", "Registration Date", "Reg. No", "Patient Name", "Test List",
					"Referred Doctor Name", "Total Amount", "Collected Amount", "Collected By Doctor", "Sharing" };

			for (String header : headers) {

				PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));

				cell.setHorizontalAlignment(Element.ALIGN_CENTER);

				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

				cell.setPadding(2);

				cell.setBorder(Rectangle.BOX);

				table.addCell(cell);
			}

			table.setHeaderRows(1);

			BigDecimal grandTotalAmount = BigDecimal.ZERO;

			BigDecimal grandCollectedAmount = BigDecimal.ZERO;

			BigDecimal grandSharing = BigDecimal.ZERO;

			BigDecimal grandCollectedByDoctor = BigDecimal.ZERO;

			int srNo = 1;

			for (PatientMaster patient : patientMasterList) {

				if (patient == null) {
					continue;
				}

				Long currentPatientId = patient.getPatientId();

				if (currentPatientId == null) {
					continue;
				}

				ReportMaster report = reportMap.get(currentPatientId);

				if (report == null) {
					continue;
				}

				BillMaster bill = billMap.get(currentPatientId);

				if (bill == null) {
					continue;
				}

				String registrationDate = "";

				String createdAt = patient.getCreatedAt();

				if (createdAt != null && createdAt.length() >= 10) {

					registrationDate = createdAt.substring(0, 10);
				}

				String patientName = "";

				if (patient.getFirstName() != null) {

					patientName = patient.getFirstName();
				}

				if (patient.getLastName() != null && !patient.getLastName().isBlank()) {

					if (!patientName.isBlank()) {
						patientName += " ";
					}

					patientName += patient.getLastName();
				}

				String testList = "";

				if (report.getReportNameList() != null && !report.getReportNameList().isEmpty()) {

					testList = String.join(", ", report.getReportNameList().keySet());
				}

				String doctorName1 = patient.getDoctorName();

				if (doctorName1 == null) {
					doctorName1 = "";
				}

				BigDecimal totalAmount = bill.getTotalAmount();

				if (totalAmount == null) {
					totalAmount = BigDecimal.ZERO;
				}

				BigDecimal collectedAmount = bill.getPaymentReceived();

				if (collectedAmount == null) {
					collectedAmount = BigDecimal.ZERO;
				}

				BigDecimal collectedByDoctor = bill.getCollectedByDoctor();

				if (collectedByDoctor == null) {
					collectedByDoctor = BigDecimal.ZERO;
				}

				Float sharingPercentage = 0.0f;

				Long currentDoctorId = patient.getDoctorId();

				if (currentDoctorId != null) {

					DoctorMaster doctor = doctorMap.get(currentDoctorId);

					if (doctor != null && doctor.getShairingPercentage() != null) {

						sharingPercentage = doctor.getShairingPercentage();
					}
				}

				BigDecimal sharing = BigDecimal.ZERO;

				BigDecimal doctorSharingPercentage = BigDecimal.valueOf(sharingPercentage.doubleValue());

				if (doctorSharingPercentage.compareTo(BigDecimal.ZERO) > 0) {

					sharing = collectedAmount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
							.multiply(doctorSharingPercentage).setScale(2, RoundingMode.HALF_UP);
				}

				addCell(table, String.valueOf(srNo++), dataFont, Element.ALIGN_CENTER);

				addCell(table, registrationDate, dataFont, Element.ALIGN_CENTER);

				addCell(table, String.valueOf(currentPatientId), dataFont, Element.ALIGN_LEFT);

				addCell(table, patientName, dataFont, Element.ALIGN_LEFT);

				addCell(table, testList, dataFont, Element.ALIGN_LEFT);

				addCell(table, doctorName1, dataFont, Element.ALIGN_LEFT);

				addCell(table, formatAmount(totalAmount), dataFont, Element.ALIGN_RIGHT);

				addCell(table, formatAmount(collectedAmount), dataFont, Element.ALIGN_RIGHT);

				addCell(table, formatAmount(collectedByDoctor), dataFont, Element.ALIGN_RIGHT);

				addCell(table, formatAmount(sharing), dataFont, Element.ALIGN_RIGHT);

				grandTotalAmount = grandTotalAmount.add(totalAmount);

				grandCollectedAmount = grandCollectedAmount.add(collectedAmount);

				grandCollectedByDoctor = grandCollectedByDoctor.add(collectedByDoctor);

				grandSharing = grandSharing.add(sharing);
			}

			PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL", totalFont));

			totalLabelCell.setColspan(6);

			totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

			totalLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

			totalLabelCell.setPadding(3);

			totalLabelCell.setBorder(Rectangle.BOX);

			table.addCell(totalLabelCell);

			addCell(table, formatAmount(grandTotalAmount), totalFont, Element.ALIGN_RIGHT);

			addCell(table, formatAmount(grandCollectedAmount), totalFont, Element.ALIGN_RIGHT);

			addCell(table, formatAmount(grandCollectedByDoctor), totalFont, Element.ALIGN_RIGHT);

			addCell(table, formatAmount(grandSharing), totalFont, Element.ALIGN_RIGHT);

			document.add(table);

			document.close();

			return outputStream.toByteArray();
		}
	}

	// ============================================================
	// CREATE INDIVIDUAL REPORT PDF
	// ============================================================

	public PdfResponse createPdf(Long patientId, String reportIds, boolean headerRequired, boolean mdSignRequired) throws Exception {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		// =====================================================
		// GET PATIENT
		// =====================================================

		PatientMaster patientDetails = patientMasterRepo.findById(patientId).orElse(null);

		if (patientDetails == null) {
			throw new RuntimeException("Patient not found : " + patientId);
		}

		// =====================================================
		// CLIENT CONFIG
		// =====================================================

		ClientConfig clientConfig = clientConfigRepo.findByLabId(patientDetails.getLabId());

		// =====================================================
		// QR CONFIGURATION
		// =====================================================

		boolean isQrRequired = false;

		int qrCodePositionHorizantal = 2;
		int qrCodePositionVertical = 2;

		float qrSize = 50f;

		if (clientConfig != null) {

			isQrRequired = clientConfig.isQrCodeRequired();

			if (clientConfig.getQrCodeHorizantalPosition() != null) {
				qrCodePositionHorizantal = clientConfig.getQrCodeHorizantalPosition();
			}

			if (clientConfig.getQrCodeVerticalPosition() != null) {
				qrCodePositionVertical = clientConfig.getQrCodeVerticalPosition();
			}
		}
		
		// =====================================================
		// QR CODE
		//
		// Generate QR ONLY when required
		// =====================================================

		byte[] qrCode = null;

		if (isQrRequired) {

			String qrUrl = Constants.SELF_BASE_URL_PROD
					+ Constants.QR_CODE_URL.replaceFirst("\\{}", String.valueOf(patientId))
							.replaceFirst("\\{}", reportIds).replaceFirst("\\{}", String.valueOf(headerRequired));

			qrCode = qrCodeService.generateQRCode(qrUrl, 2, 2);
		}
		
		MdDoctorMaster mdDoctorDetails = mdDoctorRepo
		        .findFirstByLabIdAndIsActiveTrueOrderByCreatedAtDesc(patientDetails.getLabId())
		        .orElse(null);
		
		// =====================================================
		// TOP MARGIN
		// =====================================================

		int topMargin = 20;

		if (clientConfig != null && clientConfig.getReportTopSpace() != null) {

			topMargin = clientConfig.getReportTopSpace();
		}

		// =====================================================
		// BOTTOM MARGIN
		// =====================================================

		int bottomMargin = 20;

		if (clientConfig != null && clientConfig.getReportBottomSpace() != null) {

			bottomMargin = clientConfig.getReportBottomSpace();
		}

		// =====================================================
		// RESERVE QR FOOTER SPACE ONLY WHEN QR IS REQUIRED
		// =====================================================

		if (isQrRequired) {

			bottomMargin = Math.max(bottomMargin, 90);
		}

		// =====================================================
		// A4 DOCUMENT
		// =====================================================

		Document document = new Document(PageSize.A4, 30, 30, 78 + topMargin, bottomMargin);

		PdfWriter writer = PdfWriter.getInstance(document, outputStream);

		// =====================================================
		// FONTS
		// =====================================================

		Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

		Font boldFont = new Font(Font.HELVETICA, 10, Font.BOLD);

		Font normalFont1 = new Font(Font.HELVETICA, 9, Font.NORMAL);

		Font boldFont1 = new Font(Font.HELVETICA, 9, Font.BOLD);

		// =====================================================
		// GET REPORT
		// =====================================================

		ReportMaster reportDetails = reportMasterRepo.findByPatientIdAndLabId(patientId, patientDetails.getLabId());

		if (reportDetails == null) {

			throw new RuntimeException("Report not found for patient : " + patientId);
		}

		// =====================================================
		// PAGE HEADER / FOOTER
		// =====================================================

		ReportPageHeader pageHeader = new ReportPageHeader(patientId, patientDetails, reportDetails, boldFont,
				topMargin, qrCode, qrCodePositionHorizantal, qrCodePositionVertical, qrSize, isQrRequired);

		writer.setPageEvent(pageHeader);

		// =====================================================
		// OPEN DOCUMENT
		// =====================================================

		document.open();

		// =====================================================
		// REPORT IDS
		// =====================================================

		List<String> reportIdList = Arrays.asList(reportIds.split("\\|"));

		// =====================================================
		// COMPLETED TEST DATA
		// =====================================================

		Map<String, List<ParameterDetails>> reportOriginal = reportDetails.getCompletedTest();

		Map<String, List<ParameterDetails>> reports = new HashMap<>();

		if (reportOriginal != null) {

			for (Map.Entry<String, List<ParameterDetails>> entry : reportOriginal.entrySet()) {

				String id = entry.getKey();

				if (id != null && id.length() >= 17) {

					String shortId = id.substring(id.length() - 17);

					reports.put(shortId, entry.getValue());
				}
			}
		}

		// =====================================================
		// GROUP TESTS
		// =====================================================

		Map<String, List<List<ParameterDetails>>> groupedTests = new LinkedHashMap<>();

		for (String reportId : reportIdList) {

			if (reportId == null || reportId.trim().isEmpty()) {

				continue;
			}

			reportId = reportId.trim();

			List<ParameterDetails> testDetails = reports.get(reportId);

			if (testDetails == null || testDetails.isEmpty()) {

				continue;
			}

			// =================================================
			// SORT BY SEQUENCE
			// =================================================

			testDetails.sort(
					Comparator.comparing(ParameterDetails::getSequence, Comparator.nullsLast(Integer::compareTo)));

			// =================================================
			// GET GROUP NAME
			// =================================================

			String groupName = "";

			for (ParameterDetails parameter : testDetails) {

				if (parameter.getSequence() != null && parameter.getSequence() == 1) {

					groupName = safe(parameter.getParameterName());

					break;
				}
			}

			if (groupName == null || groupName.trim().isEmpty()) {

				groupName = "OTHER";
			}

			// =================================================
			// ADD TEST TO GROUP
			// =================================================

			groupedTests.computeIfAbsent(groupName, k -> new ArrayList<>()).add(testDetails);
		}

		// =====================================================
		// PRINT GROUPS
		// =====================================================

		int groupIndex = 0;

		for (Map.Entry<String, List<List<ParameterDetails>>> groupEntry : groupedTests.entrySet()) {

			String groupName = groupEntry.getKey();

			List<List<ParameterDetails>> testsInGroup = groupEntry.getValue();

			// =================================================
			// GROUP TITLE
			// =================================================

			addGroupTitle(document, groupName, boldFont);

			// =================================================
			// PRINT TESTS
			// =================================================

			for (int testIndex = 0; testIndex < testsInGroup.size(); testIndex++) {

				List<ParameterDetails> testDetails = testsInGroup.get(testIndex);

				// =================================================
				// REMOVE GROUP NAME PARAMETER
				// =================================================

				List<ParameterDetails> parametersForTest = new ArrayList<>();

				for (ParameterDetails parameter : testDetails) {

					if (parameter.getSequence() != null && parameter.getSequence() == 1) {

						continue;
					}

					parametersForTest.add(parameter);
				}

				// =================================================
				// CREATE TEST TABLE
				// =================================================

				PdfPTable testTable = createTestTable(parametersForTest, normalFont, boldFont, normalFont1, boldFont1);

				// =================================================
				// CALCULATE REQUIRED HEIGHT
				// =================================================

				float requiredTestHeight = calculateTestHeight(testDetails);

				requiredTestHeight += 4;

				// =================================================
				// AVAILABLE PAGE HEIGHT
				// =================================================

				float currentY = writer.getVerticalPosition(true);

				float availableHeight = currentY - document.bottomMargin();

				// =================================================
				// TEST DOES NOT FIT
				// =================================================

				if (requiredTestHeight > availableHeight) {

					document.newPage();

					// Print group name again
					// on new page
					addGroupTitle(document, groupName, boldFont);
				}

				// =================================================
				// TEST WRAPPER
				// =================================================

				PdfPTable testWrapper = new PdfPTable(1);

				testWrapper.setWidthPercentage(100);

				testWrapper.setKeepTogether(true);

				// =================================================
				// TEST CELL
				// =================================================

				PdfPCell testCell = new PdfPCell(testTable);

				testCell.setBorder(PdfPCell.NO_BORDER);

				testCell.setPadding(0);

				testWrapper.addCell(testCell);

				// =================================================
				// ADD TEST
				// =================================================

				document.add(testWrapper);
			}

			// =================================================
			// SPACE BETWEEN GROUPS
			// =================================================

			groupIndex++;

			if (groupIndex < groupedTests.size()) {

				Paragraph groupSpace = new Paragraph(" ");

				groupSpace.setLeading(2);

				document.add(groupSpace);
			}
		}

		// =====================================================
		// CLOSE DOCUMENT
		// =====================================================

		document.close();

		// =====================================================
		// FILE NAME
		// =====================================================

		String fileName = "Report-" + safe(patientDetails.getFirstName()) + " " + safe(patientDetails.getMiddleName())
				+ " " + safe(patientDetails.getLastName()) + ".pdf";

		// =====================================================
		// RESPONSE
		// =====================================================

		return new PdfResponse(outputStream.toByteArray(), fileName);
	}

	// ============================================================
	// ADD GROUP TITLE
	// ============================================================

	private void addGroupTitle(Document document, String groupName, Font boldFont) throws Exception {

		PdfPTable groupTitleTable = createTestTitleTable(groupName, boldFont);

		PdfPTable groupTitleWrapper = new PdfPTable(1);

		groupTitleWrapper.setWidthPercentage(100);

		PdfPCell groupTitleCell = new PdfPCell(groupTitleTable);

		groupTitleCell.setBorder(PdfPCell.NO_BORDER);

		groupTitleCell.setPadding(0);

		groupTitleWrapper.addCell(groupTitleCell);

		document.add(groupTitleWrapper);
	}

	// ============================================================
	// PATIENT TABLE
	// ============================================================

	private PdfPTable createPatientTable(Long patientId, PatientMaster patientDetails, ReportMaster reportDetails,
			Font boldFont) throws Exception {

		PdfPTable patientTable = new PdfPTable(4);

		patientTable.setWidthPercentage(100);

		patientTable.setWidths(new float[] { 15, 50, 15, 30 });

		addPatientCell(patientTable, "Reg No", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, ": " + patientId + " / OPD", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, "Sex / Age", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable,
				": " + safe(patientDetails.getGender()) + " / " + safe(patientDetails.getYear()) + "Y", boldFont,
				Element.ALIGN_RIGHT);

		addPatientCell(patientTable, "Name", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, ": " + safe(patientDetails.getFirstName()) + " "
				+ safe(patientDetails.getMiddleName()) + " " + safe(patientDetails.getLastName()), boldFont,
				Element.ALIGN_LEFT);

		addPatientCell(patientTable, "Reg Date", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, ": " + safe(patientDetails.getCreatedAt()), boldFont, Element.ALIGN_RIGHT);

		addPatientCell(patientTable, "Referred Dr", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, ": " + safe(patientDetails.getDoctorName()), boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, "Report Date", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, ": " + safe(reportDetails.getCreatedAt()), boldFont, Element.ALIGN_RIGHT);

		patientTable.addCell(createEmptyCell(4));

		return patientTable;
	}

	// ============================================================
	// TEST TITLE
	// ============================================================

	private PdfPTable createTestTitleTable(String testName, Font boldFont) {

		PdfPTable table = new PdfPTable(1);

		table.setWidthPercentage(100);

		PdfPCell cell = new PdfPCell(new Phrase(testName, boldFont));

		// IMPORTANT:
		// Use TOP | BOTTOM if you want borders.
		cell.setBorder(PdfPCell.TOP | PdfPCell.BOTTOM);

		cell.setBorderWidthTop(0.8f);

		cell.setBorderWidthBottom(0.8f);

		cell.setHorizontalAlignment(Element.ALIGN_CENTER);

		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		cell.setPaddingTop(3);

		cell.setPaddingBottom(3);

		cell.setPaddingLeft(2);

		cell.setPaddingRight(2);

		table.addCell(cell);

		return table;
	}

	// ============================================================
	// TEST TABLE
	// ============================================================

	private PdfPTable createTestTable(List<ParameterDetails> testDetails, Font normalFont, Font boldFont,
			Font normalFont1, Font boldFont1) {

		PdfPTable testTable = new PdfPTable(4);

		testTable.setWidthPercentage(100);

		testTable.setWidths(new float[] { 40, 15, 15, 30 });

		for (ParameterDetails parameter : testDetails) {

			// ====================================================
			// SEQUENCE 1
			// ====================================================

			if (parameter.getSequence() != null && parameter.getSequence() == 1) {

				continue;
			}

			String parameterName = safe(parameter.getParameterName());

			String value = safe(parameter.getValue());

			String unit = safe(parameter.getUnit());

			String referenceRange = getReferenceRange(parameter);

			Font parameterNameFont = parameter.getSequence() != null && parameter.getSequence() == 2 ? boldFont
					: Boolean.TRUE.equals(parameter.getIsNameBold()) ? boldFont1 : normalFont1;

			addParameterCell(testTable, parameterName, parameterNameFont, Element.ALIGN_LEFT);

			// ====================================================
			// DESCRIPTION
			// ====================================================

			if (Boolean.TRUE.equals(parameter.getIsValueDiscription())) {

				PdfPCell descriptionCell = new PdfPCell(
						new Phrase(value, Boolean.TRUE.equals(parameter.getIsBold()) ? boldFont1 : normalFont1));

				descriptionCell.setBorder(PdfPCell.NO_BORDER);

				descriptionCell.setColspan(3);

				descriptionCell.setPaddingLeft(5);

				descriptionCell.setPaddingRight(5);

				descriptionCell.setPaddingTop(1);

				descriptionCell.setPaddingBottom(1);

				descriptionCell.setHorizontalAlignment(Element.ALIGN_LEFT);

				descriptionCell.setVerticalAlignment(Element.ALIGN_TOP);

				testTable.addCell(descriptionCell);

			} else {

				addParameterCell(testTable, value, Boolean.TRUE.equals(parameter.getIsBold()) ? boldFont1 : normalFont1,
						Element.ALIGN_LEFT);

				addParameterCell(testTable, unit, normalFont1, Element.ALIGN_LEFT);

				addParameterCell(testTable, referenceRange, normalFont1, Element.ALIGN_LEFT);
			}
		}

		return testTable;
	}

	// ============================================================
	// COLUMN HEADER
	// ============================================================

	private PdfPTable createColumnHeaderTable(Font boldFont) {

		PdfPTable table = new PdfPTable(4);

		table.setWidthPercentage(100);

		table.setWidths(new float[] { 40, 15, 15, 30 });

		addHeaderCell(table, "Test Name", boldFont, Element.ALIGN_LEFT);

		addHeaderCell(table, "Result", boldFont, Element.ALIGN_LEFT);

		addHeaderCell(table, "Unit", boldFont, Element.ALIGN_LEFT);

		addHeaderCell(table, "Reference Range", boldFont, Element.ALIGN_LEFT);

		return table;
	}

	// ============================================================
	// HEADER CELL
	// ============================================================

	private void addHeaderCell(PdfPTable table, String text, Font font, int alignment) {

		PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));

		cell.setBorder(PdfPCell.TOP);

		cell.setBorderWidthTop(0.1f);

		cell.setPaddingLeft(5);

		cell.setPaddingRight(5);

		cell.setPaddingTop(2);

		cell.setPaddingBottom(4);

		cell.setHorizontalAlignment(alignment);

		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		table.addCell(cell);
	}

	// ============================================================
	// PARAMETER CELL
	// ============================================================

	private void addParameterCell(PdfPTable table, String text, Font font, int alignment) {

		PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));

		cell.setBorder(PdfPCell.NO_BORDER);

		cell.setPaddingLeft(5);

		cell.setPaddingRight(5);

		cell.setPaddingTop(1);

		cell.setPaddingBottom(1);

		cell.setHorizontalAlignment(alignment);

		cell.setVerticalAlignment(Element.ALIGN_TOP);

		table.addCell(cell);
	}

	// ============================================================
	// REFERENCE RANGE
	// ============================================================

	private String getReferenceRange(ParameterDetails parameter) {

		if (parameter.getLowerRange() != null && parameter.getUpperRange() != null) {

			return parameter.getLowerRange().stripTrailingZeros().toPlainString() + " - "
					+ parameter.getUpperRange().stripTrailingZeros().toPlainString();
		}

		if (parameter.getParameterRange() != null && !parameter.getParameterRange().isEmpty()) {

			return parameter.getParameterRange();
		}

		return "";
	}

	// ============================================================
	// CALCULATE TEST HEIGHT
	// ============================================================

	private float calculateTestHeight(List<ParameterDetails> testDetails) {

		float height = 0;

		height += 22;

		height += 20;

		for (ParameterDetails parameter : testDetails) {

			if (parameter.getSequence() != null && parameter.getSequence() == 1) {

				continue;
			}

			height += 17;

			if (Boolean.TRUE.equals(parameter.getIsValueDiscription())) {

				String value = safe(parameter.getValue());

				int length = value.length();

				if (length > 70) {
					height += 12;
				}

				if (length > 140) {
					height += 12;
				}

				if (length > 210) {
					height += 12;
				}

				if (length > 280) {
					height += 12;
				}
			}

			String range = getReferenceRange(parameter);

			if (range.contains("\n")) {

				int lines = range.split("\n").length;

				height += (lines - 1) * 11;
			}
		}

		height += 5;

		return height;
	}

	// ============================================================
	// EMPTY CELL
	// ============================================================

	private PdfPCell createEmptyCell(int colspan) {

		PdfPCell cell = new PdfPCell(new Phrase(""));

		cell.setBorder(PdfPCell.NO_BORDER);

		cell.setColspan(colspan);

		cell.setPadding(0);

		return cell;
	}

	// ============================================================
	// SAFE
	// ============================================================

	private String safe(Object value) {

		return value != null ? String.valueOf(value) : "";
	}

	// ============================================================
	// PATIENT CELL
	// ============================================================

	private void addPatientCell(PdfPTable table, String text, Font font, int alignment) {

		PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));

		cell.setBorder(PdfPCell.NO_BORDER);

		cell.setPaddingLeft(0);

		cell.setPaddingRight(0);

		cell.setPaddingTop(1);

		cell.setPaddingBottom(1);

		cell.setHorizontalAlignment(alignment);

		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		table.addCell(cell);
	}

	// ============================================================
	// PAGE HEADER + FOOTER EVENT
	// ============================================================

	private class ReportPageHeader extends PdfPageEventHelper {

		private final PatientMaster patientDetails;
		private final ReportMaster reportDetails;
		private final Long patientId;
		private final Font boldFont;
		private final int topMargin;

		private final byte[] qrCode;
		private final int qrCodePositionHorizantal;
		private final int qrCodePositionVertical;
		private final float qrSize;
		private final boolean isQrRequired;

		public ReportPageHeader(Long patientId, PatientMaster patientDetails, ReportMaster reportDetails, Font boldFont,
				int topMargin, byte[] qrCode, int qrCodePositionHorizantal, int qrCodePositionVertical, float qrSize,
				boolean isQrRequired) {

			this.patientId = patientId;
			this.patientDetails = patientDetails;
			this.reportDetails = reportDetails;
			this.boldFont = boldFont;
			this.topMargin = topMargin;

			this.qrCode = qrCode;
			this.qrCodePositionHorizantal = qrCodePositionHorizantal;
			this.qrCodePositionVertical = qrCodePositionVertical;
			this.qrSize = qrSize;
			this.isQrRequired = isQrRequired;
		}

		@Override
		public void onEndPage(PdfWriter writer, Document document) {

			try {

				PdfContentByte canvas = writer.getDirectContent();

				canvas.saveState();

				// =================================================
				// PAGE SIZE
				// =================================================

				float pageWidth = document.getPageSize().getWidth();

				float pageHeight = document.getPageSize().getHeight();

				// =================================================
				// DOCUMENT WIDTH
				// =================================================

				float left = document.leftMargin();

				float right = pageWidth - document.rightMargin();

				float width = right - left;

				// =================================================
				// PATIENT HEADER
				// =================================================

				PdfPTable headerTable = createPatientTable(patientId, patientDetails, reportDetails, boldFont);

				headerTable.setTotalWidth(width);

				float headerY = pageHeight - (20 + topMargin);

				headerTable.writeSelectedRows(0, -1, left, headerY, canvas);

				// =================================================
				// COLUMN HEADER
				// =================================================

				PdfPTable columnHeaderTable = createColumnHeaderTable(boldFont);

				float patientHeaderHeight = headerTable.getTotalHeight();

				columnHeaderTable.setTotalWidth(width);

				float columnHeaderY = headerY - patientHeaderHeight - 5;

				columnHeaderTable.writeSelectedRows(0, -1, left, columnHeaderY, canvas);

				// =================================================
				// FOOTER
				// =================================================

				drawEndOfReport(canvas, document);

				canvas.restoreState();

			} catch (Exception e) {

				throw new RuntimeException("Error while creating PDF page header/footer", e);
			}
		}

		// =========================================================
		// FOOTER + QR CODE
		// =========================================================

		private void drawEndOfReport(PdfContentByte canvas, Document document) {

			// =====================================================
			// PAGE DIMENSIONS
			// =====================================================

			float pageWidth = document.getPageSize().getWidth();

			float left = document.leftMargin();

			float right = pageWidth - document.rightMargin();

			float centerX = (left + right) / 2;

			// =====================================================
			// LINE POSITION
			// =====================================================

			float lineY;

			if (isQrRequired) {

				// QR required
				// Reserve larger footer area

				lineY = document.bottomMargin() + 75;

			} else {

				// QR not required
				// Normal footer area

				lineY = document.bottomMargin() + 18;
			}

			// =====================================================
			// SINGLE HORIZONTAL BORDER LINE
			// =====================================================

			canvas.setLineWidth(0.5f);

			canvas.moveTo(left, lineY);

			canvas.lineTo(right, lineY);

			canvas.stroke();

			// =====================================================
			// END OF REPORT
			// =====================================================

			Font footerFont = new Font(Font.HELVETICA, 10, Font.BOLD);

			float textY = lineY - 15;

			ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase("End of Report", footerFont), centerX,
					textY, 0);

			// =====================================================
			// QR CODE
			// =====================================================

			// QR disabled
			if (!isQrRequired) {
				return;
			}

			// QR not available
			if (qrCode == null || qrCode.length == 0) {
				return;
			}

			try {

				// =================================================
				// CREATE IMAGE
				// =================================================

				Image qrImage = Image.getInstance(qrCode);

				// =================================================
				// QR SIZE
				// =================================================

				qrImage.scaleAbsolute(qrSize, qrSize);

				// =================================================
				// HORIZONTAL POSITION
				//
				// 1 = LEFT
				// 2 = CENTER
				// 3 = RIGHT
				// =================================================

				float qrX;

				if (qrCodePositionHorizantal == 1) {

					// ---------------------------------------------
					// LEFT
					// ---------------------------------------------

					qrX = left;

				} else if (qrCodePositionHorizantal == 3) {

					// ---------------------------------------------
					// RIGHT
					// ---------------------------------------------

					qrX = right - qrSize;

				} else {

					// ---------------------------------------------
					// CENTER
					// ---------------------------------------------

					qrX = centerX - (qrSize / 2);
				}

				// =================================================
				// VERTICAL POSITION
				//
				// 1 = TOP
				// 2 = BOTTOM
				// =================================================

				float qrY;

				if (qrCodePositionVertical == 1) {

					// ---------------------------------------------
					// TOP
					// ---------------------------------------------

					qrY = lineY - 20 - qrSize;

				} else {

					// ---------------------------------------------
					// BOTTOM
					// ---------------------------------------------

					qrY = document.bottomMargin() + 5;
				}

				// =================================================
				// SET QR POSITION
				// =================================================

				qrImage.setAbsolutePosition(qrX, qrY);

				// =================================================
				// ADD QR
				// =================================================

				canvas.addImage(qrImage);

			} catch (Exception e) {

				throw new RuntimeException("Error while adding QR code", e);
			}
		}
	}

	// ============================================================
	// ADD CELL
	// ============================================================

	private void addCell(PdfPTable table, String value, Font font, int alignment) {

		PdfPCell cell = new PdfPCell(new Phrase(value != null ? value : "", font));

		cell.setHorizontalAlignment(alignment);

		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		cell.setPadding(2);

		cell.setBorder(Rectangle.BOX);

		table.addCell(cell);
	}

	// ============================================================
	// FORMAT AMOUNT
	// ============================================================

	private String formatAmount(BigDecimal amount) {

		if (amount == null) {
			return "0.00";
		}

		return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
	}
}