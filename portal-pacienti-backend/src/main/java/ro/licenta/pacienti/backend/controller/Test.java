package ro.licenta.pacienti.backend.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import ro.licenta.commons.domain.Clinic;
import ro.licenta.commons.domain.Departament;
import ro.licenta.commons.domain.Grade;
import ro.licenta.commons.domain.Investigation;
import ro.licenta.commons.domain.InvestigationType;

@Component
public class Test {

	@Autowired
	private ReactiveMongoTemplate mongodb;
	
//	@PostConstruct
	public void postConstruct() {
		this.parseAndSaveDepartments();
	}
	
	
	private void parseAndSaveDepartments() {
		String[] departments = new String[] {
			"PSIHOLOGIE",
			"PSIHOTERAPIE",
			"ECOGRAFIE",
			"MEDICINA INTERNA",
			"CARDIOLOGIE",
			"MEDICINA MUNCII COMPETENTA",
			"DIABET ZAHARAT",
			"NUTRITIE SI BOLI METABOLICE",
			"ENDOSCOPIE DIGESTIVA",
			"GASTROENTEROLOGIE",
			"NEUROCHIRURGIE",
			"OBSTETRICA - GINECOLOGIE",
			"PEDIATRIE",
			"HEMATOLOGIE",
			"CARDIOLOGIE PEDIATRICA",
			"ECOGRAFIE MORFOFETALA",
			"OFTALMOLOGIE",
			"CHIRURGIE GENERALA"
		};
		Flux.fromArray(departments)
			.map(Departament::new)
			.flatMap(d -> mongodb.save(d, Departament.KEY_SPACE))
			.subscribe();
		
		String[] grades = new String[] {
			"Psiholog",
			"Medic specialist",
			"Nutritionist",
			"Medic primar",
			"Conferentiar",
			"Doctor in stiinte"
		};
		Flux.fromArray(grades)
			.map(Grade::new)
			.flatMap(d -> mongodb.save(d, Grade.KEY_SPACE))
			.subscribe();
		
		Clinic c1 = new Clinic();
		c1.setName("Spital Cluj");
		c1.setPhoneNumber("0752 500 300");
		c1.getAddress().setCity("Cluj");
		c1.getAddress().setCounty("Cluj");
		c1.getAddress().setStreet("Str. Pictor Theodor Aman");
		c1.getAddress().setNumber("nr. 40");
		
		Clinic c2 = new Clinic();
		c2.setName("Policlinica Centrul Civic Brasov");
		c2.setPhoneNumber("0729 009 370");
		c2.getAddress().setCity("Brasov");
		c2.getAddress().setCounty("Brasov");
		c2.getAddress().setStreet("Str. Mihail Kogalniceanu");
		c2.getAddress().setNumber("nr. 16");
		
		Clinic c3 = new Clinic();
		c3.setName("Policlinica Centrala Craiova");
		c3.setPhoneNumber("021 9268");
		c3.getAddress().setCity("Craiova");
		c3.getAddress().setCounty("Dolj");
		c3.getAddress().setStreet("Str. Paltinis");
		c3.getAddress().setNumber("nr. 1");
		
		Clinic[] clinics = new Clinic[] {c1, c2, c3};
		
		Flux.fromArray(clinics)
		.flatMap(d -> mongodb.save(d, Clinic.KEY_SPACE))
		.subscribe();
		
		String[] investigationTypes = new String[] {
			"BIOCHIMIE",
			"MARKERI ENDOCRINI",
			"Imunologie",
			"ANATOMIE PATOLOGICA",
			"Proceduri medicale",
			"Interventii Chirurgicale"
		};
		Flux.fromArray(investigationTypes)
			.map(InvestigationType::new)
			.flatMap(d -> mongodb.save(d, InvestigationType.KEY_SPACE))
			.subscribe();
		
		Investigation i1 = new Investigation();
		i1.setName("1,25-(OH)2-Vitamina D3");
		i1.setPrice(262.00f);
		i1.setType(investigationTypes[0]);
		
		Investigation i2 = new Investigation();
		i2.setName("17 cetosteroizi (urina)");
		i2.setPrice(170.00f);
		i2.setType(investigationTypes[1]);
		
		Investigation[] investigations = new Investigation[] {i1, i2};
		
		Flux.fromArray(investigations)
		.flatMap(d -> mongodb.save(d, Investigation.KEY_SPACE))
		.subscribe();
	}
}
