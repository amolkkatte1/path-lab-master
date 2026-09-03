package pathlabmaster.pojo;

import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

public class ReportMasterResponse {
	private Long reportId;
	private Long patientId;
	private String prefix;
	private String firstName;
	private String middleName;
	private String lastName;
	private Integer age;
	private Long doctorId;
	private String doctorName;
	private Long labId;
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, List<ParameterDetails>> pendingTest;
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, List<ParameterDetails>> completedTest;
	private String patientCreatedAt;
	private String reportCreatedAt;
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, Map<String, Boolean>> status;
	
	public ReportMasterResponse(PatientMaster patient, ReportMaster report) {

		// PatientMaster
		this.patientId = patient.getPatientId();
		this.prefix = patient.getPrefix();
		this.firstName = patient.getFirstName();
		this.middleName = patient.getMiddleName();
		this.lastName = patient.getLastName();
		this.age = patient.getAge();

		this.doctorId = patient.getDoctorId();
		this.doctorName = patient.getDoctorName();
		this.labId = patient.getLabId();

		this.patientCreatedAt = patient.getCreatedAt();

		// ReportMaster
		this.reportId = report.getReportId();

		this.pendingTest = report.getPendingTest();
		this.completedTest = report.getCompletedTest();
		this.status = report.getStatus();

		this.reportCreatedAt = report.getCreatedAt();
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
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Long getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(Long doctorId) {
		this.doctorId = doctorId;
	}
	public String getDoctorName() {
		return doctorName;
	}
	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
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
	public String getPatientCreatedAt() {
		return patientCreatedAt;
	}
	public void setPatientCreatedAt(String patientCreatedAt) {
		this.patientCreatedAt = patientCreatedAt;
	}
	public String getReportCreatedAt() {
		return reportCreatedAt;
	}
	public void setReportCreatedAt(String reportCreatedAt) {
		this.reportCreatedAt = reportCreatedAt;
	}
	public Map<String, Map<String, Boolean>> getStatus() {
		return status;
	}
	public void setStatus(Map<String, Map<String, Boolean>> status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "ReportMasterResponse [reportId=" + reportId + ", patientId=" + patientId + ", prefix=" + prefix
				+ ", firstName=" + firstName + ", middleName=" + middleName + ", lastName=" + lastName + ", age=" + age
				+ ", doctorId=" + doctorId + ", doctorName=" + doctorName + ", labId=" + labId + ", pendingTest="
				+ pendingTest + ", completedTest=" + completedTest + ", patientCreatedAt=" + patientCreatedAt
				+ ", reportCreatedAt=" + reportCreatedAt + ", status=" + status + "]";
	}
}
