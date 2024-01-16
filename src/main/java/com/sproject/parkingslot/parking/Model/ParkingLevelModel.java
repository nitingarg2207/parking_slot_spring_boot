package com.sproject.parkingslot.parking.Model;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
