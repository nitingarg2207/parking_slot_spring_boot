package com.sproject.parkingslot.parking.Entity;

import com.sproject.parkingslot.parking.Model.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "VEHICLE")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    @Id
    private String id;

    @Column(name = "VEHICLE_TYPE")
    @Enumerated(value = EnumType.STRING)
    private VehicleType vehicleType;

    @Column(name = "LEVEL_ID")
    private int levelId;

    @Column(name = "SLOT_ID")
    private int slotId;

}