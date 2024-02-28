package com.example.demo.controllers;

import com.example.demo.entities.Appointment;
import com.example.demo.repositories.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AppointmentController {

    @Autowired
    AppointmentRepository appointmentRepository;

    public AppointmentController(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();

        return appointments.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(appointments);
    }

    @GetMapping("/appointments/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable("id") long id) {

        return appointmentRepository.findById(id)
                .map(appointment -> ResponseEntity.ok(appointment))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/appointment")
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {

        if (!isValidTime(appointment))
            return ResponseEntity.badRequest().build();

        if (hasOverlap(appointment))
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();

        Appointment createdAppointment = appointmentRepository.save(appointment);
        return ResponseEntity.ok(createdAppointment);
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<Object> deleteAppointment(@PathVariable("id") long id) {

        return appointmentRepository.findById(id)
                .map(appointment -> {
                    appointmentRepository.deleteById(id);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/appointments")
    public ResponseEntity<Void> deleteAllAppointments() {
        appointmentRepository.deleteAll();
        return ResponseEntity.ok().build();

    }

    //    AUXILIARY METHODS ON POST
    private boolean isValidTime(Appointment appointment) {
        return !appointment.getStartsAt().isEqual(appointment.getFinishesAt());
    }

    private boolean hasOverlap(Appointment newAppointment) {
        List<Appointment> appointments = appointmentRepository.findAll();

        for (Appointment existingAppointment : appointments) {
            if (existingAppointment.overlaps(newAppointment)) {
                return true;
            }
        }
        return false;
    }

}