package org.cellocad.adaptors.ucfadaptor;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.cellocad.MIT.dnacompiler.Args;
import org.cellocad.MIT.dnacompiler.Gate;
import org.cellocad.MIT.dnacompiler.GateLibrary;
import org.cellocad.MIT.dnacompiler.UCF;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Bryan Der on 7/29/15.
 */
public class UCFValidator {




    //validateResponseFunctions only checks to see if each individual response_function object has the required fields.
    //it does not check to see that all gates have a response function.
    public boolean allGatesHaveResponseFunctions(GateLibrary gate_library) {
        for (Gate g : gate_library.get_GATES_BY_NAME().values()) {
            if(g.get_equation().isEmpty() || g.get_params().isEmpty() || g.get_variable_names().isEmpty()) {
                System.out.println("invalid UCF. response_function data missing for " + g.name);
                return false;
            }
        }
        return true;
    }

    //validateGateParts only checks to see if each individual gate_parts object has the required fields,
    //it does not check to see that each gate object has gate_parts defined.
    public boolean allGatesHaveGateParts(GateLibrary gate_library) {

        for(Gate g: gate_library.get_GATES_BY_NAME().values()) {
            if(g.get_downstream_parts().isEmpty()) {
                return false;
            }
            if( g.get_regulable_promoter().get_name().length() == 0) {
                return false;
            }
        }
        return true;
    }


    //Validate that each object in each collection has the required fields.
    public JSONObject validateAllUCFCollections(UCF ucf, Args options) {

        JSONObject ucf_validation_map = new JSONObject();
        ucf_validation_map.put("is_valid", true);

        ucf_validation_map.put("header", validateHeader(ucf.get_header()));
        ucf_validation_map.put("measurement_std", validateMeasurementStd(ucf.get_measurement_std()));
        ucf_validation_map.put("logic_constraints", validateLogicConstraints(ucf.get_logic_constraints()));
        ucf_validation_map.put("gates", validateGates(ucf.get_gates()));
        ucf_validation_map.put("parts", validateParts(ucf.get_parts()));
        ucf_validation_map.put("response_functions", validateResponseFunctions(ucf.get_response_functions(), options));
        ucf_validation_map.put("gate_parts", validateGateParts(ucf.get_gate_parts()));
        ucf_validation_map.put("gate_toxicity", validateGateToxicity(ucf.get_gate_toxicity()));
        ucf_validation_map.put("gate_cytometry", validateGateCytometry(ucf.get_gate_cytometry()));
        ucf_validation_map.put("eugene_rules", validateEugeneRules(ucf.get_eugene_rules()));
        ucf_validation_map.put("genetic_locations", validateGeneticLocations(ucf.get_genetic_locations()));
        ucf_validation_map.put("tandem_promoters", validateTandemPromoters(ucf.get_tandem_promoters()));

        if( (boolean) ucf_validation_map.get("header") == false ||
                (boolean) ucf_validation_map.get("measurement_std") == false ||
                (boolean) ucf_validation_map.get("logic_constraints") == false ||
                (boolean) ucf_validation_map.get("gates") == false ||
                (boolean) ucf_validation_map.get("parts") == false ||
                (boolean) ucf_validation_map.get("gate_parts") == false ||
                (boolean) ucf_validation_map.get("response_functions") == false
                ) {
            ucf_validation_map.put("is_valid", false);
        }

        if( ! (boolean) ucf_validation_map.get("gate_toxicity")) {
            options.set_toxicity(false);
        }
        if( ! (boolean) ucf_validation_map.get("gate_cytometry")) {
            options.set_histogram(false);
        }
        if( ! (boolean) ucf_validation_map.get("genetic_locations")) {
            options.set_plasmid(false);
        }
        if( ! (boolean) ucf_validation_map.get("tandem_promoters")) {
            options.set_tandem_promoter(false);
        }


        return ucf_validation_map;
    }
    

    public boolean fieldIsMissing(JSONObject obj, List<String> required_fields) {

        for(String field: required_fields) {
            if(!obj.containsKey(field)) {
                System.out.println("missing field: " + field);
                return true;
            }
        }

        return false;
    }


    public boolean validateHeader(JSONObject map) {

        if(map.isEmpty()) {
            return false;
        }

        List<String> required_fields = new ArrayList<String>();
        required_fields.add("version");
        required_fields.add("organism");
        required_fields.add("genome");
        required_fields.add("media");
        required_fields.add("temperature");
        required_fields.add("growth");

        if(fieldIsMissing(map, required_fields)) {
            return false;
        }


        logger.info("header is valid");

        return true;
    }


