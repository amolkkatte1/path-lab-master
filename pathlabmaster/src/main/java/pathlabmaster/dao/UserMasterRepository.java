package pathlabmaster.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import pathlabmaster.pojo.UserMaster;

public interface UserMasterRepository extends JpaRepository<UserMaster, Long> {
}