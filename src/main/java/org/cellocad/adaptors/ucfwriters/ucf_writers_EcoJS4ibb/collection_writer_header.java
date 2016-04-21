package org.cellocad.adaptors.ucfwriters.ucf_writers_EcoJS4ibb;


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
        obj.put("description", "TetR homologs: PhlF, SrpR, BM3R1, HlyIIR, BetI, AmtR, AmeR, QacR, IcaRA");
        obj.put("version", "EcoJS4ib");
        obj.put("date", date.toString());
        obj.put("author", new String[]{"Bryan Der"});
        obj.put("organism", "Escherichia coli NEB 10-beta");
        obj.put("genome", "NEB 10 ∆(ara-leu) 7697 araD139 fhuA ∆lacX74 galK16 galE15 e14- φ80dlacZ∆M15  recA1 relA1 endA1 nupG  rpsL (StrR) rph spoT1 ∆(mrr-hsdRMS-mcrBC)");
        obj.put("media", "M9 minimal media composed of M9 media salts (6.78 g/L Na2HPO4, 3 g/L KH2PO4, 1 g/L NH4Cl, 0.5 g/L NaCl, 0.34 g/L thiamine hydrochloride, 0.4% D-glucose, 0.2% Casamino acids, 2 mM MgSO4, and 0.1 mM CaCl2; kanamycin (50 ug/ml), spectinomycin (50 ug/ml)");
        obj.put("temperature", "37");
        obj.put("growth", "Inoculation: Individual colonies into M9 media, 16 hours overnight in plate shaker.  Dilution: Next day, cells dilute ~200-fold into M9 media with antibiotics, growth for 3 hours.  Induction: Cells diluted ~650-fold into M9 media with antibiotics.  Growth: shaking incubator for 5 hours.  Arrest protein production: PBS and 2mg/ml kanamycin.  Measurement: flow cytometry, data processing for RPU normalization.");

        objects.add(obj);

        return objects;
    }
}
