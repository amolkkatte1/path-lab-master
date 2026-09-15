package pathlabmaster.pojo;

public class PatientDashboardResponse {

    private Long totalPatients;
    private Long currentMonthPatients;
    private Long previousMonthPatients;
    private Double monthlyGrowthPercentage;

    public PatientDashboardResponse() {
    }

    public PatientDashboardResponse(
            Long totalPatients,
            Long currentMonthPatients,
            Long previousMonthPatients,
            Double monthlyGrowthPercentage) {

        this.totalPatients = totalPatients;
        this.currentMonthPatients = currentMonthPatients;
        this.previousMonthPatients = previousMonthPatients;
        this.monthlyGrowthPercentage = monthlyGrowthPercentage;
    }

    public Long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(Long totalPatients) {
        this.totalPatients = totalPatients;
    }

    public Long getCurrentMonthPatients() {
        return currentMonthPatients;
    }

    public void setCurrentMonthPatients(Long currentMonthPatients) {
        this.currentMonthPatients = currentMonthPatients;
    }

    public Long getPreviousMonthPatients() {
        return previousMonthPatients;
    }

    public void setPreviousMonthPatients(Long previousMonthPatients) {
        this.previousMonthPatients = previousMonthPatients;
    }

    public Double getMonthlyGrowthPercentage() {
        return monthlyGrowthPercentage;
    }

    public void setMonthlyGrowthPercentage(Double monthlyGrowthPercentage) {
        this.monthlyGrowthPercentage = monthlyGrowthPercentage;
    }
}