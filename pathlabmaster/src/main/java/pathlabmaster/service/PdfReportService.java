package pathlabmaster.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import pathlabmaster.dao.BillMasterRepository;
import pathlabmaster.dao.ClientConfigRepository;
import pathlabmaster.dao.DoctorMasterRepository;
import pathlabmaster.dao.PatientMasterRepository;
import pathlabmaster.dao.ReportMasterRepository;
import pathlabmaster.pojo.BillMaster;
import pathlabmaster.pojo.ClientConfig;
import pathlabmaster.pojo.DoctorMaster;
import pathlabmaster.pojo.ParameterDetails;
import pathlabmaster.pojo.PatientMaster;
import pathlabmaster.pojo.PdfResponse;
import pathlabmaster.pojo.ReportMaster;
import pathlabmaster.utility.Response;
import com.lowagie.text.pdf.PdfPageEventHelper;

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

	public byte[] generatePdf(String fromDate, String toDate, Long labId, String firstName, String lastName,
			Long patientId, String doctorName, Long doctorId) throws IOException {

		/*
		 * ============================================================ FILTER LOG
		 * ============================================================
		 */

		System.out.println("================================");
		System.out.println("PDF Report Filters");
		System.out.println("================================");
		System.out.println("From Date   : " + fromDate);
		System.out.println("To Date     : " + toDate);
		System.out.println("Lab ID      : " + labId);
		System.out.println("First Name  : " + firstName);
		System.out.println("Last Name   : " + lastName);
		System.out.println("Patient ID  : " + patientId);
		System.out.println("Doctor Name : " + doctorName);
		System.out.println("Doctor ID   : " + doctorId);

		/*
		 * ============================================================ DATABASE DATA
		 * ============================================================
		 */

		List<ReportMaster> reportMasterList = reportMasterRepo.filterReports(labId, fromDate, toDate, patientId);

		List<BillMaster> billMasters = billMasterRepo.filterBills(labId, fromDate, toDate);

		List<PatientMaster> patientMasterList = patientMasterRepo.filterPatients(labId, fromDate, toDate, firstName,
				lastName, patientId, doctorName, doctorId);

		List<DoctorMaster> doctorList = doctorMasterRepo.findByLabId(labId);

		/*
		 * ============================================================ NULL SAFE LISTS
		 * ============================================================
		 */

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

		/*
		 * ============================================================ CREATE LOOKUP
		 * MAPS ============================================================
		 */

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

		/*
		 * ============================================================ GET LAB NAME
		 * ============================================================
		 */

		String labName = "";

		if (!patientMasterList.isEmpty()) {

			labName = patientMasterList.get(0).getLabName();
		}

		if (labName == null || labName.isBlank()) {

			labName = "Lab Report";
		}

		/*
		 * ============================================================ CREATE PDF
		 * DOCUMENT
		 *
		 * A4 PORTRAIT ============================================================
		 */

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

			/*
			 * IMPORTANT:
			 *
			 * PageSize.A4 = PORTRAIT
			 *
			 * PageSize.A4.rotate() = LANDSCAPE
			 */

			Document document = new Document(PageSize.A4, 15, 15, 15, 15);

			PdfWriter.getInstance(document, outputStream);

			document.open();

			/*
			 * ======================================================== FONTS
			 * ========================================================
			 */

			Font labNameFont = new Font(Font.HELVETICA, 14, Font.BOLD);

			Font dateFont = new Font(Font.HELVETICA, 8, Font.NORMAL);

			Font headerFont = new Font(Font.HELVETICA, 5.5f, Font.BOLD);

			Font dataFont = new Font(Font.HELVETICA, 5.5f, Font.NORMAL);

			Font totalFont = new Font(Font.HELVETICA, 6, Font.BOLD);

			/*
			 * ======================================================== LAB NAME
			 * ========================================================
			 */

			Paragraph labParagraph = new Paragraph(labName, labNameFont);

			labParagraph.setAlignment(Element.ALIGN_CENTER);

			labParagraph.setSpacingAfter(3);

			document.add(labParagraph);

			/*
			 * ======================================================== DATE RANGE
			 * ========================================================
			 */

			String dateRange = "From Date: " + (fromDate != null ? fromDate : "") + "    To Date: "
					+ (toDate != null ? toDate : "");

			Paragraph dateParagraph = new Paragraph(dateRange, dateFont);

			dateParagraph.setAlignment(Element.ALIGN_CENTER);

			dateParagraph.setSpacingAfter(6);

			document.add(dateParagraph);

			/*
			 * ======================================================== CREATE PDF TABLE
			 *
			 * 10 COLUMNS ========================================================
			 */

			PdfPTable table = new PdfPTable(10);

			table.setWidthPercentage(100);

			/*
			 * ======================================================== PORTRAIT COLUMN
			 * WIDTHS
			 *
			 * Total = 100
			 *
			 * More space given to: - Test List - Patient Name - Doctor Name
			 * ========================================================
			 */

			table.setWidths(new float[] {

					4f, // Sr No
					8f, // Registration Date
					10f, // Reg No
					11f, // Patient Name
					22f, // Test List
					13f, // Doctor
					7f, // Total Amount
					8f, // Collected Amount
					9f, // Collected By Doctor
					8f // Sharing
			});

			/*
			 * ======================================================== TABLE HEADER
			 * ========================================================
			 */

			String[] headers = {

					"Sr No", "Registration Date", "Reg. No", "Patient Name", "Test List", "Referred Doctor Name",
					"Total Amount", "Collected Amount", "Collected By Doctor", "Sharing" };

			for (String header : headers) {

				PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));

				cell.setHorizontalAlignment(Element.ALIGN_CENTER);

				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

				cell.setPadding(2);

				cell.setBorder(Rectangle.BOX);

				table.addCell(cell);
			}

			/*
			 * ======================================================== REPEAT HEADER ON
			 * EVERY PAGE ========================================================
			 */

			table.setHeaderRows(1);

			/*
			 * ======================================================== GRAND TOTALS
			 * ========================================================
			 */

			BigDecimal grandTotalAmount = BigDecimal.ZERO;

			BigDecimal grandCollectedAmount = BigDecimal.ZERO;

			BigDecimal grandSharing = BigDecimal.ZERO;

			BigDecimal grandCollectedByDoctor = BigDecimal.ZERO;

			/*
			 * ======================================================== DATA ROWS
			 * ========================================================
			 */

			int srNo = 1;

			for (PatientMaster patient : patientMasterList) {

				if (patient == null) {
					continue;
				}

				Long patientIdCurrent = patient.getPatientId();

				if (patientIdCurrent == null) {
					continue;
				}

				/*
				 * ==================================================== REPORT
				 * ====================================================
				 */

				ReportMaster report = reportMap.get(patientIdCurrent);

				if (report == null) {
					continue;
				}

				/*
				 * ==================================================== BILL
				 * ====================================================
				 */

				BillMaster bill = billMap.get(patientIdCurrent);

				if (bill == null) {
					continue;
				}

				/*
				 * ==================================================== REGISTRATION DATE
				 * ====================================================
				 */

				String registrationDate = "";

				String createdAt = patient.getCreatedAt();

				if (createdAt != null && createdAt.length() >= 10) {

					registrationDate = createdAt.substring(0, 10);
				}

				/*
				 * ==================================================== PATIENT NAME
				 * ====================================================
				 */

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

				/*
				 * ==================================================== TEST LIST
				 * ====================================================
				 */

				String testList = "";

				if (report.getReportNameList() != null && !report.getReportNameList().isEmpty()) {

					testList = String.join(", ", report.getReportNameList().keySet());
				}

				/*
				 * ==================================================== DOCTOR NAME
				 * ====================================================
				 */

				String doctorName1 = patient.getDoctorName();

				if (doctorName1 == null) {

					doctorName1 = "";
				}

				/*
				 * ==================================================== TOTAL AMOUNT
				 * ====================================================
				 */

				BigDecimal totalAmount = bill.getTotalAmount();

				if (totalAmount == null) {

					totalAmount = BigDecimal.ZERO;
				}

				/*
				 * ==================================================== COLLECTED AMOUNT
				 * ====================================================
				 */

				BigDecimal collectedAmount = bill.getPaymentReceived();

				if (collectedAmount == null) {

					collectedAmount = BigDecimal.ZERO;
				}

				/*
				 * ==================================================== COLLECTED BY DOCTOR
				 * ====================================================
				 */

				BigDecimal collectedByDoctor = bill.getCollectedByDoctor();

				if (collectedByDoctor == null) {

					collectedByDoctor = BigDecimal.ZERO;
				}

				/*
				 * ==================================================== SHARING PERCENTAGE
				 * ====================================================
				 */

				Float sharingPercentage = 0.0f;

				Long currentDoctorId = patient.getDoctorId();

				if (currentDoctorId != null) {

					DoctorMaster doctor = doctorMap.get(currentDoctorId);

					if (doctor != null && doctor.getShairingPercentage() != null) {

						sharingPercentage = doctor.getShairingPercentage();
					}
				}

				BigDecimal doctorSharingPercentage = BigDecimal.valueOf(sharingPercentage.doubleValue());

				/*
				 * ==================================================== SHARING CALCULATION
				 * ====================================================
				 */

				BigDecimal sharing = BigDecimal.ZERO;

				if (doctorSharingPercentage.compareTo(BigDecimal.ZERO) > 0) {

					sharing = collectedAmount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
							.multiply(doctorSharingPercentage).setScale(2, RoundingMode.HALF_UP);
				}

				/*
				 * ==================================================== ADD PDF ROW
				 * ====================================================
				 */

				addCell(table, String.valueOf(srNo++), dataFont, Element.ALIGN_CENTER);

				addCell(table, registrationDate, dataFont, Element.ALIGN_CENTER);

				/*
				 * IMPORTANT:
				 *
				 * Reg No remains String.
				 */

				addCell(table, String.valueOf(patientIdCurrent), dataFont, Element.ALIGN_LEFT);

				addCell(table, patientName, dataFont, Element.ALIGN_LEFT);

				addCell(table, testList, dataFont, Element.ALIGN_LEFT);

				addCell(table, doctorName1, dataFont, Element.ALIGN_LEFT);

				addCell(table, formatAmount(totalAmount), dataFont, Element.ALIGN_RIGHT);

				addCell(table, formatAmount(collectedAmount), dataFont, Element.ALIGN_RIGHT);

				addCell(table, formatAmount(collectedByDoctor), dataFont, Element.ALIGN_RIGHT);

				addCell(table, formatAmount(sharing), dataFont, Element.ALIGN_RIGHT);

				/*
				 * ==================================================== GRAND TOTALS
				 * ====================================================
				 */

				grandTotalAmount = grandTotalAmount.add(totalAmount);

				grandCollectedAmount = grandCollectedAmount.add(collectedAmount);

				grandCollectedByDoctor = grandCollectedByDoctor.add(collectedByDoctor);

				grandSharing = grandSharing.add(sharing);
			}

			/*
			 * ======================================================== GRAND TOTAL ROW
			 * ========================================================
			 */

			PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL", totalFont));

			/*
			 * Merge first 6 columns
			 */

			totalLabelCell.setColspan(6);

			totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

			totalLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

			totalLabelCell.setPadding(3);

			totalLabelCell.setBorder(Rectangle.BOX);

			table.addCell(totalLabelCell);

			/*
			 * ======================================================== TOTAL AMOUNT
			 * ========================================================
			 */

			addCell(table, formatAmount(grandTotalAmount), totalFont, Element.ALIGN_RIGHT);

			/*
			 * ======================================================== TOTAL COLLECTED
			 * ========================================================
			 */

			addCell(table, formatAmount(grandCollectedAmount), totalFont, Element.ALIGN_RIGHT);

			/*
			 * ======================================================== TOTAL COLLECTED BY
			 * DOCTOR ========================================================
			 */

			addCell(table, formatAmount(grandCollectedByDoctor), totalFont, Element.ALIGN_RIGHT);

			/*
			 * ======================================================== TOTAL SHARING
			 * ========================================================
			 */

			addCell(table, formatAmount(grandSharing), totalFont, Element.ALIGN_RIGHT);

			/*
			 * ======================================================== ADD TABLE TO
			 * DOCUMENT ========================================================
			 */

			document.add(table);

			/*
			 * ======================================================== CLOSE PDF
			 * ========================================================
			 */

			document.close();

			return outputStream.toByteArray();
		}
	}

	/*
	 * ================================================================ ADD CELL
	 * ================================================================
	 */

	private void addCell(PdfPTable table, String value, Font font, int alignment) {

		PdfPCell cell = new PdfPCell(new Phrase(value != null ? value : "", font));

		cell.setHorizontalAlignment(alignment);

		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		cell.setPadding(2);

		cell.setBorder(Rectangle.BOX);

		table.addCell(cell);
	}

	/*
	 * ================================================================ FORMAT
	 * AMOUNT ================================================================
	 */

	private String formatAmount(BigDecimal amount) {

		if (amount == null) {

			return "0.00";
		}

		return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
	}

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

	public PdfResponse createPdf(Long patientId, String reportIds) throws Exception {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		// =====================================================
		// A4 Page
		// =====================================================

		/*
		 * IMPORTANT:
		 *
		 * Top margin is increased because patient details + Test Name / Result / Unit /
		 * Reference Range are printed by page event on every page.
		 */
		PatientMaster patientDetails = patientMasterRepo.findById(patientId).orElse(null);
		ClientConfig clientConfig = clientConfigRepo.findByLabId(patientDetails.getLabId());
		int topMargin=clientConfig.getReportTopSpace();
		Document document = new Document(PageSize.A4, 30, 30, (78+topMargin), 25);
		

		PdfWriter writer = PdfWriter.getInstance(document, outputStream);

		// =====================================================
		// Fonts
		// =====================================================

		Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

		Font boldFont = new Font(Font.HELVETICA, 10, Font.BOLD);

		Font normalFont1 = new Font(Font.HELVETICA, 9, Font.NORMAL);

		Font boldFont1 = new Font(Font.HELVETICA, 9, Font.BOLD);

		// =====================================================
		// Get Patient
		// =====================================================

	

		if (patientDetails == null) {

			document.close();

			throw new RuntimeException("Patient not found : " + patientId);
		}

		// =====================================================
		// Get Report
		// =====================================================

		ReportMaster reportDetails = reportMasterRepo.findByPatientIdAndLabId(patientId, patientDetails.getLabId());

		if (reportDetails == null) {

			document.close();

			throw new RuntimeException("Report not found for patient : " + patientId);
		}

		// =====================================================
		// PAGE HEADER
		//
		// Patient details + column header will automatically
		// appear on every page.
		// =====================================================

		ReportPageHeader pageHeader = new ReportPageHeader(patientId, patientDetails, reportDetails, boldFont,topMargin);

		writer.setPageEvent(pageHeader);

		// =====================================================
		// Open Document
		// =====================================================

		document.open();

		// =====================================================
		// IMPORTANT
		//
		// DO NOT add patientTable here.
		//
		// It is already printed by ReportPageHeader.
		// =====================================================

		// =====================================================
		// Report IDs
		// =====================================================

		List<String> reportIdList = Arrays.asList(reportIds.split("\\|"));

		// =====================================================
		// Completed Tests
		// =====================================================

		Map<String, List<ParameterDetails>> reportOriginal = reportDetails.getCompletedTest();

		Map<String, List<ParameterDetails>> reports = new HashMap<>();

		// =====================================================
		// Convert Report IDs
		// =====================================================

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
		// Generate Tests
		// =====================================================

		for (String reportId : reportIdList) {

			if (reportId == null || reportId.trim().isEmpty()) {

				continue;
			}

			reportId = reportId.trim();

			List<ParameterDetails> testDetails = reports.get(reportId);

			// =================================================
			// Test Not Found
			// =================================================

			if (testDetails == null || testDetails.isEmpty()) {

				continue;
			}

			// =================================================
			// Sort By Sequence
			// =================================================

			testDetails.sort(
					Comparator.comparing(ParameterDetails::getSequence, Comparator.nullsLast(Integer::compareTo)));

			// =================================================
			// Test Name
			// =================================================

			String testName = "";

			for (ParameterDetails parameter : testDetails) {

				if (parameter.getSequence() != null && parameter.getSequence() == 1) {

					testName = safe(parameter.getParameterName());

					break;
				}
			}

			// =================================================
			// Create Test Title
			// =================================================

			PdfPTable testTitleTable = createTestTitleTable(testName, boldFont);

			// =================================================
			// Create ONLY parameter rows
			//
			// NO HEADER HERE
			// =================================================

			PdfPTable testTable = createTestTable(testDetails, normalFont, boldFont, normalFont1, boldFont1);

			// =================================================
			// Calculate approximate test height
			// =================================================

			float requiredHeight = calculateTestHeight(testDetails);

			// =================================================
			// Current available position
			// =================================================

			float currentY = writer.getVerticalPosition(true);

			float availableHeight = currentY - document.bottomMargin();

			// =================================================
			// If complete test doesn't fit
			// =================================================

			if (requiredHeight > availableHeight) {

				document.newPage();

				/*
				 * Do NOT manually add patient table or column header here.
				 *
				 * PdfPageEventHelper will automatically print:
				 *
				 * Patient details Test Name | Result | Unit | Reference Range
				 *
				 * on the new page.
				 */

			}

			// =================================================
			// Complete Test Wrapper
			// =================================================

			PdfPTable completeTestTable = new PdfPTable(1);

			completeTestTable.setWidthPercentage(100);

			/*
			 * Keep title + all parameter rows together.
			 */
			completeTestTable.setKeepTogether(true);

			// =================================================
			// Test Title
			// =================================================

			PdfPCell titleCell = new PdfPCell(testTitleTable);

			titleCell.setBorder(PdfPCell.NO_BORDER);

			titleCell.setPadding(0);

			completeTestTable.addCell(titleCell);

			// =================================================
			// Parameter Table
			// =================================================

			PdfPCell parameterCell = new PdfPCell(testTable);

			parameterCell.setBorder(PdfPCell.NO_BORDER);

			parameterCell.setPadding(0);

			completeTestTable.addCell(parameterCell);

			// =================================================
			// Add Complete Test
			// =================================================

			document.add(completeTestTable);

			// =================================================
			// Space Between Tests
			// =================================================

			Paragraph space = new Paragraph(" ");

			space.setLeading(2);

			document.add(space);
		}

		// =====================================================
		// Close
		// =====================================================

		document.close();

		// =====================================================
		// Return PDF
		// =====================================================

		return new PdfResponse(outputStream.toByteArray(), "Report-" + safe(patientDetails.getFirstName()) + " "
				+ safe(patientDetails.getMiddleName()) + " " + safe(patientDetails.getLastName()) + ".pdf");
	}
	/*
	 * ========================================================= PATIENT TABLE
	 * =========================================================
	 */

	private PdfPTable createPatientTable(Long patientId, PatientMaster patientDetails, ReportMaster reportDetails,
			Font boldFont) throws Exception {

		PdfPTable patientTable = new PdfPTable(4);

		patientTable.setWidthPercentage(100);

		patientTable.setWidths(new float[] { 15, 50, 15, 30 });

		// =====================================================
		// Row 1
		// =====================================================

		addPatientCell(patientTable, "Reg No", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, ": " + patientId + " / OPD", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, "Sex / Age", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable,
				": " + safe(patientDetails.getGender()) + " / " + safe(patientDetails.getYear()) + "Y", boldFont,
				Element.ALIGN_RIGHT);

		// =====================================================
		// Row 2
		// =====================================================

		addPatientCell(patientTable, "Name", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, ": " + safe(patientDetails.getFirstName()) + " "
				+ safe(patientDetails.getMiddleName()) + " " + safe(patientDetails.getLastName()), boldFont,
				Element.ALIGN_LEFT);

		addPatientCell(patientTable, "Reg Date", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, ": " + safe(patientDetails.getCreatedAt()), boldFont, Element.ALIGN_RIGHT);

		// =====================================================
		// Row 3
		// =====================================================

		addPatientCell(patientTable, "Referred Dr", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, ": " + safe(patientDetails.getDoctorName()), boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, "Report Date", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, ": " + safe(reportDetails.getCreatedAt()), boldFont, Element.ALIGN_RIGHT);

		patientTable.addCell(createEmptyCell(4));
		return patientTable;
	}

	/*
	 * ========================================================= Merge Patient Table
	 * + Bottom Line =========================================================
	 */

	private PdfPTable mergePatientTableWithLine(PdfPTable patientTable, PdfPTable lineTable) {

		PdfPTable wrapper = new PdfPTable(1);

		wrapper.setWidthPercentage(100);

		PdfPCell patientCell = new PdfPCell(patientTable);

		patientCell.setBorder(PdfPCell.NO_BORDER);

		patientCell.setPadding(0);

		wrapper.addCell(patientCell);

		PdfPCell lineCell = new PdfPCell(lineTable);

		lineCell.setBorder(PdfPCell.NO_BORDER);

		lineCell.setPaddingTop(1);
		lineCell.setPaddingBottom(2);
		lineCell.setPaddingLeft(0);
		lineCell.setPaddingRight(0);

		wrapper.addCell(lineCell);

		return wrapper;
	}

	/*
	 * ========================================================= TEST TITLE
	 * =========================================================
	 */

	private PdfPTable createTestTitleTable(String testName, Font boldFont) {

		PdfPTable table = new PdfPTable(1);

		table.setWidthPercentage(100);

		PdfPCell cell = new PdfPCell(new Phrase(testName, boldFont));

		// -----------------------------------------------------
		// NO OUTER BOX
		// -----------------------------------------------------

		cell.setBorder(PdfPCell.NO_BORDER);

		// -----------------------------------------------------
		// Top + Bottom horizontal lines
		// -----------------------------------------------------

//		cell.setBorder(PdfPCell.TOP | PdfPCell.BOTTOM);

		cell.setBorderWidthTop(0.8f);
		cell.setBorderWidthBottom(0.8f);

		// -----------------------------------------------------
		// Alignment
		// -----------------------------------------------------

		cell.setHorizontalAlignment(Element.ALIGN_CENTER);

		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		// -----------------------------------------------------
		// Compact spacing
		// -----------------------------------------------------

		cell.setPaddingTop(3);

		cell.setPaddingBottom(3);

		cell.setPaddingLeft(2);

		cell.setPaddingRight(2);

		table.addCell(cell);

		return table;
	}

	/*
	 * ========================================================= TEST TABLE
	 * =========================================================
	 */

	private PdfPTable createTestTable(List<ParameterDetails> testDetails, Font normalFont, Font boldFont,
			Font normalFont1, Font boldFont1) {

		PdfPTable testTable = new PdfPTable(4);

		testTable.setWidthPercentage(100);

		// =====================================================
		// Column Widths
		// =====================================================

		testTable.setWidths(new float[] { 40, 15, 15, 30 });

		// =====================================================
		// IMPORTANT
		//
		// NO HEADER HERE
		//
		// Header is printed once per page by
		// ReportPageHeader.
		// =====================================================

		// =====================================================
		// Parameter Rows
		// =====================================================

		for (ParameterDetails parameter : testDetails) {

			// =================================================
			// Sequence 1 = Test Name
			// =================================================

			if (parameter.getSequence() != null && parameter.getSequence() == 1) {

				continue;
			}

			String parameterName = safe(parameter.getParameterName());

			String value = safe(parameter.getValue());

			String unit = safe(parameter.getUnit());

			String referenceRange = getReferenceRange(parameter);

			// =================================================
			// Parameter Name Font
			// =================================================

			Font parameterNameFont = parameter.getSequence() != null && parameter.getSequence() == 2 ? boldFont
					: Boolean.TRUE.equals(parameter.getIsNameBold()) ? boldFont1 : normalFont1;

			// =================================================
			// Parameter Name
			// =================================================

			addParameterCell(testTable, parameterName, parameterNameFont, Element.ALIGN_LEFT);

			// =================================================
			// Description Parameter
			// =================================================

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

				descriptionCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

				testTable.addCell(descriptionCell);

			} else {

				// =================================================
				// Result
				// =================================================

				addParameterCell(testTable, value, Boolean.TRUE.equals(parameter.getIsBold()) ? boldFont1 : normalFont1,
						Element.ALIGN_LEFT);

				// =================================================
				// Unit
				// =================================================

				addParameterCell(testTable, unit, normalFont1, Element.ALIGN_LEFT);

				// =================================================
				// Reference Range
				// =================================================

				addParameterCell(testTable, referenceRange, normalFont1, Element.ALIGN_LEFT);
			}
		}

		return testTable;
	}

	/*
	 * ========================================================= HEADER CELL
	 * =========================================================
	 */
	private PdfPTable createColumnHeaderTable(Font boldFont) {

		PdfPTable table = new PdfPTable(4);

		table.setWidthPercentage(100);

		table.setWidths(new float[] { 40, 15, 15, 30 });
		

		// =====================================================
		// Test Name
		// =====================================================

		addHeaderCell(table, "Test Name", boldFont, Element.ALIGN_LEFT);

		// =====================================================
		// Result
		// =====================================================

		addHeaderCell(table, "Result", boldFont, Element.ALIGN_LEFT);

		// =====================================================
		// Unit
		// =====================================================

		addHeaderCell(table, "Unit", boldFont, Element.ALIGN_LEFT);

		// =====================================================
		// Reference Range
		// =====================================================

		addHeaderCell(table, "Reference Range", boldFont, Element.ALIGN_LEFT);

		return table;
	}

	private void addHeaderCell(PdfPTable table, String text, Font font, int alignment) {

		PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));

		// =====================================================
		// ONLY TOP + BOTTOM
		// =====================================================

		cell.setBorder(PdfPCell.TOP);

		cell.setBorderWidthTop(0.1f);
