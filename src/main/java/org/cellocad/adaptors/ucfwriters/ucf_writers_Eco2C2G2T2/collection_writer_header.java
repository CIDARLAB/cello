package org.cellocad.adaptors.ucfwriters.ucf_writers_Eco2C2G2T2;


import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_header extends collection_writer {

    @Override
    public ArrayList<Map> getObjects() {

        ArrayList<Map> objects = new ArrayList<>();

        Date date = new Date();

        Map obj = new LinkedHashMap();
        obj.put("collection", "header");
        obj.put("description", "CRISRPi NOR/NOT gates, maximum of 5");
        obj.put("version", "Eco2C2G2T2");
        obj.put("date", date.toString());
        obj.put("author", new String[]{"Bryan Der"});
        obj.put("organism", "Escherichia coli strain K-12 substrain MG1655");
        obj.put("genome", "K-12 MG1655* [F-kilvG- rfb-50 rph-1 D(araCBAD) D(LacI)]");
        obj.put("media", "Cells were grown in LB Miller broth (Difco, MI, 90003-350) for overnight growth and cloning, and MOPS EZ Rich Defined Medium (Teknova, CA, M2105) with 0.4% glycerol carbon source for measurement experiments.  Ampicillin (100 lg/ml), kanamycin (50 lg/ml), and spectinomycin sulfate (50 lg/ml) were used to maintain plasmids. Arabinose (Sigma Aldrich, MO, A3256), 2,4-diacetylphloroglucinol (Santa Cruz Biotechnology, TX, CAS 2161-86-6), and anhydrotetracycline (aTc) (Sigma Aldrich, MO, 37919) were used as chemical inducers. The fluorescent protein reporters YFP (Cormack et al, 1996) and mRFP1 (Campbell et al, 2002) were measured with cytometry to determine gene expression.");
        obj.put("temperature", "37");
        obj.put("growth", "Escherichia coli MG1655* cells were transformed with three plasmids encoding: (i) inducible dCas9, (ii) one or more sgRNAs, and (iii) a fluorescent reporter. Cells were plated on LB agar plates with appropriate antibiotics. Transformed colonies were inoculated intoMOPS EZ Rich Defined Medium with 0.4% glycerol and appropriate antibiotics and were then grown overnight in V-bottom 96-well plates (Nunc, Roskilde, Denmark, 249952) in an ELMI Digital Thermos Microplates shaker incubator (Elmi Ltd, Riga, Latvia) at 1,000 rpm and 37°C. The next day, cultures were diluted 180-fold into EZ Rich Medium with antibiotics and grown with the same shaking incubator parameters for 3 h. At 3 h, cells were diluted 700-fold into EZ Rich Medium with antibiotics and inducers. The cells were grown using the same shaking incubator parameters for 6 h. For cytometry measurements, 40 ll of the cell culture was added to 160 ll of phosphate-buffered saline with 0.5 mg/ml kanamycin to arrest cell growth. The cells were placed in a 4°C refrigerator for 1 h to allow the fluorophores to mature prior to cytometry analysis.");


        objects.add(obj);

        return objects;
    }
}
