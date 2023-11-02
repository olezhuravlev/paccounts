package org.paccounts.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.paccounts.config.listener.OperationMongoEventListener;
import org.paccounts.repository.HouseholdRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Collection;
import java.util.Collections;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.host:#{null}}")
    private String mongodbHost;

    @Value("${spring.data.mongodb.port:#{null}}")
    private String mongodbPort;

    @Value("${spring.data.mongodb.database:#{null}}")
    private String mongodbDatabaseName;

    @Value("${spring.data.mongodb.authentication-database:#{null}}")
    private String mongodbAuthenticationDatabase;

    @Value("${spring.data.mongodb.uri:#{null}}")
    private String mongodbUri;

    @Value("${spring.data.mongodb.username:#{null}}")
    private String mongodbUsername;

    @Value("${spring.data.mongodb.password:#{null}}")
    private String mongodbPassword;

    @Override
    protected String getDatabaseName() {
        return mongodbDatabaseName;
    }

    @Override
    public boolean autoIndexCreation() {
        return true;
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        ConnectionString connectionString = new ConnectionString(mongodbUri);
        builder.applyConnectionString(connectionString);
    }

    @Override
    public Collection<String> getMappingBasePackages() {
        return Collections.singleton("org.paccounts");
    }

    @Bean
    public OperationMongoEventListener operationSaveMongoEventListener(HouseholdRepo householdRepo) {
        return new OperationMongoEventListener(householdRepo);
    }

    @Override
    public void configureConverters(MongoCustomConversions.MongoConverterConfigurationAdapter adapter) {
        // adapter.registerConverter(new ArticleReadConverter());
        // adapter.registerConverter(new ArticleWriteConverter());
    }
}
