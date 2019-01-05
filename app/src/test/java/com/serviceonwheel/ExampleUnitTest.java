package com.serviceonwheel;

import com.serviceonwheel.utils.Utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void emailValidator_CorrectEmailSimple_ReturnsTrue() {
        assertTrue(Utils.isValidEmail("name@email.com"));
    }

    @Test
    public void emailValidator_InvalidEmailNoTld_ReturnsFalse() {
        assertFalse(Utils.isValidEmail("fh@fh"));
    }
}