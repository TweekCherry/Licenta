package ro.licenta.administrare.backend.controller;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import ro.licenta.commons.domain.Roles;
import ro.licenta.commons.repository.AccountRepository;
import ro.licenta.commons.repository.ClinicRepository;
import ro.licenta.commons.repository.DepartmentRepository;
import ro.licenta.commons.repository.InvestigationRepository;
import ro.licenta.commons.repository.InvestigationTypeRepository;
import ro.licenta.commons.repository.MedicRepository;
import ro.licenta.commons.repository.ProfileRepository;

@Component
public class Test {

	@Autowired
	private ReactiveMongoTemplate mongodb;
	
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private InvestigationRepository investigationRepository;
	@Autowired
	private InvestigationTypeRepository investigationTypeRepository;
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private MedicRepository medicRepository;
	@Autowired
	private ProfileRepository profileRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private ClinicRepository clinicRepository;
	
//	@PostConstruct
	public void postConstruct() throws Exception {
		accountRepository.findAll()
			.filter(a -> a.getRoles().contains(Roles.ROLE_MEDIC))
			.collectList()
			.map(accounts -> {
				return IntStream.range(0, accounts.size()).mapToObj(i -> {
					return accounts.get(i).setEmail("medic"+i+"@test.com");
				}).collect(Collectors.toList());
			}).flatMapMany(Flux::fromIterable)
			.flatMap(accountRepository::save)
			.subscribe();
		
//		clinicRepository.findAll().collectList().flatMapMany(clinics -> {
//			Random random = new Random();
//			return medicRepository.findAll()
//				.flatMap(medic -> {
//					int index = random.nextInt(0, clinics.size());
//					medic.setClinic(clinics.get(index).getId());
//					return medicRepository.save(medic);
//				});
//		}).subscribe();
//		List<Document> medics = objectMapper.readerFor(new TypeReference<List<Document>>() { }).readValue(new File("D:\\logs\\new2.json"));
//		AtomicInteger integer = new AtomicInteger(0);
//		String password = passwordEncoder.encode("admin");
//		Flux.fromIterable(medics)
//		.flatMap(d -> {
//			ObjectId id = new ObjectId();
//			Medic medic = new Medic();
//			medic.setId(id);
//			medic.setTitle(d.getString("Titlu"));
//			medic.setGrade(d.getString("GradProfesional"));
//			Arrays.asList(d.getString("Specialitati").split(","))
//				.forEach(s -> medic.getDepartments().add(s));
//			
//			Account account = new Account();
//			account.setId(id);
//			account.setEmail("medic"+integer.incrementAndGet()+"@test.com");
//			account.setPassword(password);
//			account.setCreateDate(LocalDate.now());
//			account.getRoles().add(Roles.ROLE_MEDIC);
//			
//			String name = d.getString("NumeMedic");
//			String firstName = "";
//			String lastName = "";
//			int index = name.lastIndexOf(' ');
//			if (index < 0) {
//				index = name.length();
//				firstName = name.substring(0, index);
//			} else {
//				firstName = name.substring(0, index);
//				lastName = name.substring(index, name.length());
//			}
//			Profile profile = new Profile();
//			profile.setId(id);
//			profile.setFirstName(firstName);
//			profile.setLastName(lastName);
//			profile.setUser(id);
//			
//			Mono<Void> departmentSaveRequest = Flux.fromIterable(medic.getDepartments())
//				.flatMap(ddep -> departmentRepository.save(new Department(ddep)))
//				.then();
//			
//			Mono<Profile> profileSaveRequest = profileRepository.save(profile);
//			Mono<Account> accountSaveRequest = accountRepository.save(account);
//			Mono<Medic> medicSaveRequest = medicRepository.save(medic);
//			return Mono.when(medicSaveRequest, accountSaveRequest, profileSaveRequest, departmentSaveRequest);
//		}).subscribe();
//		
//		List<Document> documents = objectMapper.readerFor(new TypeReference<List<Document>>() { }).readValue(new File("D:\\logs\\new.json"));
//		Flux.fromIterable(documents)
//			.flatMap(d -> {
//				Investigation investigation = new Investigation();
//				investigation.setName(d.getString("Denumire"));
//				investigation.setPrice(d.getDouble("MinPret").floatValue());
//				investigation.setType(d.getString("Tip"));
//				investigation.setDepartment(d.getString("Specialitate"));
//				Mono<Investigation> investigationSaveRequest = investigationRepository.save(investigation);
//				
//				InvestigationType type = new InvestigationType();
//				type.setName(investigation.getType());
//				Mono<InvestigationType> investigationTypeRequest = investigationTypeRepository.save(type);
//				
//				Department department = new Department();
//				department.setName(investigation.getDepartment());
//				Mono<Department> departmentSaveRequest = departmentRepository.save(department);
//				
//				return Mono.when(investigationSaveRequest, investigationTypeRequest, departmentSaveRequest);
//			}).subscribe();
	}
}
