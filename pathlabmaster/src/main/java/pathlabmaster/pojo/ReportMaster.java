package pathlabmaster.pojo;

import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "ReportMaster")
public class ReportMaster {

	@Id
	private Long reportId;
	private Long patientId;
	private Long labId;
	@Transient
	private Map<String, List<ParameterDetails>> pendingTest;
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, Map<String, String>> pendingTest1;
	@Transient
	private Map<String, List<ParameterDetails>> completedTest;
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, Map<String, String>> completedTest1;
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, Integer> reportNameList;
	@Column(nullable = false)
	private boolean mdSignRequired = false;
	private Long createdBy;
	private Long updatedBy;
	private String createdAt;
	private String updatedAt;
	@Transient
	private Map<String, Map<String, Boolean>> status;
	@JdbcTypeCode(SqlTypes.JSON)
	Map<String, Map<String, Integer>> status1;

	public ReportMaster() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

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

	public Map<String, List<ParameterDetails>> getPendingTest() {
		return pendingTest;
	}

	public void setPendingTest(Map<String, List<ParameterDetails>> pendingTest) {
		this.pendingTest = pendingTest;
	}

	public Map<String, List<ParameterDetails>> getCompletedTest() {
		return completedTest;
	}

	public void setCompletedTest(Map<String, List<ParameterDetails>> completedTest) {
		this.completedTest = completedTest;
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

	public Map<String, Map<String, Boolean>> getStatus() {
		return status;
	}

	public void setStatus(Map<String, Map<String, Boolean>> status) {
		this.status = status;
	}

	public Map<String, Integer> getReportNameList() {
		return reportNameList;
	}

	public void setReportNameList(Map<String, Integer> reportNameList) {
		this.reportNameList = reportNameList;
	}

	public boolean isMdSignRequired() {
		return mdSignRequired;
	}

	public void setMdSignRequired(boolean mdSignRequired) {
		this.mdSignRequired = mdSignRequired;
	}

	public Map<String, Map<String, String>> getPendingTest1() {
		return pendingTest1;
	}

	public void setPendingTest1(Map<String, Map<String, String>> pendingTest1) {
		this.pendingTest1 = pendingTest1;
	}

	public Map<String, Map<String, String>> getCompletedTest1() {
		return completedTest1;
	}

	public void setCompletedTest1(Map<String, Map<String, String>> completedTest1) {
		this.completedTest1 = completedTest1;
	}

	public Map<String, Map<String, Integer>> getStatus1() {
		return status1;
	}

	public void setStatus1(Map<String, Map<String, Integer>> status1) {
		this.status1 = status1;
	}

	@Override
	public String toString() {
		return "ReportMaster [reportId=" + reportId + ", patientId=" + patientId + ", labId=" + labId + ", pendingTest="
				+ pendingTest + ", pendingTest1=" + pendingTest1 + ", completedTest=" + completedTest
				+ ", completedTest1=" + completedTest1 + ", reportNameList=" + reportNameList + ", mdSignRequired="
				+ mdSignRequired + ", createdBy=" + createdBy + ", updatedBy=" + updatedBy + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + ", status=" + status + ", status1=" + status1 + "]";
	}

}
