package com.example.doctorhibernate.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

// lombok dependency eliminate boyle parts of the code  make it simpler and cleaner
// conctructors (default only for best practice) getters and setters
// sometimes we use the @Data annotation for hash and other but risk losing the lazy aspect


@Entity
@Table(name = "doctors")
@Getter // for setters
@Setter // for getters
@NoArgsConstructor // for  the default parameter
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matricule")
    private Long matr;

    @Column(name = "firstName", nullable = false)
    private String first_name;

    @Column (name= "lastName", nullable = false)
    private String last_name;

    @Column(name ="email", nullable = false, unique = true)
    private String email;

    @Column(name = "speciality", nullable = false)
    private String speciality;

    @Column(name = "departement", nullable= false)
    private String department;

    @Column(name = "license_number", unique = true)
    private String licenseNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Doctor(String first_name, String last_name, String email, String speciality, String department,String licenseNumber)
    {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.speciality = speciality;
        this.department = department;
        this.licenseNumber = licenseNumber;
    }

    // helper methods

    public String getFullName() {
        return first_name + " " + last_name;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "matricule=" + matr +
                ", firstName='" + first_name + '\'' +
                ", lastName='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", speciality='" + speciality + '\'' +
                ", departement='" + department + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
