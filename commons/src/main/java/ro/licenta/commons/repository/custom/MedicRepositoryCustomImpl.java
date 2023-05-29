package ro.licenta.commons.repository.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import ro.licenta.commons.domain.Medic;

@Repository
public class MedicRepositoryCustomImpl implements MedicRepositoryCustom {

	@Autowired
	private ReactiveMongoTemplate monogdb;
	
	@Override
	public Flux<Medic> findAllWithDetails() {
		// TODO Auto-generated method stub
		return Flux.empty();
	}

}
