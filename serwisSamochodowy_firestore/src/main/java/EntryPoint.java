import model.Car;
import model.Service;

import java.io.IOException;
import java.util.*;

public class EntryPoint {
    public static void main(String[] args) {
        FireStoreService service;
        try {
            service = new FireStoreService();
        } catch (IOException e) {
            System.out.println("Nieudana inicjalizacja firestore: "+e.getMessage());
            return;
        }

        Scanner scanner=new Scanner(System.in);

        int choose;
        while (true) {
            System.out.println(
                    "[1] - dodaj losowy serwis\n" +
                            "[2] - aktualzuj serwis\n" +
                            "[3] - usuń serwis\n" +
                            "[4] - pobierz serwis po ID\n" +
                            "[5] - Pobierz serwisy w przedziale czasu\n" +
                            "[6] - przetwarzanie\n" +
                            "[7] - pobierz serwisy\n" +
                            "[8] - pobierz pracowników serwisu\n" +
                            "[9] - pobierz serwisowany samochód\n");
            choose=scanner.nextInt();
            scanner.nextLine();
            switch (choose) {
                case 1: {
                    Service carService =ExampleData.randomService();
                    service.saveService(carService);
                    System.out.println("Dodano: "+carService);
                    break;
                }
                case 2: {
                    System.out.println("Podaj id serwisu do aktualizacji: ");
                    String id = scanner.nextLine();
                    System.out.println("Podaj nowy model auta: ");
                    String newModel = scanner.nextLine();
                    service.updateCarModel(id, newModel);
                    break;
                }
                case 3: {
                    System.out.println("Podaj id serwisu do usunięcia: ");
                    String id = scanner.nextLine();
                    service.removeService(id);
                    break;
                }
                case 4: {
                    System.out.println("Podaj id serwisu: ");
                    String id = scanner.nextLine();
                    try {
                        System.out.println(service.getServiceById(id));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case 5: {
                    System.out.println("Od roku: ");
                    int from = scanner.nextInt();
                    System.out.println("Do roku: ");
                    int to = scanner.nextInt();
                    Map<String, Date> servicesInYears=service.findServicesInYears(from, to);
                    servicesInYears.forEach((id, serviceDate) -> System.out.println("ID: "+id+" Serwis z: " + serviceDate));
                    break;
                }
                case 6: {
                    System.out.println("Zwiększanie wieku pracownikow o 1...");
                    service.incrementWorkersAge();
                    break;
                }
                case 7: {
                    Map<String, Date> allServices=service.getAllServices();
                    allServices.forEach((id, serviceDate) -> System.out.println("ID: "+id+" Serwis z: " + serviceDate));
                    break;
                }
                case 8: {
                    System.out.println("Podaj id serwisu: ");
                    String idService = scanner.nextLine();
                    service.getServiceWorkers(idService).forEach(System.out::println);
                    break;
                }
                case 9: {
                    System.out.println("Podaj id serwisu: ");
                    String id = scanner.nextLine();
                    Optional<Car> serviceCar=service.getServiceCar(id);
                    if (serviceCar.isPresent()) {
                        System.out.println(serviceCar.get());
                    } else {
                        System.out.println("Brak samochodu");
                    }
                    break;
                }
                default: {
                    System.out.println("Nieodpowiednia akcja!");
                    break;
                }
            }
        }
    }
}
