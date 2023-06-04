package ro.licenta.commons.repository;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Appointment;
import ro.licenta.commons.domain.Appointment.AppointmentStatus;
import ro.licenta.commons.repository.custom.AppointmentRepositoryCustom;

@Repository
public interface AppointmentRepository extends ReactiveMongoRepository<Appointment, ObjectId>, AppointmentRepositoryCustom {

	public Flux<Appointment> findByUser(ObjectId user);
	
	public Mono<Boolean> existsByMedicAndTimestamp(ObjectId medic, LocalDateTime timestamp);

	public Mono<Void> deleteByIdAndUser(ObjectId id, ObjectId user);

	public Mono<Appointment> findByMedicAndStatus(ObjectId medic, AppointmentStatus status);
	
}
