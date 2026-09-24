package pathlabmaster.pojo;

import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ReportMaster")
public class ReportMaster {

	@Id
	private Long reportId;
	private Long patientId;
	private Long labId;
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, List<ParameterDetails>> pendingTest;
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, List<ParameterDetails>> completedTest;
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String,Integer> reportNameList;
	@Column(nullable = false)
	private boolean mdSignRequired = false;
	private Long createdBy;
	private Long updatedBy;
	private String createdAt;
	private String updatedAt;
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, Map<String, Boolean>> status;
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
	@Override
	public String toString() {
		return "ReportMaster [reportId=" + reportId + ", patientId=" + patientId + ", labId=" + labId + ", pendingTest="
				+ pendingTest + ", completedTest=" + completedTest + ", reportNameList=" + reportNameList
				+ ", mdSignRequired=" + mdSignRequired + ", createdBy=" + createdBy + ", updatedBy=" + updatedBy
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", status=" + status + "]";
	}
	
}
