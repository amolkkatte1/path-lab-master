package pathlabmaster.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import pathlabmaster.pojo.ClientConfig;

public interface ClientConfigRepository extends JpaRepository<ClientConfig, Long> {

	ClientConfig findByLabId(Long labId);


}