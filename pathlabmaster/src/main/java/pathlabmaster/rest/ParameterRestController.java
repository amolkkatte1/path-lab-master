package pathlabmaster.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pathlabmaster.pojo.ParameterMaster;
import pathlabmaster.service.IParameterService;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.Utility;

@RestController
@RequestMapping("/parameter")
//@CrossOrigin(origins = "http://localhost:5174")
public class ParameterRestController {
	@Autowired
	IParameterService parameterService;
	ObjectMapper mapper = new ObjectMapper();
	
	@GetMapping("/")
	public String sayHello() {
		return "Parameter Service Working Amol!";
	}
	
	@PostMapping("/create")
	public Response createParameter(@RequestBody ParameterMaster parameterDetails) throws JsonProcessingException {
		System.out.println("Create Parameter Api Started : "+Utility.toJsonString(parameterDetails));
		Response response =parameterService.createParameter(parameterDetails);
		System.out.println("Create Parameter Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/update")
	public Response updateParameter(@RequestBody ParameterMaster parameterDetails) throws JsonProcessingException {
		System.out.println("Update Parameter Api Started : "+Utility.toJsonString(parameterDetails));
		Response response =parameterService.updateParameter(parameterDetails);
		System.out.println("Update Parameter Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/get")
	public Response getParameter(@RequestBody ParameterMaster parameterDetails) throws JsonProcessingException {
		System.out.println("Get Parameter Api Started : "+Utility.toJsonString(parameterDetails));
		Response response =parameterService.getParameter(parameterDetails);
		System.out.println("Get Parameter Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@GetMapping("/list")
	public Response getParameterList() throws JsonProcessingException{
		System.out.println("Get Parameter List Api Started ");
		Response response =parameterService.getParameterList();
		System.out.println("Get Parameter List Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/delete")
	public Response deleteParameter(@RequestBody ParameterMaster parameterDetails) throws JsonProcessingException {
		System.out.println("Delete Parameter Api Started : "+Utility.toJsonString(parameterDetails));
		Response response =parameterService.deleteParameter(parameterDetails);
		System.out.println("Delete Parameter Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	@GetMapping("/list/testId/{testId}")
	public Response getParameterListByTestId(@PathVariable Long testId) throws JsonProcessingException {
		System.out.println("Get getParameterListByTestId Api Started : " + testId);
		Response response = parameterService.getParameterListByTestId(testId);
		System.out.println("Get getParameterListByTestId Api Completed : " + Utility.toJsonString(response));
		return response;
	}
	@PostMapping("/create/list")
	public Response createParameterList(@RequestBody List<ParameterMaster> parameterDetailList) throws JsonProcessingException {
		System.out.println("Create Parameter Api Started : "+Utility.toJsonString(parameterDetailList));
		Response response =parameterService.createParameterList(parameterDetailList);
		System.out.println("Create Parameter Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
}