//		cell.setBorderWidthBottom(0.8f);

		// =====================================================
		// Compact spacing
		// =====================================================

		cell.setPaddingLeft(5);
		cell.setPaddingRight(5);

		cell.setPaddingTop(2);
		cell.setPaddingBottom(4);

		cell.setHorizontalAlignment(alignment);

		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		table.addCell(cell);
	}

	/*
	 * ========================================================= PARAMETER CELL
	 * =========================================================
	 */

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

	/*
	 * ========================================================= REFERENCE RANGE
	 * =========================================================
	 */

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

	/*
	 * ========================================================= APPROXIMATE TEST
	 * HEIGHT =========================================================
	 */

	private float calculateTestHeight(List<ParameterDetails> testDetails) {

		float height = 0;

		// =====================================================
		// Test title
		// =====================================================

		height += 22;

		// =====================================================
		// Header
		// =====================================================

		height += 20;

		// =====================================================
		// Parameter rows
		// =====================================================

		for (ParameterDetails parameter : testDetails) {

			// Sequence 1 = test name
			if (parameter.getSequence() != null && parameter.getSequence() == 1) {

				continue;
			}

			// -------------------------------------------------
			// Normal row
			// -------------------------------------------------

			height += 17;

			// -------------------------------------------------
			// Description row
			// -------------------------------------------------

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

			// -------------------------------------------------
			// Multi-line reference range
			// -------------------------------------------------

			String range = getReferenceRange(parameter);

			if (range.contains("\n")) {

				int lines = range.split("\n").length;

				height += (lines - 1) * 11;
			}
		}

		// =====================================================
		// Small gap after test
		// =====================================================

		height += 5;

		return height;
	}

	/*
	 * ========================================================= EMPTY CELL
	 * =========================================================
	 */

	private PdfPCell createEmptyCell(int colspan) {

		PdfPCell cell = new PdfPCell(new Phrase(""));

		cell.setBorder(PdfPCell.NO_BORDER);

		cell.setColspan(colspan);

		cell.setPadding(0);

		return cell;
	}

	/*
	 * ========================================================= SAFE STRING
	 * =========================================================
	 */

	private String safe(Object value) {

		return value != null ? String.valueOf(value) : "";
	}

	/*
	 * ========================================================= PATIENT CELL
	 * =========================================================
	 */
	private void addPatientCell(PdfPTable table, String text, Font font, int alignment) {

		PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));

		// =====================================================
		// NO BORDER
		// =====================================================
		cell.setBorder(PdfPCell.NO_BORDER);

		// =====================================================
		// SPACING
		// =====================================================
		cell.setPaddingLeft(0);
		cell.setPaddingRight(0);
		cell.setPaddingTop(1);
		cell.setPaddingBottom(1);

		// =====================================================
		// ALIGNMENT
		// =====================================================
		cell.setHorizontalAlignment(alignment);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		// =====================================================
		// ADD CELL
		// =====================================================
		table.addCell(cell);
	}

	private class ReportPageHeader extends PdfPageEventHelper {

		private final PatientMaster patientDetails;
		private final ReportMaster reportDetails;
		private final Long patientId;

		private final Font boldFont;
		private final int topMargin;

		public ReportPageHeader(Long patientId, PatientMaster patientDetails, ReportMaster reportDetails,
				Font boldFont, int topMargin) {

			this.patientId = patientId;
			this.patientDetails = patientDetails;
			this.reportDetails = reportDetails;
			this.boldFont = boldFont;
			this.topMargin = topMargin;
		}

		@Override
		public void onEndPage(PdfWriter writer, Document document) {

			try {

				PdfContentByte canvas = writer.getDirectContent();

				// =====================================================
				// Save current canvas state
				// =====================================================

				canvas.saveState();

				// =====================================================
				// Header table
				// =====================================================

				PdfPTable headerTable = createPatientTable(patientId, patientDetails, reportDetails, boldFont);

				// =====================================================
				// Test column header
				// =====================================================

				PdfPTable columnHeaderTable = createColumnHeaderTable(boldFont);

				// =====================================================
				// Position
				// =====================================================

				float pageWidth = document.getPageSize().getWidth();

				float left = document.leftMargin();

				float width = pageWidth - document.leftMargin() - document.rightMargin();

				// =====================================================
				// Patient header
				// =====================================================

				headerTable.setTotalWidth(width);
				headerTable.writeSelectedRows(0, -1, left, document.getPageSize().getHeight() - (20+topMargin), canvas);

				// =====================================================
				// Column header
				// =====================================================

				float patientHeaderHeight = headerTable.getTotalHeight();

				columnHeaderTable.setTotalWidth(width);

				float columnHeaderY = document.getPageSize().getHeight() - (20+topMargin) - patientHeaderHeight - 5;

				columnHeaderTable.writeSelectedRows(0, -1, left, columnHeaderY, canvas);

				// =====================================================
				// Restore
				// =====================================================

				canvas.restoreState();

			} catch (Exception e) {
				throw new RuntimeException("Error while creating PDF page header", e);
			}
		}
	}

}
