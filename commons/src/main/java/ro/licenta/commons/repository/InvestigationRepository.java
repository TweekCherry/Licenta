package ro.licenta.commons.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import ro.licenta.commons.domain.Investigation;
import ro.licenta.commons.repository.custom.InvestigationRepositoryCustom;

@Repository
public interface InvestigationRepository extends ReactiveMongoRepository<Investigation, ObjectId>, InvestigationRepositoryCustom {

}
