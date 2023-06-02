package ro.licenta.commons.repository.custom;

import java.time.LocalDateTime;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Appointment;

public interface AppointmentRepositoryCustom {

	public Flux<Appointment> aggregateAll(ObjectId medic);

	public Mono<UpdateResult> cancelByMedic(ObjectId id);

	public Mono<List<LocalDateTime>> findBookedAppointmentDates(ObjectId clinic, ObjectId medic);

	public Flux<Appointment> aggregateByUser(ObjectId user);

}
