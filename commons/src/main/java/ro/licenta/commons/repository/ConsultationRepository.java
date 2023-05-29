package ro.licenta.commons.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import ro.licenta.commons.domain.Consultation;

@Repository
public interface ConsultationRepository extends ReactiveMongoRepository<Consultation, String> {

	public Flux<Consultation> findByUser(String user);
	
	public Flux<Consultation> findByMedicId(String id);
	
}
