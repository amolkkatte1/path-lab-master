package pathlabmaster.pojo;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "UserMaster")
public class UserMaster {
	@Id
	private Long userId;
    private String firstName;
    private String lastName;
    private Long personalMobileNumber;
    private Long workMobileNumber;
    private String mailId;
    private String address;
    private String landmark;
    private String city;
    private String district;
    private String state;
    private String country;
    private Integer pincode;
    private String labName;
    private Long labId;
    private Long userTypeId;
    private String userName;
    private String password;
    private String userTypeName;
	private Date updatedAt;
	private String updatedBy;
	private Date createdAt;
	private String createdBy;
	public UserMaster() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Long getPersonalMobileNumber() {
		return personalMobileNumber;
	}
	public void setPersonalMobileNumber(Long personalMobileNumber) {
		this.personalMobileNumber = personalMobileNumber;
	}
	public Long getWorkMobileNumber() {
		return workMobileNumber;
	}
	public void setWorkMobileNumber(Long workMobileNumber) {
		this.workMobileNumber = workMobileNumber;
	}
	public String getMailId() {
		return mailId;
	}
	public void setMailId(String mailId) {
		this.mailId = mailId;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLandmark() {
		return landmark;
	}
	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public Integer getPincode() {
		return pincode;
	}
	public void setPincode(Integer pincode) {
		this.pincode = pincode;
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
	public Long getUserTypeId() {
		return userTypeId;
	}
	public void setUserTypeId(Long userTypeId) {
		this.userTypeId = userTypeId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserTypeName() {
		return userTypeName;
	}
	public void setUserTypeName(String userTypeName) {
		this.userTypeName = userTypeName;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	@Override
	public String toString() {
		return "UserMaster [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", personalMobileNumber=" + personalMobileNumber + ", workMobileNumber=" + workMobileNumber
				+ ", mailId=" + mailId + ", address=" + address + ", landmark=" + landmark + ", city=" + city
				+ ", district=" + district + ", state=" + state + ", country=" + country + ", pincode=" + pincode
				+ ", labName=" + labName + ", labId=" + labId + ", userTypeId=" + userTypeId + ", userName=" + userName
				+ ", password=" + password + ", userTypeName=" + userTypeName + ", updatedAt=" + updatedAt
				+ ", updatedBy=" + updatedBy + ", createdAt=" + createdAt + ", createdBy=" + createdBy + "]";
	}
	
}