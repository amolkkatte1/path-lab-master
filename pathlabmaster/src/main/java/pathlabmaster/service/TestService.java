package pathlabmaster.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pathlabmaster.dao.TestChargesMasterRepository;
import pathlabmaster.dao.TestMasterRepository;
import pathlabmaster.pojo.TestChargesMaster;
import pathlabmaster.pojo.TestMaster;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.ResponseStatus;
import pathlabmaster.utility.Utility;

@Service
public class TestService implements ITestService {
	@Autowired
	private TestMasterRepository testRepo;
	@Autowired
	private TestChargesMasterRepository testChargesRepo;

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
		List<TestMaster> testList = testRepo.findAll();
		return new Response(ResponseStatus.success, 1, "Get Test List successfully", testList);
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
		Optional<TestMaster> optionalTest = testRepo.findById(testDetails.getTestId());
		TestMaster test = null;
		if (optionalTest.isPresent()) {
			test = optionalTest.get();
		} else {
			throw new RuntimeException("	Test not found");
		}
		return new Response(ResponseStatus.success, 1, "Get Test successfully", test);

	}

	@Override
	public Response getTestList(Long labId) {
		List<TestMaster> testList = testRepo.findByCreatedBy(20251202131602460L);
		List<TestMaster> testListNew = new ArrayList<>();
		List<TestChargesMaster> chargesList = testChargesRepo.findByLabId(labId);
		if(chargesList!=null && !chargesList.isEmpty()) {
			Map<Long, Integer> chargesMap = new HashMap<>();
			for(TestChargesMaster test:chargesList) {
				chargesMap.put(test.getTestChargesId(), test.getTestCharges());
			}
			for(TestMaster test:testList) {
				test.setTestCharges(chargesMap.containsKey(test.getTestId()) ?chargesMap.get(test.getTestId()):test.getTestCharges());
				test.setLabId(labId);
				testListNew.add(test);
			}
		}
		if (testListNew == null || testListNew.isEmpty()) {
			testListNew=testList;
		}
		
		return new Response(ResponseStatus.success, 1, "Get Test List successfully", testListNew);
	}

	@Override
	public Response saveTestCharges(List<TestMaster> testDetails) {
		List<TestChargesMaster> chargesList = new ArrayList<>();
		for(TestMaster test: testDetails) {
			chargesList.add(new TestChargesMaster(test.getTestId(),test.getLabId(),test.getTestCharges()));
		}
		List<TestChargesMaster> savedData = testChargesRepo.saveAll(chargesList);
		return new Response(ResponseStatus.success, 1, "Test Charges Saved", savedData);
	}

	@Override
	public Response addTestCharges(Long labId) {
		List<TestChargesMaster> chargesList = testChargesRepo.findByLabId(labId);
		List<TestMaster> testList = testRepo.findByCreatedBy(20251202131602460L);
		List<TestChargesMaster> savedData = new ArrayList<>();
		List<TestChargesMaster> chargesListNew = new ArrayList<>();
		if(chargesList!=null && !chargesList.isEmpty()) {
			Map<Long, Integer> chargesMap = new HashMap<>();
			for(TestChargesMaster test:chargesList) {
				chargesMap.put(test.getTestChargesId(), test.getTestCharges());
			}
			for(TestMaster test:testList) {
				if(!chargesMap.containsKey(test.getTestId())) {
					chargesListNew.add(new TestChargesMaster(test.getTestId(),labId,test.getTestCharges()));
				}
			}
			savedData = testChargesRepo.saveAll(chargesListNew);
		}else {
			for(TestMaster test:testList) {
				chargesListNew.add(new TestChargesMaster(test.getTestId(),labId,test.getTestCharges()));
			}
			savedData = testChargesRepo.saveAll(chargesListNew);
		}
		return new Response(ResponseStatus.success, 1, "Test Charges Added", savedData);
	}

	
}
