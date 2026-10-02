package pathlabmaster.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pathlabmaster.dao.BillMasterRepository;
import pathlabmaster.dao.DoctorMasterRepository;
import pathlabmaster.dao.PatientMasterRepository;
import pathlabmaster.dao.ReportMasterRepository;
import pathlabmaster.pojo.BillMaster;
import pathlabmaster.pojo.DoctorMaster;
import pathlabmaster.pojo.PatientMaster;
import pathlabmaster.pojo.ReportMaster;

@Service
public class ExcelReportService {

	@Autowired
	private ReportMasterRepository reportMasterRepo;

	@Autowired
	private BillMasterRepository billMasterRepo;

	@Autowired
	private PatientMasterRepository patientMasterRepo;

	@Autowired
	private DoctorMasterRepository doctorMasterRepo;

	public byte[] generateExcel(String fromDate, String toDate, Long labId, String firstName, String lastName,
			Long patientId, String doctorName, Long doctorId) throws IOException {

		/*
		 * ============================================================ FILTER LOG
		 * ============================================================
		 */

		System.out.println("================================");
		System.out.println("Excel Report Filters");
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
		 * ============================================================ CREATE WORKBOOK
		 * ============================================================
		 */

		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

			XSSFSheet sheet = (XSSFSheet) workbook.createSheet("Patient Report");

			/*
			 * ======================================================== HEADER FONT
			 * ========================================================
			 */

			Font headerFont = workbook.createFont();

			headerFont.setBold(true);

			headerFont.setFontHeightInPoints((short) 11);

			/*
			 * ======================================================== HEADER STYLE
			 * ========================================================
			 */

			CellStyle headerStyle = workbook.createCellStyle();

			headerStyle.setFont(headerFont);

			headerStyle.setAlignment(HorizontalAlignment.CENTER);

			headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

			headerStyle.setBorderTop(BorderStyle.THIN);

			headerStyle.setBorderBottom(BorderStyle.THIN);

			headerStyle.setBorderLeft(BorderStyle.THIN);

			headerStyle.setBorderRight(BorderStyle.THIN);

			/*
			 * ======================================================== DATA STYLE
			 * ========================================================
			 */

			CellStyle dataStyle = workbook.createCellStyle();

			dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

			dataStyle.setBorderTop(BorderStyle.THIN);

			dataStyle.setBorderBottom(BorderStyle.THIN);

			dataStyle.setBorderLeft(BorderStyle.THIN);

			dataStyle.setBorderRight(BorderStyle.THIN);

			/*
			 * ======================================================== REGISTRATION NUMBER
			 * STYLE ========================================================
			 */

			CellStyle regNoStyle = workbook.createCellStyle();

			regNoStyle.setVerticalAlignment(VerticalAlignment.CENTER);

			regNoStyle.setAlignment(HorizontalAlignment.LEFT);

			regNoStyle.setBorderTop(BorderStyle.THIN);

			regNoStyle.setBorderBottom(BorderStyle.THIN);

			regNoStyle.setBorderLeft(BorderStyle.THIN);

			regNoStyle.setBorderRight(BorderStyle.THIN);

			/*
			 * ======================================================== AMOUNT STYLE
			 * ========================================================
			 */

			CellStyle amountStyle = workbook.createCellStyle();

			amountStyle.setVerticalAlignment(VerticalAlignment.CENTER);

			amountStyle.setBorderTop(BorderStyle.THIN);

			amountStyle.setBorderBottom(BorderStyle.THIN);

			amountStyle.setBorderLeft(BorderStyle.THIN);

			amountStyle.setBorderRight(BorderStyle.THIN);

			amountStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));

			/*
			 * ======================================================== SUMMARY LABEL STYLE
			 * ========================================================
			 */

			Font summaryLabelFont = workbook.createFont();

			summaryLabelFont.setBold(true);

			summaryLabelFont.setFontHeightInPoints((short) 10);

			CellStyle summaryLabelStyle = workbook.createCellStyle();

			summaryLabelStyle.setFont(summaryLabelFont);

			summaryLabelStyle.setAlignment(HorizontalAlignment.LEFT);

			summaryLabelStyle.setVerticalAlignment(VerticalAlignment.CENTER);

			summaryLabelStyle.setBorderTop(BorderStyle.THIN);

			summaryLabelStyle.setBorderBottom(BorderStyle.THIN);

			summaryLabelStyle.setBorderLeft(BorderStyle.THIN);

			summaryLabelStyle.setBorderRight(BorderStyle.THIN);

			/*
			 * ======================================================== SUMMARY VALUE STYLE
			 * ========================================================
			 */

			CellStyle summaryValueStyle = workbook.createCellStyle();

			summaryValueStyle.setAlignment(HorizontalAlignment.RIGHT);

			summaryValueStyle.setVerticalAlignment(VerticalAlignment.CENTER);

			summaryValueStyle.setBorderTop(BorderStyle.THIN);

			summaryValueStyle.setBorderBottom(BorderStyle.THIN);

			summaryValueStyle.setBorderLeft(BorderStyle.THIN);

			summaryValueStyle.setBorderRight(BorderStyle.THIN);

			summaryValueStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));

			/*
			 * ======================================================== SUMMARY TOTAL LABEL
			 * STYLE ========================================================
			 */

			Font summaryTotalFont = workbook.createFont();

			summaryTotalFont.setBold(true);

			summaryTotalFont.setFontHeightInPoints((short) 10);

			CellStyle summaryTotalLabelStyle = workbook.createCellStyle();

			summaryTotalLabelStyle.setFont(summaryTotalFont);

			summaryTotalLabelStyle.setAlignment(HorizontalAlignment.LEFT);

			summaryTotalLabelStyle.setVerticalAlignment(VerticalAlignment.CENTER);

			summaryTotalLabelStyle.setBorderTop(BorderStyle.THIN);

			summaryTotalLabelStyle.setBorderBottom(BorderStyle.THIN);

			summaryTotalLabelStyle.setBorderLeft(BorderStyle.THIN);

			summaryTotalLabelStyle.setBorderRight(BorderStyle.THIN);

			/*
			 * ======================================================== SUMMARY TOTAL VALUE
			 * STYLE ========================================================
			 */

			CellStyle summaryTotalValueStyle = workbook.createCellStyle();

			summaryTotalValueStyle.setFont(summaryTotalFont);

			summaryTotalValueStyle.setAlignment(HorizontalAlignment.RIGHT);

			summaryTotalValueStyle.setVerticalAlignment(VerticalAlignment.CENTER);

			summaryTotalValueStyle.setBorderTop(BorderStyle.THIN);

			summaryTotalValueStyle.setBorderBottom(BorderStyle.THIN);

			summaryTotalValueStyle.setBorderLeft(BorderStyle.THIN);

			summaryTotalValueStyle.setBorderRight(BorderStyle.THIN);

			summaryTotalValueStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));

			/*
			 * ======================================================== GET LAB NAME
			 * ========================================================
			 */

			String labName = "";

			if (!patientMasterList.isEmpty()) {

				labName = patientMasterList.get(0).getLabName();
			}

			if (labName == null || labName.isBlank()) {

				labName = "Lab Report";
			}

			/*
			 * ======================================================== LAB NAME ROW
			 * ========================================================
			 */

			Row labNameRow = sheet.createRow(0);

			labNameRow.setHeightInPoints(28);

			Cell labNameCell = labNameRow.createCell(0);

			labNameCell.setCellValue(labName);

			labNameCell.setCellStyle(headerStyle);

			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));

			/*
			 * ======================================================== DATE RANGE ROW
			 * ========================================================
			 */

			Row dateRow = sheet.createRow(1);

			dateRow.setHeightInPoints(22);

			Cell dateCell = dateRow.createCell(0);

			String dateRange = "From Date: " + (fromDate != null ? fromDate : "") + "    To Date: "
					+ (toDate != null ? toDate : "");

			dateCell.setCellValue(dateRange);

			dateCell.setCellStyle(headerStyle);

			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 10));

			/*
			 * ======================================================== TABLE HEADER
			 * ========================================================
			 */

			Row header = sheet.createRow(2);

			header.setHeightInPoints(25);

			String[] headers = { "Sr No", "Registration Date", "Reg. No", "Patient Name", "Test List",
					"Referred Doctor Name", "Total Amount", "Discount", "Collected Amount", "Collected By Doctor",
					"Sharing" };

			for (int i = 0; i < headers.length; i++) {

				Cell cell = header.createCell(i);

				cell.setCellValue(headers[i]);

				cell.setCellStyle(headerStyle);
			}

			/*
			 * ======================================================== DATA START ROW
			 * ========================================================
			 */

			int rowNumber = 3;

			/*
			 * ======================================================== GRAND TOTALS
			 * ========================================================
			 */

			BigDecimal grandTotalAmount = BigDecimal.ZERO;

			BigDecimal grandDiscount = BigDecimal.ZERO;

			BigDecimal grandCollectedAmount = BigDecimal.ZERO;

			BigDecimal grandSharing = BigDecimal.ZERO;

			BigDecimal grandCollectedByDoctor = BigDecimal.ZERO;

			/*
			 * ======================================================== PATIENT LOOP
			 * ========================================================
			 */

			for (PatientMaster patient : patientMasterList) {

				if (patient == null) {
					continue;
				}

				Long patientIdCurrent = patient.getPatientId();

				if (patientIdCurrent == null) {
					continue;
				}

				/*
				 * ==================================================== GET REPORT
				 * ====================================================
				 */

				ReportMaster report = reportMap.get(patientIdCurrent);

				if (report == null) {
					continue;
				}

				/*
				 * ==================================================== GET BILL
				 * ====================================================
				 */

				BillMaster bill = billMap.get(patientIdCurrent);

				if (bill == null) {
					continue;
				}

				/*
				 * ==================================================== SR NO
				 * ====================================================
				 */

				int srNo = rowNumber - 2;

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
				 * ==================================================== DISCOUNT
				 * ====================================================
				 */

				BigDecimal discount = bill.getDiscount();

				if (discount == null) {

					discount = BigDecimal.ZERO;
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
				 * ==================================================== DOCTOR SHARING
				 * PERCENTAGE ====================================================
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
				 *
				 * Sharing = (Collected Amount + Collected By Doctor) * Sharing Percentage / 100
				 * ====================================================
				 */

				BigDecimal sharing = BigDecimal.ZERO;

				if (doctorSharingPercentage.compareTo(BigDecimal.ZERO) > 0) {

					BigDecimal sharingBase = collectedAmount.add(collectedByDoctor);

					sharing = sharingBase.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
							.multiply(doctorSharingPercentage).setScale(2, RoundingMode.HALF_UP);
				}

				/*
				 * ==================================================== CREATE ROW
				 * ====================================================
				 */

				Row row = sheet.createRow(rowNumber++);

				/*
				 * ==================================================== 1. SR NO
				 * ====================================================
				 */

				Cell srCell = row.createCell(0);

				srCell.setCellValue(srNo);

				srCell.setCellStyle(dataStyle);

				/*
				 * ==================================================== 2. REGISTRATION DATE
				 * ====================================================
				 */

				Cell registrationCell = row.createCell(1);

				registrationCell.setCellValue(registrationDate);

				registrationCell.setCellStyle(dataStyle);

				/*
				 * ==================================================== 3. REG NO
				 * ====================================================
				 */

				Cell regNoCell = row.createCell(2);

				regNoCell.setCellValue(String.valueOf(patientIdCurrent));

				regNoCell.setCellStyle(regNoStyle);

				/*
				 * ==================================================== 4. PATIENT NAME
				 * ====================================================
				 */

				Cell patientCell = row.createCell(3);

				patientCell.setCellValue(patientName);

				patientCell.setCellStyle(dataStyle);

				/*
				 * ==================================================== 5. TEST LIST
				 * ====================================================
				 */

				Cell testCell = row.createCell(4);

				testCell.setCellValue(testList);

				testCell.setCellStyle(dataStyle);

				/*
				 * ==================================================== 6. DOCTOR NAME
				 * ====================================================
				 */

				Cell doctorCell = row.createCell(5);

				doctorCell.setCellValue(doctorName1);

				doctorCell.setCellStyle(dataStyle);

				/*
				 * ==================================================== 7. TOTAL AMOUNT
				 * ====================================================
				 */

				Cell totalCell = row.createCell(6);

				totalCell.setCellValue(totalAmount.doubleValue());

				totalCell.setCellStyle(amountStyle);

				/*
				 * ==================================================== 8. DISCOUNT
				 * ====================================================
				 */

				Cell discountCell = row.createCell(7);

				discountCell.setCellValue(discount.doubleValue());

				discountCell.setCellStyle(amountStyle);

				/*
				 * ==================================================== 9. COLLECTED AMOUNT
				 * ====================================================
				 */

				Cell collectedCell = row.createCell(8);

				collectedCell.setCellValue(collectedAmount.doubleValue());

				collectedCell.setCellStyle(amountStyle);

				/*
				 * ==================================================== 10. COLLECTED BY DOCTOR
				 * ====================================================
				 */

				Cell collectedByCell = row.createCell(9);

				collectedByCell.setCellValue(collectedByDoctor.doubleValue());

				collectedByCell.setCellStyle(amountStyle);

				/*
				 * ==================================================== 11. SHARING
				 * ====================================================
				 */

				Cell sharingCell = row.createCell(10);

				sharingCell.setCellValue(sharing.doubleValue());

				sharingCell.setCellStyle(amountStyle);

				/*
				 * ==================================================== GRAND TOTAL CALCULATION
				 * ====================================================
				 */

				grandTotalAmount = grandTotalAmount.add(totalAmount);

				grandDiscount = grandDiscount.add(discount);

				grandCollectedAmount = grandCollectedAmount.add(collectedAmount);

				grandCollectedByDoctor = grandCollectedByDoctor.add(collectedByDoctor);

				grandSharing = grandSharing.add(sharing);
			}

			/*
			 * ======================================================== CREATE EXCEL TABLE
			 *
			 * Header row = row 2 First data row = row 3 Last data row = rowNumber - 1
			 *
			 * Grand total is NOT included.
			 * ========================================================
			 */

			if (rowNumber > 3) {

				int headerRowIndex = 2;

				int lastDataRowIndex = rowNumber - 1;

				AreaReference areaReference = new AreaReference(new CellReference(headerRowIndex, 0),
						new CellReference(lastDataRowIndex, 10), SpreadsheetVersion.EXCEL2007);

				XSSFTable table = sheet.createTable(areaReference);

				table.setName("PatientReportTable");

				table.setDisplayName("PatientReportTable");

				/*
				 * ==================================================== TABLE STYLE
				 * ====================================================
				 */

				CTTableStyleInfo styleInfo = table.getCTTable().addNewTableStyleInfo();

				styleInfo.setName("TableStyleMedium2");

				styleInfo.setShowFirstColumn(false);

				styleInfo.setShowLastColumn(false);

				styleInfo.setShowRowStripes(true);

				styleInfo.setShowColumnStripes(false);
			}

			/*
			 * ======================================================== GRAND TOTAL ROW
			 *
			 * This is outside the Excel table.
			 * ========================================================
			 */

			Row totalRow = sheet.createRow(rowNumber++);

			/*
			 * TOTAL LABEL
			 */

			Cell totalLabel = totalRow.createCell(5);

			totalLabel.setCellValue("TOTAL");

			totalLabel.setCellStyle(headerStyle);

			/*
			 * TOTAL AMOUNT
			 */

			Cell grandTotalCell = totalRow.createCell(6);

			grandTotalCell.setCellValue(grandTotalAmount.doubleValue());

			grandTotalCell.setCellStyle(amountStyle);

			/*
			 * TOTAL DISCOUNT
			 */

			Cell grandDiscountCell = totalRow.createCell(7);

			grandDiscountCell.setCellValue(grandDiscount.doubleValue());

			grandDiscountCell.setCellStyle(amountStyle);

			/*
			 * TOTAL COLLECTED AMOUNT
			 */

			Cell grandCollectedCell = totalRow.createCell(8);

			grandCollectedCell.setCellValue(grandCollectedAmount.doubleValue());

			grandCollectedCell.setCellStyle(amountStyle);

			/*
			 * TOTAL COLLECTED BY DOCTOR
			 */

			Cell grandCollectedByDoctorCell = totalRow.createCell(9);

			grandCollectedByDoctorCell.setCellValue(grandCollectedByDoctor.doubleValue());

			grandCollectedByDoctorCell.setCellStyle(amountStyle);

			/*
			 * TOTAL SHARING
			 */

			Cell grandSharingCell = totalRow.createCell(10);

			grandSharingCell.setCellValue(grandSharing.doubleValue());

			grandSharingCell.setCellStyle(amountStyle);

			/*
			 * ======================================================== SUMMARY CALCULATIONS
			 * ========================================================
			 */

			BigDecimal totalAfterDiscount = grandTotalAmount.subtract(grandDiscount);

			BigDecimal sharingToPay = grandSharing.subtract(grandCollectedByDoctor);

			/*
			 * Prevent negative values.
			 */

			if (totalAfterDiscount.compareTo(BigDecimal.ZERO) < 0) {

				totalAfterDiscount = BigDecimal.ZERO;
			}

			if (sharingToPay.compareTo(BigDecimal.ZERO) < 0) {

				sharingToPay = BigDecimal.ZERO;
			}

			/*
			 * ======================================================== SUMMARY START
			 * ========================================================
			 */

			// Add one blank row before summary
			rowNumber++;

			/*
			 * ======================================================== SUMMARY TITLE
			 * ========================================================
			 */

			Row summaryTitleRow = sheet.createRow(rowNumber++);

			Cell summaryTitleCell = summaryTitleRow.createCell(5);

			summaryTitleCell.setCellValue("SUMMARY");

			summaryTitleCell.setCellStyle(summaryTotalLabelStyle);

			/*
			 * Merge SUMMARY title across columns 5 and 6
			 */

			sheet.addMergedRegion(new CellRangeAddress(summaryTitleRow.getRowNum(), summaryTitleRow.getRowNum(), 5, 6));

			/*
			 * ======================================================== 1. TOTAL AMOUNT TO
			 * BE COLLECTED ========================================================
			 */

			Row summaryRow1 = sheet.createRow(rowNumber++);

			Cell summaryLabel1 = summaryRow1.createCell(5);

			summaryLabel1.setCellValue("Total Amount Need To Be collected");

			summaryLabel1.setCellStyle(summaryLabelStyle);

			Cell summaryValue1 = summaryRow1.createCell(6);

			summaryValue1.setCellValue(grandTotalAmount.doubleValue());

			summaryValue1.setCellStyle(summaryValueStyle);

			/*
			 * ======================================================== 2. DISCOUNT
			 * ========================================================
			 */

			Row summaryRow2 = sheet.createRow(rowNumber++);

			Cell summaryLabel2 = summaryRow2.createCell(5);

			summaryLabel2.setCellValue("Discount");

			summaryLabel2.setCellStyle(summaryLabelStyle);

			Cell summaryValue2 = summaryRow2.createCell(6);

			summaryValue2.setCellValue(grandDiscount.doubleValue());

			summaryValue2.setCellStyle(summaryValueStyle);

			/*
			 * ======================================================== 3. TOTAL
			 *
			 * grandTotalAmount - grandDiscount
			 * ========================================================
			 */

			Row summaryRow3 = sheet.createRow(rowNumber++);

			Cell summaryLabel3 = summaryRow3.createCell(5);

			summaryLabel3.setCellValue("Total");

			summaryLabel3.setCellStyle(summaryTotalLabelStyle);

			Cell summaryValue3 = summaryRow3.createCell(6);

			summaryValue3.setCellValue(totalAfterDiscount.doubleValue());

			summaryValue3.setCellStyle(summaryTotalValueStyle);

			/*
			 * ======================================================== 4. AMOUNT COLLECTED
			 * ========================================================
			 */

			Row summaryRow4 = sheet.createRow(rowNumber++);

			Cell summaryLabel4 = summaryRow4.createCell(5);

			summaryLabel4.setCellValue("Amount Collected");

			summaryLabel4.setCellStyle(summaryLabelStyle);

			Cell summaryValue4 = summaryRow4.createCell(6);

			summaryValue4.setCellValue(grandCollectedAmount.doubleValue());

			summaryValue4.setCellStyle(summaryValueStyle);

			/*
			 * ======================================================== 5. AMOUNT COLLECTED
			 * BY DR ========================================================
			 */

			Row summaryRow5 = sheet.createRow(rowNumber++);

			Cell summaryLabel5 = summaryRow5.createCell(5);

			summaryLabel5.setCellValue("Amount Collected By Dr");

			summaryLabel5.setCellStyle(summaryLabelStyle);

			Cell summaryValue5 = summaryRow5.createCell(6);

			summaryValue5.setCellValue(grandCollectedByDoctor.doubleValue());

			summaryValue5.setCellStyle(summaryValueStyle);

			/*
			 * ======================================================== 6. SHARING NEED TO
			 * PAY
			 *
			 * grandSharing - grandCollectedByDoctor
			 * ========================================================
			 */

			Row summaryRow6 = sheet.createRow(rowNumber++);

			Cell summaryLabel6 = summaryRow6.createCell(5);

			summaryLabel6.setCellValue("Sharing Need To Pay");

			summaryLabel6.setCellStyle(summaryTotalLabelStyle);

			Cell summaryValue6 = summaryRow6.createCell(6);

			summaryValue6.setCellValue(sharingToPay.doubleValue());

			summaryValue6.setCellStyle(summaryTotalValueStyle);

			/*
			 * ======================================================== COLUMN WIDTHS
			 * ========================================================
			 */

			sheet.setColumnWidth(0, 3000);

			sheet.setColumnWidth(1, 6000);

			sheet.setColumnWidth(2, 5500);

			sheet.setColumnWidth(3, 9000);

			sheet.setColumnWidth(4, 22000);

			sheet.setColumnWidth(5, 10000);

			sheet.setColumnWidth(6, 5000);

			sheet.setColumnWidth(7, 5000);

			sheet.setColumnWidth(8, 6000);

			sheet.setColumnWidth(9, 7000);

			sheet.setColumnWidth(10, 5000);

			/*
			 * ======================================================== FREEZE TOP 3 ROWS
			 * ========================================================
			 */

			sheet.createFreezePane(0, 3);

			/*
			 * ======================================================== WRITE EXCEL
			 * ========================================================
			 */

			workbook.write(outputStream);

			return outputStream.toByteArray();
		}
	}
}