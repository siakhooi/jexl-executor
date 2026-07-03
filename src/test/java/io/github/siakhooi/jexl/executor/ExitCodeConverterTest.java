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
        assertThrows(IllegalArgumentException.class, this::rejectNullValue);
    }

    @Test
    void toProcessExitCode_rejectsString() {
        assertThrows(IllegalArgumentException.class, this::rejectStringValue);
    }

    @Test
    void toProcessExitCode_rejectsFractionalDouble() {
        assertThrows(IllegalArgumentException.class, this::rejectFractionalDouble);
    }

    @Test
    void toProcessExitCode_rejectsFractionalBigDecimal() {
        assertThrows(IllegalArgumentException.class, this::rejectFractionalBigDecimal);
    }

    private void rejectNullValue() {
        ExitCodeConverter.toProcessExitCode(null);
    }

    private void rejectStringValue() {
        ExitCodeConverter.toProcessExitCode("1");
    }

    private void rejectFractionalDouble() {
        ExitCodeConverter.toProcessExitCode(1.5d);
    }

    private void rejectFractionalBigDecimal() {
        ExitCodeConverter.toProcessExitCode(new BigDecimal("2.1"));
    }
}
