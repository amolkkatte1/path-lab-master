package pathlabmaster.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pathlabmaster.pojo.ParameterGroupMaster;
import pathlabmaster.service.IParameterGroupService;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.Utility;

@RestController
@RequestMapping("/parameterGroup")
public class ParameterGroupRestController {
	@Autowired
	IParameterGroupService parameterGroupService;
	ObjectMapper mapper = new ObjectMapper();
	
	@GetMapping("/")
	public String sayHello() {
		return "ParameterGroup Service Working Amol!";
	}
	
	@PostMapping("/create")
	public Response createParameterGroup(@RequestBody ParameterGroupMaster parameterGroupDetails) throws JsonProcessingException {
		System.out.println("Create ParameterGroup Api Started : "+Utility.toJsonString(parameterGroupDetails));
		Response response =parameterGroupService.createParameterGroup(parameterGroupDetails);
		System.out.println("Create ParameterGroup Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/update")
	public Response updateParameterGroup(@RequestBody ParameterGroupMaster parameterGroupDetails) throws JsonProcessingException {
		System.out.println("Update ParameterGroup Api Started : "+Utility.toJsonString(parameterGroupDetails));
		Response response =parameterGroupService.updateParameterGroup(parameterGroupDetails);
		System.out.println("Update ParameterGroup Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/get")
	public Response getParameterGroup(@RequestBody ParameterGroupMaster parameterGroupDetails) throws JsonProcessingException {
		System.out.println("Get ParameterGroup Api Started : "+Utility.toJsonString(parameterGroupDetails));
		Response response =parameterGroupService.getParameterGroup(parameterGroupDetails);
		System.out.println("Get ParameterGroup Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@GetMapping("/list")
	public Response getParameterGroupList() throws JsonProcessingException{
		System.out.println("Get ParameterGroup List Api Started ");
		Response response =parameterGroupService.getParameterGroupList();
		System.out.println("Get ParameterGroup List Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/delete")
	public Response deleteParameterGroup(@RequestBody ParameterGroupMaster parameterGroupDetails) throws JsonProcessingException {
		System.out.println("Delete ParameterGroup Api Started : "+Utility.toJsonString(parameterGroupDetails));
		Response response =parameterGroupService.deleteParameterGroup(parameterGroupDetails);
		System.out.println("Delete ParameterGroup Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
}
