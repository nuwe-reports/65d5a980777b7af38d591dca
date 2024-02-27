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
        } catch (Exception e) {
            fail("application fail: " + e.getMessage());
        }
    }

    /**
     * Configure the objects required for testing before each test.
     */
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

    void setUpAppointments(Appointment appointment1, Appointment appointment2, String startsAt1, String finishesAt1, String startsAt2, String finishesAt2) {
        appointment1.setStartsAt(parseDateTime(startsAt1));
        appointment1.setFinishesAt(parseDateTime(finishesAt1));
        appointment2.setStartsAt(parseDateTime(startsAt2));
        appointment2.setFinishesAt(parseDateTime(finishesAt2));
    }

    @Test
    @DisplayName("Detects overlap when appointments start at the same time")
    void shouldDetectOverlapWhenAppointmentsStartAtTheSameTime() {

        // Case 1: A.starts == B.starts
        Appointment appointment1 = entityManager.find(Appointment.class, a1.getId());
        Appointment appointment2 = entityManager.find(Appointment.class, a2.getId());

        setUpAppointments(appointment1, appointment2, "08:00 01/01/2024", "08:15 01/01/2024", "08:00 01/01/2024", "08:30 01/01/2024");
        assertTrue(appointment1.overlaps(appointment2), "Appointments should overlap");

        appointment2.setStartsAt(parseDateTime("09:00 01/01/2024"));
        assertFalse(appointment1.overlaps(appointment2), "Appointments should not overlap when start times differ");

        appointment2.setFinishesAt(parseDateTime("08:10 01/01/2024"));
        assertFalse(appointment2.overlaps(appointment1));
    }

    @Test
    @DisplayName("Detects overlap when appointments end at the same time")
    void shouldDetectOverlapWhenAppointmentsEndAtTheSameTime() {

        // Case 2: A.finishes == B.finishes
        Appointment appointment1 = entityManager.find(Appointment.class, a1.getId());
        Appointment appointment2 = entityManager.find(Appointment.class, a2.getId());

        setUpAppointments(appointment1, appointment2, "08:00 01/01/2024", "08:30 01/01/2024", "08:15 01/01/2024", "08:30 01/01/2024");
        assertTrue(appointment1.overlaps(appointment2), "Overlaps when end times are equal");
    }

    @Test
    @DisplayName("Detects overlap when appointment starts after and finishes within another")
    public void shouldDetectOverlapWhenAppointmentStartsAfterAndEndsWithinAnother() {

        // Case 3: B.starts < A.starts && A.finishes < B.finishes
        Appointment appointment1 = entityManager.find(Appointment.class, a1.getId());
        Appointment appointment2 = entityManager.find(Appointment.class, a2.getId());

        setUpAppointments(appointment1, appointment2, "09:30 01/01/2024", "10:30 01/01/2024", "09:00 01/01/2024", "10:00 01/01/2024");
        assertTrue(appointment1.overlaps(appointment2), "Appointments should not overlap when the second starts after and finishes within the first");
    }

    @Test
    @DisplayName("Detects overlap when appointment starts within and finishes after another")
    public void shouldDetectOverlapWhenAppointmentStartsWithinAndEndsAfterAnother() {
        // Case 4: B.starts < A.starts && A.finishes < B.finishes
        Appointment appointment1 = entityManager.find(Appointment.class, a1.getId());
        Appointment appointment2 = entityManager.find(Appointment.class, a2.getId());

        setUpAppointments(appointment1, appointment2, "09:30 01/01/2024", "10:30 01/01/2024", "10:00 01/01/2024", "11:00 01/01/2024");
        assertTrue(appointment1.overlaps(appointment2));
    }

    @Test
    @DisplayName("Does not detect overlap when one appointment fully encloses another")
    public void shouldNotDetectOverlapWhenAppointmentFullyEnclosesAnother() {
        // Case 4: B.starts < A.starts && A.finishes < B.finishes
        Appointment appointment1 = entityManager.find(Appointment.class, a1.getId());
        Appointment appointment2 = entityManager.find(Appointment.class, a2.getId());

        setUpAppointments(appointment1, appointment2, "09:00 01/01/2024", "10:30 01/01/2024", "08:00 01/01/2024", "11:00 01/01/2024");
        assertFalse(appointment1.overlaps(appointment2));
    }

}


