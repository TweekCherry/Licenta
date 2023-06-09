package ro.licenta.commons.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VapidSubscription implements Serializable {
	private static final long serialVersionUID = -7826693760065187505L;
	
	private String endpoint;
	private VapidKey keys;
	private LocalDateTime expirationDate;
}
