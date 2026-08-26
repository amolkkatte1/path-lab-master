package pathlabmaster.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import pathlabmaster.pojo.PatientMaster;

public interface PatientMasterRepository extends JpaRepository<PatientMaster, Long> {

	
}