package pathlabmaster.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import pathlabmaster.pojo.ParameterMaster;

public interface ParameterMasterRepository extends JpaRepository<ParameterMaster, Long> {
}