import com.github.javafaker.Faker;
import model.Car;
import model.Service;
import model.Worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExampleData {
    public static Service randomService() {
        Faker faker = new Faker();
        int numberOfWorkers = faker.number().numberBetween(1, 5);
        List<Worker> workers = new ArrayList<Worker>();
        for (int i=0; i < numberOfWorkers; i++) {
            workers.add(randomWorker());
        }
        return new Service(workers, randomCar(), faker.date().past(2000, TimeUnit.DAYS));
    }

    public static Worker randomWorker() {
        Faker faker = new Faker();
        return new Worker(faker.name().firstName(), faker.name().lastName(), faker.number().numberBetween(18, 80), faker.number().randomDouble(2, 1500, 4500));
    }

    public static Car randomCar() {
        Faker faker = new Faker();
        return new Car(faker.funnyName().name(), faker.code().isbn13(), faker.date().past(30, TimeUnit.DAYS));
    }
}
