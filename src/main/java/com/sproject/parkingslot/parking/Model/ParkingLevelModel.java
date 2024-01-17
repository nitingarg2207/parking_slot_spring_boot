package com.sproject.parkingslot.parking.Model;

import com.sproject.parkingslot.parking.Entity.ParkingSlot;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ParkingLevelModel {
    private Integer id;
    private String name;
    private Integer carAvailable;

    private Integer busAvailable;

    private Integer bikeAvailable;

    private Integer carOccupied;

    private Integer busOccupied;

    private Integer bikeOccupied;

    private List<ParkingSlotModel> slotsList;
//    private List<VehicleModel> vehiclesList;
}
