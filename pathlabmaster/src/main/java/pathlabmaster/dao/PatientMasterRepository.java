package pathlabmaster.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pathlabmaster.pojo.PatientMaster;

public interface PatientMasterRepository extends JpaRepository<PatientMaster, Long> {

	List<PatientMaster> findByLabId(Long labId);

	PatientMaster findByPatientIdAndLabId(Long patientId, Long labId);

	List<PatientMaster> findByLabIdAndCreatedAtStartingWith(Long labId, String today);

	// Total patients for lab
	long countByLabId(Long labId);

	// Patients created in a particular month/date
	long countByLabIdAndCreatedAtStartingWith(Long labId, String date);
	@Query("""
		    SELECT p FROM PatientMaster p
		    WHERE p.labId = :labId
		      AND (:fromDate IS NULL OR :fromDate = '' OR p.createdAt >= CONCAT(:fromDate, ' 00:00:00'))
		      AND (:toDate IS NULL OR :toDate = '' OR p.createdAt <= CONCAT(:toDate, ' 23:59:59'))
		      AND (:firstName IS NULL OR :firstName = '' OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')))
		      AND (:lastName IS NULL OR :lastName = '' OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')))
		      AND (:patientId IS NULL OR p.patientId = :patientId)
		      AND (:doctorName IS NULL OR :doctorName = '' OR LOWER(p.doctorName) LIKE LOWER(CONCAT('%', :doctorName, '%')))
		      AND (:doctorId IS NULL OR p.doctorId = :doctorId)
		    ORDER BY p.createdAt DESC
		    """)
		List<PatientMaster> filterPatients(
		        @Param("labId") Long labId,
		        @Param("fromDate") String fromDate,
		        @Param("toDate") String toDate,
		        @Param("firstName") String firstName,
		        @Param("lastName") String lastName,
		        @Param("patientId") Long patientId,
		        @Param("doctorName") String doctorName,
		        @Param("doctorId") Long doctorId
		);

	PatientMaster findByPatientId(Long patientId);
}