package pathlabmaster.pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TestChargesMaster")
public class TestChargesMaster {
	@Id
	private Long testChargesId;
	private Long labId;
	private Integer testCharges;

	public TestChargesMaster() {
		super();
		// TODO Auto-generated constructor stub
	}


	public TestChargesMaster(Long testId, Long labId, Integer testCharges) {
		this.testChargesId = testId;
		this.labId = labId;
		this.testCharges = testCharges;
		
	}

	
	public Long getTestChargesId() {
		return testChargesId;
	}

	public void setTestChargesId(Long testChargesId) {
		this.testChargesId = testChargesId;
	}

	public Long getLabId() {
		return labId;
	}

	public void setLabId(Long labId) {
		this.labId = labId;
	}

	public Integer getTestCharges() {
		return testCharges;
	}

	public void setTestCharges(Integer testCharges) {
		this.testCharges = testCharges;
	}

	@Override
	public String toString() {
		return "TestChargesMaster [testChargesId=" + testChargesId + ", labId=" + labId + ", testCharges=" + testCharges
				+ "]";
	}

}