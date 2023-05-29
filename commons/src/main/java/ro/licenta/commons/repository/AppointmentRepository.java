package ro.licenta.commons.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import ro.licenta.commons.domain.Appointment;

@Repository
public interface AppointmentRepository extends ReactiveMongoRepository<Appointment, String> {

	public Flux<Appointment> findByUser(String user);
	
}
