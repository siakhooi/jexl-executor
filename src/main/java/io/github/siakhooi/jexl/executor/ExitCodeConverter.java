package io.github.siakhooi.jexl.executor;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Converts a JEXL evaluation result into a Java {@code int} exit code.
 */
final class ExitCodeConverter {

    private ExitCodeConverter() {
    }

    /**
     * @throws IllegalArgumentException if the value is null, not a number, non-integral, NaN/infinite, or outside int range
     */
    static int toProcessExitCode(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Exit code expression evaluated to null");
        }
        if (!(value instanceof Number num)) {
            throw new IllegalArgumentException(
                    "Exit code expression must yield a number, got " + value.getClass().getSimpleName());
        }
        if (num instanceof Double d) {
            if (Double.isNaN(d) || Double.isInfinite(d) || d != Math.rint(d)) {
                throw new IllegalArgumentException("Exit code expression must yield an integral number, got " + value);
            }
            return doubleWholeToInt(d);
        }
        if (num instanceof Float f) {
            if (Float.isNaN(f) || Float.isInfinite(f) || f != Math.rint(f)) {
                throw new IllegalArgumentException("Exit code expression must yield an integral number, got " + value);
            }
            return doubleWholeToInt(f.doubleValue());
        }
        if (num instanceof BigDecimal bd) {
            try {
                return bd.toBigIntegerExact().intValueExact();
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException("Exit code expression must yield an integral number, got " + value,
                        e);
            }
        }
        if (num instanceof BigInteger bi) {
            try {
                return bi.intValueExact();
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException("Exit code expression out of int range: " + value, e);
            }
        }
        long lv = num.longValue();
        if (lv != (long) (int) lv) {
            throw new IllegalArgumentException("Exit code expression out of int range: " + value);
        }
        return (int) lv;
    }

    private static int doubleWholeToInt(double d) {
        long lv = (long) d;
        if (lv != (long) (int) lv) {
            throw new IllegalArgumentException("Exit code expression out of int range: " + d);
        }
        return (int) lv;
    }
}