    public boolean validateMeasurementStd(JSONObject map) {

        if(map.isEmpty()) {
            return false;
        }

        List<String> required_fields = new ArrayList<String>();
        required_fields.add("signal_carrier_units");
        required_fields.add("normalization_instructions");
        required_fields.add("plasmid_description");
        required_fields.add("plasmid_sequence");

        if(fieldIsMissing(map, required_fields)) {
            return false;
        }

        logger.info("measurement_std is valid");

        return true;
    }


    public boolean validateLogicConstraints(JSONObject map) {

        if(map.isEmpty()) {
            return false;
        }

        if(!map.containsKey("available_gates")) {
            return false;
        }

        try {
            JSONArray available_gates = (JSONArray) map.get("available_gates");

            for (int i=0; i < available_gates.size(); i++) {

                JSONObject obj = (JSONObject) available_gates.get(i);

                List<String> required_fields = new ArrayList<String>();
                required_fields.add("type");
                required_fields.add("max_instances");

                if(fieldIsMissing(obj, required_fields)) {
                    return false;
                }
            }
        }catch(Exception e) {
            return false;
        }

        logger.info("logic_constraints are valid");

        return true;
    }


    public boolean validateGates(JSONArray jsonArray) {

        if(jsonArray.isEmpty()) {
            return false;
        }

        for (int i=0; i < jsonArray.size(); i++) {

            JSONObject obj = (JSONObject) jsonArray.get(i);

            List<String> required_fields = new ArrayList<String>();
            required_fields.add("regulator");
            required_fields.add("group_name");
            required_fields.add("gate_name");
            required_fields.add("gate_type");
            required_fields.add("system");
            //optional: color_hexcode
            //optional: inducer (this was added for an IWBDA demo, not really used in Cello).

            if(fieldIsMissing(obj, required_fields)) {
                return false;
            }

            Pattern p = Pattern.compile("[^a-zA-Z0-9_]");
            boolean hasSpecialChar = p.matcher(obj.get("gate_name").toString()).find();
            if(hasSpecialChar) {
                return false;
            }

        }

        logger.info("gates are valid");

        return true;
    }


    public boolean validateResponseFunctions(JSONArray jsonArray, Args options) {

        if(jsonArray.size() == 0) {
            return false;
        }

        for (int i=0; i < jsonArray.size(); i++) {

            JSONObject obj = (JSONObject) jsonArray.get(i);

            List<String> required_fields = new ArrayList<String>();
            required_fields.add("gate_name");
            required_fields.add("equation");
            required_fields.add("variables");
            required_fields.add("parameters");

            if(fieldIsMissing(obj, required_fields)) {
                return false;
            }


            JSONArray variables = (JSONArray) obj.get("variables");

            if(variables.size() == 0) {
                return false;
            }

            for (int j=0; j < variables.size(); j++) {

                JSONObject v = (JSONObject) variables.get(j);

                if(!v.containsKey("name")) {
                    return false;
                }

                if(!v.containsKey("off_threshold")) {
                    options.set_noise_margin(false);
                }
                else {
                    try {
                        Double d = Double.valueOf(v.get("off_threshold").toString());
                    }catch (Exception e) {
                        return false;
                    }
                }

                if(!v.containsKey("on_threshold")) {
                    options.set_noise_margin(false);
                }
                else {
                    try {
                        Double d = Double.valueOf(v.get("on_threshold").toString());
                    }catch (Exception e) {
                        return false;
                    }
                }
            }

            JSONArray parameters = (JSONArray) obj.get("parameters");

            if(parameters.size() == 0) {
                return false;
            }

            for (int j=0; j < parameters.size(); j++) {

                JSONObject p = (JSONObject) parameters.get(j);

                if(!p.containsKey("name")) {
                    return false;
                }
                if(!p.containsKey("value")) {
                    return false;
                }

                if(p.containsValue("a")){
                    options.set_tpmodel(true);
                }
                try {
                    Double d = Double.valueOf(p.get("value").toString());
                }catch (Exception e) {
                    return false;
                }

            }
        }

        logger.info("response_functions are valid");

        return true;
    }


    public boolean validateParts(JSONArray jsonArray) {

        if(jsonArray.isEmpty()) {
            return false;
        }

        for (int i=0; i < jsonArray.size(); i++) {

            JSONObject obj = (JSONObject) jsonArray.get(i);

            List<String> required_fields = new ArrayList<String>();
            required_fields.add("type");
            required_fields.add("name");
            required_fields.add("dnasequence");

            if(fieldIsMissing(obj, required_fields)) {
                return false;
            }
        }

        logger.info("parts are valid");

        return true;
    }


