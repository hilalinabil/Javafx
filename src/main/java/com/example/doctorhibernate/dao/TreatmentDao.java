package com.example.doctorhibernate.dao;


import com.example.doctorhibernate.entities.Treatment;

import java.util.List;

public interface TreatmentDao {
    List<Treatment> findAll();
    Treatment findById(Long id);
    void save(Treatment treatment);
    void update(Treatment treatment);
    void delete(Treatment treatment);
    //List<Treatment> findPatientById(Long id);
}
