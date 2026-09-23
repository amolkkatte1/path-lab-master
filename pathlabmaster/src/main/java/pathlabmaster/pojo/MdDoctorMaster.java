package pathlabmaster.pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "MdDoctorMaster")
public class MdDoctorMaster {

    @Id
    private Long mdDoctorId;

    private String doctorName;
    private String doctorMailId;
    private Long doctorMobileNumber;
    private Float shairingPercentage;
    private String labName;
    private Long labId;
    private String educationQulification;

    // Google Drive / external image URL
    private String signUrl;

    // Doctor signature image stored directly in PostgreSQL
    @Lob
    private byte[] signImage;

    private boolean isActive;
    private Integer signPosition;
    private Long createdBy;
    private Long updatedBy;
    private String createdAt;
    private String updatedAt;

    public MdDoctorMaster() {
        super();
    }

    public Long getMdDoctorId() {
        return mdDoctorId;
    }

    public void setMdDoctorId(Long mdDoctorId) {
        this.mdDoctorId = mdDoctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorMailId() {
        return doctorMailId;
    }

    public void setDoctorMailId(String doctorMailId) {
        this.doctorMailId = doctorMailId;
    }

    public Long getDoctorMobileNumber() {
        return doctorMobileNumber;
    }

    public void setDoctorMobileNumber(Long doctorMobileNumber) {
        this.doctorMobileNumber = doctorMobileNumber;
    }

    public Float getShairingPercentage() {
        return shairingPercentage;
    }

    public void setShairingPercentage(Float shairingPercentage) {
        this.shairingPercentage = shairingPercentage;
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

    public String getEducationQulification() {
        return educationQulification;
    }

    public void setEducationQulification(String educationQulification) {
        this.educationQulification = educationQulification;
    }

    public String getSignUrl() {
        return signUrl;
    }

    public void setSignUrl(String signUrl) {
        this.signUrl = signUrl;
    }

    public byte[] getSignImage() {
        return signImage;
    }

    public void setSignImage(byte[] signImage) {
        this.signImage = signImage;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getSignPosition() {
        return signPosition;
    }

    public void setSignPosition(Integer signPosition) {
        this.signPosition = signPosition;
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

    @Override
    public String toString() {
        return "MdDoctorMaster [mdDoctorId=" + mdDoctorId
                + ", doctorName=" + doctorName
                + ", doctorMailId=" + doctorMailId
                + ", doctorMobileNumber=" + doctorMobileNumber
                + ", shairingPercentage=" + shairingPercentage
                + ", labName=" + labName
                + ", labId=" + labId
                + ", educationQulification=" + educationQulification
                + ", signUrl=" + signUrl
                + ", isActive=" + isActive
                + ", signPosition=" + signPosition
                + ", createdBy=" + createdBy
                + ", updatedBy=" + updatedBy
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + "]";
    }
}