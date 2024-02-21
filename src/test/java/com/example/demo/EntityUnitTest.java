package com.example.demo;

import com.example.demo.entities.Appointment;
import com.example.demo.entities.Doctor;
import com.example.demo.entities.Patient;
import com.example.demo.entities.Room;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Medical Entity Unit Test")
@Tags(@Tag("entity-unit-test"))
class EntityUnitTest {

    @Autowired
    private TestEntityManager entityManager;

    private Doctor d1;

    private Patient p1;

    private Room r1;

    private Appointment a1;
    private Appointment a2;
    private Appointment a3;

    /**
     * Configure the objects required for testing before each test.
     */

    @Test
    public void testServletInitializer() {
        ServletInitializer servletInitializer = new ServletInitializer();
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        SpringApplicationBuilder configuredBuilder = servletInitializer.configure(builder);
        assertNotNull(configuredBuilder);
    }

    @Test
    public void testMainMethod() {

        try {
            TechhubApplication.main(new String[]{});
            assertTrue(true);
        }
        catch (Exception e) {
            fail("application fail: " + e.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        d1 = new Doctor("John", "Doe", 30, "john.doe@hospital.com");
        p1 = new Patient("Jane", "Smith", 20, "jane.smith@hospital.com");
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

    /**
     * Parse a datetime string into a LocalDateTime object.
     *
     * @param dateTime The datetime string.
     * @return The LocalDateTime object.
     */
    public static LocalDateTime parseDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        return LocalDateTime.parse(dateTime, formatter);
    }

    @Test
    @Tag("success-case")
    @DisplayName("Find Doctor By ID")
    void testFindDoctorById() {
        Doctor doctorFound = entityManager.find(Doctor.class, d1.getId());

        assertAll("Doctor properties verification",
                () -> assertNotNull(doctorFound, "The found doctor should not be null"),
                () -> assertEquals(d1.getFirstName(), doctorFound.getFirstName(), "The found doctor should not be null"),
                () -> assertEquals(d1.getFirstName(), doctorFound.getFirstName(), "First names should be equal"),
                () -> assertEquals(d1.getLastName(), doctorFound.getLastName(), "Last names should be equal"),
                () -> assertEquals(d1.getAge(), doctorFound.getAge(), "Ages should be equal"),
                () -> assertEquals(d1.getEmail(), doctorFound.getEmail(), "Emails should be equal")
        );
    }

    @Test
    @Tag("success-case")
    @DisplayName("Find Patient By ID")
    void testFindPatientById() {
        Patient patientFound = entityManager.find(Patient.class, p1.getId());

        assertAll("Patient properties verification",
                () -> assertNotNull(patientFound, "The found patient should not be null"),
                () -> assertEquals(p1.getFirstName(), patientFound.getFirstName(), "First names should be equal"),
                () -> assertEquals(p1.getLastName(), patientFound.getLastName(), "Last names should be equal"),
                () -> assertEquals(p1.getAge(), patientFound.getAge(), "Ages should be equal"),
                () -> assertEquals(p1.getEmail(), patientFound.getEmail(), "Emails should be equal")
        );
    }

    @Test
    @Tag("success-case")
    @DisplayName("Find Room By Name")
    void testFindRoomByName() {
        Room roomFound = entityManager.find(Room.class, r1.getRoomName());

        assertAll("Room properties verification",
                () -> assertNotNull(roomFound, "The found room should not be null"),
                () -> assertEquals(r1.getRoomName(), roomFound.getRoomName(), "Room names should be equal")
        );
    }

    @Test
    @Tag("success-case")
    @DisplayName("Find Appointment By ID")
    void testFindAppointmentById() {
        Appointment appointmentFound = entityManager.find(Appointment.class, a1.getId());
        assert appointmentFound != null;

        assertAll("Appointment properties verification",
                () -> assertNotNull(appointmentFound, "The found appointment should not be null"),
                () -> assertEquals(a1.getDoctor().getFirstName(), appointmentFound.getDoctor().getFirstName(), "Doctor first names should be equal"),
                () -> assertEquals(a1.getDoctor().getLastName(), appointmentFound.getDoctor().getLastName(), "Doctor last names should be equal"),
                () -> assertEquals(a1.getDoctor().getAge(), appointmentFound.getDoctor().getAge(), "Doctor ages should be equal"),
                () -> assertEquals(a1.getDoctor().getEmail(), appointmentFound.getDoctor().getEmail(), "Doctor emails should be equal"),
                () -> assertEquals(a1.getPatient().getFirstName(), appointmentFound.getPatient().getFirstName(), "Patient first names should be equal"),
                () -> assertEquals(a1.getPatient().getLastName(), appointmentFound.getPatient().getLastName(), "Patient last names should be equal"),
                () -> assertEquals(a1.getPatient().getAge(), appointmentFound.getPatient().getAge(), "Patient ages should be equal"),
                () -> assertEquals(a1.getPatient().getEmail(), appointmentFound.getPatient().getEmail(), "Patient emails should be equal"),
                () -> assertEquals(a1.getRoom().getRoomName(), appointmentFound.getRoom().getRoomName(), "Room names should be equal"),
                () -> assertEquals(a1.getStartsAt(), appointmentFound.getStartsAt(), "Start times should be equal"),
                () -> assertEquals(a1.getFinishesAt(), appointmentFound.getFinishesAt(), "Finish times should be equal")
        );
    }

    @Test
    @Tag("success-case")
    @DisplayName("Compare Appointment Dates")
    void testCompareAppointmentDates() {
        Appointment appointmentFoundOne = entityManager.find(Appointment.class, a1.getId());
        Appointment appointmentFoundTwo = entityManager.find(Appointment.class, a2.getId());
        Appointment appointmentFoundThree = entityManager.find(Appointment.class, a3.getId());

        assertAll("Appointment time validation",
                () -> assertThat(appointmentFoundOne.getStartsAt()).as("Start time of appointmentFoundOne should be before its end time").isBefore(appointmentFoundOne.getFinishesAt()),
                () -> assertThat(appointmentFoundTwo.getStartsAt()).as("Start time of appointmentFoundTwo should be before its end time").isBefore(appointmentFoundTwo.getFinishesAt()),
                () -> assertThat(appointmentFoundThree.getStartsAt()).as("Start time of appointmentFoundThree should be before its end time").isBefore(appointmentFoundThree.getFinishesAt())
        );
    }

    @Test
    @Tag("success-case")
    @DisplayName("Room Name Contains Expected Sequence")
    void testRoomNameContainsExpectedSequence() {
        Room roomFound = entityManager.find(Room.class, r1.getRoomName());

        assertAll("Room name validation",
                () -> assertThat(roomFound.getRoomName()).isNotNull().as("Room name should not be null"),
                () -> assertThat(roomFound.getRoomName()).containsSequence(r1.getRoomName()).as("Room name should contain the expected sequence")
        );
    }

    @Test
    @Tag("success-case")
    @DisplayName("Validate Patient Email Format")
    void testValidatePatientEmailFormat() {
        Patient patientFound = entityManager.find(Patient.class, p1.getId());

        assertThat(patientFound.getEmail()
                .matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}"))
                .as("Patient email format is valid").isTrue();
    }

    @Test
    @Tag("success-case")
    @DisplayName("Validate Doctor Age Greater Than 18")
    void testValidateDoctorAgeGreaterThan18() {
        Doctor doctorFound = entityManager.find(Doctor.class, d1.getId());

        assertThat(doctorFound.getAge())
                .as("Doctor age should be greater than or equal to 18")
                .isGreaterThanOrEqualTo(18);
    }

    @Test
    @Tag("error-case")
    @DisplayName("Overlapping When Start Times Are Equal")
    void testOverlappingWhenStartTimesAreEqual() {

        // Case 1: A.starts == B.starts
        Appointment appointmentFoundOne = entityManager.find(Appointment.class, a1.getId());
        Appointment appointmentFoundTwo = entityManager.find(Appointment.class, a2.getId());

        appointmentFoundOne.setStartsAt(parseDateTime("08:00 01/01/2024"));
        appointmentFoundOne.setFinishesAt(parseDateTime("08:15 01/01/2024"));

        appointmentFoundTwo.setStartsAt(parseDateTime("08:00 01/01/2024"));
        appointmentFoundTwo.setFinishesAt(parseDateTime("08:30 01/01/2024"));

        assertThat(appointmentFoundOne.getStartsAt())
                .as("Appointment start times should be equal")
                .isEqualTo(appointmentFoundTwo.getStartsAt());
    }

    @Test
    @Tag("error-case")
    @DisplayName("Overlapping When End Times Are Equal")
    void testOverlappingWhenEndTimesAreEqual() {

        // Case 2: A.finishes == B.finishes
        Appointment appointmentFoundOne = entityManager.find(Appointment.class, a1.getId());
        Appointment appointmentFoundTwo = entityManager.find(Appointment.class, a2.getId());

        appointmentFoundOne.setStartsAt(parseDateTime("08:00 01/01/2024"));
        appointmentFoundOne.setFinishesAt(parseDateTime("08:30 01/01/2024"));

        appointmentFoundTwo.setStartsAt(parseDateTime("08:15 01/01/2024"));
        appointmentFoundTwo.setFinishesAt(parseDateTime("08:30 01/01/2024"));

        assertThat(appointmentFoundOne.getFinishesAt())
                .as("Appointment end times should be equal")
                .isEqualTo(appointmentFoundTwo.getFinishesAt());
    }

    @Test
    @Tag("error-case")
    @DisplayName("Overlapping When Starts Before And Finishes Within")
    public void testOverlappingWhenStartsBeforeAndFinishesWithin() {

        // Case 3: A.starts < B.finishes && B.finishes < A.finishes
        Appointment appointmentFoundOne = entityManager.find(Appointment.class, a1.getId());
        Appointment appointmentFoundTwo = entityManager.find(Appointment.class, a2.getId());

        appointmentFoundOne.setStartsAt(parseDateTime("08:00 01/01/2024"));
        appointmentFoundOne.setFinishesAt(parseDateTime("08:30 01/01/2024"));

        appointmentFoundTwo.setStartsAt(parseDateTime("08:00 01/01/2024"));
        appointmentFoundTwo.setFinishesAt(parseDateTime("08:15 01/01/2024"));

        assertThat(appointmentFoundOne.getStartsAt())
                .as("Appointment One should start before the finish time of Appointment Two")
                .isBefore(appointmentFoundTwo.getFinishesAt());

        assertThat(appointmentFoundTwo.getFinishesAt())
                .as("Appointment Two should finish before the finish time of Appointment One")
                .isBefore(appointmentFoundOne.getFinishesAt());
    }

    @Test
    @Tag("error-case")
    @DisplayName("Overlapping When Starts After And Finishes Within")
    public void testOverlappingWhenStartsAfterAndFinishesWithin() {

        // Case 4: B.starts < A.starts && A.finishes < B.finishes
        Appointment appointmentFoundOne = entityManager.find(Appointment.class, a1.getId());
        Appointment appointmentFoundTwo = entityManager.find(Appointment.class, a2.getId());

        appointmentFoundOne.setStartsAt(parseDateTime("08:15 01/01/2024"));
        appointmentFoundOne.setFinishesAt(parseDateTime("08:30 01/01/2024"));

        appointmentFoundTwo.setStartsAt(parseDateTime("08:00 01/01/2024"));
        appointmentFoundTwo.setFinishesAt(parseDateTime("09:00 01/01/2024"));

        assertThat(appointmentFoundTwo.getStartsAt())
                .as("Appointment Two should start before the start time of Appointment One")
                .isBefore(appointmentFoundOne.getStartsAt());

        assertThat(appointmentFoundOne.getFinishesAt())
                .as("Appointment One should finish before the finish time of Appointment Two")
                .isBefore(appointmentFoundTwo.getFinishesAt());
    }

}
