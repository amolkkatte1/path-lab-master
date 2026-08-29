package pathlabmaster.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pathlabmaster.pojo.DoctorMaster;
import pathlabmaster.pojo.PatientMaster;
import pathlabmaster.service.IDoctorService;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.Utility;

@RestController
@RequestMapping("/doctor")
//@CrossOrigin(origins = "http://localhost:5174")
public class DoctorRestController {
	@Autowired
	IDoctorService doctorService;
	ObjectMapper mapper = new ObjectMapper();
	
	@GetMapping("/")
	public String sayHello() {
		return "Doctor Service Working Amol!";
	}
	
	@PostMapping("/create")
	public Response createDoctor(@RequestBody DoctorMaster doctorDetails) throws JsonProcessingException {
		System.out.println("Create Doctor Api Started : "+Utility.toJsonString(doctorDetails));
		Response response =doctorService.createDoctor(doctorDetails);
		System.out.println("Create Doctor Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/update")
	public Response updateDoctor(@RequestBody DoctorMaster doctorDetails) throws JsonProcessingException {
		System.out.println("Update Doctor Api Started : "+Utility.toJsonString(doctorDetails));
		Response response =doctorService.updateDoctor(doctorDetails);
		System.out.println("Update Doctor Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/get")
	public Response getDoctor(@RequestBody DoctorMaster doctorDetails) throws JsonProcessingException {
		System.out.println("Get Doctor Api Started : "+Utility.toJsonString(doctorDetails));
		Response response =doctorService.getDoctor(doctorDetails);
		System.out.println("Get Doctor Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@GetMapping("/list")
	public Response getDoctorList() throws JsonProcessingException{
		System.out.println("Get Doctor List Api Started ");
		Response response =doctorService.getDoctorList();
		System.out.println("Get Doctor List Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/delete")
	public Response deleteDoctor(@RequestBody DoctorMaster doctorDetails) throws JsonProcessingException {
		System.out.println("Delete Doctor Api Started : "+Utility.toJsonString(doctorDetails));
		Response response =doctorService.deleteDoctor(doctorDetails);
		System.out.println("Delete Doctor Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@GetMapping("/list/labId/{labId}")
	public Response getDoctorByLabId(@PathVariable Long labId) throws JsonProcessingException {
		System.out.println("Get getDoctorByLabId Api Started : " + labId);
		Response response = doctorService.getDoctorByLabId(labId);
		System.out.println("Get getDoctorByLabId Api Completed : " + Utility.toJsonString(response));
		return response;
	}
}
