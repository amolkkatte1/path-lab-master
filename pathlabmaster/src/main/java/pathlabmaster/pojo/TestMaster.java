package pathlabmaster.pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TestMaster")
public class TestMaster {
	@Id
	private Long testId;
	private String testName;
	private String parameterGroupList;
	private String parameterList;
	private Long serviceId;
	private String serviceName;
	private String serviceShortName;
	private Long serviceGroupId;
	private String labName;
	private Long labId;
	private Long serviceGroupName;
	private Integer testCharges;
	private Long createdBy;
	private Long updatedBy;
	private String createdAt;
	private String updatedAt;

	public TestMaster() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getTestId() {
		return testId;
	}

	public void setTestId(Long testId) {
		this.testId = testId;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getParameterGroupList() {
		return parameterGroupList;
	}

	public void setParameterGroupList(String parameterGroupList) {
		this.parameterGroupList = parameterGroupList;
	}

	public String getParameterList() {
		return parameterList;
	}

	public void setParameterList(String parameterList) {
		this.parameterList = parameterList;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceShortName() {
		return serviceShortName;
	}

	public void setServiceShortName(String serviceShortName) {
		this.serviceShortName = serviceShortName;
	}

	public Long getServiceGroupId() {
		return serviceGroupId;
	}

	public void setServiceGroupId(Long serviceGroupId) {
		this.serviceGroupId = serviceGroupId;
	}

	public Long getServiceGroupName() {
		return serviceGroupName;
	}

	public void setServiceGroupName(Long serviceGroupName) {
		this.serviceGroupName = serviceGroupName;
	}

	public Integer getTestCharges() {
		return testCharges;
	}

	public void setTestCharges(Integer testCharges) {
		this.testCharges = testCharges;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getLabName() {
		return labName;
	}

	public void setLabName(String labName) {
		this.labName = labName;
	}

	public Long getLabId() {
		return labId;
	}

	public void setLabId(Long labId) {
		this.labId = labId;
	}

	@Override
	public String toString() {
		return "TestMaster [testId=" + testId + ", testName=" + testName + ", parameterGroupList=" + parameterGroupList
				+ ", parameterList=" + parameterList + ", serviceId=" + serviceId + ", serviceName=" + serviceName
				+ ", serviceShortName=" + serviceShortName + ", serviceGroupId=" + serviceGroupId + ", labName="
				+ labName + ", labId=" + labId + ", serviceGroupName=" + serviceGroupName + ", testCharges="
				+ testCharges + ", createdBy=" + createdBy + ", updatedBy=" + updatedBy + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + "]";
	}

}