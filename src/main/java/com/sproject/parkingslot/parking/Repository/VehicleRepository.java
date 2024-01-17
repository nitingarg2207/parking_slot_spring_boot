package com.sproject.parkingslot.parking.Repository;

import com.sproject.parkingslot.parking.Entity.ParkingSlot;
import com.sproject.parkingslot.parking.Entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
    //    Optional<List<Vehicle>> findAllByLevelId(Integer id);
    @Query("SELECT v FROM Vehicle v WHERE v.slotId IN :slotIds")
    List<Vehicle> findBySlotIds(@Param("slotIds") List<Integer> slotIds);

    Vehicle findBySlotId(Integer id);
}