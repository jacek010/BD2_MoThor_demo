package MoThorApplication.Models;

public class EmployeeCarListModel{
    Integer carID;
    String carModelName;
    String manufacturerName;
    String carTypeName;
    String color;
    Integer enginePower;
    Float dailyLendingPrice;
    Integer maintenance;
    Integer active;
    String additionalInfo;

    public EmployeeCarListModel(Integer carID, String carModelName, String manufacturerName, String carTypeName, String color, Integer enginePower, Float dailyLendingPrice, Integer maintenance, Integer active, String additionalInfo) {
        this.carID=carID;
        this.carModelName = carModelName;
        this.manufacturerName = manufacturerName;
        this.carTypeName = carTypeName;
        this.color = color;
        this.enginePower = enginePower;
        this.dailyLendingPrice = dailyLendingPrice;
        this.maintenance = maintenance;
        this.active = active;
        this.additionalInfo = additionalInfo;
    }

    public Integer getCarID() {
        return carID;
    }

    public void setCarID(Integer carID) {
        this.carID = carID;
    }

    public String getCarModelName() {
        return carModelName;
    }

    public void setCarModelName(String carModelName) {
        this.carModelName = carModelName;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getCarTypeName() {
        return carTypeName;
    }

    public void setCarTypeName(String carTypeName) {
        this.carTypeName = carTypeName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getEnginePower() {
        return enginePower;
    }

    public void setEnginePower(Integer enginePower) {
        this.enginePower = enginePower;
    }

    public Float getDailyLendingPrice() {
        return dailyLendingPrice;
    }

    public void setDailyLendingPrice(Float dailyLendingPrice) {
        this.dailyLendingPrice = dailyLendingPrice;
    }

    public Integer getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(Integer maintenance) {
        this.maintenance = maintenance;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

}
