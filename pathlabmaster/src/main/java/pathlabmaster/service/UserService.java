package pathlabmaster.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pathlabmaster.dao.UserMasterRepository;
import pathlabmaster.pojo.UserMaster;
import pathlabmaster.utility.Response;
import pathlabmaster.utility.ResponseStatus;
import pathlabmaster.utility.Utility;

@Service
public class UserService implements IUserService {

	@Autowired
	private UserMasterRepository userRepo;

	@Override
	public Response createUser(UserMaster userDetails) {
		userDetails.setUserId(Utility.generateId());
		userDetails.setUpdatedAt(Utility.getCurrentTime());
		userDetails.setCreatedAt(Utility.getCurrentTime());
		UserMaster savedUser = userRepo.save(userDetails);
		System.out.println(savedUser.getUserId()); 
		return new Response(ResponseStatus.success, 1, "User created successfully", savedUser);
	}

	@Override
	public Response updateUser(UserMaster userDetails) {
		userDetails.setUpdatedAt(Utility.getCurrentTime());
		UserMaster savedUser = userRepo.save(userDetails);
		System.out.println(savedUser.getUserId()); 
		return new Response(ResponseStatus.success, 1, "User Update successfully", savedUser);
	}
	
	@Override
	public Response getUser(UserMaster userDetails) {
		Optional<UserMaster> optionalUser = userRepo.findById(userDetails.getUserId());
		UserMaster user = null;
		if (optionalUser.isPresent()) {
		    user = optionalUser.get();
		} else {
		    throw new RuntimeException("User not found");
		}
		return new Response(ResponseStatus.success, 1, "Get User successfully", user);
	}

	@Override
	public Response getUserList() {
		List<UserMaster> userList = userRepo.findAll();
		return new Response(ResponseStatus.success, 1, "Get User List successfully", userList);
	}

	@Override
	public Response deleteUser(UserMaster userDetails) {
		Optional<UserMaster> optionalUser = userRepo.findById(userDetails.getUserId());
		UserMaster user = null;
		if (optionalUser.isPresent()) {
		    user = optionalUser.get();
		} else {
		    throw new RuntimeException("User not found");
		}
		userRepo.deleteById(userDetails.getUserId());
		return new Response(ResponseStatus.success, 1, "Delete User successfully", user);
	}

	@Override
	public Response login(UserMaster userDetails) {
		Optional<UserMaster> optionalUser = userRepo.findByUserNameAndPassword(userDetails.getUserName(),userDetails.getPassword());
		if (optionalUser.isPresent()) {
		    UserMaster user = optionalUser.get();
		    return new Response(ResponseStatus.success, 1, "Login Successfully", user);
		} else {
			return new Response(ResponseStatus.failure, 0, "Invalid username or password");
		}
	}
}
