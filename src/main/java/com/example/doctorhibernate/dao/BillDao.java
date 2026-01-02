package com.example.doctorhibernate.dao;

import com.example.doctorhibernate.entities.Bill;

import java.util.List;

public interface BillDao {
    void save(Bill bill);
    List<Bill> findAll();
    Bill findById(Long id);
    void delete(Bill bill);
}
