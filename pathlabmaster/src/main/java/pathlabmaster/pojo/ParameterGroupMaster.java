package pathlabmaster.pojo;


import jakarta.persistence.*;

@Entity
@Table(name = "ParameterGroupMaster")
public class ParameterGroupMaster {

    @Id
    private Long parameterGroupId;
    private String parameterGroupName;
    private String parameterList;
    private Integer sequence;
    private Long createdBy;
    private Long updatedBy;
    private String createdAt;
    private String updatedAt;

    public Long getParameterGroupId() {
        return parameterGroupId;
    }

    public void setParameterGroupId(Long parameterGroupId) {
        this.parameterGroupId = parameterGroupId;
    }

    public String getParameterGroupName() {
        return parameterGroupName;
    }

    public void setParameterGroupName(String parameterGroupName) {
        this.parameterGroupName = parameterGroupName;
    }

    public String getParameterList() {
        return parameterList;
    }

    public void setParameterList(String parameterList) {
        this.parameterList = parameterList;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
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
}