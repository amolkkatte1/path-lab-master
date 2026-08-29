package pathlabmaster.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pathlabmaster.pojo.DoctorMaster;

public interface DoctorMasterRepository extends JpaRepository<DoctorMaster, Long> {

	List<DoctorMaster> findByLabId(Long labId);

}