package MoThorApplication.Models;

public class EmployeeClientViewModel {
    Integer clientID;
    String ClientDrivingLicense;
    String PreviousOrders;
    int Verified;

    public EmployeeClientViewModel(Integer clientID, String clientDrivingLicense, String previousOrders, int verified) {
        this.clientID = clientID;
        ClientDrivingLicense = clientDrivingLicense;
        PreviousOrders = previousOrders;
        Verified = verified;
    }

    public Integer getClientID() {
        return clientID;
    }

    public void setClientID(Integer clientID) {
        this.clientID = clientID;
    }

    public String getClientDrivingLicense() {
        return ClientDrivingLicense;
    }

    public void setClientDrivingLicense(String clientDrivingLicense) {
        ClientDrivingLicense = clientDrivingLicense;
    }

    public String getPreviousOrders() {
        return PreviousOrders;
    }

    public void setPreviousOrders(String previousOrders) {
        PreviousOrders = previousOrders;
    }

    public int getVerified() {
        return Verified;
    }

    public void setVerified(int verified) {
        Verified = verified;
    }
}
