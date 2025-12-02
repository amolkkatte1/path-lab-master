package pathlabmaster.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pathlabmaster.pojo.UserTypeMaster;
import pathlabmaster.service.IUserTypeService;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.Utility;

@RestController
@RequestMapping("/userType")
public class UserTypeRestController {
	@Autowired
	IUserTypeService userTypeService;
	ObjectMapper mapper = new ObjectMapper();

	@GetMapping("/")
	public String sayHello() {
		return "User Type Service Working !";
	}

	@PostMapping("/create")
	public Response createUser(@RequestBody UserTypeMaster userTypeDetails) throws JsonProcessingException {
		System.out.println("Create User Type Api Started : " + Utility.toJsonString(userTypeDetails));
		Response response = userTypeService.createUserType(userTypeDetails);
		System.out.println("Create User Type Api Completed : " + Utility.toJsonString(response));
		return response;
	}

	@PostMapping("/update")
	public Response updateUser(@RequestBody UserTypeMaster userTypeDetails) throws JsonProcessingException {
		System.out.println("Update User Type Api Started : " + Utility.toJsonString(userTypeDetails));
		Response response = userTypeService.updateUserType(userTypeDetails);
		System.out.println("Update User Type Api Completed : " + Utility.toJsonString(response));
		return response;
	}

	@PostMapping("/get")
	public Response getUser(@RequestBody UserTypeMaster userTypeDetails) throws JsonProcessingException {
		System.out.println("Get User Type Api Started : " + Utility.toJsonString(userTypeDetails));
		Response response = userTypeService.getUserType(userTypeDetails);
		System.out.println("Get User Type Api Completed : " + Utility.toJsonString(response));
		return response;
	}

	@GetMapping("/list")
	public Response getUserList() throws JsonProcessingException {
		System.out.println("Get User Type List Api Started ");
		Response response = userTypeService.getUserTypeList();
		System.out.println("Get User Type List Api Completed : " + Utility.toJsonString(response));
		return response;
	}

	@PostMapping("/delete")
	public Response deleteUser(@RequestBody UserTypeMaster userTypeDetails) throws JsonProcessingException {
		System.out.println("Delete User Type Api Started : " + Utility.toJsonString(userTypeDetails));
		Response response = userTypeService.deleteUserType(userTypeDetails);
		System.out.println("Delete User Type Api Completed : " + Utility.toJsonString(response));
		return response;
	}
}
