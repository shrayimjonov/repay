package uz.repay.autodebit.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Value("${spring.data.mongodb.port}")
    private int port;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Value("${spring.data.mongodb.username}")
    private String username;

    @Value("${spring.data.mongodb.password}")
    private String password;

    @Value("${spring.data.mongodb.connection.timeout:5000}")
    private int connectionTimeout;

    @Value("${spring.data.mongodb.socket.timeout:10000}")
    private int socketTimeout;

    @Value("${spring.data.mongodb.server.selection.timeout:5000}")
    private int serverSelectionTimeout;

    @Bean
    public MongoClient mongoClient() {
        try {
            String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
            String encodedPassword = URLEncoder.encode(password, StandardCharsets.UTF_8);

            String mongoUri = String.format("mongodb://%s:%d/%s",
                    encodedUsername, encodedPassword, host, port, database);

            log.info("Configuring MongoDB client with URI: {}", mongoUri.replaceAll(":[^:@]+@", ":****@"));

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(mongoUri))
                    .uuidRepresentation(UuidRepresentation.STANDARD)
                    .applyToSocketSettings(builder ->
                            builder.connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS)
                                   .readTimeout(socketTimeout, TimeUnit.MILLISECONDS))
                    .applyToClusterSettings(builder ->
                            builder.serverSelectionTimeout(serverSelectionTimeout, TimeUnit.MILLISECONDS))
                    .build();

            return MongoClients.create(settings);
        } catch (Exception e) {
            log.error("Failed to configure MongoDB client: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {
        log.info("Creating MongoDB database factory for database: {}", database);
        return new SimpleMongoClientDatabaseFactory(mongoClient, database);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        MongoTemplate template = new MongoTemplate(mongoDatabaseFactory);
        template.setEntityLifecycleEventsEnabled(false);
        return template;
    }
}