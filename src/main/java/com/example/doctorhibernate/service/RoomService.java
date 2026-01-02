package com.example.doctorhibernate.service;

import com.example.doctorhibernate.entities.Room;

import java.util.List;

public interface RoomService {

    Room addRoom(String number, String type, int capacity, boolean occupied);
    List<Room> getAllRooms();
    Room updateRoom(Long id, String number, String type, int capacity, boolean occupied);
    void deleteRoom(Long id);
}
