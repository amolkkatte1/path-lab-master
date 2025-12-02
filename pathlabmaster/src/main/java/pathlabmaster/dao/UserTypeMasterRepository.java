package pathlabmaster.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import pathlabmaster.pojo.UserTypeMaster;

public interface UserTypeMasterRepository extends JpaRepository<UserTypeMaster, Long> {
}