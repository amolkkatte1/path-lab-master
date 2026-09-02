package pathlabmaster.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	
	
	
}
