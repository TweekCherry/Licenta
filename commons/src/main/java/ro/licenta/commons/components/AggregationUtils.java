package ro.licenta.commons.components;

import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;

public class AggregationUtils {

	public static AggregationOperation stringArrayToObjectIdArray(String inputField, String outputField) {
		return TypedAggregation.addFields()
				.addFieldWithValueOf(outputField, 
						new Document("$map", new Document()
								.append("input", "$"+inputField)
								.append("in", 
									new Document("id", 
										new Document("$toObjectId", "$$this")
									)
								)
					)).build();
	}

	public static AggregationOperation objectArrayToObjectIdArray(String inputField, String outputField) {
		return TypedAggregation.addFields()
				.addFieldWithValueOf(outputField, 
						new Document("$map", new Document()
								.append("input", "$"+inputField)
								.append("in", 
									new Document("id", 
										new Document("$toObjectId", "$$this")
									)
								)
					)).build();
	}
	
}
