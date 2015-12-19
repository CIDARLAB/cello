package org.cellocad.adaptors.ucfadaptor;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Created by Bryan Der on 6/24/15.
 */
public class collection_reader_gates_test {


    /**
     * SBOLPartWriter of computeLogic method, of class BooleanLogic.
     */
    @Test
    public void testGateNameAllowedCharacters() {

        Pattern p = Pattern.compile("[^a-zA-Z0-9_]");

        assertTrue(p.matcher("A1 AmtR").find());
        assertFalse(p.matcher("A1_AmtR").find());

    }
}
