package com.hms.repository;

import com.hms.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByDoctorCode(String doctorCode);

    Optional<Doctor> findByPhone(String phone);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByDoctorCode(String doctorCode);
}