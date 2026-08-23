package pathlabmaster.pojo;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ParameterGroupMaster")
public class ParameterGroupMaster {
	@Id
	private Long parameterGroupId;

	public ParameterGroupMaster() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getParameterGroupId() {
		return parameterGroupId;
	}

	public void setParameterGroupId(Long parameterGroupId) {
		this.parameterGroupId = parameterGroupId;
	}

	@Override
	public String toString() {
		return "ParameterGroupMaster [parameterGroupId=" + parameterGroupId + "]";
	}
	

}
