package com.example.doctorhibernate.service.Impl;

import com.example.doctorhibernate.dao.BillDao;
import com.example.doctorhibernate.dao.daoImpl.BillDaoImpl;
import com.example.doctorhibernate.entities.Bill;
import com.example.doctorhibernate.entities.Patient;
import com.example.doctorhibernate.entities.Treatment;
import com.example.doctorhibernate.service.BillService;

import java.time.LocalDate;
import java.util.List;

public class BillServiceImpl implements BillService {
    private final BillDao billDao = new BillDaoImpl();

    @Override
    public Bill generateBillForPatient(Patient patient) {
        Bill bill = new Bill();
        bill.setPatient(patient);
        bill.setIssueDate(LocalDate.now());
        bill.setPaid(false);

        double roomCost = 0;
        if (patient.getRoom() != null) {
            bill.setRoomNumber(patient.getRoom().getNumber());
            bill.setRoomType(patient.getRoom().getType());

            if ("ICU".equalsIgnoreCase(patient.getRoom().getType())) {
                roomCost = 500.0;
            } else {
                roomCost = 200.0;
            }
            bill.setRoomPrice(roomCost);
        }

        double treatmentCost = 0;
        StringBuilder summary = new StringBuilder();

        List<Treatment> treatments = patient.getTreatments();
        if (treatments != null) {
            for (Treatment t : treatments) {
                treatmentCost += t.getCost();
                summary.append(t.getTreatmentType()).append(" ($").append(t.getCost()).append("), ");
            }
        }

        bill.setTreatmentSummary(summary.toString());
        bill.setTotalTreatmentCost(treatmentCost);

        bill.setTotalAmount(roomCost + treatmentCost);

        // 4. Save
        billDao.save(bill);
        return bill;
    }

    @Override
    public List<Bill> getAllBills() {
        return billDao.findAll();
    }

    @Override
    public void deleteBill(Long id) {
        Bill bill = billDao.findById(id);
        if (bill != null) {
            billDao.delete(bill);
        }
    }
}
