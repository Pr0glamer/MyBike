package nix.project.mybike.controllers.rest;


import nix.project.mybike.models.Bike;
import nix.project.mybike.services.BikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/bikes")
public class BikeRESTController {
    @Autowired
    private BikesService bikeService;

    @GetMapping
    public ResponseEntity<List<Bike>> findAll() {
        return new ResponseEntity<>(bikeService.findAll(true), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bike> findById(@PathVariable int id) {
        Bike bike = bikeService.findById(id);
        if (bike == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bike, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Bike> create(@RequestBody Bike bike) {
        bikeService.save(bike);
        return new ResponseEntity<>(bike, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bike> update(@PathVariable int id, @RequestBody Bike bike) {
        Bike existingBike = bikeService.findById(id);
        if (existingBike == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        existingBike.setTitle(bike.getTitle());
        existingBike.setProducer(bike.getProducer());
        existingBike.setYear(bike.getYear());
        existingBike.setPrice(bike.getPrice());
        existingBike.setOwner(bike.getOwner());
        existingBike.setTakenAt(bike.getTakenAt());
        existingBike.setPicture(bike.getPicture());
        bikeService.save(existingBike);
        return new ResponseEntity<>(existingBike, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Bike bike = bikeService.findById(id);
        if (bike == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        bikeService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}