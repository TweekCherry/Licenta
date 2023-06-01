package ro.licenta.administrare.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Department;
import ro.licenta.commons.repository.DepartmentRepository;
import ro.licenta.commons.repository.InvestigationRepository;
import ro.licenta.commons.repository.MedicRepository;

@RestController
@RequestMapping("/departments")
public class DepartmentsController extends DefaultController {

	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private InvestigationRepository investigationRepository;
	@Autowired
	private MedicRepository medicRepository;
	
	@GetMapping
	public Mono<List<Department>> findAll() {
		return departmentRepository.findAll().collectList();
	}
	
	@PostMapping
	public Mono<Department> save(@RequestParam(name = "id", required = false) String id, @RequestBody Department department) {
		Mono<UpdateResult> deleteDepartment = Mono.empty();
		if (id != null) {
			deleteDepartment = departmentRepository.deleteById(id)
				.then(investigationRepository.replaceDepartment(id, department.getName()))
				.then(medicRepository.replaceDepartment(id, department.getName()));
		}
		return deleteDepartment
			.then(departmentRepository.save(department));
	}
	
	@DeleteMapping("/{id}")
	public Mono<Void> delete(@PathVariable("id") String id) {
		return departmentRepository.deleteById(id)
			.then(investigationRepository.unlinkDepartment(id))
			.then(medicRepository.unlinkDepartment(id))
			.then();
	}
}
