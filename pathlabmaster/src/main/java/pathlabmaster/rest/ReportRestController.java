package pathlabmaster.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pathlabmaster.pojo.ReportMaster;
import pathlabmaster.pojo.ReportRegistrationRequest;
import pathlabmaster.service.IReportService;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.Utility;

@RestController
@RequestMapping("/report")
//@CrossOrigin(origins = "http://localhost:5174")
public class ReportRestController {
	@Autowired
	IReportService reportService;
	ObjectMapper mapper = new ObjectMapper();
	
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
	
	
}
