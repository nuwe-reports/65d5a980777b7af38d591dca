package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import com.example.demo.entities.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
@TestInstance(Lifecycle.PER_CLASS)
class EntityUnitTest {

	@Autowired
	private TestEntityManager entityManager;

	private Doctor d1;

	private Patient p1;

    private Room r1;

    private Appointment a1;
    private Appointment a2;
    private Appointment a3;

    @BeforeEach
    void setUp() {
        d1 = new Doctor("John", "Doe", 30, "john.doe@hospital.com");
        p1 = new Patient("Jane", "Smith", 20, "dane.smith@hospital.com");
        r1 = new Room("Psychiatry");

        a1 = new Appointment(p1, d1, r1, parseDateTime("10:00 01/03/2024"), parseDateTime("10:30 01/03/2024"));
        a2 = new Appointment(p1, d1, r1, parseDateTime("14:00 15/03/2024"), parseDateTime("14:30 15/03/2024"));
        a3 = new Appointment(p1, d1, r1, parseDateTime("15:20 06/06/2024"), parseDateTime("15:40 06/06/2024"));

        entityManager.persist(d1);
        entityManager.persist(p1);
        entityManager.persist(r1);

        entityManager.persist(a1);
        entityManager.persist(a2);
        entityManager.persist(a3);

        entityManager.flush();
        entityManager.clear();
    }

