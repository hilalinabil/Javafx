package com.example.doctorhibernate.service;

import com.example.doctorhibernate.entities.Treatment;

import java.util.List;

public interface TreatmentService {
    void addTreatment(Treatment treatment);
    List<Treatment> getAllTreatments();
    void removeTreatment(Treatment treatment);
    void updateTreatment(Treatment treatment);
}
