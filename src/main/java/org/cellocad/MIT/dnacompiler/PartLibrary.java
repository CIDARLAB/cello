package org.cellocad.MIT.dnacompiler;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

public class PartLibrary {

    @Getter
    @Setter
    private HashMap<String, Part> _ALL_PARTS = new HashMap<String, Part>(); //map name to Part
    
    
    @Getter
    @Setter
    private ArrayList<Part> _scars = new ArrayList<>();


    public void set_scars() {
        ArrayList<Part> scars = new ArrayList<>();

        scars.add(new Part("Escar", "scar", "gctt")); //module begin
        scars.add(new Part("Xscar", "scar", "tgtc"));
        scars.add(new Part("Vscar", "scar", "tctg"));
        scars.add(new Part("Uscar", "scar", "gggc"));
//        scars.add(new Part("Rscar", "scar", "gtaa"));
        scars.add(new Part("Fscar", "scar", "cgct"));
        scars.add(new Part("Dscar", "scar", "aggt"));
        scars.add(new Part("Bscar", "scar", "tacg"));
        scars.add(new Part("Ascar", "scar", "ggag"));
        scars.add(new Part("Yscar", "scar", "attg"));
        scars.add(new Part("Qscar", "scar", "gagt"));
        scars.add(new Part("Pscar", "scar", "ccta"));
        scars.add(new Part("Oscar", "scar", "atgc"));
        scars.add(new Part("Nscar", "scar", "tcca"));
        scars.add(new Part("Mscar", "scar", "ccgt"));
        scars.add(new Part("Lscar", "scar", "gtta"));
        scars.add(new Part("Cscar", "scar", "aatg")); //module end

        for(Part scar: scars) {
            _ALL_PARTS.put(scar.get_name(), scar);
        }

        _scars = scars;

    }
}
