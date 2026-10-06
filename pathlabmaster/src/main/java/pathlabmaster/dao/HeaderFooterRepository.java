package pathlabmaster.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import pathlabmaster.pojo.HeaderFooter;

public interface HeaderFooterRepository extends JpaRepository<HeaderFooter, Long> {

	  HeaderFooter findByLabId(Long labId);

	

}