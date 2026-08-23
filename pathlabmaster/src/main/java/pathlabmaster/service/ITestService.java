package pathlabmaster.service;

import pathlabmaster.pojo.TestMaster;
import pathlabmaster.utility.Response;

public interface ITestService {

	Response createTest(TestMaster testDetails);

	Response getTest(TestMaster testDetails);

	Response getTestList();

	Response deleteTest(TestMaster testDetails);

	Response updateTest(TestMaster testDetails);

	

}
