package pathlabmaster.pojo;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TestGroupMaster")

public class TestGroupMaster {
@Id
 private Long testGroupId;
 private String testGroupName;
 private String testList;
 private Long serviceId;
 private String serviceName;
 private String serviceShortName;
 private Long serviceGroupId;
 private String serviceGroupName;
 private String testGroupCharges;
 private Long createdBy;
 private Long updatedBy;
 private Date createdAt;
 private Date updatedAt;
public Long getTestGroupId() {
	return testGroupId;
}
public void setTestGroupId(Long testGroupId) {
	this.testGroupId = testGroupId;
}
public String getTestGroupName() {
	return testGroupName;
}
public void setTestGroupName(String testGroupName) {
	this.testGroupName = testGroupName;
}
public String getTestList() {
	return testList;
}
public void setTestList(String testList) {
	this.testList = testList;
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
public String getServiceGroupName() {
	return serviceGroupName;
}
public void setServiceGroupName(String serviceGroupName) {
	this.serviceGroupName = serviceGroupName;
}
public String getTestGroupCharges() {
	return testGroupCharges;
}
public void setTestGroupCharges(String testGroupCharges) {
	this.testGroupCharges = testGroupCharges;
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
public Date getCreatedAt() {
	return createdAt;
}
public void setCreatedAt(Date createdAt) {
	this.createdAt = createdAt;
}
public Date getUpdatedAt() {
	return updatedAt;
}
public void setUpdatedAt(Date updatedAt) {
	this.updatedAt = updatedAt;
}
@Override
public String toString() {
	return "TestGroupMaster [testGroupId=" + testGroupId + ", testGroupName=" + testGroupName + ", testList=" + testList
			+ ", serviceId=" + serviceId + ", serviceName=" + serviceName + ", serviceShortName=" + serviceShortName
			+ ", serviceGroupId=" + serviceGroupId + ", serviceGroupName=" + serviceGroupName + ", testGroupCharges="
			+ testGroupCharges + ", createdBy=" + createdBy + ", updatedBy=" + updatedBy + ", createdAt=" + createdAt
			+ ", updatedAt=" + updatedAt + "]";
}
 
}
