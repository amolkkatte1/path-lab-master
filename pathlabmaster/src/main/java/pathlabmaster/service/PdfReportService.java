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

import com.lowagie.text.Chunk;
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
import pathlabmaster.utility.Utility;

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
	@Autowired
	IReportService reportService;
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

	// ============================================================
	// CREATE INDIVIDUAL REPORT PDF
	// ============================================================

	public PdfResponse createPdf(Long patientId, String reportIds, boolean headerRequired,
	        boolean mdSignRequired, boolean printGroup) throws Exception {

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

	    boolean isQrRequired = false;
	    int qrCodePositionHorizantal = 2;
	    int qrCodePositionVertical = 2;
	    float qrSize = 60f;

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
	    // =====================================================

	    byte[] qrCode = null;

	    if (isQrRequired) {

			String qrUrl = Constants.SELF_BASE_URL_PROD
					+ Constants.QR_CODE_URL.replaceFirst("\\{}", String.valueOf(patientId))
							.replaceFirst("\\{}", reportIds).replaceFirst("\\{}", String.valueOf(headerRequired))
							.replaceFirst("\\{}", String.valueOf(mdSignRequired))
							.replaceFirst("\\{}", String.valueOf(printGroup));

	        qrCode = qrCodeService.generateQRCode(qrUrl, 2, 2);
	    }

	    // =====================================================
	    // MD SIGNATURE
	    // =====================================================

	    MdDoctorMaster mdDoctorDetails = null;
	    byte[] mdSign = null;

	    int mdSignPositionHorizantal = 3;

	    if (mdSignRequired) {

	        mdDoctorDetails = mdDoctorRepo
	                .findFirstByLabIdAndIsActiveTrueOrderByCreatedAtDesc(
	                        patientDetails.getLabId())
	                .orElse(null);

	        if (mdDoctorDetails != null) {

	            mdSign = mdDoctorDetails.getSignImage();

	            if (mdDoctorDetails.getSignPosition() != null) {
	                mdSignPositionHorizantal = mdDoctorDetails.getSignPosition();
	            }
	        }
	    }

	    // =====================================================
	    // PAGE SPACING
	    // =====================================================

	    int topMargin = 20;

	    if (clientConfig != null
	            && clientConfig.getReportTopSpace() != null) {

	        topMargin = clientConfig.getReportTopSpace();
	    }

	    int bottomMargin = 20;

	    if (clientConfig != null
	            && clientConfig.getReportBottomSpace() != null) {

	        bottomMargin = clientConfig.getReportBottomSpace();
	    }

	    if (isQrRequired && mdSignRequired) {

	        bottomMargin = Math.max(
	                bottomMargin,
	                125);

	    } else if (isQrRequired) {

	        bottomMargin = Math.max(
	                bottomMargin,
	                90);

	    } else if (mdSignRequired) {

	        bottomMargin = Math.max(
	                bottomMargin,
	                100);
	    }

	    // =====================================================
	    // DOCUMENT
	    // =====================================================

	    Document document = new Document(
	            PageSize.A4,
	            30,
	            30,
	            78 + topMargin,
	            bottomMargin);

	    PdfWriter writer = PdfWriter.getInstance(
	            document,
	            outputStream);

	    // =====================================================
	    // FONTS
	    // =====================================================

	    Font normalFont = new Font(
	            Font.HELVETICA,
	            10,
	            Font.NORMAL);

	    Font boldFont = new Font(
	            Font.HELVETICA,
	            10,
	            Font.BOLD);

	    Font normalFont1 = new Font(
	            Font.HELVETICA,
	            10,
	            Font.NORMAL);

	    Font boldFont1 = new Font(
	            Font.HELVETICA,
	            10,
	            Font.BOLD);

	    // =====================================================
	    // GET REPORT
	    // =====================================================

	    ReportMaster reportDetails =
	            reportMasterRepo.findByPatientIdAndLabId(
	                    patientId,
	                    patientDetails.getLabId());

	    if (reportDetails == null) {
	        throw new RuntimeException(
	                "Report not found for patient : " + patientId);
	    }

	    reportDetails.setPendingTest1(null);

	    reportDetails =
	            reportService.convertReportMasterForUI(
	                    reportDetails);

	    if (reportDetails == null) {
	        throw new RuntimeException(
	                "Report not found for patient : " + patientId);
	    }

	    // =====================================================
	    // PAGE HEADER
	    //
	    // IMPORTANT:
	    // Only patient header is drawn in onEndPage().
	    // Column header is now added in document flow.
	    // =====================================================

	    ReportPageHeader pageHeader =
	            new ReportPageHeader(
	                    patientId,
	                    patientDetails,
	                    reportDetails,
	                    boldFont,
	                    topMargin,
	                    qrCode,
	                    qrCodePositionHorizantal,
	                    qrCodePositionVertical,
	                    qrSize,
	                    isQrRequired,
	                    mdSign,
	                    mdDoctorDetails,
	                    mdSignPositionHorizantal,
	                    mdSignRequired);

	    writer.setPageEvent(pageHeader);

	    document.open();

	    // =====================================================
	    // REPORT IDS
	    // =====================================================

	    List<String> reportIdList =
	            Arrays.asList(reportIds.split("\\|"));

	    // =====================================================
	    // ORIGINAL COMPLETED TESTS
	    // =====================================================

	    Map<String, List<ParameterDetails>> reportOriginal =
	            reportDetails.getCompletedTest();

	    Map<String, List<ParameterDetails>> reports =
	            new HashMap<>();

	    if (reportOriginal != null) {

	        for (Map.Entry<String, List<ParameterDetails>> entry
	                : reportOriginal.entrySet()) {

	            String id = entry.getKey();

	            if (id != null && id.length() >= 17) {

	                String shortId =
	                        id.substring(id.length() - 17);

	                reports.put(
	                        shortId,
	                        entry.getValue());
	            }
	        }
	    }

	    // =====================================================
	    // GROUP TESTS
	    // =====================================================

	    Map<String, List<List<ParameterDetails>>> groupedTests =
	            new LinkedHashMap<>();

	    for (String reportId : reportIdList) {

	        if (reportId == null
	                || reportId.trim().isEmpty()) {
	            continue;
	        }

	        reportId = reportId.trim();

	        List<ParameterDetails> testDetails =
	                reports.get(reportId);

	        if (testDetails == null
	                || testDetails.isEmpty()) {
	            continue;
	        }

	        // Sort parameters by sequence
	        testDetails.sort(
	                Comparator.comparing(
	                        ParameterDetails::getSequence,
	                        Comparator.nullsLast(
	                                Integer::compareTo)));

	        // =================================================
	        // FIND GROUP NAME
	        // sequence = 1
	        // =================================================

	        String groupName = "";

	        for (ParameterDetails parameter : testDetails) {

	            if (parameter.getSequence() != null
	                    && parameter.getSequence() == 1) {

	                groupName =
	                        safe(parameter.getParameterName());

	                break;
	            }
	        }

	        if (groupName == null
	                || groupName.trim().isEmpty()) {

	            groupName = "OTHER";
	        }

	        groupedTests
	                .computeIfAbsent(
	                        groupName,
	                        k -> new ArrayList<>())
	                .add(testDetails);
	    }

	    // =====================================================
	    // HAEMATOLOGY FIRST
	    // =====================================================

	    if (groupedTests.size() > 1) {

	        Map<String, List<List<ParameterDetails>>> orderedGroups =
	                new LinkedHashMap<>();

	        String haematologyKey = null;

	        for (String groupName : groupedTests.keySet()) {

	            if ("HAEMATOLOGY".equalsIgnoreCase(
	                    groupName != null
	                            ? groupName.trim()
	                            : "")) {

	                haematologyKey = groupName;
	                break;
	            }
	        }

	        if (haematologyKey != null) {

	            orderedGroups.put(
	                    haematologyKey,
	                    groupedTests.get(haematologyKey));
	        }

	        for (Map.Entry<String, List<List<ParameterDetails>>> entry
	                : groupedTests.entrySet()) {

	            if (haematologyKey != null
	                    && entry.getKey().equals(haematologyKey)) {
	                continue;
	            }

	            orderedGroups.put(
	                    entry.getKey(),
	                    entry.getValue());
	        }

	        groupedTests = orderedGroups;
	    }

	    // =====================================================
	    // PRINT GROUPS / TESTS
	    // =====================================================

	    int groupIndex = 0;

	    for (Map.Entry<String, List<List<ParameterDetails>>> groupEntry
	            : groupedTests.entrySet()) {

	        String groupName = groupEntry.getKey();

	        List<List<ParameterDetails>> testsInGroup =
	                groupEntry.getValue();

	        boolean groupTitlePrinted = false;

	        int previousTestPage = -1;

	        // =================================================
	        // EACH TEST
	        // =================================================

	        for (int testIndex = 0;
	                testIndex < testsInGroup.size();
	                testIndex++) {

	            List<ParameterDetails> testDetails =
	                    testsInGroup.get(testIndex);

	            // =============================================
	            // REMOVE SEQUENCE 1
	            // =============================================

	            List<ParameterDetails> parametersForTest =
	                    new ArrayList<>();

	            for (ParameterDetails parameter : testDetails) {

	                if (parameter.getSequence() != null
	                        && parameter.getSequence() == 1) {
	                    continue;
	                }

	                parametersForTest.add(parameter);
	            }

	            // =============================================
	            // CREATE TEST TABLE
	            // =============================================

	            PdfPTable testTable =
	                    createTestTable(
	                            parametersForTest,
	                            normalFont,
	                            boldFont,
	                            normalFont1,
	                            boldFont1);

	            // =================================================
	            // PRINT GROUP = FALSE
	            // One complete test per page
	            // =================================================

	            if (!printGroup) {

	                if (groupIndex > 0
	                        || testIndex > 0) {

	                    document.newPage();
	                }

	                // =============================================
	                // 1. TEST NAME / GROUP TITLE
	                // =============================================

	                addGroupTitle(
	                        document,
	                        groupName,
	                        boldFont);

	                // =============================================
	                // 2. COLUMN HEADER
	                // =============================================

	                PdfPTable columnHeaderTable =
	                        createColumnHeaderTable(boldFont);

	                document.add(columnHeaderTable);

	                // =============================================
	                // 3. TEST PARAMETERS
	                // =============================================

	                PdfPTable testWrapper =
	                        new PdfPTable(1);

	                testWrapper.setWidthPercentage(100);
	                testWrapper.setKeepTogether(true);

	                PdfPCell testCell =
	                        new PdfPCell(testTable);

	                testCell.setBorder(
	                        PdfPCell.NO_BORDER);

	                testCell.setPadding(0);

	                testWrapper.addCell(testCell);

	                document.add(testWrapper);

	                continue;
	            }

	            // =================================================
	            // PRINT GROUP = TRUE
	            // =================================================

	            float requiredTestHeight =
	                    calculateTestHeight(testDetails);

	            requiredTestHeight += 4;

	            float currentY =
	                    writer.getVerticalPosition(true);

	            float availableHeight =
	                    currentY - document.bottomMargin();

	            // =================================================
	            // GROUP TITLE
	            // =================================================

	            if (!groupTitlePrinted) {

	                float groupTitleHeight = 18f;

	                float requiredHeight =
	                        groupTitleHeight
	                        + requiredTestHeight;

	                if (requiredHeight > availableHeight) {

	                    document.newPage();

	                    addGroupTitle(
	                            document,
	                            groupName,
	                            boldFont);

	                } else {

	                    addGroupTitle(
	                            document,
	                            groupName,
	                            boldFont);
	                }

	                groupTitlePrinted = true;

	            } else {

	                if (requiredTestHeight > availableHeight) {

	                    document.newPage();

	                    addGroupTitle(
	                            document,
	                            groupName,
	                            boldFont);
	                }
	            }

	            // =================================================
	            // COLUMN HEADER
	            //
	            // Test title is already printed above.
	            // Now print column header.
	            // =================================================

	            PdfPTable columnHeaderTable =
	                    createColumnHeaderTable(boldFont);

	            document.add(columnHeaderTable);

	            // =================================================
	            // CURRENT PAGE
	            // =================================================

	            int currentPage =
	                    writer.getPageNumber();

	            // =================================================
	            // SEPARATOR BETWEEN TESTS
	            // =================================================

	            boolean addSeparator =
	                    testIndex > 0
	                    && previousTestPage == currentPage;

	            if (addSeparator) {

	                Paragraph spaceBeforeLine =
	                        new Paragraph(" ");

	                spaceBeforeLine.setLeading(3);

	                document.add(spaceBeforeLine);

	                PdfPTable separatorTable =
	                        new PdfPTable(1);

	                separatorTable.setWidthPercentage(100);

	                PdfPCell separatorCell =
	                        new PdfPCell();

	                separatorCell.setBorder(
	                        PdfPCell.TOP);

	                separatorCell.setBorderWidthTop(
	                        0.8f);

	                separatorCell.setPaddingTop(0);
	                separatorCell.setPaddingBottom(0);

	                separatorTable.addCell(
	                        separatorCell);

	                document.add(separatorTable);

	                Paragraph spaceAfterLine =
	                        new Paragraph(" ");

	                spaceAfterLine.setLeading(3);

	                document.add(spaceAfterLine);
	            }

	            // =================================================
	            // TEST TABLE
	            // =================================================

	            PdfPTable testWrapper =
	                    new PdfPTable(1);

	            testWrapper.setWidthPercentage(100);
	            testWrapper.setKeepTogether(true);

	            PdfPCell testCell =
	                    new PdfPCell(testTable);

	            testCell.setBorder(
	                    PdfPCell.NO_BORDER);

	            testCell.setPadding(0);

	            testWrapper.addCell(testCell);

	            document.add(testWrapper);

	            // =================================================
	            // SPACE AFTER TEST
	            // =================================================

	            Paragraph testSpace =
	                    new Paragraph(" ");

	            testSpace.setLeading(5);

	            document.add(testSpace);

	            previousTestPage =
	                    writer.getPageNumber();
	        }

	        groupIndex++;

	        // =====================================================
	        // SPACE BETWEEN GROUPS
	        // =====================================================

	        if (printGroup
	                && groupIndex < groupedTests.size()) {

	            Paragraph groupSpace =
	                    new Paragraph(" ");

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

	    String fileName =
	            "Report-"
	            + safe(patientDetails.getFirstName())
	            + " "
	            + safe(patientDetails.getMiddleName())
	            + " "
	            + safe(patientDetails.getLastName())
	            + ".pdf";

	    return new PdfResponse(
	            outputStream.toByteArray(),
	            fileName);
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
		String space = "Y\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";
		if(patientDetails.getGender().equalsIgnoreCase("female")){
			space = "Y\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";
		}else if(patientDetails.getGender().equalsIgnoreCase("other")) {
			space = "Y\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";
		}

		addPatientCell(patientTable, "Reg No", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, ": " + patientId + " / OPD", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, "Sex / Age", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, ": " + safe(patientDetails.getGender()) + " / " + safe(patientDetails.getYear())
				+ space,
				boldFont, Element.ALIGN_RIGHT);

		addPatientCell(patientTable, "Name", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, ": " + safe(patientDetails.getPrefix()) + " "+safe(patientDetails.getFirstName()) + " "
				+ safe(patientDetails.getMiddleName()) + " " + safe(patientDetails.getLastName()), boldFont,
				Element.ALIGN_LEFT);

		addPatientCell(patientTable, "Reg Date", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, ": " + safe(Utility.formatDateTime(patientDetails.getCreatedAt())), boldFont, Element.ALIGN_RIGHT);

		addPatientCell(patientTable, "Referred Dr", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, ": " + safe(patientDetails.getDoctorName()), boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, "Report Date", boldFont, Element.ALIGN_LEFT);

		addPatientCell(patientTable, ": " + safe(Utility.formatDateTime(reportDetails.getCreatedAt())), boldFont, Element.ALIGN_RIGHT);

		patientTable.addCell(createEmptyCell(4));

		return patientTable;
	}

	// ============================================================
	// TEST TITLE
	// ============================================================

	// ============================================================
	// TEST TITLE
	// ============================================================

	private PdfPTable createTestTitleTable(String testName, Font boldFont) {

		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(100);

		PdfPCell cell = new PdfPCell(new Phrase(testName, boldFont));

		// Only bottom border
		cell.setBorder(PdfPCell.BOTTOM);
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

	// ============================================================
	// TEST TABLE
	// ============================================================

	// ============================================================
	// TEST TABLE
	// ============================================================

	private PdfPTable createTestTable(List<ParameterDetails> testDetails, Font normalFont, Font boldFont,
			Font normalFont1, Font boldFont1) {

		PdfPTable testTable = new PdfPTable(4);

		testTable.setWidthPercentage(100);
		testTable.setWidths(new float[] { 40, 15, 15, 30 });

		for (ParameterDetails parameter : testDetails) {

			if (parameter.getSequence() != null && parameter.getSequence() == 1) {
				continue;
			}

			String parameterName = safe(parameter.getParameterName());
			String value = safe(parameter.getValue());
			String unit = safe(parameter.getUnit());
			String referenceRange = getReferenceRange(parameter);

			boolean sequence2 = parameter.getSequence() != null && parameter.getSequence() == 2;

			boolean descriptionParameter = Boolean.TRUE.equals(parameter.getIsDescriptionParameter());

			// =====================================================
			// PARAMETER NAME
			// =====================================================

			Font parameterNameFont = sequence2 ? boldFont
					: Boolean.TRUE.equals(parameter.getIsNameBold()) ? boldFont1 : normalFont1;

			if (descriptionParameter) {

				PdfPCell parameterNameCell = new PdfPCell(new Phrase(parameterName, parameterNameFont));

				parameterNameCell.setBorder(PdfPCell.NO_BORDER);
				parameterNameCell.setColspan(4);

				parameterNameCell.setPaddingLeft(5);
				parameterNameCell.setPaddingRight(5);

				// Reduced spacing
				parameterNameCell.setPaddingTop(0);
				parameterNameCell.setPaddingBottom(0);

				parameterNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				parameterNameCell.setVerticalAlignment(Element.ALIGN_TOP);

				testTable.addCell(parameterNameCell);

			} else {

				PdfPCell parameterNameCell = new PdfPCell(new Phrase(parameterName, parameterNameFont));

				parameterNameCell.setBorder(PdfPCell.NO_BORDER);

				parameterNameCell.setPaddingLeft(5);
				parameterNameCell.setPaddingRight(5);

				// =================================================
				// REDUCED HEIGHT FOR HAEMOGRAM / SEQUENCE 2
				// =================================================

				if (sequence2) {
					parameterNameCell.setPaddingTop(0);
					parameterNameCell.setPaddingBottom(2);
				} else {
					parameterNameCell.setPaddingTop(1);
					parameterNameCell.setPaddingBottom(1);
				}

				parameterNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				parameterNameCell.setVerticalAlignment(Element.ALIGN_TOP);

				testTable.addCell(parameterNameCell);
			}

			// =====================================================
			// CHECK NUMBER VALUE AGAINST RANGE
			// =====================================================

			boolean outOfRange = false;

			if ("number".equalsIgnoreCase(safe(parameter.getDataType())) && parameter.getValue() != null
					&& !parameter.getValue().trim().isEmpty() && parameter.getLowerRange() != null
					&& parameter.getUpperRange() != null) {

				try {

					BigDecimal actualValue = new BigDecimal(parameter.getValue().trim());

					BigDecimal lowerRange = parameter.getLowerRange();
					BigDecimal upperRange = parameter.getUpperRange();

					if (actualValue.compareTo(lowerRange) < 0 || actualValue.compareTo(upperRange) > 0) {

						outOfRange = true;
					}

				} catch (NumberFormatException e) {
					// Ignore invalid numeric values
				}
			}

			// =====================================================
			// VALUE FONT
			// =====================================================

			Font valueFont = outOfRange ? boldFont1
					: Boolean.TRUE.equals(parameter.getIsBold()) ? boldFont1 : normalFont1;

			// =====================================================
			// DESCRIPTION VALUE
			// =====================================================

			if (Boolean.TRUE.equals(parameter.getIsValueDiscription())) {

				PdfPCell descriptionCell;

				if (outOfRange) {

					Chunk valueChunk = new Chunk(value, boldFont1);
					valueChunk.setBackground(java.awt.Color.YELLOW);

					descriptionCell = new PdfPCell(new Phrase(valueChunk));

				} else {

					descriptionCell = new PdfPCell(new Phrase(value, valueFont));
				}

				descriptionCell.setBorder(PdfPCell.NO_BORDER);
				descriptionCell.setColspan(3);

				descriptionCell.setPaddingLeft(5);
				descriptionCell.setPaddingRight(5);
				descriptionCell.setPaddingTop(1);
				descriptionCell.setPaddingBottom(1);

				descriptionCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				descriptionCell.setVerticalAlignment(Element.ALIGN_TOP);

				testTable.addCell(descriptionCell);

			} else if (!descriptionParameter) {

				// =================================================
				// NORMAL VALUE CELL
				// =================================================

				PdfPCell valueCell;

				if (outOfRange) {

					Chunk valueChunk = new Chunk(value, boldFont1);
					valueChunk.setBackground(java.awt.Color.YELLOW);

					valueCell = new PdfPCell(new Phrase(valueChunk));

				} else {

					valueCell = new PdfPCell(new Phrase(value, valueFont));
				}

				valueCell.setBorder(PdfPCell.NO_BORDER);

				valueCell.setPaddingLeft(5);
				valueCell.setPaddingRight(5);

				// Reduce sequence 2 row height
				if (sequence2) {
					valueCell.setPaddingTop(0);
					valueCell.setPaddingBottom(0);
				} else {
					valueCell.setPaddingTop(1);
					valueCell.setPaddingBottom(1);
				}

				valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				valueCell.setVerticalAlignment(Element.ALIGN_TOP);

				testTable.addCell(valueCell);

				// =================================================
				// UNIT
				// =================================================

				addParameterCell(testTable, unit, normalFont1, Element.ALIGN_LEFT);

				// =================================================
				// REFERENCE RANGE
				// =================================================

				addParameterCell(testTable, referenceRange, normalFont1, Element.ALIGN_LEFT);
			}

			// =====================================================
			// SEQUENCE 2 BORDER
			// =====================================================
			// Keep the line but reduce the empty space before it.
			// =====================================================

			if (sequence2) {

				// Small space before border
				PdfPCell spaceCell = new PdfPCell();

				spaceCell.setColspan(4);
				spaceCell.setBorder(PdfPCell.NO_BORDER);

				// OLD: 4f
				// NEW: 1f
				spaceCell.setFixedHeight(1f);

				spaceCell.setPadding(0);

				testTable.addCell(spaceCell);

				// =================================================
				// BORDER LINE
				// =================================================

				PdfPCell lineCell = new PdfPCell();

				lineCell.setColspan(4);
				lineCell.setBorder(PdfPCell.BOTTOM);

				lineCell.setBorderWidthBottom(0.8f);

				lineCell.setPadding(0);

				// Keep line itself thin
				lineCell.setFixedHeight(1f);

				testTable.addCell(lineCell);
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

	// ============================================================
	// HEADER CELL
	// ============================================================

	private void addHeaderCell(PdfPTable table, String text, Font font, int alignment) {

		PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));

		// Same border thickness
		cell.setBorder(PdfPCell.BOTTOM);

//		cell.setBorderWidthTop(0.8f);
		cell.setBorderWidthBottom(0.8f);

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

	    BigDecimal lowerRange = parameter.getLowerRange();
	    BigDecimal upperRange = parameter.getUpperRange();
	    String parameterRange = parameter.getParameterRange();

	    boolean hasParameterRange = parameterRange != null && !parameterRange.isEmpty();

	    if (lowerRange != null && upperRange != null) {
	        String lower = lowerRange.stripTrailingZeros().toPlainString();
	        String upper = upperRange.stripTrailingZeros().toPlainString();
	        if (hasParameterRange && parameterRange.equalsIgnoreCase("upto")) {
	            return parameterRange + " " + upper;
	        }else if (hasParameterRange) {
	            return lower + " " + parameterRange + " " + upper;
	        }

	        return lower + " - " + upper;
	    }

	    if (upperRange != null && hasParameterRange) {
	        return parameterRange + "  "
	                + upperRange.stripTrailingZeros().toPlainString();
	    }

	    if (hasParameterRange) {
	        return parameterRange;
	    }

	    return "";
	}

	// ============================================================
	// CALCULATE TEST HEIGHT
	// ============================================================

	// ============================================================
	// CALCULATE TEST HEIGHT
	// ============================================================

	// ============================================================
	// CALCULATE TEST HEIGHT
	// ============================================================

	private float calculateTestHeight(List<ParameterDetails> testDetails) {

		float height = 0;

		// Test title / basic table space
		height += 22;

		// Base test row space
		height += 20;

		for (ParameterDetails parameter : testDetails) {

			if (parameter.getSequence() != null && parameter.getSequence() == 1) {
				continue;
			}

			// =====================================================
			// NORMAL PARAMETER ROW
			// =====================================================

			height += 17;

			// =====================================================
			// DESCRIPTION
			// =====================================================

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

			// =====================================================
			// REFERENCE RANGE
			// =====================================================

			String range = getReferenceRange(parameter);

			if (range.contains("\n")) {

				int lines = range.split("\n").length;

				height += (lines - 1) * 11;
			}

			// =====================================================
			// SEQUENCE 2
			// =====================================================
			// Reduced from:
			// 4f space + 1f border
			//
			// To:
			// 1f space + 1f border
			// =====================================================

			if (parameter.getSequence() != null && parameter.getSequence() == 2) {

				height += 1; // space before border
				height += 1; // border
			}
		}

		// Space after test
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

		cell.setPaddingTop(4);

		cell.setPaddingBottom(4);

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

// =====================================================
// QR
// =====================================================

		private final byte[] qrCode;
		private final int qrCodePositionHorizantal;
		private final int qrCodePositionVertical;
		private final float qrSize;
		private final boolean isQrRequired;

// =====================================================
// MD SIGN
// =====================================================

		private final byte[] mdSign;
		private final MdDoctorMaster mdDoctorDetails;
		private final int mdSignPositionHorizantal;
		private final boolean mdSignRequired;

		public ReportPageHeader(Long patientId, PatientMaster patientDetails, ReportMaster reportDetails, Font boldFont,
				int topMargin,

				// QR
				byte[] qrCode, int qrCodePositionHorizantal, int qrCodePositionVertical, float qrSize,
				boolean isQrRequired,

				// MD SIGN
				byte[] mdSign, MdDoctorMaster mdDoctorDetails, int mdSignPositionHorizantal, boolean mdSignRequired) {

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

			this.mdSign = mdSign;
			this.mdDoctorDetails = mdDoctorDetails;
			this.mdSignPositionHorizantal = mdSignPositionHorizantal;
			this.mdSignRequired = mdSignRequired;
		}

// =====================================================
// ON END PAGE
// =====================================================

		@Override
		public void onEndPage(PdfWriter writer, Document document) {
		    try {
		        PdfContentByte canvas = writer.getDirectContent();
		        canvas.saveState();

		        float pageWidth = document.getPageSize().getWidth();
		        float pageHeight = document.getPageSize().getHeight();

		        float left = document.leftMargin();
		        float right = pageWidth - document.rightMargin();
		        float width = right - left;

		        // =====================================================
		        // PATIENT HEADER
		        // =====================================================

		        PdfPTable headerTable =
		                createPatientTable(
		                        patientId,
		                        patientDetails,
		                        reportDetails,
		                        boldFont);

		        headerTable.setTotalWidth(width);

		        float headerY =
		                pageHeight - (20 + topMargin);

		        headerTable.writeSelectedRows(
		                0,
		                -1,
		                left,
		                headerY,
		                canvas);

		        // =====================================================
		        // BOTTOM BORDER AFTER PATIENT HEADER
		        // =====================================================

		        float patientHeaderHeight = headerTable.getTotalHeight();

		        float lineY = headerY - patientHeaderHeight - 6;

		        canvas.setLineWidth(0.8f);
		        canvas.moveTo(left, lineY);
		        canvas.lineTo(right, lineY);
		        canvas.stroke();

		        // =====================================================
		        // FOOTER
		        // =====================================================

		        drawEndOfReport(
		                canvas,
		                document);

		        canvas.restoreState();

		    } catch (Exception e) {

		        throw new RuntimeException(
		                "Error while creating PDF page header/footer",
		                e);
		    }
		}

// =====================================================
// END OF REPORT
// =====================================================

		private void drawEndOfReport(PdfContentByte canvas, Document document) {

			float pageWidth = document.getPageSize().getWidth();

			float left = document.leftMargin();

			float right = pageWidth - document.rightMargin();

			float centerX = (left + right) / 2;

			// =====================================================
			// FOOTER LINE
			// =====================================================

			float lineY;

			if (isQrRequired || mdSignRequired) {

				lineY = document.bottomMargin() + 100;

			} else {

				lineY = document.bottomMargin() + 18;
			}

			canvas.setLineWidth(0.5f);

			canvas.moveTo(left, lineY - 80);
			canvas.lineTo(right, lineY - 80);

			canvas.stroke();

			// =====================================================
			// END OF REPORT
			// =====================================================

			Font footerFont = new Font(Font.HELVETICA, 11, Font.BOLD);

			ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase("End of Report", footerFont), centerX,
					lineY - 95, 0);

			// =====================================================
			// DYNAMIC CONTENT ROW
			// =====================================================

			float contentY = lineY - 40;

			// =====================================================
			// CALCULATE BLOCK WIDTH
			// =====================================================

			float qrWidth = isQrRequired ? qrSize : 0;

			float mdWidth = mdSignRequired ? 90f : 0;

			float gap = 25f;

			// =====================================================
			// DYNAMIC POSITIONS
			// =====================================================

			float qrX = -1;
			float mdX = -1;

			// -----------------------------------------------------
			// QR POSITION
			// -----------------------------------------------------

			if (isQrRequired) {

				if (qrCodePositionHorizantal == 1) {

					// LEFT
					qrX = left;

				} else if (qrCodePositionHorizantal == 2) {

					// CENTER
					qrX = centerX - (qrWidth / 2);

				} else {

					// RIGHT
					qrX = right - qrWidth;
				}
			}

			// -----------------------------------------------------
			// MD POSITION
			// -----------------------------------------------------

			if (mdSignRequired) {

				if (mdSignPositionHorizantal == 1) {

					// LEFT
					mdX = left;

				} else if (mdSignPositionHorizantal == 2) {

					// CENTER
					mdX = centerX - (mdWidth / 2);

				} else {

					// RIGHT
					mdX = right - mdWidth;
				}
			}

			// =====================================================
			// OVERLAP CHECK
			// =====================================================

			if (isQrRequired && mdSignRequired) {

				float qrRight = qrX + qrWidth;

				float mdRight = mdX + mdWidth;

				boolean overlap = qrX < mdRight && mdX < qrRight;

				if (overlap) {

					// ---------------------------------------------
					// Both are configured at same position.
					// Automatically separate them.
					// ---------------------------------------------

					if (qrCodePositionHorizantal == 1) {

						// QR LEFT
						qrX = left;

						// MD RIGHT
						mdX = right - mdWidth;

					} else if (qrCodePositionHorizantal == 3) {

						// QR RIGHT
						qrX = right - qrWidth;

						// MD LEFT
						mdX = left;

					} else {

						// Both CENTER
						// Put them side-by-side around center.

						float totalWidth = qrWidth + gap + mdWidth;

						float startX = centerX - (totalWidth / 2);

						qrX = startX;

						mdX = startX + qrWidth + gap;
					}
				}
			}

			// =====================================================
			// DRAW QR
			// =====================================================

			if (isQrRequired) {

				drawQrCode(canvas, qrX, contentY);
			}

			// =====================================================
			// DRAW MD
			// =====================================================

			if (mdSignRequired) {

				drawMdSignature(canvas, mdX, contentY);
			}
		}

// =====================================================
// DRAW MD SIGNATURE
// =====================================================

		private void drawMdSignature(PdfContentByte canvas, float mdX, float contentY) {

			if (mdSign == null || mdSign.length == 0 || mdDoctorDetails == null) {
				return;
			}

			try {

				// =================================================
				// SIGNATURE
				// =================================================

				float signWidth = 100f;
				float signHeight = 40f;

				Image signImage = Image.getInstance(mdSign);

				signImage.scaleAbsolute(signWidth, signHeight);

				float signY = contentY - signHeight;

				// =================================================
				// SHIFT FULL MD BLOCK FROM BORDER
				// =================================================

				float shift = 20f;

				if (mdX < canvas.getPdfWriter().getPageSize().getWidth() / 2) {

					// LEFT SIDE
					mdX = mdX + shift;

				} else {

					// RIGHT SIDE
					mdX = mdX - shift;
				}

				// =================================================
				// ADD SIGNATURE
				// =================================================

				signImage.setAbsolutePosition(mdX, signY - 60);

				canvas.addImage(signImage);

				// =================================================
				// CENTER OF MD BLOCK
				// =================================================

				float mdCenterX = mdX + (signWidth / 2);

				// =================================================
				// DOCTOR NAME
				// =================================================

				String doctorName = safe(mdDoctorDetails.getDoctorName());

				Font doctorNameFont = new Font(Font.HELVETICA, 11, Font.BOLD);

				float doctorNameY = signY - 10;

				ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(doctorName, doctorNameFont),
						mdCenterX, doctorNameY - 60, 0);

				// =================================================
				// QUALIFICATION
				// =================================================

				String qualification = safe(mdDoctorDetails.getEducationQulification());

				Font qualificationFont = new Font(Font.HELVETICA, 11, Font.BOLD);

				// =================================================
				// MULTI-LINE QUALIFICATION
				// =================================================

				if (qualification != null && !qualification.trim().isEmpty()) {

					ColumnText qualificationColumn = new ColumnText(canvas);

					qualification = qualification.replace("\r\n", "\n").replace("\r", "\n");

					Paragraph qualificationParagraph = new Paragraph();

					qualificationParagraph.setFont(qualificationFont);
					qualificationParagraph.setAlignment(Element.ALIGN_CENTER);
					qualificationParagraph.setLeading(13f);

					String[] qualificationLines = qualification.split("\n");

					for (String line : qualificationLines) {

						Paragraph lineParagraph = new Paragraph(line.trim(), qualificationFont);

						lineParagraph.setAlignment(Element.ALIGN_CENTER);
						lineParagraph.setLeading(13f);

						qualificationParagraph.add(lineParagraph);
					}

					// =================================================
					// QUALIFICATION POSITION
					// =================================================

					float qualificationTop = doctorNameY - 72;
					float qualificationBottom = qualificationTop - 35;

					qualificationColumn.setSimpleColumn(mdX - 20, qualificationBottom, mdX + signWidth + 20,
							qualificationTop + 12);

					qualificationColumn.addElement(qualificationParagraph);

					qualificationColumn.go();
				}

			} catch (Exception e) {

				throw new RuntimeException("Error while adding MD signature", e);
			}
		}

// =====================================================
// DRAW QR CODE
// =====================================================

		private void drawQrCode(PdfContentByte canvas, float qrX, float contentY) {

			if (qrCode == null || qrCode.length == 0) {
				return;
			}

			try {

				Image qrImage = Image.getInstance(qrCode);

				// =====================================================
				// QR SIZE
				// =====================================================
				qrImage.scaleAbsolute(qrSize, qrSize);

				// =====================================================
				// POSITION
				// =====================================================
				float qrY = contentY - qrSize - 60;

				qrImage.setAbsolutePosition(qrX, qrY);

				// =====================================================
				// ADD QR CODE
				// =====================================================
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