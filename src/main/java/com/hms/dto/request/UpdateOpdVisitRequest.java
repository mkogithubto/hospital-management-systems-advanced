package com.hms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateOpdVisitRequest {

    @NotNull(message = "Visit date is required")
    private LocalDate visitDate;

    @NotBlank(message = "Chief complaint is required")
    @Size(max = 500, message = "Chief complaint must not exceed 500 characters")
    private String chiefComplaint;

    private String diagnosis;

    @NotBlank(message = "Status is required")
    private String status;
}