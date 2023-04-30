package helpdesk_ticketing_system.hts_notifications;

import com.amazonaws.services.lambda.runtime.Context;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;

public class MongoDb {
    private final MongoCollection<Document> mongoIssuesCollection;

    public MongoDb() {
        String connectionUri = System.getenv("mongodb_connection_uri");
        String username = System.getenv("mongodb_username");
        String password = System.getenv("mongodb_password");
        String database = System.getenv("mongodb_database");
        String issuesCollection = System.getenv("mongodb_collection_issues");

        String connectionString = String.format(connectionUri, username, password);
        MongoClient mongoClient = MongoClients.create(
                MongoClientSettings.builder()
                        .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
                        .applyConnectionString(new ConnectionString(connectionString))
                        .build()
        );

        mongoIssuesCollection = mongoClient.getDatabase(database).getCollection(issuesCollection);
    }
    String getSubmitterOfIssue(Object issueId, Context context)
    {
        String submitted_by = null;
        try (MongoCursor<Document> cursor = mongoIssuesCollection
                .find(Filters.eq("_id", issueId))
                .projection(Projections.include("submitted_by"))
                .cursor()) {

            if (cursor.hasNext())
                submitted_by = cursor.next().getString("submitted_by");

        } catch (Exception e) {
            context.getLogger().log("Exception thrown in : " + this.getClass().getName() + "\n");
            context.getLogger().log("Exception class : " + e.getClass().getName() + "\n");
            context.getLogger().log("Exception message : " + e.getMessage() + "\n");
            throw new RuntimeException(e);
        }
        return submitted_by;
    }
}
