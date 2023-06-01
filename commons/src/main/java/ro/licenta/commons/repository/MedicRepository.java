package ro.licenta.commons.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import ro.licenta.commons.domain.Medic;
import ro.licenta.commons.repository.custom.MedicRepositoryCustom;

@Repository
public interface MedicRepository extends ReactiveMongoRepository<Medic, ObjectId>, MedicRepositoryCustom {

}