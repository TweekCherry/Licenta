package ro.licenta.pacienti.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Department;
import ro.licenta.commons.repository.DepartmentRepository;

@RestController
@RequestMapping("/departments")
public class DepartmentsController extends DefaultController {

	@Autowired
	private DepartmentRepository departmentRepository;
	
	@GetMapping
	public Mono<List<Department>> findAll() {
		return departmentRepository.findAll().collectList();
	}
}
