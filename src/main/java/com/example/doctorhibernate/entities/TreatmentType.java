package com.example.doctorhibernate.entities;

public enum TreatmentType {
    CONSULTATION("Consultation"),
    CHIRURGIE("Chirurgie"),
    RADIOLOGIE("Radiologie"),
    ANALYSE("Analyse Médicale"),
    URGENCE("Urgence");

    private final String label;
    TreatmentType(String label) { this.label = label; }
    @Override public String toString() { return label; }
}