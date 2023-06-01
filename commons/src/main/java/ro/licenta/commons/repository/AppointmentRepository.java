package ro.licenta.commons.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import ro.licenta.commons.domain.Appointment;
import ro.licenta.commons.repository.custom.AppointmentRepositoryCustom;

@Repository
public interface AppointmentRepository extends ReactiveMongoRepository<Appointment, ObjectId>, AppointmentRepositoryCustom {

	public Flux<Appointment> findByUser(ObjectId user);
	
}
