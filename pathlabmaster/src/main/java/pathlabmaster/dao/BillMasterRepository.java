package pathlabmaster.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pathlabmaster.pojo.BillMaster;

public interface BillMasterRepository extends JpaRepository<BillMaster, Long> {

	BillMaster findByLabId(Long labId);

	@Query("""
			SELECT p FROM BillMaster p
			WHERE p.labId = :labId
			  AND (:fromDate IS NULL OR :fromDate = '' OR p.createdAt >= CONCAT(:fromDate, ' 00:00:00'))
			  AND (:toDate IS NULL OR :toDate = '' OR p.createdAt <= CONCAT(:toDate, ' 23:59:59'))

			ORDER BY p.createdAt DESC
			""")
	List<BillMaster> filterBills(@Param("labId") Long labId, @Param("fromDate") String fromDate,
			@Param("toDate") String toDate);

}