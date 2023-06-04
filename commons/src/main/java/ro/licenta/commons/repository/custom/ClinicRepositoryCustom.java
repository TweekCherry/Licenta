package ro.licenta.commons.repository.custom;

import java.util.List;

import org.bson.types.ObjectId;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Clinic;

public interface ClinicRepositoryCustom {

	Mono<List<Clinic>> findByInvestigationId(ObjectId id);
	

}