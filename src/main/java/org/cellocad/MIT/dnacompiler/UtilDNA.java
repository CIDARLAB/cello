package org.cellocad.MIT.dnacompiler;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Bryan Der on 9/15/14.
 */
public class UtilDNA {

    /***********************************************************************

     Synopsis    [  ]

     Reverse complement of DNA sequence

     ***********************************************************************/
    public static String getReverseComplement(String seq){
        int nchar = seq.length();
        char rev_comp_nucs [] = new char [nchar];
        for(int i=0; i<nchar; ++i){
            rev_comp_nucs[nchar-1-i] = getPairingNucleotide(seq.charAt(i));
        }
        String s_rev_comp = new String(String.valueOf(rev_comp_nucs));

        return s_rev_comp;
    }

    /***********************************************************************

     Synopsis    [  ]

     ***********************************************************************/
    public static char getPairingNucleotide(char nuc){
        char pair_nuc = ' ';
        if (nuc == 'A'){
            pair_nuc = 'T';
        } else if (nuc == 'T'){
            pair_nuc = 'A';
        }else if (nuc == 'G'){
            pair_nuc = 'C';
        }else if (nuc == 'C'){
            pair_nuc = 'G';
        }else if (nuc == 'a'){
            pair_nuc = 't';
        }else if (nuc == 't'){
            pair_nuc = 'a';
        }else if (nuc == 'g'){
            pair_nuc = 'c';
        }else if (nuc == 'c'){
            pair_nuc = 'g';
        }else if (nuc == 'U'){
            pair_nuc = 'A';
        }else if (nuc == 'u'){
            pair_nuc = 'a';
        }
        return pair_nuc;
    }


    /***********************************************************************

     Synopsis    [  ]

     Remove file extension (used for ape LOCUS)

     ***********************************************************************/
    public static String getBaseName(String file){
        File verilog_file = new File(file);
        String filename =  verilog_file.getName();
        String[] tokens = filename.split("\\.(?=[^\\.]+$)");
        return tokens[0];
    }

    /***********************************************************************

     Synopsis    [  ]

     Date (used for ape LOCUS)

     ***********************************************************************/
    public static String getDate4GenBank(){
        DateFormat date_format = new SimpleDateFormat("dd-MMM-yyyy");
        String date = date_format.format(new Date());
        return date;
    }
}
