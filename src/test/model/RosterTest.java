package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RosterTest {
    Roster roster;

    @BeforeEach
    public void runBefore() {
        roster = new Roster();
    }

    @Test
    public void testConstructor() {
        assertEquals(0, roster.roster.size());
    }
}