package com.sproject.parkingslot.parking.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sproject.parkingslot.parking.Model.VehicleType;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "PARK_SLOT")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "LEVEL_ID")
    private Integer levelId;

    @Column(name = "SLOT_TYPE")
    @Enumerated(value = EnumType.STRING)
    private VehicleType slotType;

    @Column(name = "OCCUPIED")
    private Boolean occupied;

    @OneToOne(mappedBy = "slot")
    private Vehicle vehicleDetails;
}