package ro.licenta.commons.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import ro.licenta.commons.domain.Department;

@Repository
public interface DepartmentRepository extends ReactiveMongoRepository<Department, String> {

}