    public boolean validateGateParts(JSONArray jsonArray) {

        if(jsonArray.isEmpty()) {
            return false;
        }

        for (int i=0; i < jsonArray.size(); i++) {

            JSONObject obj = (JSONObject) jsonArray.get(i);

            List<String> required_fields = new ArrayList<String>();
            required_fields.add("gate_name");
            required_fields.add("expression_cassettes");
            required_fields.add("promoter");

            if (fieldIsMissing(obj, required_fields)) {
                return false;
            }

            JSONArray expression_cassettes = (JSONArray) obj.get("expression_cassettes");

            for (int j = 0; j < expression_cassettes.size(); j++) {

                JSONObject x = (JSONObject) expression_cassettes.get(j);

                if (!x.containsKey("maps_to_variable")) {
                    return false;
                }
                if (!x.containsKey("cassette_parts")) {
                    return false;
                }
            }
        }


        logger.info("gate_parts are valid");

        return true;
    }


    public boolean validateGateToxicity(JSONArray jsonArray) {

        if(jsonArray.isEmpty()) {
            return false;
        }

        for (int i=0; i < jsonArray.size(); i++) {

            JSONObject obj = (JSONObject) jsonArray.get(i);

            List<String> required_fields = new ArrayList<String>();
            required_fields.add("gate_name");
            required_fields.add("input");
            required_fields.add("growth");

            if (fieldIsMissing(obj, required_fields)) {
                return false;
            }

            JSONArray input_vals = (JSONArray) obj.get("input");

            for (int j=0; j < input_vals.size(); j++) {
                try {
                    Double.valueOf(input_vals.get(j).toString());
                }
                catch (Exception e) {
                    return false;
                }
            }

            JSONArray growth_vals = (JSONArray) obj.get("growth");

            for (int j=0; j < input_vals.size(); j++) {
                try {
                    Double.valueOf(growth_vals.get(j).toString());
                }
                catch (Exception e) {
                    return false;
                }
            }
        }

        return true;
    }


    public boolean validateGateCytometry(JSONArray jsonArray) {

        if(jsonArray.isEmpty()) {
            return false;
        }

        for (int i=0; i < jsonArray.size(); i++) {

            JSONObject obj = (JSONObject) jsonArray.get(i);

            List<String> required_fields = new ArrayList<String>();
            required_fields.add("gate_name");
            required_fields.add("cytometry_data");

            if (fieldIsMissing(obj, required_fields)) {
                return false;
            }

            JSONArray cytometry_data = (JSONArray) obj.get("cytometry_data");

            for (int j=0; j < cytometry_data.size(); j++) {

                JSONObject x = (JSONObject) cytometry_data.get(j);

                if (!x.containsKey("input")) {
                    return false;
                }
                if (!x.containsKey("output_bins")) {
                    return false;
                }
                if (!x.containsKey("output_counts")) {
                    return false;
                }

                try {
                    Double.valueOf(x.get("input").toString());
                }
                catch (Exception e) {
                    return false;
                }

                JSONArray output_bins = (JSONArray) x.get("output_bins");
                JSONArray output_counts = (JSONArray) x.get("output_counts");

                if(output_bins.size() != output_counts.size()) {
                    return false;
                }

                for (int k=0; k < output_bins.size(); k++) {
                    try {
                        Double.valueOf(output_bins.get(k).toString());
                    }
                    catch (Exception e) {
                        return false;
                    }
                }

                for (int k=0; k < output_counts.size(); k++) {
                    try {
                        Double.valueOf(output_counts.get(k).toString());
                    }
                    catch (Exception e) {
                        return false;
                    }
                }
            }

        }

        return true;
    }


