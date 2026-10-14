
package com.hms.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class OpdVisitResponse {

    private Long id;
    private String opdVisitCode;

    private Long patientId;
    private String patientName;

    private Long doctorId;
    private String doctorName;

    private LocalDate visitDate;
    private String chiefComplaint;
    private String diagnosis;
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
