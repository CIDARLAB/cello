package org.cellocad.adaptors.ucfwriters.ucf_writers_Eco3C3G3T3;

/**
 * Created by Bryan Der on 5/28/15.
 */

import org.cellocad.MIT.dnacompiler.Args;

import java.util.ArrayList;
import java.util.Map;

public class collection_writer {

    public ArrayList<Map> getObjects() {

        return new ArrayList<Map>();
    }

    public static String getRootPath() {
        Args options = new Args();
        return options.get_home();
    }
}
