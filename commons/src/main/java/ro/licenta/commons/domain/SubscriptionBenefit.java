package ro.licenta.commons.domain;

import org.bson.types.ObjectId;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SubscriptionBenefit {

	@EqualsAndHashCode.Include
	private ObjectId investigation;
	private Float discount;
	private Investigation investigationData;
	
}
