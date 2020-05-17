package model;

import java.util.Date;
import java.util.List;

public class Service {
    private List<Worker> workers;
    private Car car;
    private Date serviceDate;

    public Service(List<Worker> workers, Car car, Date serviceDate) {
        this.workers=workers;
        this.car=car;
        this.serviceDate=serviceDate;
    }

    public Service() {
    }

    public List<Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(List<Worker> workers) {
        this.workers=workers;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car=car;
    }

    public Date getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(Date serviceDate) {
        this.serviceDate=serviceDate;
    }

    @Override
    public String toString() {
        return "Service{" +
                "workers=" + workers +
                ", car=" + car +
                ", date=" + serviceDate +
                '}';
    }
}
