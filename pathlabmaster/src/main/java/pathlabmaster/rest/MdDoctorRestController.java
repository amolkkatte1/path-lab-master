package pathlabmaster.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pathlabmaster.pojo.MdDoctorMaster;
import pathlabmaster.service.IMdDoctorService;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.Utility;

@RestController
@RequestMapping("/doctor/md")
//@CrossOrigin(origins = "http://localhost:5174")
public class MdDoctorRestController {
	@Autowired
	IMdDoctorService mdDoctorService;
	ObjectMapper mapper = new ObjectMapper();
	
	@GetMapping("/")
	public String sayHello() {
		return "MdDoctor Service Working Amol!";
	}
	
	@PostMapping(value = "/create", consumes = "multipart/form-data")
	public Response createMdDoctor(@RequestPart("doctor") String doctorJson,@RequestPart(value = "signature", required = false) MultipartFile signature) throws Exception {
		MdDoctorMaster doctor = new ObjectMapper().readValue(doctorJson, MdDoctorMaster.class);
		if (signature != null && !signature.isEmpty()) {
			doctor.setSignImage(signature.getBytes());
		}
		System.out.println("Create MdDoctor Api Started : " + Utility.toJsonString(doctor));
		Response response = mdDoctorService.createMdDoctor(doctor);
		System.out.println("Create MdDoctor Api Completed : " + Utility.toJsonString(response));
		return response;
	}
	
	@PostMapping("/update")
	public Response updateMdDoctor(@RequestBody MdDoctorMaster MdDoctorDetails) throws JsonProcessingException {
		System.out.println("Update MdDoctor Api Started : "+Utility.toJsonString(MdDoctorDetails));
		Response response =mdDoctorService.updateMdDoctor(MdDoctorDetails);
		System.out.println("Update MdDoctor Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/get")
	public Response getMdDoctor(@RequestBody MdDoctorMaster MdDoctorDetails) throws JsonProcessingException {
		System.out.println("Get MdDoctor Api Started : "+Utility.toJsonString(MdDoctorDetails));
		Response response =mdDoctorService.getMdDoctor(MdDoctorDetails);
		System.out.println("Get MdDoctor Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@GetMapping("/list")
	public Response getMdDoctorList() throws JsonProcessingException{
		System.out.println("Get MdDoctor List Api Started ");
		Response response =mdDoctorService.getMdDoctorList();
		System.out.println("Get MdDoctor List Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/delete")
	public Response deleteMdDoctor(@RequestBody MdDoctorMaster MdDoctorDetails) throws JsonProcessingException {
		System.out.println("Delete MdDoctor Api Started : "+Utility.toJsonString(MdDoctorDetails));
		Response response =mdDoctorService.deleteMdDoctor(MdDoctorDetails);
		System.out.println("Delete MdDoctor Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@GetMapping("/list/labId/{labId}")
	public Response getMdDoctorByLabId(@PathVariable Long labId) throws JsonProcessingException {
		System.out.println("Get getMdDoctorByLabId Api Started : " + labId);
		Response response = mdDoctorService.getMdDoctorByLabId(labId);
		System.out.println("Get getMdDoctorByLabId Api Completed : " + Utility.toJsonString(response));
		return response;
	}
}
