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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sproject.parkingslot.parking.Model.VehicleType.BIKE;

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
                return new Response<>("Vehicle already parked", HttpStatus.CONFLICT);
            }

            Optional<ParkingSlot> availableSlot = findAvailableSlot(vehicleModel.getVehicleType());
            Integer slot, level;
            if (availableSlot.isPresent()) {
                availableSlot.get().setOccupied(true);
                parkingSlotRepository.save(availableSlot.get());
                System.out.println("Step 1");

                Vehicle vehicle = new Vehicle();
                vehicle.setId(vehicleModel.getVehicleNumber());
                vehicle.setVehicleType(vehicleModel.getVehicleType());
                vehicle.setSlotId(availableSlot.get().getId());
                System.out.println("Step 2");
                vehicleRepository.save(vehicle);
                slot = availableSlot.get().getId();
                level = availableSlot.get().getLevelId();

                ParkingLevel parkingLevel = parkingRepository.findById(availableSlot.get().getLevelId()).get();

                switch (vehicleModel.getVehicleType()) {
                    case BIKE -> {
                        parkingLevel.setBikeAvailable(parkingLevel.getBikeAvailable() - 1);
                        parkingLevel.setBikeOccupied(parkingLevel.getBikeOccupied() + 1);
                    }
                    case BUS -> {
                        parkingLevel.setBusAvailable(parkingLevel.getBusAvailable() - 1);
                        parkingLevel.setBikeOccupied(parkingLevel.getBikeOccupied() + 1);
                        parkingLevel.setBusOccupied(parkingLevel.getBusOccupied() + 1);
                    }
                    case CAR -> {
                        parkingLevel.setCarAvailable(parkingLevel.getCarAvailable() - 1);
                        parkingLevel.setCarOccupied(parkingLevel.getCarOccupied() + 1);
                    }
                }
                parkingRepository.save(parkingLevel);
            } else {
                return new Response<>("no spot available");
            }
            return new Response<>("Vehicle Parked Successfully at Level: " + level + " and Slot: " + slot, HttpStatus.CREATED);
        } catch (Exception e) {
            return new Response<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isVehicleParked(String vehicleNumber) {
        return vehicleRepository.existsById(vehicleNumber);
    }

    private Optional<ParkingSlot> findAvailableSlot(VehicleType vehicleType) {
        return parkingSlotRepository.findFirstBySlotTypeAndOccupiedFalse(vehicleType);
    }

    @Override
    public Response<String> unpark(VehicleModel vehicleModel) {
        try {

            if (!isVehicleParked(vehicleModel.getVehicleNumber())) {
                return new Response<>("Vehicle not found", HttpStatus.NOT_FOUND);
            }
            Optional<Vehicle> vehicle = vehicleRepository.findById(vehicleModel.getVehicleNumber());

            Optional<ParkingSlot> parkedSlot = parkingSlotRepository.findById(vehicle.get().getSlotId());
            ParkingLevel parkingLevel = parkingRepository.findById(parkedSlot.get().getLevelId()).get();
            parkedSlot.get().setOccupied(false);
            parkingSlotRepository.save(parkedSlot.get());

            vehicleRepository.deleteById(vehicleModel.getVehicleNumber());
            switch (vehicle.get().getVehicleType()) {
                case BIKE -> {
                    parkingLevel.setBikeAvailable(parkingLevel.getBikeAvailable() + 1);
                    parkingLevel.setBikeOccupied(parkingLevel.getBikeOccupied() - 1);
                    break;
                }
                case BUS -> {
                    parkingLevel.setBusAvailable(parkingLevel.getBusAvailable() + 1);
                    parkingLevel.setBikeOccupied(parkingLevel.getBikeOccupied() + 1);
                    parkingLevel.setBusOccupied(parkingLevel.getBusOccupied() - 1);
                    break;
                }
                case CAR -> {
                    parkingLevel.setCarAvailable(parkingLevel.getCarAvailable() + 1);
                    parkingLevel.setCarOccupied(parkingLevel.getCarOccupied() - 1);
                    break;
                }
            }
            parkingRepository.save(parkingLevel);
            return new Response<>("Vehicle Un-parked successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new Response<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Response<ParkingLevel> addLevel(ParkingLevelModel parkingLevelModel) {
        ParkingLevel parkingLevel = ParkingLevel.builder()
                .name(parkingLevelModel.getName())
                .bikeAvailable(parkingLevelModel.getBikeAvailable())
                .carAvailable(parkingLevelModel.getCarAvailable())
                .busAvailable(parkingLevelModel.getBusAvailable())
                .bikeOccupied(0)
                .carOccupied(0)
                .busOccupied(0)
                .build();

        parkingLevel = parkingRepository.save(parkingLevel);

        initializeParkingSlots(parkingLevel);

        return new Response<>(parkingRepository.findFirstByName(parkingLevel.getName()));
    }

    private void initializeParkingSlots(ParkingLevel parkingLevel) {
        int bikeCapacity = parkingLevel.getBikeAvailable();
        int carCapacity = parkingLevel.getCarAvailable();
        int busCapacity = parkingLevel.getBusAvailable();

        for (int i = 1; i <= bikeCapacity; i++) {
            saveParkingSlot(parkingLevel.getId(), BIKE);
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
    public Response<String> decreaseLevel(Integer id) {
        Response<String> result;
        try {
            Optional<ParkingLevel> parkingLevel = parkingRepository.findById(id);
            if (parkingLevel.isEmpty()) {
                result = new Response<>("No parking level found", HttpStatus.NOT_FOUND);
            } else {
                List<ParkingSlot> slots = parkingSlotRepository.findAllByLevelId(id);
                List<Integer> slotIds = slots.stream().map(ParkingSlot::getId).collect(Collectors.toList());
                List<Vehicle> vehicles = vehicleRepository.findBySlotIds(slotIds);
                vehicleRepository.deleteAll(vehicles);
                parkingSlotRepository.deleteAll(slots);
                parkingRepository.deleteById(id);
                result = new Response<>("Level " + id + " deleted successfully", HttpStatus.CREATED);
            }

        } catch (Exception e) {
            result = new Response<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    @Override
    public Response<List<ParkingLevelModel>> statistics() {
        List<ParkingLevelModel> myList = new ArrayList<>();
        for (ParkingLevel parking : parkingRepository.findAllLevels()) {
            ParkingLevelModel parkingModel = ParkingLevelModel.builder().id(parking.getId())
                    .name(parking.getName())
                    .bikeOccupied(parking.getBikeOccupied())
                    .carOccupied(parking.getCarOccupied())
                    .busOccupied(parking.getBusOccupied())
                    .bikeAvailable(parking.getBikeAvailable())
                    .carAvailable(parking.getCarAvailable())
                    .busAvailable(parking.getBusAvailable())
                    .slotsList(listOfSlotModels(parking.getSlotsList()))
                    .build();
            myList.add(parkingModel);
        }
        if (!myList.isEmpty()) return new Response<>(myList);
        return new Response<>(null, HttpStatus.NO_CONTENT);
    }

    private List<ParkingSlotModel> listOfSlotModels(List<ParkingSlot> list) {
        return list.stream().map(x -> new ParkingSlotModel(x.getId(),
                x.getLevelId(),
                x.getSlotType(),
                x.getOccupied(),
                Optional.ofNullable(x.getVehicleDetails())
                        .map(vd -> new VehicleModel(vd.getId(), vd.getVehicleType(), vd.getSlotId()))
                        .orElse(null)
        )).toList();
    }
}
