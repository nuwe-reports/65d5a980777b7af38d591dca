
package com.example.demo;

import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import java.time.LocalDateTime;
import java.time.format.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.demo.controllers.*;
import com.example.demo.repositories.*;
import com.example.demo.entities.*;
import com.fasterxml.jackson.databind.ObjectMapper;



/** TODO
 * Implement all the unit test in its corresponding class.
 * Make sure to be as exhaustive as possible. Coverage is checked ;)
 */

@WebMvcTest(DoctorController.class)
class DoctorControllerUnitTest {

    @MockBean
    private DoctorRepository doctorRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenValidDoctors_whenGetAllDoctors_thenReturnsListOfDoctorsWithStatusCode200() throws Exception {
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(new Doctor("John", "Doe", 30, "john.doe@hospital.com"));
        when(doctorRepository.findAll()).thenReturn(doctors);

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].age").value(30))
                .andExpect(jsonPath("$[0].email").value("john.doe@hospital.com"));
    }

    @Test
    void createDoctor_ReturnsCreatedDoctor() throws Exception {

        Doctor doctorToCreate = new Doctor("John", "Doe", 30, "john.doe@hospital.com");
        when(doctorRepository.save(eq(doctorToCreate))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/doctor")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(doctorToCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.email").value("john.doe@hospital.com"))
                .andExpect(jsonPath("$.firstName").isString())
                .andExpect(jsonPath("$.lastName").isString())
                .andExpect(jsonPath("$.age").isNumber())
                .andExpect(jsonPath("$.firstName").isString());
    }

    @Test
    void givenNoValidDoctorId_whenDeleteDoctor_thenReturnsNoContentWithStatusCode404() throws Exception {
        long doctorId = 1L;

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/doctors/{id}", doctorId))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidDoctorId_whenDeleteDoctor_thenReturnsOkWithStatusCode200() throws Exception {

        long doctorId = 1L;

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(new Doctor("John", "Doe",
                30, "john.doe@hospital.com")));

        mockMvc.perform(delete("/api/doctors/{id}", doctorId))
                .andExpect(status().isOk());
    }

    @Test
    void givenValidDoctors_whenDeleteAllDoctors_thenReturnsOkWithStatusCode200() throws Exception{
        mockMvc.perform(delete("/api/doctors"))
                .andExpect(status().isOk());

        verify(doctorRepository, times(1)).deleteAll();
    }

    @Test
    void givenValidDoctorId_whenGetDoctorById_theReturnsOkWithDoctor() throws Exception {
        long doctorId = 1L;

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(new Doctor("John", "Doe",
                30, "john.doe@hospital.com")));

        mockMvc.perform(get("/api/doctors/{id}", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.email").value("john.doe@hospital.com"));
    }

    @Test
    void givenNoDoctors_whenGetAllDoctors_thenReturnsEmptyListWithStatusCode204() throws Exception {
        when(doctorRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenInvalidDoctorId_whenGetDoctorById_thenReturnsNotFoundWithStatusCode404() throws Exception {
        long invalidDoctorId = -1L;

        when(doctorRepository.findById(invalidDoctorId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/doctors/{id}", invalidDoctorId))
                .andExpect(status().isNotFound());
    }

}


@WebMvcTest(PatientController.class)
class PatientControllerUnitTest {

    @MockBean
    private PatientRepository patientRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenValidPatients_whenGetAllPatients_thenReturnsListOfPatientsWithStatusCode200() throws Exception {
        List<Patient> patients = new ArrayList<>();
        patients.add(new Patient("Jane", "Smith", 20, "jane.smith@hospital.com"));
        when(patientRepository.findAll()).thenReturn(patients);

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].firstName").value("Jane"))
                .andExpect(jsonPath("$[0].lastName").value("Smith"))
                .andExpect(jsonPath("$[0].age").value(20))
                .andExpect(jsonPath("$[0].email").value("jane.smith@hospital.com"));
    }

    @Test
    void createPatient_ReturnsCreatedPatient() throws Exception {

        Patient patientToCreate = new Patient("Jane", "Smith", 20, "jane.smith@hospital.com");
        when(patientRepository.save(eq(patientToCreate))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/patient")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(patientToCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.age").value(20))
                .andExpect(jsonPath("$.email").value("jane.smith@hospital.com"))
                .andExpect(jsonPath("$.firstName").isString())
                .andExpect(jsonPath("$.lastName").isString())
                .andExpect(jsonPath("$.age").isNumber())
                .andExpect(jsonPath("$.firstName").isString());
    }

    @Test
    void givenNoValidPatientId_whenDeletePatient_thenReturnsNoContentWithStatusCode404() throws Exception {
        long patientId = 1L;

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/patients/{id}", patientId))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidPatientId_whenDeletePatient_thenReturnsOkWithStatusCode200() throws Exception {

        long patientId = 1L;

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(new Patient(
                "Jane", "Smith", 20, "jane.smith@hospital.com")));

        mockMvc.perform(delete("/api/patients/{id}", patientId))
                .andExpect(status().isOk());
    }

    @Test
    void givenValidPatients_whenDeleteAllPatients_thenReturnsOkWithStatusCode200() throws Exception{
        mockMvc.perform(delete("/api/patients"))
                .andExpect(status().isOk());

        verify(patientRepository, times(1)).deleteAll();
    }

    @Test
    void givenValidPatientId_whenGetPatientById_theReturnsOkWithPatient() throws Exception {
        long patientId = 1L;

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(new Patient("Jane", "Smith",
                20, "jane.smith@hospital.com")));

        mockMvc.perform(get("/api/patients/{id}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.age").value(20))
                .andExpect(jsonPath("$.email").value("jane.smith@hospital.com"));
    }

    @Test
    void givenNoPatients_whenGetAllPatients_thenReturnsEmptyListWithStatusCode204() throws Exception {
        when(patientRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenInvalidPatientId_whenGetPatientById_thenReturnsNotFoundWithStatusCode404() throws Exception {
        long invalidPatientId = -1L;

        when(patientRepository.findById(invalidPatientId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/patients/{id}", invalidPatientId))
                .andExpect(status().isNotFound());
    }

}

@WebMvcTest(RoomController.class)
class RoomControllerUnitTest {

    @MockBean
    private RoomRepository roomRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenValidRoom_whenGetAllRooms_thenReturnsListOfRoomsWithStatusCode200() throws Exception {
        List<Room> rooms = new ArrayList<>();

        rooms.add(new Room("psychiatry"));
        when(roomRepository.findAll()).thenReturn(rooms);

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].roomName").isString())
                .andExpect(jsonPath("$[0].roomName").value("psychiatry"));
    }

    @Test
    void createRoom_ReturnsCreatedRoom() throws Exception {

        Room roomToCreate = new Room("psychiatry");
        when(roomRepository.save(eq(roomToCreate))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/room")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(roomToCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomName").value("psychiatry"))
                .andExpect(jsonPath("$.roomName").isString());
    }

    @Test
    void givenNoValidRoomName_whenDeleteRoom_thenReturnsNoContentWithStatusCode404() throws Exception {
        String roomName = "orthopedic";

        when(roomRepository.findByRoomName(roomName)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/rooms/{roomName}", roomName))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidRoomName_whenDeleteDoctor_thenReturnsOkWithStatusCode200() throws Exception {

        String roomName = "orthopedic";

        when(roomRepository.findByRoomName(roomName)).thenReturn(Optional.of(new Room("psychiatry")));

        mockMvc.perform(delete("/api/rooms/{roomName}", roomName))
                .andExpect(status().isOk());
    }

    @Test
    void givenValidRooms_whenDeleteAllRooms_thenReturnsOkWithStatusCode200() throws Exception{
        mockMvc.perform(delete("/api/rooms"))
                .andExpect(status().isOk());

        verify(roomRepository, times(1)).deleteAll();
    }

    @Test
    void givenValidRoomName_whenGetRoomName_theReturnsOkWithRoom() throws Exception {
        String roomName = "dermatology";

        when(roomRepository.findByRoomName(roomName)).thenReturn(Optional.of(new Room("dermatology")));

        mockMvc.perform(get("/api/rooms/{roomName}", roomName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomName").value("dermatology"));
    }

    @Test
    void givenInvalidRoomName_whenGetRoomName_thenReturnsNotFoundWithStatusCode404() throws Exception {
        String invalidRoomName = "as345asd8";

        when(roomRepository.findByRoomName(invalidRoomName)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/rooms/{roomName}", invalidRoomName))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenEmptyRooms_whenGetRooms_thenReturnsNoContentWithStatusCode204() throws Exception {

        when(roomRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isNoContent());
    }



}