    public boolean validateEugeneRules(JSONObject map) {

        if(map.isEmpty()) {
            return false;
        }

        List<String> required_fields = new ArrayList<String>();
        required_fields.add("eugene_gate_rules");
        required_fields.add("eugene_part_rules");

        if(fieldIsMissing(map, required_fields)) {
            return false;
        }


        JSONArray gate_rules = (JSONArray) map.get("eugene_gate_rules");
        JSONArray part_rules = (JSONArray) map.get("eugene_part_rules");


        ArrayList<String> keywords = new ArrayList<String>();

        //counting
        keywords.add("CONTAINS");
        keywords.add("NOTCONTAINS");
        keywords.add("EXACTLY");
        keywords.add("NOTEXACTLY");
        keywords.add("MORETHAN");
        keywords.add("NOTMORETHAN");
        keywords.add("SAME_COUNT");
        keywords.add("WITH");
        keywords.add("NOTWITH");
        keywords.add("THEN");

        //positioning
        keywords.add("STARTSWITH");
        keywords.add("ENDSWITH");
        keywords.add("AFTER");
        keywords.add("ALL_AFTER");
        keywords.add("SOME_AFTER");
        keywords.add("BEFORE");
        keywords.add("ALL_BEFORE");
        keywords.add("SOME_BEFORE");
        keywords.add("NEXTTO");
        keywords.add("ALL_NEXTTO");
        keywords.add("SOME_NEXTTO");

        //pairing
        keywords.add("EQUALS");
        keywords.add("NOTEQUALS");

        //orientation
        keywords.add("ALL_FORWARD");
        keywords.add("ALL_REVERSE");
        keywords.add("FORWARD");
        keywords.add("ALL_FORWARD");
        keywords.add("REVERSE");
        keywords.add("ALL_REVERSE");
        keywords.add("SAME_ORIENTATION");
        keywords.add("ALL_SAME_ORIENTATION");
        keywords.add("ALTERNATE_ORIENTATION");

        //interaction
        keywords.add("REPRESSES");
        keywords.add("INDUCES");
        keywords.add("DRIVES");

        //logic
        keywords.add("NOT");
        keywords.add("AND");
        keywords.add("OR");


        ArrayList<String> all_rules = new ArrayList<String>();
        all_rules.addAll(gate_rules);
        all_rules.addAll(part_rules);

        for(int i=0; i<all_rules.size(); ++i) {

            String[] tokens = all_rules.get(i).toString().split(" ");
            for(int j=0; j<tokens.length; ++j) {
                if(keywords.contains(tokens[j])) {
                    return true;
                }
            }

            return false;
        }

        return true;
    }


    public boolean validateGeneticLocations(JSONObject map) {
    	

        /*List<String> required_fields = new ArrayList<String>();
        required_fields.add("locations");
        required_fields.add("sensor_module_location");
        required_fields.add("circuit_module_location");
        required_fields.add("output_module_location");

        if(fieldIsMissing(map, required_fields)) {
            return false;
        }*/

        if(map.containsKey("locations")) {

            JSONArray locations = (JSONArray) map.get("locations");

            for (int i = 0; i < locations.size(); ++i) {

                JSONObject obj = (JSONObject) locations.get(i);

                if (!obj.containsKey("name")) {
                    return false;
                }
                if (!obj.containsKey("file")) {
                    return false;
                }
            }
        }

        if(map.containsKey("sensor_module_location")) {
            JSONArray locations = (JSONArray) map.get("sensor_module_location");

            for (int i = 0; i < locations.size(); ++i) {

                JSONObject obj = (JSONObject) locations.get(i);

                if (!obj.containsKey("location_name")) {
                    return false;
                }
                if (!obj.containsKey("bp_start") || !obj.containsKey("bp_end")){
                    return false;
                }
            }
        }

        if(map.containsKey("circuit_module_location")) {
            JSONArray locations = (JSONArray) map.get("sensor_module_location");

            for (int i = 0; i < locations.size(); ++i) {

                JSONObject obj = (JSONObject) locations.get(i);

                if (!obj.containsKey("location_name")) {
                    return false;
                }
                if (!obj.containsKey("bp_start") || !obj.containsKey("bp_end")){
                	return false;
                }
            }
        }

        if(map.containsKey("output_module_location")) {
            JSONArray locations = (JSONArray) map.get("sensor_module_location");

            for (int i = 0; i < locations.size(); ++i) {

                JSONObject obj = (JSONObject) locations.get(i);

                if (!obj.containsKey("location_name")) {
                    return false;
                }
                if (!obj.containsKey("bp_start") || !obj.containsKey("bp_end")){
                	return false;
                }
            }
        }

        return true;
    }


    public boolean validateTandemPromoters(JSONArray jsonArray) {

        if(jsonArray.isEmpty()) {
            return false;
        }

        for (int i=0; i < jsonArray.size(); i++) {

            JSONObject obj = (JSONObject) jsonArray.get(i);

            List<String> required_fields = new ArrayList<String>();
            required_fields.add("name");
            required_fields.add("gateA");
            required_fields.add("gateB");
            required_fields.add("0x_equation");
            required_fields.add("1x_equation");
            required_fields.add("x0_equation");
            required_fields.add("x1_equation");
            required_fields.add("0x_params");
            required_fields.add("1x_params");
            required_fields.add("x0_params");
            required_fields.add("x1_params");


            if (fieldIsMissing(obj, required_fields)) {
                return false;
            }

        }

        return true;
    }

    @Getter
    @Setter
    private String threadDependentLoggername;

    private Logger logger  = Logger.getLogger(getClass());
}

