package pathlabmaster.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.core.JsonProcessingException;

import pathlabmaster.dao.TestMasterRepository;
import pathlabmaster.pojo.TestMaster;
import pathlabmaster.pojo.UserMaster;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.ResponseStatus;
import pathlabmaster.utility.Utility;
@Service
public class TestService implements ITestService{
@Autowired
private TestMasterRepository testRepo;
	@Override
	public Response createTest(TestMaster testDetails) {
		testDetails.setTestId(Utility.generateId());
		testDetails.setUpdatedAt(Utility.getCurrentTime());
		testDetails.setCreatedAt(Utility.getCurrentTime());
		TestMaster savedTest = testRepo.save(testDetails);
		System.out.println(savedTest.getTestId());
		return new Response(ResponseStatus.success, 1, "Test created successfully", savedTest);
	}
	@Override
	public Response updateTest(TestMaster testDetails) {
		testDetails.setUpdatedAt(Utility.getCurrentTime());
		TestMaster savedTest = testRepo.save(testDetails);
		System.out.println(savedTest.getTestId()); 
		return new Response(ResponseStatus.success, 1, "Test Update successfully", savedTest);
		
	}
	@Override
	public Response getTestList() {
//		TestMaster testDetails =  new ;
//		Optional<TestMaster> optionalTest = testRepo.findById(testDetails.getTestId());
//		TestMaster test= null;
//		if (optionalTest.isPresent()) {
//		    test = optionalTest.get();
//		} else {
//		    throw new RuntimeException("	Test not found");
//		}
		return new Response(ResponseStatus.success, 1, "Get Test successfully", null);
	
	}
	@Override
	public Response deleteTest(TestMaster testDetails) {
		Optional<TestMaster> optionalTest = testRepo.findById(testDetails.getTestId());
		TestMaster test = null;
		if (optionalTest.isPresent()) {
		    test = optionalTest.get();
		} else {
		    throw new RuntimeException("Test not found");
		}
		testRepo.deleteById(testDetails.getTestId());
		return new Response(ResponseStatus.success, 1, "Delete Test successfully", test);

	}
	@Override
	public Response getTest(TestMaster testDetails) {
		List<TestMaster> testList = testRepo.findAll();
		return new Response(ResponseStatus.success, 1, "Get Test List successfully", testList);
		}
	}


