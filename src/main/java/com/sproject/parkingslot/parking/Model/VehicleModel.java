package com.sproject.parkingslot.parking.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleModel {
    private String vehicleNumber;
    private VehicleType vehicleType;
}
