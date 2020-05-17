import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import model.Car;
import model.Service;
import model.Worker;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;


/*
    services --|
            workers --|
            cars    --|
 */
public class FireStoreService {

    private final String SERVICE_COLLECTION_NAME="services";
    private final String WORKERS_COLLECTION_NAME="workers";
    private final String CARS_COLLECTION_NAME="cars";

    private final Firestore db;

    public FireStoreService() throws IOException {
        FileInputStream serviceAccount=
                new FileInputStream("src/main/resources/googleKey.json");

        FirebaseOptions options=new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://serwissamochodowy-64e1e.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
        db=FirestoreClient.getFirestore();
    }

    private CollectionReference getServicesCollection() {
        return db.collection(SERVICE_COLLECTION_NAME);
    }

    private CollectionReference getWorkersCollection(String serviceId) {
        return db.collection(SERVICE_COLLECTION_NAME).document(serviceId).collection(WORKERS_COLLECTION_NAME);
    }

    private CollectionReference getCarsCollection(String serviceId) {
        return db.collection(SERVICE_COLLECTION_NAME).document(serviceId).collection(CARS_COLLECTION_NAME);
    }

    public Map<String, Date> getAllServices() {
        Map<String, Date> result=new HashMap<>();
        try {
            this.getServicesCollection().get().get().getDocuments().forEach(document -> { //drugi get() blokuje
                result.put(document.getId(), document.toObject(Service.class).getServiceDate());
            });
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public void saveService(Service service) {
        ApiFuture<DocumentReference> ref=this.getServicesCollection().add(Map.of("serviceDate", service.getServiceDate()));
        service.getWorkers().forEach(worker -> {
            try {
                this.getWorkersCollection(ref.get().getId()).add(worker);
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Błąd zapsywania pracownika " + e.getMessage());
            }
        });
        try {
            this.getCarsCollection(ref.get().getId()).add(service.getCar());
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Błąd dodawania samochodu " + e.getLocalizedMessage());
        }
    }

    public List<Worker> getServiceWorkers(String serviceId) {
        try {
            return this.getWorkersCollection(serviceId).get().get().toObjects(Worker.class);
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Błąd pobierania pracowników: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public Optional<Car> getServiceCar(String serviceId) {
        try {
            return this.getCarsCollection(serviceId).get().get().toObjects(Car.class).stream().findFirst();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Błąd pobierania pracowników: " + e.getMessage());
        }
        return Optional.empty();
    }

    public void updateCarModel(String serviceId, String newModel) {
        Optional<QueryDocumentSnapshot> car=Optional.empty();
        try {
            car=this.getCarsCollection(serviceId).get().get().getDocuments().stream().findFirst();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Błąd aktualizacji: " + e.getMessage());
        }
        car.ifPresent(queryDocumentSnapshot -> this.getCarsCollection(serviceId)
                .document(queryDocumentSnapshot.getId())
                .update("model", newModel));
    }

    public void removeService(String serviceId) {
        this.getServicesCollection().document(serviceId).delete();
    }

    public Service getServiceById(String serviceId) throws Exception {
        Optional<Car> car=getServiceCar(serviceId);
        List<Worker> serviceWorkers=getServiceWorkers(serviceId);
        try {
            Service service=this.getServicesCollection().document(serviceId).get().get().toObject(Service.class);
            if (service != null) {
                service.setWorkers(serviceWorkers);
                car.ifPresent(service::setCar);
                return service;
            }
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Błąd pobierania serwisu: " + e.getMessage());
        }
        throw new Exception("Brak serwisu o podanym ID");
    }

    public Map<String, Date> findServicesInYears(int from, int to) {
        Calendar fromCalendar=Calendar.getInstance();
        fromCalendar.set(from, Calendar.JANUARY, 1, 0, 0);
        Calendar toCalendar=Calendar.getInstance();
        toCalendar.set(to, Calendar.DECEMBER, 31, 0, 0);

        Map<String, Date> result=new HashMap<>();

        try {
            this.getServicesCollection()
                    .whereGreaterThan("serviceDate", new Date(fromCalendar.getTimeInMillis()))
                    .whereLessThan("serviceDate", new Date(toCalendar.getTimeInMillis()))
                    .get().get().getDocuments().forEach(document -> {
                result.put(document.getId(), document.toObject(Service.class).getServiceDate());
            });
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Błąd filtrowania serwisów: " + e.getMessage());
        }
        return result;
    }

    public void incrementWorkersAge() {
        this.getServicesCollection().listDocuments().iterator().forEachRemaining(documentReference -> {
            getWorkersCollection(documentReference.getId()).listDocuments().forEach(workerDocument -> {
                workerDocument.update("age", FieldValue.increment(1D));
            });
        });
    }
}
