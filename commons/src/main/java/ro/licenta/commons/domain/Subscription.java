package ro.licenta.commons.domain;

import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Document(Subscription.KEY_SPACE)
public class Subscription {
	
	public static final String KEY_SPACE = "subscriptions";
	
	@Id
	private ObjectId id;
	private String name;
	private String description;
	private Set<SubscriptionBenefit> benefits = new HashSet<>();
	private Float price;
	private String image;
}
