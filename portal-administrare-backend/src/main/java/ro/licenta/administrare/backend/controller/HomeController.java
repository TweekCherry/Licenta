package ro.licenta.administrare.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Roles;
import ro.licenta.commons.repository.AccountRepository;
import ro.licenta.commons.repository.ClinicRepository;
import ro.licenta.commons.repository.ConsultationRepository;
import ro.licenta.commons.repository.DepartmentRepository;
import ro.licenta.commons.repository.InvestigationRepository;
import ro.licenta.commons.repository.MedicRepository;
import ro.licenta.commons.repository.SubscriptionRepository;

@RestController
public class HomeController extends DefaultController {

	@Autowired
	private InvestigationRepository investigationRepository;
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private ClinicRepository clinicRepository;
	@Autowired
	private MedicRepository medicRepository;
	@Autowired
	private SubscriptionRepository subscriptionRepository;
	@Autowired
	private ConsultationRepository consultationRepository;
	@Autowired
	private AccountRepository accountRepository;
	
	@GetMapping("/stats")
	public Mono<Statistics> getStats() {
		return Mono.zip(
			investigationRepository.count(),
			departmentRepository.count(),
			clinicRepository.count(),
			medicRepository.count(),
			subscriptionRepository.count(),
			consultationRepository.count(),
			accountRepository.countByRoles(Roles.CLIENT)
		).map(data -> {
			Statistics stats = new Statistics();
			stats.setInvestigationsCounter(data.getT1());
			stats.setDepartmentsCounter(data.getT2());
			stats.setClinicsCounter(data.getT3());
			stats.setMedicsCounter(data.getT4());
			stats.setSubscriptionsCounter(data.getT5());
			stats.setConsultationsCounter(data.getT6());
			stats.setPatientsCounter(data.getT7());
			return stats;
		});
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class Statistics {
		private Long investigationsCounter;
		private Long subscriptionsCounter;
		private Long medicsCounter;
		private Long clinicsCounter;
		private Long departmentsCounter;
		private Long consultationsCounter;
		private Long patientsCounter;
	}
}
