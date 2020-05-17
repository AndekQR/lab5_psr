import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.*;
import com.mongodb.client.model.Updates;
import model.Service;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;

import static com.mongodb.client.model.Filters.*;

public class MongoService {

    private static final String SERVICE_COLLECTION_NAME="service";

    private final MongoDatabase db;
    private final MongoClient mongoClient;

    public MongoService(String dbName, String userName, String pwd) {
        this.mongoClient=this.getMongoClient(dbName, userName, pwd);
        this.db=mongoClient.getDatabase(dbName);
    }

    private MongoClient getMongoClient(String dbName, String userName, String pwd) {
        MongoCredential credential=MongoCredential.createCredential(userName, dbName, pwd.toCharArray());
        CodecRegistry pojoCodecRegistry=CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClientSettings mongoClientSettings=MongoClientSettings.builder()
                .credential(credential)
                .codecRegistry(pojoCodecRegistry)
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    private <T> MongoCollection<T> getServiceCollection(Class<T> tClass) {
        return db.getCollection(SERVICE_COLLECTION_NAME, tClass);
    }

    public void close() {
        this.mongoClient.close();
    }

    public void insertService(Service service) {
        this.getServiceCollection(Service.class).insertOne(service);
    }

    public ArrayList<Document> getAllServices(int limit) {
        return iterableToArray(this.getServiceCollection(Document.class).find().limit(limit));
    }

    public long removeService(String id) {
        return this.getServiceCollection(Service.class).deleteOne(new Document("_id", new ObjectId(id))).getDeletedCount();
    }

    public Service getService(String id) {
        return this.getServiceCollection(Service.class).find(new Document("_id", new ObjectId(id))).first();
    }

    public ArrayList<Service> findByDates(Date from, Date to) {
        FindIterable<Service> services=this.getServiceCollection(Service.class).find(and(gt("serviceDate", from), lt("serviceDate", to)));
        return iterableToArray(services);
    }

    private <T> ArrayList<T> iterableToArray(FindIterable<T> iterable) {
        ArrayList<T> services=new ArrayList<>();
        MongoCursor<T> cursor=iterable.cursor();
        while (cursor.hasNext()) {
            services.add(cursor.next());
        }
        return services;
    }

    public long updateCarModel(String serviceId, String newModel) {
        return this.getServiceCollection(Service.class).updateOne(
                eq("_id", new ObjectId(serviceId)),
                new Document("$set", new Document("car.model", newModel))
        ).getModifiedCount();
    }

    public long incrementWorkersAge() {
        return this.getServiceCollection(Service.class).updateMany(
                exists("workers"),
                Updates.inc("workers.$[].age", 1))
                .getModifiedCount();
    }
}
