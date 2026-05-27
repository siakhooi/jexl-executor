package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

class ExitCodeConverterTest {

    @Test
    void toProcessExitCode_acceptsWholeNumbers() {
        assertEquals(0, ExitCodeConverter.toProcessExitCode(0));
        assertEquals(42, ExitCodeConverter.toProcessExitCode(42));
        assertEquals(3, ExitCodeConverter.toProcessExitCode(3L));
        assertEquals(3, ExitCodeConverter.toProcessExitCode((short) 3));
        assertEquals(3, ExitCodeConverter.toProcessExitCode((byte) 3));
        assertEquals(7, ExitCodeConverter.toProcessExitCode(7.0d));
        assertEquals(7, ExitCodeConverter.toProcessExitCode(7.0f));
        assertEquals(9, ExitCodeConverter.toProcessExitCode(BigDecimal.valueOf(9)));
        assertEquals(11, ExitCodeConverter.toProcessExitCode(BigInteger.valueOf(11)));
    }

    @Test
    void toProcessExitCode_rejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> ExitCodeConverter.toProcessExitCode(null));
    }

    @Test
    void toProcessExitCode_rejectsString() {
        assertThrows(IllegalArgumentException.class, () -> ExitCodeConverter.toProcessExitCode("1"));
    }

    @Test
    void toProcessExitCode_rejectsFractionalDouble() {
        assertThrows(IllegalArgumentException.class, () -> ExitCodeConverter.toProcessExitCode(1.5d));
    }

    @Test
    void toProcessExitCode_rejectsFractionalBigDecimal() {
        assertThrows(IllegalArgumentException.class, () -> ExitCodeConverter.toProcessExitCode(new BigDecimal("2.1")));
    }
}
