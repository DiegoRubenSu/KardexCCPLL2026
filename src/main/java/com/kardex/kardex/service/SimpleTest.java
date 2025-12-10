package com.kardex.kardex.service;

import org.junit.Test;
import static org.junit.Assert.*;

public class SimpleTest {

    @Test
    public void testSumaSimple() {
        assertEquals("2 + 2 deberia ser 4", 4, 2 + 2); // JUnit 4: orden diferente
    }

    @Test
    public void testTrying() {
        String texto = "Inventario";
        assertTrue(true);
    }
}