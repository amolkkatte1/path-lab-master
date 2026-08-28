package pathlabmaster.service;

import pathlabmaster.pojo.ParameterGroupMaster;
import pathlabmaster.utility.Response;

public interface IParameterGroupService {

	Response createParameterGroup(ParameterGroupMaster parameterGroupDetails);

	Response updateParameterGroup(ParameterGroupMaster parameterGroupDetails);

	Response getParameterGroup(ParameterGroupMaster parameterGroupDetails);

	Response getParameterGroupList();

	Response deleteParameterGroup(ParameterGroupMaster parameterGroupDetails);

}
