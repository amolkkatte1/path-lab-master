package pathlabmaster.service;

import pathlabmaster.pojo.ParameterMaster;
import pathlabmaster.utility.Response;

public interface IParameterService {

	Response createParameter(ParameterMaster parameterDetails);

	Response updateParameter(ParameterMaster parameterDetails);

	Response getParameter(ParameterMaster parameterDetails);

	Response getParameterList();

	Response deleteParameter(ParameterMaster parameterDetails);

	

}
