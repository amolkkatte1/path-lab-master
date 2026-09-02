package pathlabmaster.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pathlabmaster.pojo.ParameterMaster;

public interface ParameterMasterRepository extends JpaRepository<ParameterMaster, Long> {
	List<ParameterMaster> findByParameterIdIn(List<Long> parameterIds);
}