package pathlabmaster.pojo;

import java.math.BigDecimal;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bill_master")
public class BillMaster {

	@Id
	private Long billId;

	private Long patientId;

	private String doctorName;

	private Long doctorId;
	private Long labId;
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String,Integer> testList;
	@Column(precision = 10, scale = 2)
	private BigDecimal totalAmount;
	@Column(precision = 10, scale = 2)
	private BigDecimal paymentReceived;
	@Column(precision = 10, scale = 2)
	private BigDecimal paymentDue;
	@Column(precision = 10, scale = 2)
	private BigDecimal discount;
	@Column(precision = 10, scale = 2)
	private BigDecimal collectedByDoctor;

	private Long createdBy;

	private Long updatedBy;

	private String createdAt;

	private String updatedAt;

	public BillMaster() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getBillId() {
		return billId;
	}

	public void setBillId(Long billId) {
		this.billId = billId;
	}

	public Long getPatientId() {
		return patientId;
	}

	public void setPatientId(Long patientId) {
		this.patientId = patientId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public Long getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Long doctorId) {
		this.doctorId = doctorId;
	}
	public Map<String, Integer> getTestList() {
		return testList;
	}

	public void setTestList(Map<String, Integer> testList) {
		this.testList = testList;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getPaymentReceived() {
		return paymentReceived;
	}

	public void setPaymentReceived(BigDecimal paymentReceived) {
		this.paymentReceived = paymentReceived;
	}

	public BigDecimal getPaymentDue() {
		return paymentDue;
	}

	public void setPaymentDue(BigDecimal paymentDue) {
		this.paymentDue = paymentDue;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getCollectedByDoctor() {
		return collectedByDoctor;
	}

	public void setCollectedByDoctor(BigDecimal collectedByDoctor) {
		this.collectedByDoctor = collectedByDoctor;
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

	public Long getLabId() {
		return labId;
	}

	public void setLabId(Long labId) {
		this.labId = labId;
	}

	@Override
	public String toString() {
		return "BillMaster [billId=" + billId + ", patientId=" + patientId + ", doctorName=" + doctorName
				+ ", doctorId=" + doctorId + ", labId=" + labId + ", testList=" + testList + ", totalAmount="
				+ totalAmount + ", paymentReceived=" + paymentReceived + ", paymentDue=" + paymentDue + ", discount="
				+ discount + ", collectedByDoctor=" + collectedByDoctor + ", createdBy=" + createdBy + ", updatedBy="
				+ updatedBy + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}


}
