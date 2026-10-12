package com.hms.repository;

import com.hms.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByAppointmentCodeAndDeletedFalse(
            String appointmentCode);

    boolean existsByAppointmentCodeAndDeletedFalse(
            String appointmentCode);

    Optional<Appointment> findByIdAndDeletedFalse(Long id);

    Page<Appointment> findAllByDeletedFalse(Pageable pageable);
}