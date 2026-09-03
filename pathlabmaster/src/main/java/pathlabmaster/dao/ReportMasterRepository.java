package pathlabmaster.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pathlabmaster.pojo.ReportMaster;

public interface ReportMasterRepository extends JpaRepository<ReportMaster, Long> {

	ReportMaster findByPatientId(Long patientId);

	ReportMaster findByPatientIdAndLabId(Long patientId, Long labId);
	List<ReportMaster> findByLabIdAndCreatedAtStartingWith(
            Long labId,
            String date
    );

}