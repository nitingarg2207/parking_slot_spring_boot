package com.sproject.parkingslot.parking.Service;

import com.sproject.parkingslot.parking.Common.Response;
import com.sproject.parkingslot.parking.Entity.ParkingLevel;
import com.sproject.parkingslot.parking.Model.ParkingLevelModel;
import com.sproject.parkingslot.parking.Model.VehicleModel;

import java.util.List;

public interface ParkingService {
     public Response<String> park(VehicleModel vehicleModel);
     public Response<String> unpark(VehicleModel vehicleModel);
     public Response<ParkingLevel> addLevel(ParkingLevelModel parkingLevelModel);
     public Response<String> decreaseLevel(Integer id);
     public Response<List<ParkingLevelModel>> statistics();
}
