package pathlabmaster.service;

import java.util.List;

import pathlabmaster.pojo.TestMaster;
import pathlabmaster.utility.Response;

public interface ITestService {

	Response createTest(TestMaster testDetails);

	Response getTest(TestMaster testDetails);

	Response getTestList(Long labId);

	Response deleteTest(TestMaster testDetails);

	Response updateTest(TestMaster testDetails);

	Response getTestList();

	Response saveTestCharges(List<TestMaster> testDetails);

	Response addTestCharges(Long labId);

	

}
