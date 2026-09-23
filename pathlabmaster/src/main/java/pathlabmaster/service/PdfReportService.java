package pathlabmaster.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import pathlabmaster.dao.BillMasterRepository;
import pathlabmaster.dao.DoctorMasterRepository;
import pathlabmaster.dao.PatientMasterRepository;
import pathlabmaster.dao.ReportMasterRepository;
import pathlabmaster.pojo.BillMaster;
import pathlabmaster.pojo.DoctorMaster;
import pathlabmaster.pojo.PatientMaster;
import pathlabmaster.pojo.ReportMaster;

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
}
