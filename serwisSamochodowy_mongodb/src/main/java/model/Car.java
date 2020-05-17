package model;

import java.util.Date;

public class Car {
    private  String mark;
    private  String model;
    private  Date productionDate;

    public Car(String mark, String model, Date productionDate) {
        this.mark=mark;
        this.model=model;
        this.productionDate=productionDate;
    }

    public Car() {
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark=mark;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model=model;
    }

    public Date getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(Date productionDate) {
        this.productionDate=productionDate;
    }

    @Override
    public String toString() {
        return "Car{" +
                "mark='" + mark + '\'' +
                ", model='" + model + '\'' +
                ", date=" + productionDate +
                '}';
    }
}
