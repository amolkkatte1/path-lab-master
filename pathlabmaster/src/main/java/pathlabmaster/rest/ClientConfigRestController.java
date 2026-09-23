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

import pathlabmaster.pojo.ClientConfig;
import pathlabmaster.service.IClientConfigService;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.Utility;


@RestController
@RequestMapping("/config")
//@CrossOrigin(origins = "http://localhost:5174")
public class ClientConfigRestController {
	@Autowired
	IClientConfigService clientConfigService;
	ObjectMapper mapper = new ObjectMapper();
	
	@GetMapping("/")
	public String sayHello() {
		return "ClientConfig Service Working Amol!";
	}
	
	@PostMapping("/create")
	public Response createClientConfig(@RequestBody ClientConfig clientConfigDetails) throws JsonProcessingException {
		System.out.println("Create ClientConfig Api Started : "+Utility.toJsonString(clientConfigDetails));
		Response response =clientConfigService.createClientConfig(clientConfigDetails);
		System.out.println("Create ClientConfig Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/update")
	public Response updateClientConfig(@RequestBody ClientConfig clientConfigDetails) throws JsonProcessingException {
		System.out.println("Update ClientConfig Api Started : "+Utility.toJsonString(clientConfigDetails));
		Response response =clientConfigService.updateClientConfig(clientConfigDetails);
		System.out.println("Update ClientConfig Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@GetMapping("/get/labId/{labId}")
	public Response getDoctorByLabId(@PathVariable Long labId) throws JsonProcessingException {
		System.out.println("Get getDoctorByLabId Api Started : " + labId);
		Response response = clientConfigService.getClientConfig(labId);
		System.out.println("Get getDoctorByLabId Api Completed : " + Utility.toJsonString(response));
		return response;
	}
	
	@GetMapping("/list")
	public Response getClientConfigList() throws JsonProcessingException{
		System.out.println("Get ClientConfig List Api Started ");
		Response response =clientConfigService.getClientConfigList();
		System.out.println("Get ClientConfig List Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/delete")
	public Response deleteClientConfig(@RequestBody ClientConfig clientConfigDetails) throws JsonProcessingException {
		System.out.println("Delete ClientConfig Api Started : "+Utility.toJsonString(clientConfigDetails));
		Response response =clientConfigService.deleteClientConfig(clientConfigDetails);
		System.out.println("Delete ClientConfig Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
}
