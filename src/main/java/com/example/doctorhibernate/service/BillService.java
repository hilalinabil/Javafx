package com.example.doctorhibernate.service;

import com.example.doctorhibernate.entities.Bill;
import com.example.doctorhibernate.entities.Patient;

import java.util.List;

public interface BillService {
    Bill generateBillForPatient(Patient patient);
    List<Bill> getAllBills();
}
