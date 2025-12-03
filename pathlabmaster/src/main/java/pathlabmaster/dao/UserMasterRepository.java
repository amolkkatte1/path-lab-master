package pathlabmaster.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pathlabmaster.pojo.UserMaster;

public interface UserMasterRepository extends JpaRepository<UserMaster, Long> {

	Optional<UserMaster> findByUserNameAndPassword(String userName, String password);

}