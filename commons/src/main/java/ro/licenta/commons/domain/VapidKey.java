package ro.licenta.commons.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VapidKey implements Serializable {
	private static final long serialVersionUID = 8775118367890852487L;
	
	private String auth;
	private String p256dh;
}
