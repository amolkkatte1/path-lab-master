package pathlabmaster.pojo;

import java.math.BigDecimal;

public class ParameterDetails {

    private String parameterName;
    private String value;
    private Integer sequence;
    private String dataType;
    private String unit;
    private String formula;
    private BigDecimal upperRange;
    private BigDecimal lowerRange;
    private Boolean isBold;

    // Getters and Setters
	public ParameterDetails(String parameterName, String value, Integer sequence, String dataType, String unit,
			String formula, BigDecimal upperRange, BigDecimal lowerRange, Boolean isBold) {

		this.parameterName = parameterName;
		this.value = value;
		this.sequence = sequence;
		this.dataType = dataType;
		this.unit = unit;
		this.formula = formula;
		this.upperRange = upperRange;
		this.lowerRange = lowerRange;
		this.isBold = isBold;
	}
    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public Boolean getIsBold() {
        return isBold;
    }

    public void setIsBold(Boolean isBold) {
        this.isBold = isBold;
    }
}
