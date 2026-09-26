package pathlabmaster.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pathlabmaster.pojo.TestMaster;

public interface TestMasterRepository extends JpaRepository<TestMaster, Long> {
	List<TestMaster> findByTestIdIn(List<Long> testIds);
}
