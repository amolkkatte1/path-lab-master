package pathlabmaster.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import pathlabmaster.pojo.TestMaster;
import pathlabmaster.pojo.UserMaster;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.Utility;
import pathlabmaster.service.ITestService;
import pathlabmaster.service.TestService;

@RestController
@RequestMapping("/test")
public class TestRestController {
	@Autowired
	ITestService testService;

	@GetMapping("/")
	public String SayHello() {
		return "test service working";

	}

	@PostMapping("/create")
	public Response createTest(@RequestBody TestMaster testDetails) throws JsonProcessingException {
		System.out.println("Create Test Api Started : " + Utility.toJsonString(testDetails));
		Response response = testService.createTest(testDetails);
		System.out.println("Create Test Api Completed : " + Utility.toJsonString(response));
		return response;

	}
	@PostMapping("/update")
	public Response updateTest(@RequestBody TestMaster testDetails) throws JsonProcessingException {
		System.out.println("Update Test Api Started : "+Utility.toJsonString(testDetails));
		Response response =testService.updateTest(testDetails);
		System.out.println("Update Test Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/get")
	public Response getTest(@RequestBody TestMaster testDetails) throws JsonProcessingException {
		System.out.println("Get Test Api Started : "+Utility.toJsonString(testDetails));
		Response response =testService.getTest(testDetails);
		System.out.println("Get Test Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@GetMapping("/list")
	public Response getTestList() throws JsonProcessingException{
		System.out.println("Get Test List Api Started ");
		Response response =testService.getTestList();
		System.out.println("Get Test List Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/delete")
	public Response deleteTest(@RequestBody TestMaster testDetails) throws JsonProcessingException {
		System.out.println("Delete Test Api Started : "+Utility.toJsonString(testDetails));
		Response response =testService.deleteTest(testDetails);
		System.out.println("Delete Test Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	
	
}
