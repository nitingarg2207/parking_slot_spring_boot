package com.sproject.parkingslot.parking.Repository;

import com.sproject.parkingslot.parking.Entity.ParkingLevel;
import com.sproject.parkingslot.parking.Entity.ParkingSlot;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingRepository extends JpaRepository<ParkingLevel, Integer> {
    ParkingLevel findFirstByName(String name);

    @EntityGraph("level.slots")
//    @Query("select l from ParkingLevel l left join fetch l.slotsList s left join s.vehicleDetails where s.occupied = true")
    @Query("select l from ParkingLevel l left join fetch l.slotsList s left join fetch s.vehicleDetails v where v is not null")
    List<ParkingLevel> findAllLevels();
}