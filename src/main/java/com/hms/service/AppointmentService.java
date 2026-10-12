package com.hms.service;

import com.hms.dto.request.AppointmentRequest;
import com.hms.dto.response.AppointmentResponse;
import org.springframework.data.domain.Page;

public interface AppointmentService {

    AppointmentResponse createAppointment(AppointmentRequest request);

    AppointmentResponse getAppointmentById(Long id);

    Page<AppointmentResponse> getAllAppointments(int page, int size);

    AppointmentResponse updateAppointment(Long id, AppointmentRequest request);

    void deleteAppointment(Long id);
}