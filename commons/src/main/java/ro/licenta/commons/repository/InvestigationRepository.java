package ro.licenta.commons.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import ro.licenta.commons.domain.Investigation;

@Repository
public interface InvestigationRepository extends ReactiveMongoRepository<Investigation, String> {

}
