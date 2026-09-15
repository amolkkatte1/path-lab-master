package pathlabmaster.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pathlabmaster.pojo.PatientMaster;

public interface PatientMasterRepository extends JpaRepository<PatientMaster, Long> {

	List<PatientMaster> findByLabId(Long labId);

	PatientMaster findByPatientIdAndLabId(Long patientId, Long labId);

	List<PatientMaster> findByLabIdAndCreatedAtStartingWith(Long labId, String today);

	// Total patients for lab
	long countByLabId(Long labId);

	// Patients created in a particular month/date
	long countByLabIdAndCreatedAtStartingWith(Long labId, String date);
}