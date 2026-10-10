package pathlabmaster.pojo;

import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ClientConfig")
public class ClientConfig {

	@Id
	private Long configId;
	private Long labId;
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, List<String>> testKeys;
	private Integer reportTopSpace;
	private Integer reportBottomSpace;
	private Integer lineSpace;
	private boolean isQrCodeRequired;
	private Integer qrCodeHorizantalPosition;
	private Integer qrCodeVerticalPosition;
	private Integer columnCount;
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, Map<String,String>> testKeyMapping;
	private Long createdBy;
	private Long updatedBy;
	private String createdAt;
	private String updatedAt;
	public ClientConfig() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Long getConfigId() {
		return configId;
	}
	public void setConfigId(Long configId) {
		this.configId = configId;
	}
	public Long getLabId() {
		return labId;
	}
	public void setLabId(Long labId) {
		this.labId = labId;
	}
	public Map<String, List<String>> getTestKeys() {
		return testKeys;
	}
	public void setTestKeys(Map<String, List<String>> testKeys) {
		this.testKeys = testKeys;
	}
	public Integer getReportTopSpace() {
		return reportTopSpace;
	}
	public void setReportTopSpace(Integer reportTopSpace) {
		this.reportTopSpace = reportTopSpace;
	}
	public Integer getReportBottomSpace() {
		return reportBottomSpace;
	}
	public void setReportBottomSpace(Integer reportBottomSpace) {
		this.reportBottomSpace = reportBottomSpace;
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
	public boolean isQrCodeRequired() {
		return isQrCodeRequired;
	}
	public void setQrCodeRequired(boolean isQrCodeRequired) {
		this.isQrCodeRequired = isQrCodeRequired;
	}
	public Integer getQrCodeHorizantalPosition() {
		return qrCodeHorizantalPosition;
	}
	public void setQrCodeHorizantalPosition(Integer qrCodeHorizantalPosition) {
		this.qrCodeHorizantalPosition = qrCodeHorizantalPosition;
	}
	public Integer getQrCodeVerticalPosition() {
		return qrCodeVerticalPosition;
	}
	public void setQrCodeVerticalPosition(Integer qrCodeVerticalPosition) {
		this.qrCodeVerticalPosition = qrCodeVerticalPosition;
	}
	public Map<String, Map<String, String>> getTestKeyMapping() {
		return testKeyMapping;
	}
	public void setTestKeyMapping(Map<String, Map<String, String>> testKeyMapping) {
		this.testKeyMapping = testKeyMapping;
	}
	public Integer getColumnCount() {
		return columnCount;
	}
	public void setColumnCount(Integer columnCount) {
		this.columnCount = columnCount;
	}
	public Integer getLineSpace() {
		return lineSpace;
	}
	public void setLineSpace(Integer lineSpace) {
		this.lineSpace = lineSpace;
	}
	@Override
	public String toString() {
		return "ClientConfig [configId=" + configId + ", labId=" + labId + ", testKeys=" + testKeys
				+ ", reportTopSpace=" + reportTopSpace + ", reportBottomSpace=" + reportBottomSpace + ", lineSpace="
				+ lineSpace + ", isQrCodeRequired=" + isQrCodeRequired + ", qrCodeHorizantalPosition="
				+ qrCodeHorizantalPosition + ", qrCodeVerticalPosition=" + qrCodeVerticalPosition + ", columnCount="
				+ columnCount + ", testKeyMapping=" + testKeyMapping + ", createdBy=" + createdBy + ", updatedBy="
				+ updatedBy + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
}
