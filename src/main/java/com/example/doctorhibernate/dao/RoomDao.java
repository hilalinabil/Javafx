package com.example.doctorhibernate.dao;

import com.example.doctorhibernate.entities.Room;

import java.util.List;

public interface RoomDao {
    void save(Room room);
    Room findById(Long id);
    List<Room> findAll();
     void update(Room room);
     void delete(Room room);
}
