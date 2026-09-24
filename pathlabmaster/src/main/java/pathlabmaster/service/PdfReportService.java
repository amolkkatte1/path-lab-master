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

	
	public PdfResponse createPdf(Long patientId, String reportIds,
	        boolean headerRequired, boolean mdSignRequired) throws Exception {

	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	    // =====================================================
	    // GET PATIENT
	    // =====================================================

	    PatientMaster patientDetails =
	            patientMasterRepo.findById(patientId).orElse(null);

	    if (patientDetails == null) {
	        throw new RuntimeException("Patient not found : " + patientId);
	    }

	    // =====================================================
	    // CLIENT CONFIG
	    // =====================================================

	    ClientConfig clientConfig =
	            clientConfigRepo.findByLabId(patientDetails.getLabId());

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
	            qrCodePositionHorizantal =
	                    clientConfig.getQrCodeHorizantalPosition();
	        }

	        if (clientConfig.getQrCodeVerticalPosition() != null) {
	            qrCodePositionVertical =
	                    clientConfig.getQrCodeVerticalPosition();
	        }
	    }

	    // =====================================================
	    // QR CODE
	    // Generate ONLY when required
	    // =====================================================

	    byte[] qrCode = null;

	    if (isQrRequired) {

	        String qrUrl =
	                Constants.SELF_BASE_URL_PROD
	                        + Constants.QR_CODE_URL
	                                .replaceFirst(
	                                        "\\{}",
	                                        String.valueOf(patientId)
	                                )
	                                .replaceFirst(
	                                        "\\{}",
	                                        reportIds
	                                )
	                                .replaceFirst(
	                                        "\\{}",
	                                        String.valueOf(headerRequired)
	                                )
	                                .replaceFirst(
	                                        "\\{}",
	                                        String.valueOf(mdSignRequired)
	                                );

	        qrCode =
	                qrCodeService.generateQRCode(
	                        qrUrl,
	                        2,
	                        2
	                );
	    }

	    // =====================================================
	    // MD DOCTOR / SIGNATURE
	    // Fetch ONLY when mdSignRequired = true
	    // =====================================================

	    MdDoctorMaster mdDoctorDetails = null;

	    byte[] mdSign = null;

	    int mdSignPositionHorizantal = 3;

	    if (mdSignRequired) {

	        mdDoctorDetails =
	                mdDoctorRepo
	                        .findFirstByLabIdAndIsActiveTrueOrderByCreatedAtDesc(
	                                patientDetails.getLabId()
	                        )
	                        .orElse(null);

	        if (mdDoctorDetails != null) {

	            mdSign =
	                    mdDoctorDetails.getSignImage();

	            if (mdDoctorDetails.getSignPosition() != null) {

	                mdSignPositionHorizantal =
	                        mdDoctorDetails.getSignPosition();
	            }
	        }
	    }

	    // =====================================================
	    // TOP MARGIN
	    // =====================================================

	    int topMargin = 20;

	    if (clientConfig != null
	            && clientConfig.getReportTopSpace() != null) {

	        topMargin =
	                clientConfig.getReportTopSpace();
	    }

	    // =====================================================
	    // BOTTOM MARGIN
	    // =====================================================

	    int bottomMargin = 20;

	    if (clientConfig != null
	            && clientConfig.getReportBottomSpace() != null) {

	        bottomMargin =
	                clientConfig.getReportBottomSpace();
	    }

	    // =====================================================
	    // RESERVE FOOTER SPACE
	    //
	    // QR only       -> 90
	    // MD only       -> 100
	    // QR + MD       -> 125
	    // Neither       -> configured bottom margin
	    // =====================================================

	    if (isQrRequired && mdSignRequired) {

	        bottomMargin =
	                Math.max(bottomMargin, 125);

	    } else if (isQrRequired) {

	        bottomMargin =
	                Math.max(bottomMargin, 90);

	    } else if (mdSignRequired) {

	        bottomMargin =
	                Math.max(bottomMargin, 100);
	    }

	    // =====================================================
	    // A4 DOCUMENT
	    // =====================================================

	    Document document =
	            new Document(
	                    PageSize.A4,
	                    30,
	                    30,
	                    78 + topMargin,
	                    bottomMargin
	            );

	    PdfWriter writer =
	            PdfWriter.getInstance(
	                    document,
	                    outputStream
	            );

	    // =====================================================
	    // FONTS
	    // =====================================================

	    Font normalFont =
	            new Font(
	                    Font.HELVETICA,
	                    10,
	                    Font.NORMAL
	            );

	    Font boldFont =
	            new Font(
	                    Font.HELVETICA,
	                    10,
	                    Font.BOLD
	            );

	    Font normalFont1 =
	            new Font(
	                    Font.HELVETICA,
	                    9,
	                    Font.NORMAL
	            );

	    Font boldFont1 =
	            new Font(
	                    Font.HELVETICA,
	                    9,
	                    Font.BOLD
	            );

	    // =====================================================
	    // GET REPORT
	    // =====================================================

	    ReportMaster reportDetails =
	            reportMasterRepo.findByPatientIdAndLabId(
	                    patientId,
	                    patientDetails.getLabId()
	            );

	    if (reportDetails == null) {

	        throw new RuntimeException(
	                "Report not found for patient : "
	                        + patientId
	        );
	    }

	    // =====================================================
	    // PAGE HEADER / FOOTER
	    // =====================================================

	    ReportPageHeader pageHeader =
	            new ReportPageHeader(
	                    patientId,
	                    patientDetails,
	                    reportDetails,
	                    boldFont,
	                    topMargin,

	                    // QR
	                    qrCode,
	                    qrCodePositionHorizantal,
	                    qrCodePositionVertical,
	                    qrSize,
	                    isQrRequired,

	                    // MD SIGN
	                    mdSign,
	                    mdDoctorDetails,
	                    mdSignPositionHorizantal,
	                    mdSignRequired
	            );

	    writer.setPageEvent(pageHeader);

	    // =====================================================
	    // OPEN DOCUMENT
	    // =====================================================

	    document.open();

	    // =====================================================
	    // REPORT IDS
	    // =====================================================

	    List<String> reportIdList =
	            Arrays.asList(
	                    reportIds.split("\\|")
	            );

	    // =====================================================
	    // COMPLETED TEST DATA
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
	                        id.substring(
	                                id.length() - 17
	                        );

	                reports.put(
	                        shortId,
	                        entry.getValue()
	                );
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

	        // =================================================
	        // SORT BY SEQUENCE
	        // =================================================

	        testDetails.sort(
	                Comparator.comparing(
	                        ParameterDetails::getSequence,
	                        Comparator.nullsLast(
	                                Integer::compareTo
	                        )
	                )
	        );

	        // =================================================
	        // GET GROUP NAME
	        // =================================================

	        String groupName = "";

	        for (ParameterDetails parameter
	                : testDetails) {

	            if (parameter.getSequence() != null
	                    && parameter.getSequence() == 1) {

	                groupName =
	                        safe(
	                                parameter.getParameterName()
	                        );

	                break;
	            }
	        }

	        if (groupName == null
	                || groupName.trim().isEmpty()) {

	            groupName = "OTHER";
	        }

	        // =================================================
	        // ADD TEST TO GROUP
	        // =================================================

	        groupedTests
	                .computeIfAbsent(
	                        groupName,
	                        k -> new ArrayList<>()
	                )
	                .add(testDetails);
	    }

	    // =====================================================
	    // PRINT GROUPS
	    // =====================================================

	    int groupIndex = 0;

	    for (Map.Entry<String, List<List<ParameterDetails>>> groupEntry
	            : groupedTests.entrySet()) {

	        String groupName =
	                groupEntry.getKey();

	        List<List<ParameterDetails>> testsInGroup =
	                groupEntry.getValue();

	        // =================================================
	        // GROUP STATE
	        // =================================================

	        boolean groupTitlePrinted = false;

	        // Track page number where previous test was printed
	        int previousTestPage = -1;

	        // =================================================
	        // PRINT TESTS
	        // =================================================

	        for (int testIndex = 0;
	                testIndex < testsInGroup.size();
	                testIndex++) {

	            List<ParameterDetails> testDetails =
	                    testsInGroup.get(testIndex);

	            // =================================================
	            // REMOVE GROUP NAME PARAMETER
	            // =================================================

	            List<ParameterDetails> parametersForTest =
	                    new ArrayList<>();

	            for (ParameterDetails parameter
	                    : testDetails) {

	                if (parameter.getSequence() != null
	                        && parameter.getSequence() == 1) {

	                    continue;
	                }

	                parametersForTest.add(parameter);
	            }

	            // =================================================
	            // CREATE TEST TABLE
	            // =================================================

	            PdfPTable testTable =
	                    createTestTable(
	                            parametersForTest,
	                            normalFont,
	                            boldFont,
	                            normalFont1,
	                            boldFont1
	                    );

	            // =================================================
	            // CALCULATE REQUIRED HEIGHT
	            // =================================================

	            float requiredTestHeight =
	                    calculateTestHeight(
	                            testDetails
	                    );

	            requiredTestHeight += 4;

	            // =================================================
	            // AVAILABLE PAGE HEIGHT
	            // =================================================

	            float currentY =
	                    writer.getVerticalPosition(true);

	            float availableHeight =
	                    currentY
	                            - document.bottomMargin();

	            // =================================================
	            // FIRST TEST OF GROUP
	            // =================================================

	            if (!groupTitlePrinted) {

	                /*
	                 * Approximate group title height.
	                 */
	                float groupTitleHeight = 18f;

	                float requiredHeight =
	                        groupTitleHeight
	                                + requiredTestHeight;

	                // =================================================
	                // GROUP TITLE + FIRST TEST DON'T FIT
	                // =================================================

	                if (requiredHeight > availableHeight) {

	                    document.newPage();

	                    addGroupTitle(
	                            document,
	                            groupName,
	                            boldFont
	                    );

	                } else {

	                    addGroupTitle(
	                            document,
	                            groupName,
	                            boldFont
	                    );
	                }

	                groupTitlePrinted = true;
	            }

	            // =================================================
	            // NEXT TEST
	            // =================================================

	            else {

	                if (requiredTestHeight > availableHeight) {

	                    /*
	                     * Test doesn't fit.
	                     * Move to next page.
	                     */
	                    document.newPage();

	                    /*
	                     * Same group continues on next page,
	                     * so print group title again.
	                     */
	                    addGroupTitle(
	                            document,
	                            groupName,
	                            boldFont
	                    );
	                }
	            }

	            // =================================================
	            // CURRENT PAGE NUMBER
	            // =================================================

	            int currentPage =
	                    writer.getPageNumber();

	            // =================================================
	            // SEPARATOR BETWEEN TESTS
	            // =================================================
	            //
	            // Add separator ONLY when:
	            //
	            // 1. This is not the first test
	            // 2. Previous test was on SAME page
	            //
	            // =================================================

	            boolean addSeparator =
	                    testIndex > 0
	                            && previousTestPage == currentPage;

	            if (addSeparator) {

	                // =================================================
	                // SPACE AFTER PREVIOUS TEST
	                // =================================================

	                Paragraph spaceBeforeLine =
	                        new Paragraph(" ");

	                spaceBeforeLine.setLeading(3);

	                document.add(
	                        spaceBeforeLine
	                );

	                // =================================================
	                // SEPARATOR LINE
	                // =================================================

	                PdfPTable separatorTable =
	                        new PdfPTable(1);

	                separatorTable.setWidthPercentage(100);

	                PdfPCell separatorCell =
	                        new PdfPCell();

	                separatorCell.setBorder(
	                        PdfPCell.TOP
	                );

	                separatorCell.setBorderWidthTop(
	                        0.5f
	                );

	                separatorCell.setPaddingTop(0);

	                separatorCell.setPaddingBottom(0);

	                separatorTable.addCell(
	                        separatorCell
	                );

	                document.add(
	                        separatorTable
	                );

	                // =================================================
	                // SPACE AFTER LINE
	                // =================================================

	                Paragraph spaceAfterLine =
	                        new Paragraph(" ");

	                spaceAfterLine.setLeading(3);

	                document.add(
	                        spaceAfterLine
	                );
	            }

	            // =================================================
	            // TEST WRAPPER
	            // =================================================

	            PdfPTable testWrapper =
	                    new PdfPTable(1);

	            testWrapper.setWidthPercentage(100);

	            testWrapper.setKeepTogether(
	                    true
	            );

	            // =================================================
	            // TEST CELL
	            // =================================================

	            PdfPCell testCell =
	                    new PdfPCell(
	                            testTable
	                    );

	            /*
	             * No outer border around test.
	             */
	            testCell.setBorder(
	                    PdfPCell.NO_BORDER
	            );

	            testCell.setPadding(0);

	            testWrapper.addCell(
	                    testCell
	            );

	            // =================================================
	            // ADD TEST
	            // =================================================

	            document.add(
	                    testWrapper
	            );

	            // =================================================
	            // SAVE PAGE OF THIS TEST
	            // =================================================

	            previousTestPage =
	                    writer.getPageNumber();
	        }

	        // =====================================================
	        // SPACE BETWEEN GROUPS
	        // =====================================================

	        groupIndex++;

	        if (groupIndex < groupedTests.size()) {

	            Paragraph groupSpace =
	                    new Paragraph(" ");

	            groupSpace.setLeading(2);

	            document.add(
	                    groupSpace
	            );
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
	                    + safe(
	                            patientDetails.getFirstName()
	                    )
	                    + " "
	                    + safe(
	                            patientDetails.getMiddleName()
	                    )
	                    + " "
	                    + safe(
	                            patientDetails.getLastName()
	                    )
	                    + ".pdf";

	    // =====================================================
	    // RESPONSE
	    // =====================================================

	    return new PdfResponse(
	            outputStream.toByteArray(),
	            fileName
	    );
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
				// END OF REPORT / FOOTER
				// =================================================

				drawEndOfReport(canvas, document);

				canvas.restoreState();

			} catch (Exception e) {

				throw new RuntimeException("Error while creating PDF page header/footer", e);
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

			Font footerFont = new Font(Font.HELVETICA, 10, Font.BOLD);

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

				Font qualificationFont = new Font(Font.HELVETICA, 9, Font.BOLD);

				float qualificationY = doctorNameY - 10;

				ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(qualification, qualificationFont),
						mdCenterX, qualificationY - 60, 0);

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

				qrImage.scaleAbsolute(qrSize, qrSize);

				float qrY = contentY - qrSize;

				qrImage.setAbsolutePosition(qrX, qrY - 60);

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