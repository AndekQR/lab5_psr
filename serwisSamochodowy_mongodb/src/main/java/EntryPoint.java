import model.Service;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class EntryPoint {

    private static final String DB_NAME="serwisSamochodowyDB";
    private static final String USER_NAME="boss";
    private static final String PWD="boss";

    public static void main(String[] args) {
        MongoService mongoService=new MongoService(DB_NAME, USER_NAME, PWD);
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
                            "[8] - wyjście");
            choose=scanner.nextInt();
            scanner.nextLine();
            switch (choose) {
                case 1: {
                    Service service=ExampleData.randomService();
                    mongoService.insertService(service);
                    System.out.println("Dodano: \n" + service);
                    break;
                }
                case 2: {
                    System.out.println("ID serwisu do aktualizacji: ");
                    String idService = scanner.nextLine();
                    System.out.println("Nowy model auta: ");
                    String newMo = scanner.nextLine();
                    System.out.println("Zaktualizowano: "+mongoService.updateCarModel(idService, newMo));
                    break;
                }
                case 3: {
                    System.out.println("ID serwisu do usunięcia: ");
                    String idToDelete=scanner.nextLine();
                    System.out.println("Liczba usuniętych serwisów: " + mongoService.removeService(idToDelete));
                    break;
                }
                case 4: {
                    System.out.println("ID szukanego seriwsu: ");
                    String idToSearch=scanner.nextLine();
                    System.out.println(mongoService.getService(idToSearch));
                    break;
                }
                case 5: {
                    System.out.println("Od roku: ");
                    int from = scanner.nextInt();
                    System.out.println("Do roku: ");
                    int to = scanner.nextInt();
                    Calendar fromC = Calendar.getInstance();
                    fromC.set(from, Calendar.JANUARY, 1, 0, 0);
                    Calendar toC = Calendar.getInstance();
                    toC.set(to, Calendar.DECEMBER, 31, 23, 59);
                    ArrayList<Service> byDates=mongoService.findByDates(new Date(fromC.getTimeInMillis()), new Date(toC.getTimeInMillis()));
                    byDates.forEach(System.out::println);
                    break;
                }
                case 6: {
                    System.out.println("Aktualizacja wieku pracowników...");
                    mongoService.incrementWorkersAge();
                    break;
                }
                case 7: {
                    System.out.println("Ilość serwisów do wyświetlenia: ");
                    int limi=scanner.nextInt();
                    scanner.nextLine();
                    ArrayList<Document> services=mongoService.getAllServices(limi);
                    services.forEach(System.out::println);
                    break;
                }
                case 8: {
                    mongoService.close();
                    return;
                }
                default: {
                    System.out.println("Nieodpowiednia akcja!");
                    break;
                }
            }
        }
    }
}
