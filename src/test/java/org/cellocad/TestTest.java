package org.cellocad;

import org.cellocad.MIT.dnacompiler.GateLibrary;
import org.cellocad.MIT.dnacompiler.Roadblock;
import org.junit.Test;

import java.util.ArrayList;

public class TestTest {


    /**
     * public void
     * no arguments
     * @Test annotation
     */
    //@Test
    public void nameOfTestFn() {


        Roadblock rb = new Roadblock();


        //get arraylist of strings
        //get gate library

        ArrayList<String> eugene_part_rules = new ArrayList<>();
        eugene_part_rules.add("STARTSWITH pTac");
        eugene_part_rules.add("STARTSWITH pBAD");
        eugene_part_rules.add("STARTSWITH pPhlF");
        eugene_part_rules.add("STARTSWITH pSrpR");
        eugene_part_rules.add("STARTSWITH pBM3R1");
        eugene_part_rules.add("STARTSWITH pQacR");


        GateLibrary gate_library = new GateLibrary(0,0);



        rb.set_roadblockers(eugene_part_rules, gate_library);


        //check a bunch of assignments for correct roadblocking decision.


        assert(true);


    }
}
