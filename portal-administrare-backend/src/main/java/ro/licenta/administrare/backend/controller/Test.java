//package ro.licenta.administrare.backend.controller;
//
//import java.io.File;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import java.util.Set;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.stream.IntStream;
//
//import javax.annotation.PostConstruct;
//
//import org.bson.Document;
//import org.bson.types.ObjectId;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import reactor.core.publisher.Mono;
//import reactor.core.scheduler.Scheduler;
//import reactor.core.scheduler.Schedulers;
//import ro.licenta.commons.domain.Account;
//import ro.licenta.commons.domain.Address;
//import ro.licenta.commons.domain.Clinic;
//import ro.licenta.commons.domain.Department;
//import ro.licenta.commons.domain.Investigation;
//import ro.licenta.commons.domain.InvestigationType;
//import ro.licenta.commons.domain.Medic;
//import ro.licenta.commons.domain.Profile;
//import ro.licenta.commons.domain.Roles;
//import ro.licenta.commons.domain.Subscription;
//import ro.licenta.commons.domain.SubscriptionBenefit;
//import ro.licenta.commons.repository.AccountRepository;
//import ro.licenta.commons.repository.ClinicRepository;
//import ro.licenta.commons.repository.DepartmentRepository;
//import ro.licenta.commons.repository.InvestigationRepository;
//import ro.licenta.commons.repository.InvestigationTypeRepository;
//import ro.licenta.commons.repository.MedicRepository;
//import ro.licenta.commons.repository.ProfileRepository;
//import ro.licenta.commons.repository.SubscriptionRepository;
//
//@Component
//public class Test {
//
//	@Autowired
//	private ReactiveMongoTemplate mongodb;
//	
//	@Autowired
//	private ObjectMapper objectMapper;
//	@Autowired
//	private InvestigationRepository investigationRepository;
//	@Autowired
//	private InvestigationTypeRepository investigationTypeRepository;
//	@Autowired
//	private DepartmentRepository departmentRepository;
//	@Autowired
//	private AccountRepository accountRepository;
//	@Autowired
//	private MedicRepository medicRepository;
//	@Autowired
//	private ProfileRepository profileRepository;
//	@Autowired
//	private PasswordEncoder passwordEncoder;
//	@Autowired
//	private ClinicRepository clinicRepository;
//	@Autowired
//	private SubscriptionRepository subscriptionRepository;
//	
////	@PostConstruct
//	public void postConstruct() throws Exception {
//		LocalDateTime start = LocalDateTime.now();
//		System.out.println("start");
//		List<InvestigationType> investigationTypes = new ArrayList<>();
//		List<Investigation> investigations = new ArrayList<>();
//		Map<String, Department> departments = new HashMap<>();
//		List<Clinic> clinics = new ArrayList<>();
//		List<Medic> medics = new ArrayList<>();
//		List<Account> acccounts = new ArrayList<>();
//		List<Profile> profiles = new ArrayList<>();
//		List<Subscription> subscriptions = new ArrayList<>();
//		Department analize = new Department("ANALIZE LABORATOR");
//		departments.put("ANALIZE LABORATOR", analize);
//		List<Document> clinicDocuments = objectMapper.readerFor(new TypeReference<List<Document>>() { }).readValue(new File("D:\\logs\\clinics.json"));
//		clinicDocuments.subList(0, 2).forEach(d -> {
//			Clinic clinic = new Clinic();
//			clinic.setId(new ObjectId());
//			clinic.setName(d.getString("name"));
//			clinic.setPhoneNumber(d.getString("ein"));
//			clinic.setAddress(new Address());
//			clinic.getAddress().setCity(d.getString("city"));
//			clinic.getAddress().setCounty(d.getString("county"));
//			clinic.getAddress().setStreet(d.getString("street_address"));
//			clinics.add(clinic);
//			
//		});
//
//		Random random = new Random();
//		String password = passwordEncoder.encode("admin");
//		AtomicInteger integer = new AtomicInteger(1);
//		List<Document> medicDocuments = objectMapper.readerFor(new TypeReference<List<Document>>() { }).readValue(new File("D:\\logs\\medics.json"));
//		medicDocuments.forEach(d -> {
//			ObjectId id = new ObjectId();
//			Medic medic = new Medic();
//			medic.setId(id);
//			medic.setTitle(d.getString("Titlu"));
//			medic.setGrade(d.getString("GradProfesional"));
//			medic.getDepartments().add(analize.getName());
//			Arrays.asList(d.getString("Specialitati").split(","))
//				.forEach(s -> {
//					Department department = new Department(s);
//					departments.put(s, department);
//					medic.getDepartments().add(s);
//				});
//			
//			int clinicIndex = random.nextInt(0, clinics.size());
//			medic.setClinic(clinics.get(clinicIndex).getId());
//			
//			medics.add(medic);
//			Account account = new Account();
//			account.setId(id);
//			account.setEmail("medic"+integer.getAndIncrement()+"@test.com");
//			account.setPassword(password);
//			account.setCreateDate(LocalDate.now());
//			account.getRoles().add(Roles.ROLE_MEDIC);
//			acccounts.add(account);
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
//			profiles.add(profile);
//		});
//		
//		List<Document> investigationDocuments = objectMapper.readerFor(new TypeReference<List<Document>>() { }).readValue(new File("D:\\logs\\investigations.json"));
//		investigationDocuments.forEach(d -> {
//			if (d.getDouble("MinPret").intValue() > 0 && departments.containsKey(d.getString("Specialitate"))) {
//				Investigation investigation = new Investigation();
//				investigation.setId(new ObjectId());
//				investigation.setName(d.getString("Denumire"));
//				investigation.setPrice(d.getDouble("MinPret").floatValue());
//				investigation.setType(d.getString("Tip"));
//				investigation.setDepartment(d.getString("Specialitate"));
//				clinics.forEach(c -> investigation.getClinics().add(c.getId()));
//				investigations.add(investigation);
//				
//				InvestigationType type = new InvestigationType();
//				type.setName(investigation.getType());
//				if (!investigationTypes.contains(type)) {
//					investigationTypes.add(type);
//				}
//				
//				Department department = new Department();
//				department.setName(investigation.getDepartment());
//				departments.put(department.getName(), department);
//			}
//		});
//		List<Document> subscriptionDocuments = objectMapper.readerFor(new TypeReference<List<Document>>() { }).readValue(new File("D:\\logs\\subscriptions.json"));
//		int investigationsSize = investigations.size() / subscriptionDocuments.size();
//		Set<Integer> consumed = new HashSet<>();
//		subscriptionDocuments.forEach(d -> {
//			Subscription subscription = new Subscription();
//			subscription.setName(d.getString("Name"));
//			subscription.setPrice(Float.valueOf(d.getString("PriceValue")));
//			subscription.setDescription(d.getString("Description"));
//			while(subscription.getBenefits().size() < investigationsSize) {
//				int index = random.nextInt(0, investigations.size());
//				while(consumed.contains(index)) {
//					index = random.nextInt(0, investigations.size());
//				}
//				SubscriptionBenefit benefit = new SubscriptionBenefit();
//				benefit.setInvestigation(investigations.get(index).getId());
//				benefit.setInvestigationData(investigations.get(index));
//				benefit.setDiscount(random.nextInt(10, 80)/10.0f);
//				subscription.getBenefits().add(benefit);
//				consumed.add(index);
//			}
//			subscriptions.add(subscription);
//		});
//		String[] cnps = new String[] {
//			"1790821302430", "2831126433226", "1910607434997"
//		};
//		IntStream.range(0, 3).forEach(i -> {
//			ObjectId id = new ObjectId();
//			Account account = new Account();
//			account.setId(id);
//			account.setEmail("patient"+i+"@test.com");
//			account.setPassword(password);
//			account.setCreateDate(LocalDate.now());
//			account.getRoles().add(Roles.ROLE_CLIENT);
//			acccounts.add(account);
//			
//			Profile profile = new Profile();
//			profile.setId(id);
//			profile.setFirstName("Patient");
//			profile.setLastName("0000"+i);
//			profile.setUser(id);
//			profile.setAddress(new Address());
//			profile.setSubscription(subscriptions.get(i));
//			profile.setSubscriptionDate(LocalDateTime.now());
//			profile.setSubscriptionExpirationDate(profile.getSubscriptionDate().plusMonths((i+1) * 3));//3,6,9
//			String cnp = cnps[i];
//			String male = "135";
//			String female = "246";
//			if (male.contains("" + cnp.charAt(0))) {
//				profile.setGender("Male");
//			} else if (female.contains("" + cnp.charAt(0))) {
//				profile.setGender("Female");
//			}
//			
//			LocalDate dateOfBirth = LocalDate.of(
//				Integer.parseInt(cnp.substring(1, 3)), // year
//				Integer.parseInt(cnp.substring(3, 5)), // month
//				Integer.parseInt(cnp.substring(5, 7)) // day
//			);
//			long age = ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());
//			profile.setAge(age);
//			profile.setDateOfBirth(dateOfBirth);
//			profile.setCnp(cnp);
//			
//			profiles.add(profile);
//		});
//		
//		ObjectId id = new ObjectId();
//		Account account = new Account();
//		account.setId(id);
//		account.setEmail("admin@test.com");
//		account.setPassword(password);
//		account.setCreateDate(LocalDate.now());
//		account.getRoles().add(Roles.ROLE_ADMIN);
//		acccounts.add(account);
//		
//		Profile profile = new Profile();
//		profile.setId(id);
//		profile.setFirstName("Admin");
//		profile.setLastName("0000");
//		profile.setUser(id);
//		profile.setAddress(new Address());
//		profiles.add(profile);
//		
//		System.out.println("Done");
//		System.out.println(ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
//		List<Mono<?>> monos = new ArrayList<>();
//		investigationTypes.stream().map(investigationTypeRepository::save).forEach(monos::add);
//		investigations.stream().map(investigationRepository::save).forEach(monos::add);
//		departments.values().stream().map(departmentRepository::save).forEach(monos::add);
//		clinics.stream().map(clinicRepository::save).forEach(monos::add);
//		medics.stream().map(medicRepository::save).forEach(monos::add);
//		acccounts.stream().map(accountRepository::save).forEach(monos::add);
//		profiles.stream().map(profileRepository::save).forEach(monos::add);
//		subscriptions.stream().map(subscriptionRepository::save).forEach(monos::add);
//		Mono.when(monos)
//			.subscribeOn(Schedulers.boundedElastic())
//			.publishOn(Schedulers.boundedElastic())
//			.doOnSuccess(v -> System.out.println(ChronoUnit.MILLIS.between(start, LocalDateTime.now())))
//			.subscribe();
//	}
//	
//}
