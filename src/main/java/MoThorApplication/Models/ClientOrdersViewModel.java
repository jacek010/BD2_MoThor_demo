package MoThorApplication.Models;

import java.util.Date;

public class ClientOrdersViewModel {

    Integer orderID;
    Integer employeeID;
    Integer carID;
    String startDate;
    String endDate;
    String orderStatus;
    Float fullLendingPrice;

    public ClientOrdersViewModel(Integer orderID, Integer employeeID, Integer carID, String startDate, String endDate, String orderStatus, Float fullLendingPrice) {
        this.orderID = orderID;
        this.employeeID = employeeID;
        this.carID = carID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.orderStatus = orderStatus;
        this.fullLendingPrice = fullLendingPrice;
    }

    public Integer getOrderID() {
        return orderID;
    }

    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }

    public Integer getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(Integer employeeID) {
        this.employeeID = employeeID;
    }

    public Integer getCarID() {
        return carID;
    }

    public void setCarID(Integer carID) {
        this.carID = carID;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Float getFullLendingPrice() {
        return fullLendingPrice;
    }

    public void setFullLendingPrice(Float fullLendingPrice) {
        this.fullLendingPrice = fullLendingPrice;
    }
}
