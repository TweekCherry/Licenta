/**
 * 
 */
package ro.licenta.commons.config;

import java.io.IOException;

import org.bson.types.ObjectId;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Configuration
@EnableTransactionManagement
@EnableReactiveMongoRepositories(basePackages = { "ro.licenta.commons.repository" }, repositoryImplementationPostfix = "CustomImpl")
public class MongodbConfig implements Jackson2ObjectMapperBuilderCustomizer  {
	
	@Bean
	@Primary
	public ReactiveMongoTransactionManager reactiveTransactionManager(ReactiveMongoDatabaseFactory dbFactory) {
	    return new ReactiveMongoTransactionManager(dbFactory);
	}
	
	@Bean
	public TransactionalOperator transactionalOperator(ReactiveTransactionManager trManager) {
		return TransactionalOperator.create(trManager);
	}
	
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		SimpleModule simpleModule = new SimpleModule("ObjectIdSerializer");
		simpleModule.addSerializer(ObjectId.class, new ObjectIdSerializer());
		simpleModule.addDeserializer(ObjectId.class, new ObjectIdDeserializer());
		mapper.registerModule(simpleModule);
		return mapper;
	}

	@Override
	public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
		jacksonObjectMapperBuilder.failOnEmptyBeans(false)
			.serializerByType(ObjectId.class, new ObjectIdSerializer())
			.deserializerByType(ObjectId.class, new ObjectIdDeserializer());
		
	}
	
	public static class ObjectIdSerializer extends JsonSerializer<ObjectId> {

		@Override
		public void serialize(ObjectId value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			gen.writeString(value.toHexString());
		}
		
	}
	
	public static class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {

		@Override
		public ObjectId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
			return new ObjectId(p.getText());
		}

		
	}
}
