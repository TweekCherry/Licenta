package ro.licenta.commons.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import ro.licenta.commons.domain.InvestigationType;

@Repository
public interface InvestigationTypeRepository extends ReactiveMongoRepository<InvestigationType, String> {

}
