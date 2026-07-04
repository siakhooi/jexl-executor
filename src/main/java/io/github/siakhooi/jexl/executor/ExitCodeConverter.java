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
        return switch (num) {
            case Double d -> floatingPointToInt(d, value);
            case Float f -> floatingPointToInt(f.doubleValue(), value);
            case BigDecimal bd -> bigDecimalToInt(bd, value);
            case BigInteger bi -> bigIntegerToInt(bi, value);
            default -> integralNumberToInt(num, value);
        };
    }

    private static int floatingPointToInt(double d, Object value) {
        if (Double.isNaN(d) || Double.isInfinite(d) || d != Math.rint(d)) {
            throw new IllegalArgumentException("Exit code expression must yield an integral number, got " + value);
        }
        return doubleWholeToInt(d);
    }

    private static int bigDecimalToInt(BigDecimal bd, Object value) {
        try {
            return bd.toBigIntegerExact().intValueExact();
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Exit code expression must yield an integral number, got " + value, e);
        }
    }

    private static int bigIntegerToInt(BigInteger bi, Object value) {
        try {
            return bi.intValueExact();
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Exit code expression out of int range: " + value, e);
        }
    }

    private static int integralNumberToInt(Number num, Object value) {
        long lv = num.longValue();
        if (lv != (int) lv) {
            throw new IllegalArgumentException("Exit code expression out of int range: " + value);
        }
        return (int) lv;
    }

    private static int doubleWholeToInt(double d) {
        long lv = (long) d;
        if (lv != (int) lv) {
            throw new IllegalArgumentException("Exit code expression out of int range: " + d);
        }
        return (int) lv;
    }
}
