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

import pathlabmaster.pojo.BillMaster;
import pathlabmaster.service.IBillMasterService;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.Utility;

@RestController
@RequestMapping("/billing")
//@CrossOrigin(origins = "http://localhost:5174")
public class BillMasterRestController {
	@Autowired
	IBillMasterService billMasterService;
	ObjectMapper mapper = new ObjectMapper();
	
	@GetMapping("/")
	public String sayHello() {
		return "Bill Service Working Amol!";
	}
	
	@PostMapping("/create")
	public Response createBillMaster(@RequestBody BillMaster BillMasterDetails) throws JsonProcessingException {
		System.out.println("Create BillMaster Api Started : "+Utility.toJsonString(BillMasterDetails));
		Response response =billMasterService.createBillMaster(BillMasterDetails);
		System.out.println("Create BillMaster Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/update")
	public Response updateBillMaster(@RequestBody BillMaster BillMasterDetails) throws JsonProcessingException {
		System.out.println("Update BillMaster Api Started : "+Utility.toJsonString(BillMasterDetails));
		Response response =billMasterService.updateBillMaster(BillMasterDetails);
		System.out.println("Update BillMaster Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@GetMapping("/get/labId/{labId}")
	public Response getDoctorByLabId(@PathVariable Long labId) throws JsonProcessingException {
		System.out.println("Get getDoctorByLabId Api Started : " + labId);
		Response response = billMasterService.getBillMaster(labId);
		System.out.println("Get getDoctorByLabId Api Completed : " + Utility.toJsonString(response));
		return response;
	}
	
	@GetMapping("/list")
	public Response getBillMasterList() throws JsonProcessingException{
		System.out.println("Get BillMaster List Api Started ");
		Response response =billMasterService.getBillMasterList();
		System.out.println("Get BillMaster List Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/delete")
	public Response deleteBillMaster(@RequestBody BillMaster BillMasterDetails) throws JsonProcessingException {
		System.out.println("Delete BillMaster Api Started : "+Utility.toJsonString(BillMasterDetails));
		Response response =billMasterService.deleteBillMaster(BillMasterDetails);
		System.out.println("Delete BillMaster Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
}
