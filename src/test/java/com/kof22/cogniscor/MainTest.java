package com.kof22.cogniscor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic tests for Cogniscor application.
 */
public class MainTest {
    
    @Test
    public void testMainClassExists() {
        assertNotNull(Main.class);
    }
    
    @Test
    public void testMainHasMainMethod() {
        try {
            Main.class.getMethod("main", String[].class);
        } catch (NoSuchMethodException e) {
            fail("Main class should have a main method");
        }
    }
}
