package com.sproject.parkingslot.parking.Repository;

import com.sproject.parkingslot.parking.Entity.ParkingSlot;
import com.sproject.parkingslot.parking.Model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Integer> {
//    ParkingSlot findFirstByLevelIdAndSlotTypeAndOccupiedIsFalse(Integer levelId, String slotType);
//    ParkingSlot findFirstByLevelIdAndSlotTypeAndOccupiedIsFalse(Integer levelId, String slotType);
    Optional<ParkingSlot> findFirstBySlotTypeAndOccupiedFalse(VehicleType slotType);
    Optional<List<ParkingSlot>> findAllByLevelId(Integer id);
}