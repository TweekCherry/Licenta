package ro.licenta.commons.domain;

import org.bson.types.ObjectId;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class SubscriptionBenefit {

	private ObjectId investigation;
	private Float discount;
	private Investigation investigationData;
	
}
