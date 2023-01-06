package MoThorApplication.Models;

public class EmployeeClientListModel {
    Integer clientID;
    String clientFirstName;
    String clientLastName;
    String clientDrivingLicense;
    String clientPhoneNumber;
    String clientEmailAddress;
    Integer previousOrders;
    Integer verified;
    String additionalInfo;

    public EmployeeClientListModel(Integer clientID, String clientFirstName, String clientLastName, String clientDrivingLicense, String clientPhoneNumber, String clientEmailAddress, Integer previousOrders, Integer verified, String additionalInfo) {
        this.clientID = clientID;
        this.clientFirstName = clientFirstName;
        this.clientLastName = clientLastName;
        this.clientDrivingLicense = clientDrivingLicense;
        this.clientPhoneNumber = clientPhoneNumber;
        this.clientEmailAddress = clientEmailAddress;
        this.previousOrders = previousOrders;
        this.verified = verified;
        this.additionalInfo = additionalInfo;
    }

    public Integer getClientID() {
        return clientID;
    }

    public void setClientID(Integer clientID) {
        this.clientID = clientID;
    }

    public String getClientFirstName() {
        return clientFirstName;
    }

    public void setClientFirstName(String clientFirstName) {
        this.clientFirstName = clientFirstName;
    }

    public String getClientLastName() {
        return clientLastName;
    }

    public void setClientLastName(String clientLastName) {
        this.clientLastName = clientLastName;
    }

    public String getClientDrivingLicense() {
        return clientDrivingLicense;
    }

    public void setClientDrivingLicense(String clientDrivingLicense) {
        this.clientDrivingLicense = clientDrivingLicense;
    }

    public String getClientPhoneNumber() {
        return clientPhoneNumber;
    }

    public void setClientPhoneNumber(String clientPhoneNumber) {
        this.clientPhoneNumber = clientPhoneNumber;
    }

    public String getClientEmailAddress() {
        return clientEmailAddress;
    }

    public void setClientEmailAddress(String clientEmailAddress) {
        this.clientEmailAddress = clientEmailAddress;
    }

    public Integer getPreviousOrders() {
        return previousOrders;
    }

    public void setPreviousOrders(Integer previousOrders) {
        this.previousOrders = previousOrders;
    }

    public Integer getVerified() {
        return verified;
    }

    public void setVerified(Integer verified) {
        this.verified = verified;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}