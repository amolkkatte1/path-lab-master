package pathlabmaster.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pathlabmaster.dao.ParameterMasterRepository;
import pathlabmaster.pojo.ParameterMaster;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.ResponseStatus;
import pathlabmaster.utility.Utility;

@Service
public class ParameterService implements IParameterService {

	@Autowired
	private ParameterMasterRepository parameterRepo;

	@Override
	public Response createParameter(ParameterMaster parameterDetails) {
		parameterDetails.setParameterId(Utility.generateId());
		parameterDetails.setUpdatedAt(Utility.getCurrentTime());
		parameterDetails.setCreatedAt(Utility.getCurrentTime());
		ParameterMaster savedParameter = parameterRepo.save(parameterDetails);
		System.out.println(savedParameter.getParameterId()); 
		return new Response(ResponseStatus.success, 1, "Parameter created successfully", savedParameter);
	}

	@Override
	public Response updateParameter(ParameterMaster parameterDetails) {
		parameterDetails.setUpdatedAt(Utility.getCurrentTime());
		ParameterMaster savedParameter = parameterRepo.save(parameterDetails);
		System.out.println(savedParameter.getParameterId()); 
		return new Response(ResponseStatus.success, 1, "Parameter Update successfully", savedParameter);
	}
	
	@Override
	public Response getParameter(ParameterMaster parameterDetails) {
		Optional<ParameterMaster> optionalParameter = parameterRepo.findById(parameterDetails.getParameterId());
		ParameterMaster Parameter = null;
		if (optionalParameter.isPresent()) {
		    Parameter = optionalParameter.get();
		} else {
		    throw new RuntimeException("Parameter not found");
		}
		return new Response(ResponseStatus.success, 1, "Get Parameter successfully", Parameter);
	}

	@Override
	public Response getParameterList() {
		List<ParameterMaster> ParameterList = parameterRepo.findAll();
		return new Response(ResponseStatus.success, 1, "Get Parameter List successfully", ParameterList);
	}

	@Override
	public Response deleteParameter(ParameterMaster parameterDetails) {
		Optional<ParameterMaster> optionalParameter = parameterRepo.findById(parameterDetails.getParameterId());
		ParameterMaster Parameter = null;
		if (optionalParameter.isPresent()) {
		    Parameter = optionalParameter.get();
		} else {
		    throw new RuntimeException("Parameter not found");
		}
		parameterRepo.deleteById(parameterDetails.getParameterId());
		return new Response(ResponseStatus.success, 1, "Delete Parameter successfully", Parameter);
	}

	
}
