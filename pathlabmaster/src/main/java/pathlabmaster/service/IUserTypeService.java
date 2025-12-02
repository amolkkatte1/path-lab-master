package pathlabmaster.service;

import pathlabmaster.pojo.UserTypeMaster;
import pathlabmaster.utility.Response;

public interface IUserTypeService {

	Response createUserType(UserTypeMaster userTypeDetails);

	Response updateUserType(UserTypeMaster userTypeDetails);

	Response getUserType(UserTypeMaster userTypeDetails);

	Response getUserTypeList();

	Response deleteUserType(UserTypeMaster userTypeDetails);
	
}
