package org.cellocad.MIT.dnacompiler;


import org.junit.Test;

public class TestRegex {

    @Test
    public void testRegex() {
        String regex = "[a-zA-Z][0-9a-zA-Z_]+";

        String query = "a";
        System.out.println(query.matches(regex));

        query = "job_0x01";
        System.out.println(query.matches(regex));

        query = "_job_0x01";
        System.out.println(query.matches(regex));

    }
}
