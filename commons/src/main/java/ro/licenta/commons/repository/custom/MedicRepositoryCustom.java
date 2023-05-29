package ro.licenta.commons.repository.custom;

import reactor.core.publisher.Flux;
import ro.licenta.commons.domain.Medic;

public interface MedicRepositoryCustom {

	public Flux<Medic> findAllWithDetails();
	
}
