package pathlabmaster.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pathlabmaster.pojo.MdDoctorMaster;

public interface MdDoctorMasterRepository extends JpaRepository<MdDoctorMaster, Long> {

	List<MdDoctorMaster> findByLabId(Long labId);
	
	Optional<MdDoctorMaster> findFirstByLabIdAndIsActiveTrueOrderByCreatedAtDesc(Long labId);

}