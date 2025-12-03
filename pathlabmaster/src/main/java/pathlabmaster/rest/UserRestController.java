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

import pathlabmaster.pojo.UserMaster;
import pathlabmaster.service.IUserService;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.Utility;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5174")
public class UserRestController {
	@Autowired
	IUserService userService;
	ObjectMapper mapper = new ObjectMapper();
	
	@GetMapping("/")
	public String sayHello() {
		return "User Service Working !";
	}
	
	@PostMapping("/create")
	public Response createUser(@RequestBody UserMaster userDetails) throws JsonProcessingException {
		System.out.println("Create User Api Started : "+Utility.toJsonString(userDetails));
		Response response =userService.createUser(userDetails);
		System.out.println("Create User Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/update")
	public Response updateUser(@RequestBody UserMaster userDetails) throws JsonProcessingException {
		System.out.println("Update User Api Started : "+Utility.toJsonString(userDetails));
		Response response =userService.updateUser(userDetails);
		System.out.println("Update User Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/get")
	public Response getUser(@RequestBody UserMaster userDetails) throws JsonProcessingException {
		System.out.println("Get User Api Started : "+Utility.toJsonString(userDetails));
		Response response =userService.getUser(userDetails);
		System.out.println("Get User Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@GetMapping("/list")
	public Response getUserList() throws JsonProcessingException{
		System.out.println("Get User List Api Started ");
		Response response =userService.getUserList();
		System.out.println("Get User List Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/delete")
	public Response deleteUser(@RequestBody UserMaster userDetails) throws JsonProcessingException {
		System.out.println("Delete User Api Started : "+Utility.toJsonString(userDetails));
		Response response =userService.deleteUser(userDetails);
		System.out.println("Delete User Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
	@PostMapping("/login")
	public Response login(@RequestBody UserMaster userDetails) throws JsonProcessingException {
		System.out.println("User login Api Started : "+Utility.toJsonString(userDetails));
		Response response =userService.login(userDetails);
		System.out.println("User login Api Completed : "+Utility.toJsonString(response));
	    return response;
	}
	
}
