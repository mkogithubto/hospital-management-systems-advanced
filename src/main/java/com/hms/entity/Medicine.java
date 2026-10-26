
package com.hms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "medicines",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_medicine_code",
                        columnNames = "medicine_code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medicine_code", nullable = false, length = 50)
    private String medicineCode;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "generic_name", length = 150)
    private String genericName;

    @Column(length = 150)
    private String manufacturer;

    @Column(length = 50)
    private String category;

    @Column(length = 50)
    private String strength;

    @Column(name = "dosage_form", length = 50)
    private String dosageForm;

    @Column(name = "prescription_required", nullable = false)
    private boolean prescriptionRequired = false;

    @Column(nullable = false, length = 30)
    private String status = "ACTIVE";

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
