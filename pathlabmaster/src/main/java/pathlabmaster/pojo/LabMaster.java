package pathlabmaster.pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "LabMaster")
public class LabMaster {
	@Id
	private Long labId;
	private String labName;
	private String firstName;
	private String lastName;
	private Long personalMobileNumber;
	private Long workMobileNumber;
	private String mailId;
	private String address;
	private String landmark;
	private String city;
	private String distirct;
	private String state;
	private String country;
	private Integer pincode;
	private String sbuscriptionStartDate;
	private String sbuscriptionEndDate;
	private Integer patientCountAlloted;
	private Long createdBy;
	private Long updatedBy;
	private String createdAt;
	private String updatedAt;
	public LabMaster() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Long getLabId() {
		return labId;
	}
	public void setLabId(Long labId) {
		this.labId = labId;
	}
	public String getLabName() {
		return labName;
	}
	public void setLabName(String labName) {
		this.labName = labName;
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
	public String getDistirct() {
		return distirct;
	}
	public void setDistirct(String distirct) {
		this.distirct = distirct;
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
	public String getSbuscriptionStartDate() {
		return sbuscriptionStartDate;
	}
	public void setSbuscriptionStartDate(String sbuscriptionStartDate) {
		this.sbuscriptionStartDate = sbuscriptionStartDate;
	}
	public String getSbuscriptionEndDate() {
		return sbuscriptionEndDate;
	}
	public void setSbuscriptionEndDate(String sbuscriptionEndDate) {
		this.sbuscriptionEndDate = sbuscriptionEndDate;
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
	public Integer getPatientCountAlloted() {
		return patientCountAlloted;
	}
	public void setPatientCountAlloted(Integer patientCountAlloted) {
		this.patientCountAlloted = patientCountAlloted;
	}
	@Override
	public String toString() {
		return "LabMaster [labId=" + labId + ", labName=" + labName + ", firstName=" + firstName + ", lastName="
				+ lastName + ", personalMobileNumber=" + personalMobileNumber + ", workMobileNumber=" + workMobileNumber
				+ ", mailId=" + mailId + ", address=" + address + ", landmark=" + landmark + ", city=" + city
				+ ", distirct=" + distirct + ", state=" + state + ", country=" + country + ", pincode=" + pincode
				+ ", sbuscriptionStartDate=" + sbuscriptionStartDate + ", sbuscriptionEndDate=" + sbuscriptionEndDate
				+ ", patientCountAlloted=" + patientCountAlloted + ", createdBy=" + createdBy + ", updatedBy="
				+ updatedBy + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
	
}
