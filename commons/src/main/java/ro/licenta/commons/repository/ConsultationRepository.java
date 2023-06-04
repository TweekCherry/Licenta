package ro.licenta.commons.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Appointment.AppointmentStatus;
import ro.licenta.commons.domain.Consultation;

@Repository
public interface ConsultationRepository extends ReactiveMongoRepository<Consultation, ObjectId> {

	public Flux<Consultation> findByAppointmentUser(ObjectId user);
	
	public Flux<Consultation> findByAppointmentMedic(ObjectId id);
	
	public Mono<Consultation> findByAppointmentMedicAndAppointmentStatus(ObjectId id, AppointmentStatus status);
	
}
