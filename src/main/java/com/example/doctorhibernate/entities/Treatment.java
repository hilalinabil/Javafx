package com.example.doctorhibernate.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "treatment")
@Getter
@Setter
@NoArgsConstructor
public class Treatment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "treat_id")//smiat dyal collone f bd
    private Long treatId;


    @Column(name = "description",nullable = false, length = 255)//mat9drch tkoun null,taille max 255
    private String description;

    @Column(name = "cost",nullable = false)
    private double cost;

    @Enumerated(EnumType.STRING)
    @Column(name = "treatment_type")
    private TreatmentType treatmentType;


}
