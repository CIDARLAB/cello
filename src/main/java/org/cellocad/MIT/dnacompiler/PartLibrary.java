package org.cellocad.MIT.dnacompiler;


import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;

public class PartLibrary {

    @Getter
    @Setter
    private HashMap<String, Part> _ALL_PARTS = new HashMap<String, Part>(); //map name to Part
    
    
    @Getter
    @Setter
    private ArrayList<Part> _scars = new ArrayList<>();

    @Getter
    @Setter
    private ArrayList<Part> _terminators = new ArrayList<>();


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

    public void genome_scars() {
        // TODO Auto-generated method stub

        ArrayList<Part> scars = new ArrayList<>();

//		module begin
        scars.add(new Part("Ascar", "scar", "ggag"));
        scars.add(new Part("Bscar", "scar", "tacg"));
        scars.add(new Part("Cscar", "scar", "aatg"));
        scars.add(new Part("Dscar", "scar", "aggt"));
        scars.add(new Part("Escar", "scar", "gctt"));
        scars.add(new Part("Fscar", "scar", "cgct"));
        scars.add(new Part("Gscar", "scar", "tgtc"));
        scars.add(new Part("Hscar", "scar", "attg"));
        scars.add(new Part("Iscar", "scar", "atag"));

//      Second fragment
        scars.add(new Part("Rscar", "scar", "gtaa"));
        scars.add(new Part("Qscar", "scar", "gagt"));
        scars.add(new Part("Pscar", "scar", "ccta"));
        scars.add(new Part("Oscar", "scar", "atgc"));
        scars.add(new Part("Nscar", "scar", "tcca"));
        scars.add(new Part("Mscar", "scar", "ccgt"));
        scars.add(new Part("Lscar", "scar", "gtta"));
        scars.add(new Part("Kscar", "scar", "gggc"));
        scars.add(new Part("Jscar", "scar", "tctg"));


        //module end

        for(Part scar: scars) {
            _ALL_PARTS.put(scar.get_name(), scar);
        }

        _scars = scars;

    }

    public void UCF_scars() {
        // TODO Auto-generated method stub
        ArrayList<Part> scars = new ArrayList<>();
        for (Map.Entry<String, Part> entry: _ALL_PARTS.entrySet() ) {
            Part name = entry.getValue();
            if (name.get_type().equals("scar")) {
                scars.add(name);
            }
        }
        _scars = scars;
    }

