package pathlabmaster.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import pathlabmaster.pojo.LabMaster;

public interface LabMasterRepository extends JpaRepository<LabMaster, Long> {

}