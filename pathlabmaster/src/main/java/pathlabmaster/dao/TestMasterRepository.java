package pathlabmaster.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import pathlabmaster.pojo.TestMaster;
import pathlabmaster.pojo.UserMaster;

public interface TestMasterRepository extends JpaRepository<TestMaster, Long> {

}
