package pathlabmaster.service;

import pathlabmaster.pojo.TestGroupMaster;
import pathlabmaster.utility.Response;

public interface ITestGroupService {

	Response createTestGroup(TestGroupMaster testGroupDetails);

	Response getTestGroupList();

	Response deleteTest(TestGroupMaster testGroupDetails);

}
