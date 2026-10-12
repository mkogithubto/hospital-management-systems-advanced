package com.hms.repository;

import com.hms.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByPatientCode(String patientCode);

    Optional<Patient> findByPhone(String phone);

    boolean existsByPhone(String phone);

    boolean existsByPatientCode(String patientCode);

}