package com.example.doctorhibernate.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table( name = "patients")
@Getter
@Setter
@NoArgsConstructor
public class Patient {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @Column(name= "CIN", unique = true, nullable = false)
    private String cin;
    @Column(name= "firstName", nullable = false)
    private String first_name;
    @Column(name= "lastName", nullable = false)
    private String last_name;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name="phone", nullable = false, unique = true)
    private String phone;
    @Column(name="allergies")
    private String allergies;


    @ManyToOne
    @JoinColumn(name = "roomId", nullable = false)
    private Room room;

    // Many Patients can be treated by One Doctor
    @ManyToOne
    @JoinColumn(name = "matricule") // Optional: nullable=false if a doctor is mandatory
    private Doctor doctor;

    // One Patient can have Many Treatments
    @ManyToMany(fetch = FetchType.EAGER) // EAGER ensures treatments load when you view the patient
    @JoinTable(
            name = "patient_treatments",
            joinColumns = @JoinColumn(name = "patient_id"),
            inverseJoinColumns = @JoinColumn(name = "treat_id")
    )
    private List<Treatment> treatments = new ArrayList<>();


    public Patient(String cin,String first_name,String last_name,String email,String phone,String allergies)
    {
        this.cin=cin;
        this.first_name=first_name;
        this.last_name=last_name;
        this.email=email;
        this.phone=phone;
        this.allergies=allergies;
    }


}
