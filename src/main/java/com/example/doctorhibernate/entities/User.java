package com.example.doctorhibernate.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_name", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name ="passwrd", nullable = false)
    private String passwrd;


    private String role = "ADMIN";




    public User(String username, String email,String passwrd)
    {
        this.username = username;
        this.email=email;
        this.passwrd = passwrd;
    }


}
