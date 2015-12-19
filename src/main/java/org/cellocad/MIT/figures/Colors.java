package org.cellocad.MIT.figures;
/**
 * Created by Bryan Der on 3/26/14.
 */

import java.util.HashMap;

/***********************************************************************
 Synopsis    [ Functions to make circuit figures. ]

 printGraphviz4GIF and printGnuplotXfer: wiring diagram with transfer functions (gnuplot) as node images.
 printGraphvizText: wiring diagram for AGRN or GRN
 printCirdnaText:   plasmid figure
 printCirdnaTextCRISPRi: specific for CRISPRi circuits

 ***********************************************************************/

public class Colors {

    public static void setColors() {

        //abstract circuit
        _DEFAULTHEX.put("0",  "000000");  //black          rgb(31,120,180)
        _DEFAULTHEX.put("1",  "1F78B4");  //blue           rgb(31,120,180)
        _DEFAULTHEX.put("2",  "33A02C");  //green          rgb(51,160,44)
        _DEFAULTHEX.put("3",  "E31A1C");  //red            rgb(227,26,28)
        _DEFAULTHEX.put("4",  "FF7F00");  //orange         rgb(255,127,0)
        _DEFAULTHEX.put("5",  "6A3D9A");  //purple         rgb(106,61,154)
        _DEFAULTHEX.put("6",  "A6CEE3");  //light blue     rgb(166,206,227)
        _DEFAULTHEX.put("7",  "B2DF8A");  //light green    rgb(178,223,138)
        _DEFAULTHEX.put("8",  "FB9A99");  //light red      rgb(251,154,153)
        _DEFAULTHEX.put("9",  "FDBF6F");  //light orange   rgb(253,191,111)
        _DEFAULTHEX.put("10", "CAB2D6");  //light purple   rgb(202,178,214)
        _DEFAULTHEX.put("11", "FF00FF");
        _DEFAULTHEX.put("12", "964B00");

    }

    public static HashMap<String, String> _DEFAULTHEX = new HashMap<String, String>();

};
