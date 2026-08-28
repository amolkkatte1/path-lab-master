package pathlabmaster.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import pathlabmaster.pojo.ParameterGroupMaster;

public interface ParameterGroupMasterRepository extends JpaRepository<ParameterGroupMaster, Long> {
}