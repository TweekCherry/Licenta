package ro.licenta.commons.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import ro.licenta.commons.domain.Consultation;

@Repository
public interface ConsultationRepository extends ReactiveMongoRepository<Consultation, ObjectId> {

	public Flux<Consultation> findByUser(ObjectId user);
	
	public Flux<Consultation> findByMedic(ObjectId id);
	
}
