package pathlabmaster.rest;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pathlabmaster.dao.PatientFilterRequest;
import pathlabmaster.pojo.PdfResponse;
import pathlabmaster.pojo.ReportMaster;
import pathlabmaster.pojo.ReportRegistrationRequest;
import pathlabmaster.service.ExcelReportService;
import pathlabmaster.service.IReportService;
import pathlabmaster.service.PdfReportService;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.Utility;

@RestController
@RequestMapping("/report")
//@CrossOrigin(origins = "http://localhost:5174")
public class ReportRestController {
	@Autowired
	IReportService reportService;
	ObjectMapper mapper = new ObjectMapper();
	@Autowired
	ExcelReportService excelReportService;

	@Autowired
	PdfReportService pdfReportService;

	@GetMapping("/")
	public String sayHello() {
		return "Report Service Working Amol!";
	}
	
	@PostMapping("/register")
	public Response createReport(@RequestBody ReportRegistrationRequest reportRegistrationRequest) throws JsonProcessingException {
		System.out.println("Register Report Api Started : "+Utility.toJsonString(reportRegistrationRequest));
		Response response =reportService.registerReport(reportRegistrationRequest);
		System.out.println("Register Report Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/save")
	public Response saveReportDetails(@RequestBody ReportMaster reportMaster) throws JsonProcessingException {
		System.out.println("Save Report Api Started : "+Utility.toJsonString(reportMaster));
		Response response =reportService.saveReportDetails(reportMaster);
		System.out.println("Save Report Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/add")
	public Response addReport(@RequestBody ReportRegistrationRequest reportRegistrationRequest) throws JsonProcessingException {
		System.out.println("addReport Report Api Started : "+Utility.toJsonString(reportRegistrationRequest));
		Response response =reportService.addReport(reportRegistrationRequest);
		System.out.println("addReport Report Api Completed : "+Utility.toJsonString(response));
	    return response;
	}

	@GetMapping("/pending-reports/patientId/{patientId}/labId/{labId}")
	public Response getPendingReportsByPatientIdAndLabId(@PathVariable Long patientId, @PathVariable Long labId)throws JsonProcessingException {
		System.out.println("Get Pending Reports API Started : patientId = " + patientId + ", labId = " + labId);
		Response response = reportService.getPendingReportsByPatientIdAndLabId(patientId, labId);
		System.out.println("Get Pending Reports API Completed : " + Utility.toJsonString(response));
		return response;
	}
	
	@GetMapping("/pending-patient/labId/{labId}")
	public Response getPendingReportsByLabId(@PathVariable Long labId)throws JsonProcessingException {
		System.out.println("Get Pending Reports by LabId API Started : "+ labId );
		Response response = reportService.getPendingReportsByLabId(labId);
		System.out.println("Get Pending Reports Reports by LabId API Completed : " + Utility.toJsonString(response));
		return response;
	}
	
	@GetMapping("/list/labId/{labId}")
	public Response getReportsListByLabId(@PathVariable Long labId)throws JsonProcessingException {
		System.out.println("Get getReportsListByLabId  API Started : "+ labId );
		Response response = reportService.getReportsListByLabId(labId);
		System.out.println("Get getReportsListByLabId  API Completed : " + Utility.toJsonString(response));
		return response;
	}
	
	@PostMapping("/list/filter")
	public Response getReportsByFilter(@RequestBody PatientFilterRequest patientFilterRequest) throws JsonProcessingException {
		System.out.println("getReportsByFilter Api Started : "+Utility.toJsonString(patientFilterRequest));
		Response response =reportService.getReportsByFilter(patientFilterRequest);
		System.out.println("getReportsByFilter Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@GetMapping("/generate")
	public ResponseEntity<byte[]> generateExcel(@RequestParam(required = false) String fromDate,
			@RequestParam(required = false) String toDate, @RequestParam(required = false) Long labId,
			@RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName,
			@RequestParam(required = false) Long patientId, @RequestParam(required = false) String doctorName,
			@RequestParam(required = false) Long doctorId) throws IOException {

		byte[] excel = excelReportService.generateExcel(fromDate, toDate, labId, firstName, lastName, patientId,
				doctorName, doctorId);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=patient-report.xlsx")
				.contentType(
						MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.body(excel);
	}
	
	@GetMapping("/generate/pdf")
	public ResponseEntity<byte[]> generatePdf(@RequestParam(required = false) String fromDate,
			@RequestParam(required = false) String toDate, @RequestParam(required = false) Long labId,
			@RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName,
			@RequestParam(required = false) Long patientId, @RequestParam(required = false) String doctorName,
			@RequestParam(required = false) Long doctorId) throws IOException {
		byte[] pdf = pdfReportService.generatePdf(fromDate, toDate, labId, firstName, lastName, patientId, doctorName,
				doctorId);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=patient-report.pdf")
				.contentType(MediaType.APPLICATION_PDF).body(pdf);
	}
	
	@GetMapping("/generate/patientId/{patientId}/reportIds/{reportIds}")
	public ResponseEntity<byte[]> getPendingReportsByPatientIdAndLabId(@PathVariable Long patientId,
			@PathVariable String reportIds) throws Exception {
		System.out.println(
				"Generate Patient Reports API Started : patientId = " + patientId + ", reportIds = " + reportIds);
		PdfResponse pdfResponse = pdfReportService.createPdf(patientId, reportIds);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pdfResponse.getFileName() + "\"")
				.contentType(MediaType.APPLICATION_PDF).body(pdfResponse.getPdf());
	}
}
