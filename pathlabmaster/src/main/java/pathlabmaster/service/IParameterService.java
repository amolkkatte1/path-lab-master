package pathlabmaster.service;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import pathlabmaster.pojo.ParameterMaster;
import pathlabmaster.utility.Response;

public interface IParameterService {

	Response createParameter(ParameterMaster parameterDetails);

	Response updateParameter(ParameterMaster parameterDetails);

	Response getParameter(ParameterMaster parameterDetails);

	Response getParameterList();

	Response deleteParameter(ParameterMaster parameterDetails);

	Response getParameterListByTestId(Long testId) throws JsonMappingException, JsonProcessingException;


	Response createParameterList(List<ParameterMaster> parameterDetailList);

	

}
