package com.sproject.parkingslot.parking.Service.Impl;

import com.sproject.parkingslot.parking.Common.Response;
import com.sproject.parkingslot.parking.Entity.ParkingLevel;
import com.sproject.parkingslot.parking.Entity.ParkingSlot;
import com.sproject.parkingslot.parking.Entity.Vehicle;
import com.sproject.parkingslot.parking.Model.*;
import com.sproject.parkingslot.parking.Repository.ParkingRepository;
import com.sproject.parkingslot.parking.Repository.ParkingSlotRepository;
import com.sproject.parkingslot.parking.Repository.VehicleRepository;
import com.sproject.parkingslot.parking.Service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingServiceImpl implements ParkingService {
    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public Response<String> park(VehicleModel vehicleModel) {
        try {
            if (isVehicleParked(vehicleModel.getVehicleNumber())) {
                return new Response<>("Vehicle already parked",HttpStatus.CONFLICT);
            }

            Optional<ParkingSlot> availableSlot = findAvailableSlot(vehicleModel.getVehicleType());
            Integer slot,level;
            if (availableSlot.isPresent()) {
                availableSlot.get().setOccupied(true);
                parkingSlotRepository.save(availableSlot.get());
                System.out.println("Step 1");

                Vehicle vehicle = new Vehicle();
                vehicle.setId(vehicleModel.getVehicleNumber());
                vehicle.setVehicleType(vehicleModel.getVehicleType());
                vehicle.setLevelId(availableSlot.get().getLevelId());
                vehicle.setSlotId(availableSlot.get().getId());
                System.out.println("Step 2");
                vehicleRepository.save(vehicle);
                slot=availableSlot.get().getId();
                level=availableSlot.get().getLevelId();

                ParkingLevel parkingLevel = parkingRepository.findById(availableSlot.get().getLevelId()).get();

                switch (vehicleModel.getVehicleType()){
                    case BIKE -> {
                        parkingLevel.setBikeAvailable(parkingLevel.getBikeAvailable()-1);
                        parkingLevel.setBikeOccupied(parkingLevel.getBikeOccupied()+1);
                        break;
                    }
                    case BUS -> {
                        parkingLevel.setBusAvailable(parkingLevel.getBusAvailable()-1);                    parkingLevel.setBikeOccupied(parkingLevel.getBikeOccupied()+1);
                        parkingLevel.setBusOccupied(parkingLevel.getBusOccupied()+1);
                        break;
                    }
                    case CAR -> {
                        parkingLevel.setCarAvailable(parkingLevel.getCarAvailable()-1);
                        parkingLevel.setCarOccupied(parkingLevel.getCarOccupied()+1);
                        break;
                    }
                }
                parkingRepository.save(parkingLevel);
            } else {
                return new Response<>("no spot available");
            }
            return new Response<>("Vehicle Parked Successfully at Level: "+level+" and Slot: "+slot, HttpStatus.CREATED);
        } catch (Exception e){
            return new Response<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isVehicleParked(String vehicleNumber) {
        return vehicleRepository.existsById(vehicleNumber);
    }

    private Optional<ParkingSlot> findAvailableSlot(VehicleType vehicleType) {
        return parkingSlotRepository.findFirstBySlotTypeAndOccupiedFalse(vehicleType);
    }

    @Override
    public Response<String> unpark(VehicleModel vehicleModel){
        try {

            if (!isVehicleParked(vehicleModel.getVehicleNumber())) {
                return new Response<>("Vehicle not found", HttpStatus.NOT_FOUND);
            }
            Optional<Vehicle> vehicle = vehicleRepository.findById(vehicleModel.getVehicleNumber());
            ParkingLevel parkingLevel = parkingRepository.findById(vehicle.get().getLevelId()).get();

            Optional<ParkingSlot> parkedSlot = parkingSlotRepository.findById(vehicle.get().getSlotId());
            parkedSlot.get().setOccupied(false);
            parkingSlotRepository.save(parkedSlot.get());

            vehicleRepository.deleteById(vehicleModel.getVehicleNumber());
            switch (vehicleModel.getVehicleType()){
                case BIKE -> {
                    parkingLevel.setBikeAvailable(parkingLevel.getBikeAvailable()+1);
                    parkingLevel.setBikeOccupied(parkingLevel.getBikeOccupied()-1);
                    break;
                }
                case BUS -> {
                    parkingLevel.setBusAvailable(parkingLevel.getBusAvailable()+1);                    parkingLevel.setBikeOccupied(parkingLevel.getBikeOccupied()+1);
                    parkingLevel.setBusOccupied(parkingLevel.getBusOccupied()-1);
                    break;
                }
                case CAR -> {
                    parkingLevel.setCarAvailable(parkingLevel.getCarAvailable()+1);
                    parkingLevel.setCarOccupied(parkingLevel.getCarOccupied()-1);
                    break;
                }
            }
            parkingRepository.save(parkingLevel);
            return new Response<>("Vehicle Un-parked successfully",HttpStatus.OK);
        } catch (Exception e){
            return new Response<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

@Override
    public Response<String> addLevel(ParkingLevelModel parkingLevelModel) {
        ParkingLevel parkingLevel = new ParkingLevel();
        parkingLevel.setId(parkingLevelModel.getId());
        parkingLevel.setName(parkingLevelModel.getName());
        parkingLevel.setBikeAvailable(parkingLevelModel.getBikeAvailable());
        parkingLevel.setCarAvailable(parkingLevelModel.getCarAvailable());
        parkingLevel.setBusAvailable(parkingLevelModel.getBusAvailable());
        parkingLevel.setBikeOccupied(0);
        parkingLevel.setCarOccupied(0);
        parkingLevel.setBusOccupied(0);

        parkingLevel = parkingRepository.save(parkingLevel);

        initializeParkingSlots(parkingLevel);

        return new Response<>("true");
    }

    private void initializeParkingSlots(ParkingLevel parkingLevel) {
        int bikeCapacity = parkingLevel.getBikeAvailable();
        int carCapacity = parkingLevel.getCarAvailable();
        int busCapacity = parkingLevel.getBusAvailable();

        for (int i = 1; i <= bikeCapacity; i++) {
            saveParkingSlot(parkingLevel.getId(), VehicleType.BIKE);
        }

        for (int i = 1; i <= carCapacity; i++) {
            saveParkingSlot(parkingLevel.getId(), VehicleType.CAR);
        }

        for (int i = 1; i <= busCapacity; i++) {
            saveParkingSlot(parkingLevel.getId(), VehicleType.BUS);
        }
    }

    private void saveParkingSlot(Integer levelId, VehicleType slotType) {
        ParkingSlot parkingSlot = new ParkingSlot();
        parkingSlot.setLevelId(levelId);
        parkingSlot.setSlotType(slotType);
        parkingSlot.setOccupied(false);
        parkingSlotRepository.save(parkingSlot);
    }

    @Override
    public Response<String> decreaseLevel(Integer id){
        try {
            Optional<ParkingLevel> parkingLevel = parkingRepository.findById(id);
            if (parkingLevel.isEmpty()) {
                return new Response<>("No parking level found", HttpStatus.NOT_FOUND);
            }

            Optional<List<Vehicle>> vehicles = vehicleRepository.findAllByLevelId(id);
            for (Vehicle vehicle : vehicles.get()) {
                vehicleRepository.deleteById(vehicle.getId());
            }

            Optional<List<ParkingSlot>> slots = parkingSlotRepository.findAllByLevelId(id);
            for (ParkingSlot slot : slots.get()) {
                parkingSlotRepository.deleteById(slot.getId());
            }

            parkingRepository.deleteById(id);
            return new Response<>("Level " + id + " deleted successfully", HttpStatus.CREATED);
        } catch (Exception e){
            return new Response<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Response<List<ParkingLevel>> statistics() {

        List<ParkingLevel> myList =new ArrayList<ParkingLevel>();
        for(ParkingLevel parking : parkingRepository.findAll()) {
            ParkingLevel parkingModel = ParkingLevel.builder().id(parking.getId()).bikeOccupied(parking.getBikeOccupied()).carOccupied(parking.getCarOccupied()).busOccupied(parking.getBusOccupied()).bikeAvailable(parking.getBikeAvailable()).carAvailable(parking.getCarAvailable()).busAvailable(parking.getBusAvailable()).build();
            myList.add(parkingModel);
        }
        if(myList.size()>0)return new Response<>(myList);
        return new Response<>(null , HttpStatus.NO_CONTENT);
    }
}
