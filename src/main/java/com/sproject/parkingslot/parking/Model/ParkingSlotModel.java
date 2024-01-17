package com.sproject.parkingslot.parking.Model;

import com.sproject.parkingslot.parking.Entity.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSlotModel {

    private Integer id;
    private Integer levelId;
    private VehicleType slotType;
    private Boolean occupied;
    private VehicleModel vehicleDetails;
}
