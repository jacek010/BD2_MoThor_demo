package MoThorApplication.Models;

public class ClientCarListModel {

    Integer carID;
    String carModelName;
    String manufacturerName;
    String carTypeName;
    String color;
    Integer enginePower;
    Float dailyLendingPrice;

    public ClientCarListModel(Integer carID, String carModelName, String manufacturerName, String carTypeName, String color, Integer enginePower, Float dailyLendingPrice) {
        this.carID=carID;
        this.carModelName = carModelName;
        this.manufacturerName = manufacturerName;
        this.carTypeName = carTypeName;
        this.color = color;
        this.enginePower = enginePower;
        this.dailyLendingPrice = dailyLendingPrice;
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
}
