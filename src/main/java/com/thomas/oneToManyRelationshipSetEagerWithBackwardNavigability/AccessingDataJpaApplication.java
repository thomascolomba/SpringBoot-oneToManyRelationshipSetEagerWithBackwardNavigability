package com.thomas.oneToManyRelationshipSetEagerWithBackwardNavigability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import com.thomas.oneToManyRelationshipSetEagerWithBackwardNavigability.domains.A;
import com.thomas.oneToManyRelationshipSetEagerWithBackwardNavigability.domains.B;
import com.thomas.oneToManyRelationshipSetEagerWithBackwardNavigability.repositories.ARepository;
import com.thomas.oneToManyRelationshipSetEagerWithBackwardNavigability.repositories.BRepository;

@SpringBootApplication
@Transactional
public class AccessingDataJpaApplication {

	private static final Logger log = LoggerFactory.getLogger(AccessingDataJpaApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(AccessingDataJpaApplication.class);
	}

	@Bean
	public CommandLineRunner demo(ARepository aRepository, BRepository bRepository) {
		return (args) -> {
			log.info("===== Persisting As and Bs");
			persistData(aRepository, bRepository);
			readData(aRepository, bRepository);
			log.info("===== Modifying some As and Bs");
			modifyData(aRepository, bRepository);
			readData(aRepository, bRepository);
			log.info("===== Deleting some As and Bs");
			deleteData(aRepository, bRepository);
			readData(aRepository, bRepository);
		};
	}
	
	private void readData(ARepository aRepository, BRepository bRepository) {
		Iterable<A> As = aRepository.findAll();
		log.info("===== As");
		for(A a : As) {
			log.info(a.toString());
		}
		
		Iterable<B> Bs = bRepository.findAll();
		log.info("===== Bs");
		for(B b : Bs) {
			log.info(b.toString());
		}
	}
	
	private void persistData(ARepository aRepository, BRepository bRepository) {
		//we build A without nested Bs, we set A to each B
		A a1 = new A("myString1");
		A a2 = new A("myString2");
		aRepository.save(a1);
		aRepository.save(a2);
		bRepository.save(new B(11, a1));
		bRepository.save(new B(12, a1));
		bRepository.save(new B(21, a2));
		bRepository.save(new B(22, a2));
		
		//we can build an A without Bs
		A a3 = new A("myString3");
		aRepository.save(a3);
	}

	private void modifyData(ARepository aRepository, BRepository bRepository) {
		//we change a1.myString and a2.myString and we affect a B previously affect at a1 to a2
		A a1 = aRepository.findByMyString("myString1").get(0);
		A a2 = aRepository.findByMyString("myString2").get(0);
		a1.setMyString("myModifiedString1");
		a2.setMyString("myModifiedString2");
		B b = bRepository.findByMyInt(11).get(0);
		b.setMyInt(-11);
		b.setA(a2);
		aRepository.save(a1);
		aRepository.save(a2);
		bRepository.save(b);
	}
	
	private void deleteData(ARepository aRepository, BRepository bRepository) {
		//we delete 1 A and 1 B related to another A
		A a1 = aRepository.findByMyString("myModifiedString1").get(0);
		aRepository.delete(a1);
		
		//we do not delete the B instance -> we want to remove that B instance from the A's list
		A a2 = aRepository.findByMyString("myModifiedString2").get(0);
		a2.getBSet().removeIf((B b) -> b.getMyInt() == 22);
		aRepository.save(a2);
	}
}
