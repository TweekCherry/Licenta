package ro.licenta.commons.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Address {

	private String city;
	private String county;
	private String street;
	private String number;
	private String details;

}
