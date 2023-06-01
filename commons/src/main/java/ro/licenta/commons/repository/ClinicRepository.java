package ro.licenta.commons.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import ro.licenta.commons.domain.Clinic;

@Repository
public interface ClinicRepository extends ReactiveMongoRepository<Clinic, ObjectId> {
	

}