package pathlabmaster.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pathlabmaster.dao.UserTypeMasterRepository;
import pathlabmaster.pojo.UserTypeMaster;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.ResponseStatus;
import pathlabmaster.utility.Utility;

@Service
public class UserTypeService implements IUserTypeService {

	@Autowired
	private UserTypeMasterRepository userTypeRepo;

	@Override
	public Response createUserType(UserTypeMaster userTypeDetails) {
		userTypeDetails.setUserTypeId(Utility.generateId());
		userTypeDetails.setUpdatedAt(Utility.getCurrentTime());
		userTypeDetails.setCreatedAt(Utility.getCurrentTime());
		UserTypeMaster savedUserType = userTypeRepo.save(userTypeDetails);
		System.out.println(savedUserType.getUserTypeId()); 
		return new Response(ResponseStatus.success, 1, "User Type created successfully", savedUserType);
	}

	@Override
	public Response updateUserType(UserTypeMaster userTypeDetails) {
		userTypeDetails.setUpdatedAt(Utility.getCurrentTime());
		UserTypeMaster savedUserType = userTypeRepo.save(userTypeDetails);
		System.out.println(savedUserType.getUserTypeId()); 
		return new Response(ResponseStatus.success, 1, "User Type Update successfully", savedUserType);
	}
	
	@Override
	public Response getUserType(UserTypeMaster userTypeDetails) {
		Optional<UserTypeMaster> optionalUser = userTypeRepo.findById(userTypeDetails.getUserTypeId());
		UserTypeMaster userType = null;
		if (optionalUser.isPresent()) {
			userType = optionalUser.get();
		} else {
		    throw new RuntimeException("User Type not found");
		}
		return new Response(ResponseStatus.success, 1, "Get User Type successfully", userType);
	}

	@Override
	public Response getUserTypeList() {
		List<UserTypeMaster> userTypeList = userTypeRepo.findAll();
		return new Response(ResponseStatus.success, 1, "Get User Type List successfully", userTypeList);
	}

	@Override
	public Response deleteUserType(UserTypeMaster userTypeDetails) {
		Optional<UserTypeMaster> optionalUserType = userTypeRepo.findById(userTypeDetails.getUserTypeId());
		UserTypeMaster userType = null;
		if (optionalUserType.isPresent()) {
			userType = optionalUserType.get();
		} else {
		    throw new RuntimeException("User Type not found");
		}
		userTypeRepo.deleteById(userTypeDetails.getUserTypeId());
		return new Response(ResponseStatus.success, 1, "Delete User Type successfully", userType);
	}
}
