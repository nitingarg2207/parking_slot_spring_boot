package com.sproject.parkingslot.parking.Entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "PARK_LEVEL")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ParkingLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CAR_OCCUPIED")
    private Integer carOccupied;

    @Column(name = "BUS_OCCUPIED")
    private Integer busOccupied;

    @Column(name = "BIKE_OCCUPIED")
    private Integer bikeOccupied;

    @Column(name = "CAR_AVAILABLE")
    private Integer carAvailable;

    @Column(name = "BUS_AVAILABLE")
    private Integer busAvailable;

    @Column(name = "BIKE_AVAILABLE")
    private Integer bikeAvailable;
}