package MoThorApplication.Models;

public class EmployeeEmployeesListModel {
    Integer employeeID;
    String employeeFirstName;
    String employeeLastName;
    String employeePhoneNumber;
    String employeeEmailAddress;
    String jobName;
    Integer lentCars;
    String additionalInfo;

    public EmployeeEmployeesListModel(Integer employeeID, String employeeFirstName, String employeeLastName, String employeePhoneNumber, String employeeEmailAddress, String jobName, Integer lentCars, String additionalInfo) {
        this.employeeID = employeeID;
        this.employeeFirstName = employeeFirstName;
        this.employeeLastName = employeeLastName;
        this.employeePhoneNumber = employeePhoneNumber;
        this.employeeEmailAddress = employeeEmailAddress;
        this.jobName = jobName;
        this.lentCars = lentCars;
        this.additionalInfo = additionalInfo;
    }

    public Integer getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(Integer employeeID) {
        this.employeeID = employeeID;
    }

    public String getEmployeeFirstName() {
        return employeeFirstName;
    }

    public void setEmployeeFirstName(String employeeFirstName) {
        this.employeeFirstName = employeeFirstName;
    }

    public String getEmployeeLastName() {
        return employeeLastName;
    }

    public void setEmployeeLastName(String employeeLastName) {
        this.employeeLastName = employeeLastName;
    }

    public String getEmployeePhoneNumber() {
        return employeePhoneNumber;
    }

    public void setEmployeePhoneNumber(String employeePhoneNumber) {
        this.employeePhoneNumber = employeePhoneNumber;
    }

    public String getEmployeeEmailAddress() {
        return employeeEmailAddress;
    }

    public void setEmployeeEmailAddress(String employeeEmailAddress) {
        this.employeeEmailAddress = employeeEmailAddress;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Integer getLentCars() {
        return lentCars;
    }

    public void setLentCars(Integer lentCars) {
        this.lentCars = lentCars;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}