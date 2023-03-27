package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/* adapted from CPSC 210 AlarmSystem at https://github.students.cs.ubc.ca/CPSC210/AlarmSystem */

/**
 * Unit tests for the Event class
 */
public class EventTest {
	private Event e;
	private Date d;
	
	//NOTE: these tests might fail if time at which line (2) below is executed
	//is different from time that line (1) is executed.  Lines (1) and (2) must
	//run in same millisecond for this test to make sense and pass.
	
	@BeforeEach
	public void runBefore() {
		e = new Event("Sensor open at door");   // (1)
		d = Calendar.getInstance().getTime();   // (2)
	}
	
	@Test
	public void testEvent() {
		assertEquals("Sensor open at door", e.getDescription());
		assertTrue(d.getTime() - e.getDate().getTime() < 19);
	}

	@Test
	public void testToString() {
		assertEquals(d.toString() + "\n" + "Sensor open at door", e.toString());
	}

    @Test
    public void testEventEqualsNull() {
        assertFalse(e.equals(null));
    }

    @Test
    public void testEventEqualsDifferentClass() {
        assertNotEquals(e, new Object());
    }

    @Test
    public void testEventEqualsDifferentEvent() {
        assertNotEquals(e, new Event("Sensor open at window"));
    }

    @Test
    public void testEventEqualsSameEvent() {
        assertEquals(e, e);
    }

    @Test
    public void testEventEqualsSameEventDifferentDate() {
        try {
            Thread.sleep(20);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        Event e2 = new Event("Sensor open at door");
        assertNotEquals(e, e2);
    }

    @Test
    public void testEventHashcode() {
        assertEquals(13 * e.getDate().hashCode() + e.getDescription().hashCode(), e.hashCode());
    }
}
