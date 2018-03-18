package io.github.aerodlyn.atsl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.github.aerodlyn.atsl.ATSLValue.COMPARE;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class to ensure the functionality of ATSLBool specific operations.
 *
 * @author  Patrick Jahnig
 * @version 2018.03.14
 */
public class ATSLBoolTest {
    private ATSLBool fBool, sBool, tBool;

    /**
     * Initializes the test values before each test run.
     */
    @BeforeEach
    public void init() {
        fBool = new ATSLBool(false);
        tBool = new ATSLBool(true);

        sBool = new ATSLBool("tRuE");
    }

    /**
     * Ensures that comparing two boolean values with any operator other than '==' or '!='
     *  throws an error. Then ensures that the correct value is returned using the allowed
     *  operators.
     */
    @Test
    public void testCompare() {
        for (COMPARE op : COMPARE.values()) {
            if (op == COMPARE.EQT || op == COMPARE.NEQ)
                continue;

            assertThrows(UnsupportedOperationException.class, () -> {
                ATSLValue.compare(fBool, tBool, op);
            });
        }

        assertFalse(ATSLValue.compare(fBool, tBool, COMPARE.EQT).getValue());
        assertTrue(ATSLValue.compare(sBool, tBool, COMPARE.EQT).getValue());
        assertTrue(ATSLValue.compare(fBool, tBool, COMPARE.NEQ).getValue());
    }

    /**
     * Ensures that an ATSLBool value returns the correct Boolean value that should be stored
     *  within it.
     */
    @Test
    public void testGetValue() {
        Object f = fBool.getValue(), s = sBool.getValue(), t = tBool.getValue();

        assertTrue(f instanceof Boolean && s instanceof Boolean && t instanceof Boolean);
        assertFalse((Boolean) f);
        assertTrue((Boolean) s);
        assertTrue((Boolean) t);
    }
}