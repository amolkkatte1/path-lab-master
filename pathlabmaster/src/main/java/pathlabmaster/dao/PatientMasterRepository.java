package pathlabmaster.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pathlabmaster.pojo.PatientMaster;

public interface PatientMasterRepository extends JpaRepository<PatientMaster, Long> {

	List<PatientMaster> findByLabId(Long labId);

	
}