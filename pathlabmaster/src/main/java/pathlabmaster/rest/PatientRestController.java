package pathlabmaster.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pathlabmaster.pojo.PatientMaster;
import pathlabmaster.service.IPatientService;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.Utility;

@RestController
@RequestMapping("/patient")
//@CrossOrigin(origins = "http://localhost:5174")
public class PatientRestController {
	@Autowired
	IPatientService patientService;
	ObjectMapper mapper = new ObjectMapper();
	
	@GetMapping("/")
	public String sayHello() {
		return "Patient Service Working Amol!";
	}
	
	@PostMapping("/create")
	public Response createPatient(@RequestBody PatientMaster patientDetails) throws JsonProcessingException {
		System.out.println("Create Patient Api Started : "+Utility.toJsonString(patientDetails));
		Response response =patientService.createPatient(patientDetails);
		System.out.println("Create Patient Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/update")
	public Response updatePatient(@RequestBody PatientMaster patientDetails) throws JsonProcessingException {
		System.out.println("Update Patient Api Started : "+Utility.toJsonString(patientDetails));
		Response response =patientService.updatePatient(patientDetails);
		System.out.println("Update Patient Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/get")
	public Response getPatient(@RequestBody PatientMaster patientDetails) throws JsonProcessingException {
		System.out.println("Get Patient Api Started : "+Utility.toJsonString(patientDetails));
		Response response =patientService.getPatient(patientDetails);
		System.out.println("Get Patient Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@GetMapping("/list")
	public Response getPatientList() throws JsonProcessingException{
		System.out.println("Get Patient List Api Started ");
		Response response =patientService.getPatientList();
		System.out.println("Get Patient List Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/delete")
	public Response deletePatient(@RequestBody PatientMaster patientDetails) throws JsonProcessingException {
		System.out.println("Delete Patient Api Started : "+Utility.toJsonString(patientDetails));
		Response response =patientService.deletePatient(patientDetails);
		System.out.println("Delete Patient Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	
	
}
