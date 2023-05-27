/**
 * 
 */
package ro.licenta.commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
@EnableTransactionManagement
@EnableReactiveMongoRepositories(basePackages = { "ro.licenta.commons.repository" }, repositoryImplementationPostfix = "CustomImpl")
public class MongodbConfig {
	
	@Bean
	@Primary
	public ReactiveMongoTransactionManager reactiveTransactionManager(ReactiveMongoDatabaseFactory dbFactory) {
	    return new ReactiveMongoTransactionManager(dbFactory);
	}
	
	@Bean
	public TransactionalOperator transactionalOperator(ReactiveTransactionManager trManager) {
		return TransactionalOperator.create(trManager);
	}
	
}
