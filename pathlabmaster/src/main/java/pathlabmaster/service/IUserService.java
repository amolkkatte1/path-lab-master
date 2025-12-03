package pathlabmaster.service;

import pathlabmaster.pojo.UserMaster;
import pathlabmaster.utility.Response;

public interface IUserService {

	Response createUser(UserMaster userDetails);

	Response updateUser(UserMaster userDetails);

	Response getUser(UserMaster userDetails);

	Response getUserList();

	Response deleteUser(UserMaster userDetails);

	Response login(UserMaster userDetails);

}