    public void yeast_scars() {
        // TODO Auto-generated method stub

        ArrayList<Part> scars = new ArrayList<>();

//		module begin
        scars.add(new Part("Ascar", "scar", "GGTCTCggctaaattcgagtgaaacacaggaagatcagaaaatcctcatttcatccatattaacaataatttcaaatgtttatttgcattatttgaaactaggcaagacaagcaacgaaacgtttttgaaaattttgagtattttcaataaatttgtagaggactcagatattgaaaaaaagctacagcaattaatacttgataagaagagtattgagaagggcaacggttcatcatctcatggatctgcacatgaacaaacaccagagtcaaacgacgttgaaattgaggctactgcgccaattgatgacaatacagacgatgataacaaaccgaagttatctgatgtagaaaaggattaaagatgctaagagatagtgatgatatttcataaataatgtaattctatatatgttaattaccttttttgcgaggcatatttatggtgaaggataagttttgaccatcaaagaaggttaatgtggctgtggtttcagggtccataaagcccacatggataacattacGCTTGCTATGTCGTCGGAGGAGATATTTATTACTTTTATTATTCTAGTTTTTTACAGTTATTTATTAATTAATTATTTTTATATGCATGCGAATAAAAAGTCTATATTTAAGTTCTTTTATTTATTAATACATTTTCCTCTACGAGCTGTCACCGGATGTGCTTTCCGGTCTGATGAGTCCGTGAGGACGAAACAGCCTCTACAAATAATTTTGTTTAAGAGCAGGTTGTTCATGGCCGTGCGTATGATGTGGGGGGCTCGGGCGTTGAAACCGGGGTTCGGAGCGCCAGGGGG"));
        scars.add(new Part("Bscar", "scar", "agCCCCATAGGGTGGTGTGTACCACCCCTGATGAGTCCAAAAGGACGAAATGGGGACTAGGGGGAAGTGAAGGCCCCACTTTGACTTTTCGTAAGTGTGGCGCGAGAATTCCGGGGGGGAGGAGGTGGGGCACACGGGGTGAGGGTTGTG"));
        scars.add(new Part("Cscar", "scar", "AGTAGTCACCGGCTGTGCTTGCCGGTCTGATGAGCCTGTGAAGGCGAAACTAGGCGGCTTAGTGGAGCAAGGGGGCTTGGCGTGATAGGCCGAAATGGTGCTGCCCAGGTCAGCTGTATGTTACGGGCGTTGGGCCGCGGGCGGGTCGGA"));
        scars.add(new Part("Dscar", "scar", "AGCGCTCAACGGGTGTGCTTCCCGTTCTGATGAGTCCGTGAGGACGAAAGCGTCCCGGGAATATCGCGTAGCACTTTAGACTCTTCCTTACGTAATGGATGACTCGGCGCGTGATCTTACGGCCGGGCGACACCTCTCATAGGTGTGCGT"));
        scars.add(new Part("Escar", "scar", "AGGGGTCAGTTGATGTGCTTTCAACTCTGATGAGTCAGTGATGACGAAACCCTCAGCGGAGTTGTTAGCTAGCTTAAGGTTTCAAAGGTTTGATCCGTGAAGGTTATCCTCCTGTGGAAGATTTTTTTTGTCAGAGCTTAGCTTACGCCG"));
        scars.add(new Part("Fscar", "scar", "AGTCGTCAAGTGCTGTGCTTGCACTTCTGATGAGGCAGTGATGCCGAAACGATACAGGATGATGTACATTGTTTTCAGCTGTCCGTGGAGTGTGCTGTGCTCCATGATTTGACTTCGACAGGGGACTCGTCGCTTCTGATGTGTCAGGGG"));
        scars.add(new Part("Gscar", "scar", "AGTACGTCTGAGCGTGATACCCGCTCACTGAAGATGGCCCGGTAGGGCCGAAACGTATAATCTGAAAAGTTGTCATGTCAAGGGCAGAGCCTGGCAACTATAGGCGGTCGACAAGTGGTCCCTTCAATGGGTTTGTGGTGTGTGGGGCAC"));
        scars.add(new Part("Hscar", "scar", "TTGACCTAGCTGCTCTGTCCCTGAAACTCGCGCACCCAGGGCGGGGGCTCGTCGAAGTCGGGCAATTTGACCTTCGGGCGGATCCGGCACGGGAGGGCGGACCACTTCCCAGCATAGTTACAAGTGCCTCCAATAAGGCTTAACTCAGGT"));
        scars.add(new Part("Iscar", "scar", "gaaatctgctcgtcagtggtgctcacactgacgaatcatgtacagatcataccgatgactgcctggcgactcacaactaagcaagacagccggaaccagcgccggcgaacaccactgcatatatggcatatcacaacagtccacgtctcaagcagttacagagatgttacgaaccactagtgcactgcagtacacggtttccttgaaatttttttgattcggtaatctccgaacagaaggaagaacgaaggaaggagcacagacttagattggtatatatacgcatatgtagtgttgaagaaacatgaaattgcccagtattcttaacccaactgcacagaacaaaaacgtgcaggaaacgaagataaatcatgtcgaaagctacatataaggaacgtgctgctactcatcctagtcctgttgctgccaagctatttaatatcatgcacgaaaagcaaacaaacttgtgtgcttcattggatgttcgtaccaccaaggaattactggagttagttgaagcattaggtcccaaaatttgtttactaaaaacacatgtggatatcttgactgatttttccatggagggcacagttaagccgctaaaggcattatccgccaagtacaattttttactcttcgaggacagaaaatttgctgacattggtaatacagtcaaattgcagtactctgcgggtgtatacagaatagcagaatgggcagacattacgaatgcacacggtgtggtgggcccaggtattgttagcggtttgaagcaggcggcagaagaagtaacaaaggaacctagaggccttttgatgttagcagaattgtcatgcaagggctccctatctactggagaatatactaagggtactgttgacattgcgaagagcgacaaagattttgttatcggctttattgctcaaagagacatgggtggaagagatgaaggttacgattggttgattatgacacccggtgtgggtttagatgacaagggagatgcattgggtcaacagtatagaaccgtggatgatgtggtttctacaggatctgacattattattgttggaagaggactatttgcaaagggaagggatgctaaggtagagggtgaacgttacagaaaagcaggctgggaagcatatttgagaagatgcggccagcaaaactaaaaaactgtattataagtaaatgcatgtatactaaactcacaaattagagcttcaatttaattatatcagttattacccgagtagagcacttgaatccactgccccgggaatctcggtcgtaatgatttctataatgacgaaaaaaaaaaaattggaaagaaaaagcttcatggcctttataaaaaggaactatccaatacctcgccagaaccaagtaacagtattttacggggcacaaatcaagaacaataagacaggactgtaaagatggacgcattgaactccaaagaacaacaagagttccaaaaagtagtggaacaaaagcaaatgaaggatttcatgcgtttgtactctaatctggtagaaagatgtttcacagactgtgtcaatgacttcacaacatcaaagctaaccaataaggaacaaacatgcatcatgaagtgctcagaaaagttcttgaagcatagcgaacgtgtagggcagcgtttccaagaacaaaacgctgccttgggacaaggcttgggccgataaggtgtactggcgtatatatatctaattatgtatctctggtgtagcccatttttagcatgtaaatataaagaaGAGACC"));

//      Second fragment
        scars.add(new Part("Rscar", "scar", "CCAGCTGAGTTTCATGGCTTGAATTACTATGTGCGGGGTAATGTGGCTTGTCGTAGCCTTGGTGAAGTATTTGGCACAAGGTCTAGGGCCTGTTGAGGGAtcgaggagaacttctagtatatctacatacctaatattattgccttattaaaaatggaatcccaacaattacatcaaaatccacattctcttcaaaatcaattgtcctgtacttccttgttcatgtgtgttcaaaaacgttatatttataggataattatactctatttctcaacaagtaattggttgtttggccgagcggtctaaggcgcctgattcaagaaatatcttgaccgcagttaactgtgggaatactcaggtatcgtaagatgcaagagttcgaatctcttagcaaccattatttttttcctcaacataacgagaacacacaggggcgctatcgcacagaatcaaattcgatgactggaaattttttgttaatttcagaggtcgcctgacgcatatacctttttcaactgaaaaattgggagaaaaaggaaaggtgagagcgccggaaccggcttttcatatagaatagagaagcgttcatgactaaatgcttgcatcacaatacttgaagttgacaatattatttaaggacctattgttttttccaataggtggttagcaatcgtcttactttctaacttttcttaccttttacatttcagcaatatatatatatatatttcaaggatataccattctaatgtctgcccctaagaagatcgtcgttttgccaggtgaccacgttggtcaagaaatcacagccgaagccattaaggttcttaaagctatttctgatgttcgttccaatgtcaagttcgatttcgaaaatcatttaattggtggtgctgctatcgatgctacaggtgttccacttccagatgaggcgctggaagcctccaagaaggctgatgccgttttgttaggtgctgtgggtggtcctaaatggggaaccggtagtgttagacctgaacaaggtttactaaaaatccgtaaagaacttcaattgtacgccaacttaagaccatgtaactttgcatccgactctcttttagacttatctccaatcaagccacaatttgctaaaggtactgacttcgttgttgtcagagaattagtgggaggtatttactttggtaagagaaaggaggacgatggtgatggtgtcgcttgggatagtgaacaatacaccgttccagaagtgcaaagaatcacaagaatggccgctttcatggccctacaacatgagccaccattgcctatttggtccttggataaagctaatgttttggcctcttcaagattatggagaaaaactgtggaggaaaccatcaagaacgaatttcctacattgaaggttcaacatcaattgattgattctgccgccatgatcctagttaagaacccaacccacctaaatggtattataatcaccagcaacatgtttggtgatatcatctccgatgaagcctccgttatcccaggttccttgggtttgttgccatctgcgtccttggcctctttgccagacaagaacaccgcatttggtttgtacgaaccatgccacggttctgctccagatttgccaaagaataaggtcaaccctatcgccactatcttgtctgctgcaatgatgttgaaattgtcattgaacttgcctgaagaaggtaaggccattgaagatgcagttaaaaaggttttggatgcaggtatcagaactggtgatttaggtggttccaacagtaccaccgaagtcggtgatgctgtcgccgaagaagttaagaaaatccttgcttaacctggaggacccttctctttagactattctactcttatgcacgtaaaaaattctaggaaatatgtattaactaggagtaaaataaccggctagtggcattcatatagccgtctgtttacatctacatcacacatttcgagtgtatatctcgcaacgttggcgttaaataggcagactcgtatcgcatgtcggtgcgacacgaaattacaaaatggaatatgttcatagggtagacgaaactatatacgcaatctacatacatttatcaagaaggagaaaaaggaggatgtaaaggaatacaggtaagcaaattgatactaatggctcaacgtgataaggaaaaagaattgcactttaacattaatattgacaaggaggagggcaccacacaaaaagttaggtgtaacagaaaatcatgaaactatgattcctaatttatatattggaggattttctctaaaaaaaaaaaaatacaacaaataaaaaacactcaatgacctgaccatttgatggagtttaagtcaataccttcttgaaccatttcccataatggtgaaagttccctcaagaattttactctgtcagaaacggccttaacgacgtagtcgacctcctcttcagtactaaatctaccaataccaaatctgatggaagaatgggctaatgcatcatccttacccagcgaGAGACC"));
        scars.add(new Part("Qscar", "scar", "CTCGGTACCAAATTCCAGAAAAGAGGCCTCCCGAAAGGGGGGCCTTTTTTCGTTTTGGTCCCACCAATAAATGAGCGAGAGTGGGCTTTGATGTAGCTGTGTATCGGTGTGCATGACGGCGCCGCGCTCGCGTAGGGTCCGGGGTTGGTG"));
        scars.add(new Part("Pscar", "scar", "AGGAGTCAATTAATGTGCTTTTAATTCTGATGAGACGGTGACGTCGAAACTCGTCAGCAATATGGCTCGGTCGCAGGCGGGTATCTGATCTTGGAGGTTGTGACATCTTATTGAAGCGGTCGGTTCTCGGGGTGCAGAAAAACGTGAGTG"));
        scars.add(new Part("Oscar", "scar", "AGACTGTCGCCGGATGTGTATCCGACCTGACGATGGCCCAAAAGGGCCGAAACAGTTGGTGGGTCTATTTAGTCACGATCTTTATAGTGTTTGCTGGGGAAAACCAAACGTGGTTAGACGGCTAATCGTATCGTGGCTTTTGGGTGGGCT"));
        scars.add(new Part("Nscar", "scar", "AGAAGTCAATTAATGTGCTTTTAATTCTGATGAGTCGGTGACGACGAAACTTGGTTGTGCAGCTCTGCGCTCGGGTGTTCGTGGTGGCGCTGATCGTTCACGGACCCGAGGAGTGGGATAGACTGCAGCCCAGGAGATAGTGACGTCCGC"));
        scars.add(new Part("Mscar", "scar", "AGCGGTCAACGCATGTGCTTTGCGTTCTGATGAGACAGTGATGTCGAAACCGGTCGGCCGCAATGGTCAAGTGACACTGTCgTGATAACGGTCGGAGCGTGCTAGGGGTTGGGTTTGCCGGCAGTGCCCGCGTAGTCTCGACCGAAAGTC"));
        scars.add(new Part("Lscar", "scar", "agCGCTGTCTGTACTTGTATCAGTACACTGACGAGTCCCTAAAGGACGAAACACCGTGATTTGATCGTAACTTATTCACCCGGTCTGTGTTATTTCCGCAAAAATAACGCTTAGTCGGCATGAGACGTGGCCGGATCGTATTGGGCTGTT"));
        scars.add(new Part("Kscar", "scar", "agTGGTCGTGATCTGAAACTCGATCACCTGATGAGCTCAAGGCAGAGCGAAACCAGCGATGTTGGTATGGTCAATGCGCGGGGCGGCCACGCGCATCTATGGCGATTAAGCCCTGGAGGGGCAAGGGTGTGGAAGGCCCTCGGCGTTCGG"));
        scars.add(new Part("Jscar", "scar", "GGTCTCgacccagaagaacagtaaaataaagcaaggtacgtgaaattaatatttttaaatggttctaaccgatgccgaagaactgcgcagtccggttataacgtctgacatgtccttttttgatttggaatccaaccactcaagtgactctgttcatttactttgcgaaaaatatacccacaaattgcccatcgaaagtgaatcgcaaaccaccttcagactggcaccgacaaagcaaagattatacagacagagtactttatacgtaccgttaagtctcaagcaaagggttttcttatttactgaacgggtaaagagtatctgggccggcttgccaagatgcaaaccgaataagtatttcaaagttgcatttgccttagccgtcctgacaccattggctatttggatattttatattgactttcgtgtacattgatcacatcgactgttctattggcaaatgaaccacgggcattgactatttttcaggttactactatatattatcatcacgggcaaggattgtaGCTTCGAATTTCTTATGATTTATGATTTTTATTATTAAATAAGTTATAAAAAAAATAAGTGTATACAAATTTTAAAGTGACACTTAGGTTTTAAAACGAAAATTCCCTCAGTCATAAGTCTGGGCTAAGCCCACTGATGAGTCGCTGAAATGCGACGAAACTTATGAATGTTTGGGCGGGCCGGGGTGGGATGGAGGCTGGCATGTGTGGGGACATTATGGGGGTAGGTATCCCAGTGCCTGGTGTGGTTAGGGGGCCG"));


        //module end

        for(Part scar: scars) {
            _ALL_PARTS.put(scar.get_name(), scar);
        }

        _scars = scars;

    }

