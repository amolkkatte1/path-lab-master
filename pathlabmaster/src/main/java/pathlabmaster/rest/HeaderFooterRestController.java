package pathlabmaster.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pathlabmaster.pojo.MdDoctorMaster;
import pathlabmaster.service.IHeaderFooterService;
import pathlabmaster.service.IMdDoctorService;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.Utility;

@RestController
@RequestMapping("/header-footer")
//@CrossOrigin(origins = "http://localhost:5174")
public class HeaderFooterRestController {
	@Autowired
	IHeaderFooterService headerFooterService;
	ObjectMapper mapper = new ObjectMapper();
	
	@GetMapping("/")
	public String sayHello() {
		return "Header footer Service Working Amol!";
	}
	
	@PostMapping(value = "/create", consumes = "multipart/form-data")
	public Response createMdDoctor( @RequestParam("labId") Long labId,@RequestPart(value = "header", required = false) MultipartFile header,@RequestPart(value = "footer", required = false) MultipartFile footer) throws Exception {
		System.out.println("Create createHeaderFooter Api Started : " + Utility.toJsonString(labId));
		Response response = headerFooterService.createHeaderFooter(labId,header,footer);
		System.out.println("Create createHeaderFooter Api Completed : " + Utility.toJsonString(response));
		return response;
	}
}