    public static LocalDateTime parseDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        return LocalDateTime.parse(dateTime, formatter);
    }

    @Test
    void testFindDoctorById() {
        Doctor doctorFound = entityManager.find(Doctor.class, d1.getId());
        assertThat(doctorFound).isNotNull();
        assertThat(doctorFound.getFirstName()).isEqualTo(d1.getFirstName());
        assertThat(doctorFound.getLastName()).isEqualTo(d1.getLastName());
        assertThat(doctorFound.getAge()).isEqualTo(d1.getAge());
        assertThat(doctorFound.getEmail()).isEqualTo(d1.getEmail());
    }

    @Test
    void testFindPatientById() {
        Patient patientFound = entityManager.find(Patient.class, p1.getId());
        assertThat(patientFound).isNotNull();
        assertThat(patientFound.getFirstName()).isEqualTo(p1.getFirstName());
        assertThat(patientFound.getLastName()).isEqualTo(p1.getLastName());
        assertThat(patientFound.getAge()).isEqualTo(p1.getAge());
        assertThat(patientFound.getEmail()).isEqualTo(p1.getEmail());
    }

    @Test
    void testFindRoomByName() {
        Room roomFound = entityManager.find(Room.class, r1.getRoomName());
        assertThat(roomFound).isNotNull();
        assertThat(roomFound.getRoomName()).isEqualTo(r1.getRoomName());
    }

    @Test
    void testFindAppointmentById() {
        Appointment appointmentFound = entityManager.find(Appointment.class, a1.getId());

        assertThat(appointmentFound).isNotNull();
        assertThat(appointmentFound.getDoctor().getFirstName()).isEqualTo(a1.getDoctor().getFirstName());
        assertThat(appointmentFound.getDoctor().getLastName()).isEqualTo(a1.getDoctor().getLastName());
        assertThat(appointmentFound.getDoctor().getAge()).isEqualTo(a1.getDoctor().getAge());
        assertThat(appointmentFound.getDoctor().getEmail()).isEqualTo(a1.getDoctor().getEmail());

        assertThat(appointmentFound.getPatient().getFirstName()).isEqualTo(a1.getPatient().getFirstName());
        assertThat(appointmentFound.getPatient().getLastName()).isEqualTo(a1.getPatient().getLastName());
        assertThat(appointmentFound.getPatient().getAge()).isEqualTo(a1.getPatient().getAge());
        assertThat(appointmentFound.getPatient().getEmail()).isEqualTo(a1.getPatient().getEmail());

        assertThat(appointmentFound.getRoom().getRoomName()).isEqualTo(a1.getRoom().getRoomName());

        assertThat(appointmentFound.getStartsAt()).isEqualTo(a1.getStartsAt());
        assertThat(appointmentFound.getFinishesAt()).isEqualTo(a1.getFinishesAt());
    }

    @Test
    void testCompareAppointmentDates() {
        Appointment appointmentFoundOne = entityManager.find(Appointment.class, a1.getId());
        Appointment appointmentFoundTwo = entityManager.find(Appointment.class, a2.getId());
        Appointment appointmentFoundThree = entityManager.find(Appointment.class, a3.getId());

        assertThat(appointmentFoundOne.getStartsAt()).isBefore(appointmentFoundOne.getFinishesAt());
        assertThat(appointmentFoundTwo.getStartsAt()).isBefore(appointmentFoundTwo.getFinishesAt());
        assertThat(appointmentFoundThree.getStartsAt()).isBefore(appointmentFoundThree.getFinishesAt());
    }

    @Test
    void testRoomNameContainsExpectedSequence() {
        Room roomFound = entityManager.find(Room.class, r1.getRoomName());
        assertThat(roomFound.getRoomName()).containsSequence(r1.getRoomName());
    }

    @Test
    void testValidatePatientEmailFormat() {
        Patient patientFound = entityManager.find(Patient.class, p1.getId());
        assertThat(patientFound.getEmail().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}"))
                .as("Email format is valid").isTrue();
    }

    @Test
    void testValidateDoctorAgeGreaterThan18() {
        Doctor doctorFound = entityManager.find(Doctor.class, d1.getId());
        assertThat(doctorFound.getAge()).isGreaterThanOrEqualTo(18);
    }

    @Test
    void testOverlappingWhenStartTimesAreEqual() {

        // Case 1: A.starts == B.starts
        Appointment appointmentFoundOne = entityManager.find(Appointment.class, a1.getId());
        Appointment appointmentFoundTwo = entityManager.find(Appointment.class, a2.getId());

        appointmentFoundOne.setStartsAt(parseDateTime("08:00 01/01/2024"));
        appointmentFoundOne.setFinishesAt(parseDateTime("08:15 01/01/2024"));

        appointmentFoundTwo.setStartsAt(parseDateTime("08:00 01/01/2024"));
        appointmentFoundTwo.setFinishesAt(parseDateTime("08:30 01/01/2024"));

        assertThat(appointmentFoundOne.getStartsAt()).isEqualTo(appointmentFoundTwo.getStartsAt());
    }

    @Test
    void testOverlappingWhenEndTimesAreEqual() {

        // Case 2: A.finishes == B.finishes
        Appointment appointmentFoundOne = entityManager.find(Appointment.class, a1.getId());
        Appointment appointmentFoundTwo = entityManager.find(Appointment.class, a2.getId());

        appointmentFoundOne.setStartsAt(parseDateTime("08:00 01/01/2024"));
        appointmentFoundOne.setFinishesAt(parseDateTime("08:30 01/01/2024"));

        appointmentFoundTwo.setStartsAt(parseDateTime("08:15 01/01/2024"));
        appointmentFoundTwo.setFinishesAt(parseDateTime("08:30 01/01/2024"));
    }

    @Test
    public void testOverlappingWhenStartsBeforeAndFinishesWithin() {

        // Case 3: A.starts < B.finishes && B.finishes < A.finishes
        Appointment appointmentFoundOne = entityManager.find(Appointment.class, a1.getId());
        Appointment appointmentFoundTwo = entityManager.find(Appointment.class, a2.getId());

        appointmentFoundOne.setStartsAt(parseDateTime("08:00 01/01/2024"));
        appointmentFoundOne.setFinishesAt(parseDateTime("08:30 01/01/2024"));

        appointmentFoundTwo.setStartsAt(parseDateTime("08:00 01/01/2024"));
        appointmentFoundTwo.setFinishesAt(parseDateTime("08:15 01/01/2024"));

        assertThat(appointmentFoundOne.getStartsAt()).isBefore(appointmentFoundTwo.getFinishesAt());
        assertThat(appointmentFoundTwo.getFinishesAt()).isBefore(appointmentFoundOne.getFinishesAt());
    }

    @Test
    public void testOverlappingWhenStartsAfterAndFinishesWithin() {

        // Case 4: B.starts < A.starts && A.finishes < B.finishes
        Appointment appointmentFoundOne = entityManager.find(Appointment.class, a1.getId());
        Appointment appointmentFoundTwo = entityManager.find(Appointment.class, a2.getId());

        appointmentFoundOne.setStartsAt(parseDateTime("08:15 01/01/2024"));
        appointmentFoundOne.setFinishesAt(parseDateTime("08:30 01/01/2024"));

        appointmentFoundTwo.setStartsAt(parseDateTime("08:00 01/01/2024"));
        appointmentFoundTwo.setFinishesAt(parseDateTime("09:00 01/01/2024"));

        assertThat(appointmentFoundTwo.getStartsAt()).isBefore(appointmentFoundOne.getStartsAt());
        assertThat(appointmentFoundOne.getFinishesAt()).isBefore(appointmentFoundTwo.getFinishesAt());
    }

}
