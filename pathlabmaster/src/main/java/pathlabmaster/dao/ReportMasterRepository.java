package pathlabmaster.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pathlabmaster.pojo.PatientMaster;
import pathlabmaster.pojo.ReportMaster;

public interface ReportMasterRepository extends JpaRepository<ReportMaster, Long> {

	ReportMaster findByPatientId(Long patientId);

	ReportMaster findByPatientIdAndLabId(Long patientId, Long labId);
	List<ReportMaster> findByLabIdAndCreatedAtStartingWith(
            Long labId,
            String date
    );
	@Query("""
		    SELECT p FROM ReportMaster p
		    WHERE p.labId = :labId
		      AND (:fromDate IS NULL OR :fromDate = '' OR p.createdAt >= CONCAT(:fromDate, ' 00:00:00'))
		      AND (:toDate IS NULL OR :toDate = '' OR p.createdAt <= CONCAT(:toDate, ' 23:59:59'))
		      AND (:patientId IS NULL OR p.patientId = :patientId)
		    ORDER BY p.createdAt DESC
		    """)
		List<ReportMaster> filterReports(
		        @Param("labId") Long labId,
		        @Param("fromDate") String fromDate,
		        @Param("toDate") String toDate,
		        @Param("patientId") Long patientId
		);
}