    public void yeast_terminators() {
        // TODO Auto-generated method stub

        ArrayList<Part> terminators = new ArrayList<>();

//		First fragment
        terminators.add(new Part("Aterminator", "terminator", "taaaAGCTTTTGATTAAGCCTTCTAGTCCAAAAAACACGTTTTTTTGTCATTTATTTCATTTTCTTAGAATAGTTTAGTTTATTCATTTTATAGTCACGAATGTTTTATGATTCTATATAGGGTTGCAAACAAGCATTTTTCATTTTATGTTAAAACAATTTCAGGTTTACCTTTTATTCTGCTTGTGGTGACGCGTGTATCCGCCCGCTCTTTTGGTCACCCATGTATTTAATTGCATAAATAATTCTTAAAAGTGGAGCTAGTCTATTTCTATTTACATACCTCTCATTTCTCATTTCCTCCTGA"));
        terminators.add(new Part("Bterminator", "terminator", "taaaAGGAAATCTCCAACCTGGACATAAACCAAAAAAATTTAATGAAATAGATTAGTTATAGTAAAAGTAATCATAATAATAAATAGATAAATAAATCCAAATGCAATTATAAATAGTATATGAAGTTATGTGTTTTCTAAAATATTAAAGGTTTATAAACGGGGTCCCGAAACTCCTCTACG"));
        terminators.add(new Part("Cterminator", "terminator", "taaaAGGGAACCTTTTACAACAAATATTTGAAAAATTACCTCCATTATTATACCTTCTCTTTATGTAATTGTTAGTTCGAAAATTTTTTCTTCATTAATATAATCAACTTCTAAAACTTTCTAAAAACGTTCTCTTTTTCGAGATTAGTGCTTCTTCCCAATCCGTAAGAAATGTTTCCTTTCTTGACAATTGGCACCAGCTGGCTACTCGTTGCTCGAAAACTACTCTCTTTTATTTTTAATTTACGAACGACCTCTTCC"));
        terminators.add(new Part("Dterminator", "terminator", "taaaACACTTTTATTTTCTTTTGGCTTGTTAACCTAAACTTGTACATATGCCCATATATCCTTAAATATATATATATAGCCAACCCGATGACGCTAAATAAACGCATTTTTTTTTTTTTTTACAGAATTATTACTTTCGTACTTGGGTTTATGCTTCCTCAGGT"));
        terminators.add(new Part("Eterminator", "terminator", "taaaTCCAGCCAGTAAAATCCATACTCAACGACGATATGAACAAATTTCCCTCATTCCGATGCTGTATATGTGTATAAATTTTTACATGCTCTTCTGTTTAGACACAGAACAGCTTTAAATAAAATGTTGGATATACTTTTTCTGCCTGTGGTGTCATCCACGCTTTTAATTCATCTCTTGTATGGTTGACAACCTCCACC"));
        terminators.add(new Part("Fterminator", "terminator", "taaaACGGTGGTGTTTGACACATCCGCCTTCTTAATGCTTTCTTTCAGTATTATGTTATTTTTTTGTTATTCGTTTTTCACTTCTAGGCTTTTTGACAGACTAGCCCCGTTATACCACCATCTTTGTGGGAAAGCCCCTAAATTGCCCTGAGCAGTATCGTTTCATGTCTAGTCTCTTTAAAGATGTTTCTTACGCGTTGCGTGTAAAACATCCTCTCATTCAAGACAGGGTTTTCTAAAAGCAATAGGGGTAGTTTAATAATTCTTATATAATCATCATATACACTATTTTTAGTTCTTAATTCTTTAATACAAACTTATTAATGTGCTCTCCATTGATCTCTTAATCAGGAGGCGATATATACCGGAAGCGGTGTACTTCCTCTGTC"));
        terminators.add(new Part("Gterminator", "terminator", "taaaGAGCATGCTTCTCTTTTTTTTTGTAGGCCAATGATAGGAAAGAACAATAGATTATAAATACGTCAGAATATAGTAGATATGTTTTTATGTTTAGACCTCGTACATAGGAATAATTGACGTTTTTTTTTGGCCAACATTTGAAATTTTTTTTTGTTACCTCGCCCTCCGCT"));
        terminators.add(new Part("Hterminator", "terminator", "taaaTATAAAATCATACATTCATATAATATCCATTACGTACGTACATCTTACATAACACATTTTTACCCCCTTTTCTTTTTTTTGTTTTCCTTTTTTGTTTTATTTAGCCGCCCGCGTTTTCCTAACGTTTTCCCCCTCCCGT"));

//      Second fragment
        terminators.add(new Part("Qterminator", "terminator", "taaaGCGGCTAAAAAGCTAAATATTCCAAAAAAACATAAAACAAAAGTTTGAGCCAATTAACCCAAATAATCTGTATCAGGGTAACGATACCTCGAGCTTCCACTAAATTCTAGTACGTCACGTTGTACGAAGTATAGAGCATCCTGAAATAATGAAAAAAAGAAACAATTGGCCGACTCCGCTTGTTTCTATTCCTTTCCACGTGATCTGAATTATTTACAGGAAGTCGCATTTTTTGCGCAGTTGTTGCTGGGTAGCAGCGGCCAGCAAGGTCTGGTGAAACTCGACTGATGCGTTGAGGACTAGAAACTGGTGGTGACAGCTAAACTGAGCTCCTCAACCTGATCATGTCATGGCTGTCTCAAAGATCTTGCAGACTGTAACTACGAGCGCATACGTATGCGCATACGTGTCTGATAGTACCCAGTGATCTGATCTATATTTTTTTGGGGGGGTTTTGAAAGGATATATAAAGGTTACACTTCCTCAGTTTAGTTTATTCTTGAGTTTGAAGTTCCTTATGATTATACTTGTCTTAAATATTAAAAAAAACCAAGAACTTAGTTTCAAATTAAATTCATCACATACACAAACAAAACAAACCTCCGCT"));
        terminators.add(new Part("Pterminator", "terminator", "taaaGCGGATCTCTTATGTCTTTACGATTTATAGTTTTCATTATCAAGtATGCCTATATTAGTATATAGCATCTTTAGATGACAGTGTTCGAAGTTTCACGAATAAAAGATAATATTCTACTTTTTGCTCCCACCGCGTTTGCTAGCACGAGTgAACACCATCCCTCGCCTGTGAGTTGTACCCATTCCTCTAAACTGTAGACATGGTAGCTTCAGCAGTGTTCGTTATGTACGGCATCCTCCAACAAACAGTCGGTTATAGTTTGTCCTGCTCCTCTGAATCGTCTCCCTCCGCT"));
        terminators.add(new Part("Oterminator", "terminator", "taaaGAGAGTAGACTTTTTCTGTGAAATTTAATGAGTTTTTGTTCACCTTTTTTACTTTTCTTCTATGCCATATGGTTAAATAAAACATGGTTCAAACGCGATTCTTTTTTATTATCTATGTCTTTATTATGTAGTCATCaTCACTATAATAACCGTTAATGAGCGACATAGTTGCATCAGTTTTTAAGGCCAATTTGATTCAGAACCTTTTCAAGTTCTTCACTTCCTCTGTC"));
        terminators.add(new Part("Nterminator", "terminator", "taaaAGTGACCTGGCTCTATAGTGTTGTCCCTCTCGCGAGGACCATTGTTGCTTGCATATGGGCTTGAAACATATGGTCATCACATCTGAGCGATTTTACCTCTTAGAATTAGTTTAGATATATATGAGTTGATGAATAAATAGTTATAAAAACTTGCTTTGGCTTCGATATATGACCGTTATTTTTGACTAAGTTTTAACGAAGGAATCTAACCTCGTCCTCCACC"));
        terminators.add(new Part("Mterminator", "terminator", "taaaGCCATTAGTAGTGTACTCAAACGAATTATTGTTGCAAATAAATAAACTTACACAGTTTGAATACATAAATCAATCAGACAAATAAATACATCGGTTCAAATTATACTAAATCTAAATACTACGTTATCGCCGTGAATTACGCAATTCGCATGTTACGTACTGCGCGTCTCTTGTTGAATATTTACCAATTGGGAAAAAGAACTCGTATTTCATTCCCCTTTTTGGAAAGGGGTGGGGAGAGACTGTTGTTCAGCCACGTCAACCTCAGGT"));
        terminators.add(new Part("Lterminator", "terminator", "taaaTCTCTGCTTTTGTGCGCGTATGTTTATGTATGTACCTCTCTCTCTATTTCTATTTTTAAACCACCCTCTCAATAAAATAAAAATAATAAAGTATTTTTAAGGAAAAGACGTGTTTAAGCACTGACTTTATCTACTTTTTGTACGTTTTCATTGATATAATGTGTTTTGTCTCTCCCTTTTCTACGAAAATTTCAAAAATTGACCAAAAAAAGGAATATATATACGAAAAACTATTATATTTATATATCATAGTGTTGATAAAAAATGTTTATCCATTGGACCGTGTATCATATGATCATCCCTCTTCC"));
        terminators.add(new Part("Kterminator", "terminator", "taaaATCCTATGAGGATATAAACAGTATTAAAAAAATCTTACCATAAAGACATACGACATTTCGAGGCGTTTTTGCTTTTCTTTTAACTTTCGTTGGCACAAACTGAAATATATGTGGAATGCCCTCTGAAAATATACTAGGAGTTTTGAGCGATCCAAACATTTTCTTTTCTTTTTTTCTCTTCCTTCTAGCGAAGTCCTCTTGCGTTAGCTGTAAACAAAATAAGTCAATTTCATTAATGAAAAGTGACATTATGGCGGTAGCTCTCTCTATCTTTGTTCAGTTATCAACATATAGTCATACTTAAAGATATAATAGCTCATAGTCATATATAGTTCTTTATACTTGAAAAAGTGATTCCTAAAAATCTCAACTTTTTTCACATTGTATTTACAAGATAATAAAATTATTACCCCTCTACG"));
        terminators.add(new Part("Jterminator", "terminator", "taaaGGAGATTGATAAGACTTTTCTAGTTGCATATCTTTTATATTTAAATCTTATCTATTAGTTAATTTTTTGTAATTTATCCTTATATATAGTCTGGTTATTCTAAAATATCATTTCAGTATCTAAAAATTCCCCTCTTTTTTCAGTTATATCTTAACAGGCGACAGTCCAAATGTTGATTTATCCCAGTCCGATTCATCAGGGTTGTGAAGCATTTTGTCAATGGTCGAAATCACATCAGTAATAGTGCCTCTTACTTGCCTCATAGAATTTCTTTCTCTTAACGTCACCGTTTGGTCTTTTCCTCCTGA"));


        //module end

        for(Part terminator: terminators) {
            _ALL_PARTS.put(terminator.get_name(), terminator);
        }

        _terminators = terminators;

    }
}
