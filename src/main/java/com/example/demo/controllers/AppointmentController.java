package com.example.demo.controllers;

import com.example.demo.entities.Appointment;
import com.example.demo.repositories.AppointmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api")
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;

    public AppointmentController(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments() {

        List<Appointment> appointments = new ArrayList<>(appointmentRepository.findAll());

        if (appointments.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(appointments);
        }
    }

    @GetMapping("/appointments/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable("id") long id) {
        Optional<Appointment> appointment = appointmentRepository.findById(id);

        return appointment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/appointment")
    public ResponseEntity<List<Appointment>> createAppointment(@RequestBody Appointment appointment) {

        if (appointment.getStartsAt().isEqual(appointment.getFinishesAt()) || appointment.getStartsAt().isAfter(appointment.getFinishesAt())) {
            return ResponseEntity.badRequest().build();
        }

        List<Appointment> appointments = appointmentRepository.findAll();
        for (Appointment a : appointments) {
            if (a.overlaps(appointment)) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
            }
        }

        appointmentRepository.save(appointment);
        return ResponseEntity.ok(appointmentRepository.findAll());
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<HttpStatus> deleteAppointment(@PathVariable("id") long id) {

        Optional<Appointment> appointment = appointmentRepository.findById(id);

        if (!appointment.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        appointmentRepository.deleteById(id);
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/appointments")
    public ResponseEntity<HttpStatus> deleteAllAppointments() {

        if (appointmentRepository.count() == 0) {
            return ResponseEntity.ok().build();
        }

        appointmentRepository.deleteAll();
        return ResponseEntity.ok().build();
    }

}
