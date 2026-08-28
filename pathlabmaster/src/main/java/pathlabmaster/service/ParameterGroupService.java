package pathlabmaster.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pathlabmaster.dao.ParameterGroupMasterRepository;
import pathlabmaster.pojo.ParameterGroupMaster;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.ResponseStatus;
import pathlabmaster.utility.Utility;

@Service
public class ParameterGroupService implements IParameterGroupService {

	@Autowired
	private ParameterGroupMasterRepository parameterGroupRepo;

	@Override
	public Response createParameterGroup(ParameterGroupMaster parameterGroupDetails) {
		parameterGroupDetails.setParameterGroupId(Utility.generateId());
		parameterGroupDetails.setUpdatedAt(Utility.getCurrentTime());
		parameterGroupDetails.setCreatedAt(Utility.getCurrentTime());
		ParameterGroupMaster savedParameterGroup = parameterGroupRepo.save(parameterGroupDetails);
		System.out.println(savedParameterGroup.getParameterGroupId()); 
		return new Response(ResponseStatus.success, 1, "ParameterGroup created successfully", savedParameterGroup);
	}

	@Override
	public Response updateParameterGroup(ParameterGroupMaster parameterGroupDetails) {
		parameterGroupDetails.setUpdatedAt(Utility.getCurrentTime());
		ParameterGroupMaster savedParameterGroup = parameterGroupRepo.save(parameterGroupDetails);
		System.out.println(savedParameterGroup.getParameterGroupId()); 
		return new Response(ResponseStatus.success, 1, "ParameterGroup Update successfully", savedParameterGroup);
	}
	
	@Override
	public Response getParameterGroup(ParameterGroupMaster parameterGroupDetails) {
		Optional<ParameterGroupMaster> optionalParameterGroup = parameterGroupRepo.findById(parameterGroupDetails.getParameterGroupId());
		ParameterGroupMaster ParameterGroup = null;
		if (optionalParameterGroup.isPresent()) {
		    ParameterGroup = optionalParameterGroup.get();
		} else {
		    throw new RuntimeException("ParameterGroup not found");
		}
		return new Response(ResponseStatus.success, 1, "Get ParameterGroup successfully", ParameterGroup);
	}

	@Override
	public Response getParameterGroupList() {
		List<ParameterGroupMaster> ParameterGroupList = parameterGroupRepo.findAll();
		return new Response(ResponseStatus.success, 1, "Get ParameterGroup List successfully", ParameterGroupList);
	}

	@Override
	public Response deleteParameterGroup(ParameterGroupMaster parameterGroupDetails) {
		Optional<ParameterGroupMaster> optionalParameterGroup = parameterGroupRepo.findById(parameterGroupDetails.getParameterGroupId());
		ParameterGroupMaster ParameterGroup = null;
		if (optionalParameterGroup.isPresent()) {
		    ParameterGroup = optionalParameterGroup.get();
		} else {
		    throw new RuntimeException("ParameterGroup not found");
		}
		parameterGroupRepo.deleteById(parameterGroupDetails.getParameterGroupId());
		return new Response(ResponseStatus.success, 1, "Delete ParameterGroup successfully", ParameterGroup);
	}

	
}
