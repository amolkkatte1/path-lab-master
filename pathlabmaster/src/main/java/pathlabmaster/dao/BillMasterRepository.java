package pathlabmaster.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import pathlabmaster.pojo.BillMaster;

public interface BillMasterRepository extends JpaRepository<BillMaster, Long> {

	BillMaster findByLabId(Long labId);

}