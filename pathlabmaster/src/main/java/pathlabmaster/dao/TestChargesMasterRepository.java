
package pathlabmaster.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pathlabmaster.pojo.TestChargesMaster;

public interface TestChargesMasterRepository
        extends JpaRepository<TestChargesMaster, Long> {

    List<TestChargesMaster> findByLabId(Long labId);

}