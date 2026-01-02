package com.example.doctorhibernate.service.Impl;


import com.example.doctorhibernate.dao.RoomDao;
import com.example.doctorhibernate.entities.Room;
import com.example.doctorhibernate.exception.AppException;
import com.example.doctorhibernate.service.RoomService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;

public class RoomServiceImpl implements RoomService {

    private final RoomDao dao;

    // We only need the DAO now, as the DAO handles the Session/Factory
    public RoomServiceImpl(RoomDao dao) {
        this.dao = dao;
    }

    @Override
    public Room addRoom(String number, String type, int capacity, boolean occupied) {
        // 1. Validate inputs
        validateRoomDetails(number, type, capacity);

        // 2. Create Object
        Room room = new Room(); // Assuming empty constructor + setters, or use your full constructor
        room.setNumber(number.trim());
        room.setType(type.trim());
        room.setCapacity(capacity);
        room.setOccupied(occupied);

        // 3. Persist via DAO
        try {
            dao.save(room);
            return room;
        } catch (Exception e) {
            throw new AppException("Failed to add room.", e);
        }
    }

    @Override
    public List<Room> getAllRooms() {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new AppException("Failed to load rooms.", e);
        }
    }

    @Override
    public Room updateRoom(Long id, String number, String type, int capacity, boolean occupied) {
        // 1. Validate inputs
        validateRoomDetails(number, type, capacity);

        try {
            // 2. Find existing room
            Room room = dao.findById(id);
            if (room == null) {
                throw new AppException("Room not found.");
            }

            // 3. Update fields
            room.setNumber(number.trim());
            room.setType(type.trim());
            room.setCapacity(capacity);
            room.setOccupied(occupied);

            // 4. Update via DAO
            dao.update(room);
            return room;
        } catch (Exception e) {
            throw new AppException("Failed to update room.", e);
        }
    }

    @Override
    public void deleteRoom(Long id) {
        try {
            Room room = dao.findById(id);
            if (room == null) {
                throw new AppException("Room not found.");
            }
            dao.delete(room);
        } catch (Exception e) {
            throw new AppException("Failed to delete room.", e);
        }
    }

    // Helper method to avoid code duplication
    private void validateRoomDetails(String number, String type, int capacity) {
        if (number == null || number.isBlank()) throw new AppException("Room number is required.");
        if (type == null || type.isBlank()) throw new AppException("Room type is required.");
        if (capacity <= 0) throw new AppException("Capacity must be > 0.");
        if (capacity > 4) throw new AppException("Capacity cannot exceed 4.");
    }

}
