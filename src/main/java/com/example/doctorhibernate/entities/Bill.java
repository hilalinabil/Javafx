package com.example.doctorhibernate.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "bills")
@Getter
@Setter
@NoArgsConstructor
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    private LocalDate issueDate;


    private String roomNumber;
    private String roomType;
    private double roomPrice;


    @Column(length = 1000)
    private String treatmentSummary;
    private double totalTreatmentCost;

    private double totalAmount;

    private boolean paid;
}
