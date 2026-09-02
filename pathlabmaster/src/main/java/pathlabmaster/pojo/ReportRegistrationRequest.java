package pathlabmaster.pojo;

import java.util.List;

public class ReportRegistrationRequest {
	private Long patientId;
	private Long labId;
	private Long userId;
	private List<TestMaster> testList;
	public Long getPatientId() {
		return patientId;
	}
	public void setPatientId(Long patientId) {
		this.patientId = patientId;
	}
	public Long getLabId() {
		return labId;
	}
	public void setLabId(Long labId) {
		this.labId = labId;
	}
	public List<TestMaster> getTestList() {
		return testList;
	}
	public void setTestList(List<TestMaster> testList) {
		this.testList = testList;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	@Override
	public String toString() {
		return "ReportRegistrationRequest [patientId=" + patientId + ", labId=" + labId + ", userId=" + userId
				+ ", testList=" + testList + "]";
	}
	
}
