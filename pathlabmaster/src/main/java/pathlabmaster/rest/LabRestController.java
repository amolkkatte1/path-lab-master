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

import pathlabmaster.pojo.LabMaster;
import pathlabmaster.service.ILabService;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.Utility;

@RestController
@RequestMapping("/lab")
//@CrossOrigin(origins = "http://localhost:5174")
public class LabRestController {
	@Autowired
	ILabService labService;
	ObjectMapper mapper = new ObjectMapper();
	
	@GetMapping("/")
	public String sayHello() {
		return "Lab Service Working Amol!";
	}
	
	@PostMapping("/create")
	public Response createLab(@RequestBody LabMaster labDetails) throws JsonProcessingException {
		System.out.println("Create Lab Api Started : "+Utility.toJsonString(labDetails));
		Response response =labService.createLab(labDetails);
		System.out.println("Create Lab Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/update")
	public Response updateLab(@RequestBody LabMaster labDetails) throws JsonProcessingException {
		System.out.println("Update Lab Api Started : "+Utility.toJsonString(labDetails));
		Response response =labService.updateLab(labDetails);
		System.out.println("Update Lab Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/get")
	public Response getLab(@RequestBody LabMaster labDetails) throws JsonProcessingException {
		System.out.println("Get Lab Api Started : "+Utility.toJsonString(labDetails));
		Response response =labService.getLab(labDetails);
		System.out.println("Get Lab Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@GetMapping("/list")
	public Response getLabList() throws JsonProcessingException{
		System.out.println("Get Lab List Api Started ");
		Response response =labService.getLabList();
		System.out.println("Get Lab List Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/delete")
	public Response deleteLab(@RequestBody LabMaster labDetails) throws JsonProcessingException {
		System.out.println("Delete Lab Api Started : "+Utility.toJsonString(labDetails));
		Response response =labService.deleteLab(labDetails);
		System.out.println("Delete Lab Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
}
