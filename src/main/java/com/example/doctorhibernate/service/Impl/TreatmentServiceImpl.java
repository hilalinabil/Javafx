package com.example.doctorhibernate.service.Impl;



import com.example.doctorhibernate.dao.TreatmentDao;
import com.example.doctorhibernate.dao.daoImpl.TreatmentDaoImpl;
import com.example.doctorhibernate.entities.Treatment;
import com.example.doctorhibernate.service.TreatmentService;

import java.util.List;

public class TreatmentServiceImpl implements TreatmentService {


    private final TreatmentDao repository = new TreatmentDaoImpl();

    @Override
    public void addTreatment(Treatment treatment) {
        if (treatment == null) throw new IllegalArgumentException("Le traitement ne peut être nul.");
        if (treatment.getCost() < 0) {
            throw new IllegalArgumentException("Le coût ne peut pas être négatif.");
        }
        repository.save(treatment);
    }

    @Override
    public List<Treatment> getAllTreatments() {
        return repository.findAll();
    }

    /*public double calculateTotalTreatmentCost(Patient patient) {
        return repository.findByPatient(patient)
                .stream()
                .mapToDouble(Treatment::getCost)
                .sum();
    }*/
  @Override
    public void removeTreatment(Treatment treatment) {
        if (treatment == null) {
            throw new IllegalArgumentException("Sélectionnez un traitement à supprimer.");
        }
        repository.delete(treatment);
    }
    @Override
    public void updateTreatment(Treatment treatment) {
        if (treatment == null || treatment.getTreatId() == null) {
            throw new IllegalArgumentException("Traitement invalide pour la mise à jour.");
        }
        repository.update(treatment);
    }
}