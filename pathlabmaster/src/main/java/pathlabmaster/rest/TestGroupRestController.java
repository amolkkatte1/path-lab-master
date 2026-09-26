package pathlabmaster.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testGroup")
public class TestGroupRestController {
//	@Autowired
//	ITestGroupService testGroupService;
@GetMapping("/")
public String SayHello() {
	return "testGroup service working";

}

//@PostMapping("/create")
//public Response createTestGroup(@RequestBody TestGroupMaster testGroupDetails) throws JsonProcessingException {
//	System.out.println("Create TestGroup Api Started : " + Utility.toJsonString(testGroupDetails));
//	Response response = testGroupService.createTestGroup(testGroupDetails);
//	System.out.println("Create TestGroup Api Completed : " + Utility.toJsonString(response));
//	return response;
//
//}
//@PostMapping("/update")
//public Response updateTestGroup(@RequestBody TestGroupMaster testGroupDetails) throws JsonProcessingException {
//	System.out.println("Update TestGroup Api Started : "+Utility.toJsonString(testGroupDetails));
//	Response response =testGroupService.createTestGroup(testGroupDetails);
//	System.out.println("Update TestGroup Api Completed : "+Utility.toJsonString(response));
//    return response;
//}
//
//@PostMapping("/get")
//public Response getTestGroup(@RequestBody TestGroupMaster testGroupDetails) throws JsonProcessingException {
//	System.out.println("Get TestGroup Api Started : "+Utility.toJsonString(testGroupDetails));
//	Response response =testGroupService.createTestGroup(testGroupDetails);
//	System.out.println("Get TestGroup Api Completed : "+Utility.toJsonString(response));
//    return response;
//}
//
//@GetMapping("/list")
//public Response getTestList() throws JsonProcessingException{
//	System.out.println("Get TestGroup List Api Started ");
//	Response response =testGroupService.getTestGroupList();
//	System.out.println("Get TestGroup List Api Completed : "+Utility.toJsonString(response));
//    return response;
//}
//
//@PostMapping("/delete")
//public Response deleteTest(@RequestBody TestGroupMaster testGroupDetails) throws JsonProcessingException {
//	System.out.println("Delete TestGroup Api Started : "+Utility.toJsonString(testGroupDetails));
//	Response response =testGroupService.deleteTest(testGroupDetails);
//	System.out.println("Delete TestGroup Api Completed : "+Utility.toJsonString(response));
//    return response;
//}
}
