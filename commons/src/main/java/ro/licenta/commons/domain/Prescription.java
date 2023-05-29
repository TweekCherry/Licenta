package ro.licenta.commons.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Prescription {

	private List<Drug> drugs = new ArrayList<>();
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class Drug {
		private String name;
		private String doze;
	}
	
}
