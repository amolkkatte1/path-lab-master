package pathlabmaster.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import pathlabmaster.pojo.DoctorMaster;

public interface DoctorMasterRepository extends JpaRepository<DoctorMaster, Long> {

}