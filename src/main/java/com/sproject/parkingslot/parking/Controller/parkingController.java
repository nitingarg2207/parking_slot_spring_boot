package com.sproject.parkingslot.parking.Controller;

import com.sproject.parkingslot.parking.Common.Response;
import com.sproject.parkingslot.parking.Entity.ParkingLevel;
import com.sproject.parkingslot.parking.Model.ParkingLevelModel;
import com.sproject.parkingslot.parking.Model.VehicleModel;
import com.sproject.parkingslot.parking.Service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
public class parkingController {
    @Autowired
    ParkingService parkingService;
    @GetMapping("/")
    public String f(){
        return "hehehe";
    }
    @PostMapping("/parking/add-level")
    public ResponseEntity<Object> addLevel(@RequestBody ParkingLevelModel parkingLevelModel){
        try{
            Response<ParkingLevel> data = parkingService.addLevel(parkingLevelModel);
            return new ResponseEntity<>(data.getReturnObject(), data.getHttpStatus());
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/parking/park")
    public ResponseEntity<String> park(@RequestBody VehicleModel vehicleModel){
        try{
            Response<String> data = parkingService.park(vehicleModel);
            return new ResponseEntity<>(data.getReturnObject(), data.getHttpStatus());
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/parking/unpark")
    public ResponseEntity<String> unpark(@RequestBody VehicleModel vehicleModel){
        try{
            Response<String> data = parkingService.unpark(vehicleModel);
            return new ResponseEntity<>(data.getReturnObject().toString(), data.getHttpStatus());
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/parking/decreaseLevel/{id}")
    public ResponseEntity<String> decreaseLevel(@PathVariable Integer id){
        try{
            Response<String> data = parkingService.decreaseLevel(id);
            return new ResponseEntity<>(data.getReturnObject(), data.getHttpStatus());
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/parking/statistics")
    public ResponseEntity<List<ParkingLevelModel>> staticstics(){
        Response<List<ParkingLevelModel>> data = parkingService.statistics();
        return new ResponseEntity<>(data.getReturnObject(), data.getHttpStatus());
    }
}
