package org.cellocad.adaptors.ucfadaptor;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.cellocad.MIT.dnacompiler.UCF;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bryan Der on 7/27/15.
 */
public class UCFReader {


    public UCF readAllCollections(String ucf_file) {

        UCF ucf = new UCF();

        try {
            FileReader reader = new FileReader(ucf_file);

            try {
                JSONParser jsonParser = new JSONParser();
                JSONArray jsonArray = (JSONArray) jsonParser.parse(reader);

                for(int i=0; i<jsonArray.size(); ++i) {

                    Map map = new HashMap();
                    try {
                        map = (Map) jsonArray.get(i);
                    } catch (Exception e) {

                    }

                    JSONObject obj = (JSONObject) jsonArray.get(i);

                    if(map.containsKey("collection")) {

                        if (map.get("collection").equals("header")) {
                            ucf.set_header( obj );
                        }
                        else if (map.get("collection").equals("measurement_std")) {
                            ucf.set_measurement_std( obj );
                        }
                        else if (map.get("collection").equals("logic_constraints")) {
                            ucf.set_logic_constraints( obj );
                        }
                        else if (map.get("collection").equals("motif_library")) {
                            ucf.get_motif_library().add( obj );
                        }
                        else if (map.get("collection").equals("response_functions")) {
                            ucf.get_response_functions().add( obj );
                        }
                        else if (map.get("collection").equals("parts")) {
                            ucf.get_parts().add( obj );
                        }
                        else if (map.get("collection").equals("gate_parts")) {
                            ucf.get_gate_parts().add( obj );
                        }
                        else if (map.get("collection").equals("gate_toxicity")) {
                            ucf.get_gate_toxicity().add( obj );
                        }
                        else if (map.get("collection").equals("gate_cytometry")) {
                            ucf.get_gate_cytometry().add( obj );
                        }
                        else if (map.get("collection").equals("gates")) {
                            ucf.get_gates().add( obj );
                        }
                        else if (map.get("collection").equals("eugene_rules")) {
                            ucf.set_eugene_rules( obj );
                        }
                        else if (map.get("collection").equals("genetic_locations")) {
                            ucf.set_genetic_locations( obj );
                        }
                        else if (map.get("collection").equals("tandem_promoter")) {
                            ucf.get_tandem_promoters().add( obj );
                        }
                    }
                    else {
                        logger.info("unknown json object in UCF");
                        return null;
                    }
                }

            }catch (org.json.simple.parser.ParseException e) {
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }


        }catch(FileNotFoundException e) {
            e.printStackTrace();
        }

        return ucf;
    }

    @Getter
    @Setter
    private String threadDependentLoggername;

    private Logger logger  = Logger.getLogger(getClass());
}
