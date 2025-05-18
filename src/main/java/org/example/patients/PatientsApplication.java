package org.example.patients;

import org.example.patients.entities.Patient;
import org.example.patients.repositories.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class PatientsApplication {
    public static void main(String[] args) {
        SpringApplication.run(PatientsApplication.class, args);
    }

    @Bean
    CommandLineRunner start(PatientRepository patientRepository) {
        return args -> {
            patientRepository.save(new Patient(null, "Yasmine", "Berrada",new Date(), true, 120));
            patientRepository.save(new Patient(null, "Hamid", "Zahraoui",new Date(), false, 80));
            patientRepository.save(new Patient(null, "Sanae", "Abid",new Date(), true, 100));
        };
    }
}
