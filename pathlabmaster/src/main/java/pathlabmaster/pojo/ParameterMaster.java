package pathlabmaster.pojo;
import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "ParameterMaster")
public class ParameterMaster {

    @Id
    private Long parameterId;
    private String parameterName;
    private String code;
    private String value;
    private Integer sequence;
    private String dataType;
    private String unit;
    private String criteria;
    private String defaultVlue;
    private String formula;
    @Column(precision = 10, scale = 2)
    private BigDecimal upperRange;
    @Column(precision = 10, scale = 2)
    private BigDecimal lowerRange;
    @Column(nullable = true)
    private Double extrimUpperRange;
    @Column(nullable = true)
    private Double extrimLowerRange;
    private Double lowerAgeRange;
    private Double upperAgeRange;
    private String method;
    private String context;

    private Boolean isHideLable;
    private Boolean isHideLableOnRemport;
    private Boolean isLocalDictonery;
    private Boolean isWrapper;
    private Boolean isCalculative;
    private Boolean isImageResize;
    private Boolean isBold;

    private Long createdBy;
    private Long updatedBy;
    private String createdAt;
    private String updatedAt;

    public Long getParameterId() {
        return parameterId;
    }

    public void setParameterId(Long parameterId) {
        this.parameterId = parameterId;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public String getDefaultVlue() {
        return defaultVlue;
    }

    public void setDefaultVlue(String defaultVlue) {
        this.defaultVlue = defaultVlue;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public BigDecimal getUpperRange() {
        return upperRange;
    }

    public void setUpperRange(BigDecimal upperRange) {
        this.upperRange = upperRange;
    }

    public BigDecimal getLowerRange() {
        return lowerRange;
    }

    public void setLowerRange(BigDecimal lowerRange) {
        this.lowerRange = lowerRange;
    }

    public Double getExtrimUpperRange() {
        return extrimUpperRange;
    }

    public void setExtrimUpperRange(Double extrimUpperRange) {
        this.extrimUpperRange = extrimUpperRange;
    }

    public Double getExtrimLowerRange() {
        return extrimLowerRange;
    }

    public void setExtrimLowerRange(Double extrimLowerRange) {
        this.extrimLowerRange = extrimLowerRange;
    }

    public Double getLowerAgeRange() {
        return lowerAgeRange;
    }

    public void setLowerAgeRange(Double lowerAgeRange) {
        this.lowerAgeRange = lowerAgeRange;
    }

    public Double getUpperAgeRange() {
        return upperAgeRange;
    }

    public void setUpperAgeRange(Double upperAgeRange) {
        this.upperAgeRange = upperAgeRange;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Boolean getIsHideLable() {
        return isHideLable;
    }

    public void setIsHideLable(Boolean isHideLable) {
        this.isHideLable = isHideLable;
    }

    public Boolean getIsHideLableOnRemport() {
        return isHideLableOnRemport;
    }

    public void setIsHideLableOnRemport(Boolean isHideLableOnRemport) {
        this.isHideLableOnRemport = isHideLableOnRemport;
    }

    public Boolean getIsLocalDictonery() {
        return isLocalDictonery;
    }

    public void setIsLocalDictonery(Boolean isLocalDictonery) {
        this.isLocalDictonery = isLocalDictonery;
    }

    public Boolean getIsWrapper() {
        return isWrapper;
    }

    public void setIsWrapper(Boolean isWrapper) {
        this.isWrapper = isWrapper;
    }

    public Boolean getIsCalculative() {
        return isCalculative;
    }

    public void setIsCalculative(Boolean isCalculative) {
        this.isCalculative = isCalculative;
    }

    public Boolean getIsImageResize() {
        return isImageResize;
    }

    public void setIsImageResize(Boolean isImageResize) {
        this.isImageResize = isImageResize;
    }

    public Boolean getIsBold() {
        return isBold;
    }

    public void setIsBold(Boolean isBold) {
        this.isBold = isBold;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "ParameterMaster [parameterId=" + parameterId + ", parameterName=" + parameterName + ", code=" + code
				+ ", value=" + value + ", sequence=" + sequence + ", dataType=" + dataType + ", unit=" + unit
				+ ", criteria=" + criteria + ", defaultVlue=" + defaultVlue + ", formula=" + formula + ", upperRange="
				+ upperRange + ", lowerRange=" + lowerRange + ", extrimUpperRange=" + extrimUpperRange
				+ ", extrimLowerRange=" + extrimLowerRange + ", lowerAgeRange=" + lowerAgeRange + ", upperAgeRange="
				+ upperAgeRange + ", method=" + method + ", context=" + context + ", isHideLable=" + isHideLable
				+ ", isHideLableOnRemport=" + isHideLableOnRemport + ", isLocalDictonery=" + isLocalDictonery
				+ ", isWrapper=" + isWrapper + ", isCalculative=" + isCalculative + ", isImageResize=" + isImageResize
				+ ", isBold=" + isBold + ", createdBy=" + createdBy + ", updatedBy=" + updatedBy + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + "]";
	}
}
