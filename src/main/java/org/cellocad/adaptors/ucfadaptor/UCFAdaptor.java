package org.cellocad.adaptors.ucfadaptor;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.cellocad.BU.parseVerilog.Convert;
import org.cellocad.MIT.dnacompiler.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Created by Bryan Der on 7/29/15.
 */
public class UCFAdaptor {

    /**
     * read the 'parts' collection from the UCF and
     * map the Part object to the part name.
     *
     * @param ucf
     * @return
     */
    public PartLibrary createPartLibrary(UCF ucf) {

        PartLibrary part_library = new PartLibrary();

        HashMap<String, Part> all_parts = new HashMap();

        //each 'part' in the UCF is a json object with these attributes:
        //name, type, dnasequence
        ArrayList<Map> part_json_objects = ucf.get_parts();

        for (Map map : part_json_objects) {

            String name = map.get("name").toString();
            String type = map.get("type").toString();
            String seq  = map.get("dnasequence").toString();

            Part p = new Part(name, type, seq);

            all_parts.put(name, p);

        }

        part_library.set_ALL_PARTS(all_parts);

        return part_library;
    }


    /**
     *
     *
     * @param ucf
     * @param n_inputs  This param is needed
     * @param n_outputs
     * @return
     */
    public GateLibrary createGateLibrary(UCF ucf, int n_inputs, int n_outputs, Args options) {

        GateLibrary gate_library = new GateLibrary(n_inputs, n_outputs);

        //read gates and create gate objects
        ArrayList<Map> gate_json_objects = ucf.get_gates();

        for(Map map: gate_json_objects) {

            Gate g = new Gate();

            g.regulator = map.get("regulator").toString();
            g.name = map.get("gate_name").toString();
            g.group = map.get("group_name").toString();
            g.type = Gate.GateType.valueOf(map.get("gate_type").toString());
            g.system = map.get("system").toString();


            if(options.get_exclude_groups().contains(g.regulator)) {
                continue;
            }

            if(map.containsKey("color_hexcode")) {
                g.colorHex = map.get("color_hexcode").toString();
            }
            else {
                g.colorHex = randomGateColor();
            }

            if(map.containsKey("inducer")) {
                g.inducer = map.get("inducer").toString();
            }
            else {
                g.inducer = "";
            }

            gate_library.get_GATES_BY_NAME().put(g.name, g);
        }


        gate_library.setHashMapsForGates();

        return gate_library;
    }

    public String randomGateColor() {
        Random random = new Random();
        Integer rint = random.nextInt(256);
        Integer gint = random.nextInt(256);
        Integer bint = random.nextInt(256);
        String rhex = "0" + Convert.InttoHex(rint).substring(2);
        String ghex = "0" + Convert.InttoHex(gint).substring(2);
        String bhex = "0" + Convert.InttoHex(bint).substring(2);
        String random_color = rhex.substring(rhex.length()-2,rhex.length()) + ghex.substring(ghex.length()-2,ghex.length()) + bhex.substring(bhex.length()-2,bhex.length());

        return random_color;
    }


    public boolean getOutputOR(UCF ucf) {

        if(ucf.get_logic_constraints().isEmpty()) {
            return false;
        }

        Map map = ucf.get_logic_constraints();

        if(!map.containsKey("available_gates")) {
            return false;
        }

        ArrayList<Map> available_gates = (ArrayList<Map>) map.get("available_gates");

        for (Map cst : available_gates) {
            if(cst.get("type").toString().equals("OUTPUT_OR")) {
                return true;
            }
        }

        return false;
    }


    public void setResponseFunctions(UCF ucf, GateLibrary gate_library) {

        ArrayList<Map> response_fn_json_objects = ucf.get_response_functions();

        for(Map map: response_fn_json_objects) {

            String gate_name = map.get("gate_name").toString();

            if (gate_library.get_GATES_BY_NAME().containsKey(gate_name)) {


                HashMap<String, Double> gate_params = new HashMap<String, Double>();
                HashMap<String, Double[]> gate_variables = new HashMap<String, Double[]>();

                ArrayList<String> gate_variable_names = new ArrayList<String>();

                String gate_equation = map.get("equation").toString();


                ArrayList<Map> parameters = (ArrayList<Map>) map.get("parameters");

                for (Map obj : parameters) {
                    String name = obj.get("name").toString();
                    Double value = Double.valueOf(obj.get("value").toString());
                    gate_params.put(name, value);
                }


                ArrayList<Map> variables = (ArrayList<Map>) map.get("variables");

                for (Map obj : variables) {

                    String name = obj.get("name").toString();

                    gate_variable_names.add(name);

                    if (!obj.containsKey("off_threshold") || !obj.containsKey("on_threshold")) {
                        gate_variables.put(name, null);
                    }
                    else {
                        Double off_threshold = Double.valueOf(obj.get("off_threshold").toString());
                        Double on_threshold = Double.valueOf(obj.get("on_threshold").toString());

                        Double[] thresholds = {off_threshold, on_threshold};

                        gate_variables.put(name, thresholds);
                    }

                }


                Gate g = gate_library.get_GATES_BY_NAME().get(gate_name);

                if (g != null) {

                    for (String v : gate_variables.keySet()) {
                        g.get_variable_wires().put(v, null);
                    }

                    g.set_params(gate_params);
                    g.set_variable_names(gate_variable_names);
                    g.set_variable_thresholds(gate_variables);
                    g.set_equation(gate_equation);
                }
            }
        }
    }


    public void setGateParts(UCF ucf, GateLibrary gate_library, PartLibrary part_library) {

        ArrayList<Map> gate_parts_json_objects = ucf.get_gate_parts();

        for(Map map: gate_parts_json_objects) {

            String gate_name = map.get("gate_name").toString();

            if(gate_library.get_GATES_BY_NAME().containsKey(gate_name)) {

                HashMap<String, ArrayList<Part>> downstream_gate_parts = new HashMap<>();

                //regulable promoter

                String promoter_name = map.get("promoter").toString();

                if(!part_library.get_ALL_PARTS().containsKey(promoter_name)) {
                    throw new IllegalStateException("reading part not found " + promoter_name);
                }

                Part regulable_promoter = new Part(part_library.get_ALL_PARTS().get(promoter_name));


                //downstream parts (can be >1 for multi-input logic gates)

                ArrayList<Map> expression_cassettes = (ArrayList<Map>) map.get("expression_cassettes");

                for (Map object : expression_cassettes) {

                    ArrayList<Part> parts = new ArrayList<Part>();
                    ArrayList<String> part_names = new ArrayList<String>((Collection<? extends String>) object.get("cassette_parts"));
                    String maps_to_variable = (String) object.get("maps_to_variable");

                    for (String part_name : part_names) {

                        if(!part_library.get_ALL_PARTS().containsKey(part_name)) {
                            throw new IllegalStateException("reading part not found " + part_name);
                        }

                        Part p = part_library.get_ALL_PARTS().get(part_name);

                        parts.add(p);

                    }

                    downstream_gate_parts.put(maps_to_variable, parts);

                }

                gate_library.get_GATES_BY_NAME().get(gate_name).set_downstream_parts(downstream_gate_parts);
                gate_library.get_GATES_BY_NAME().get(gate_name).set_regulable_promoter(regulable_promoter);
            }
        }
    }

    public void setGateToxicity(UCF ucf, GateLibrary gate_library, Args options) {

        ArrayList<Map> gate_toxicity_json_objects = ucf.get_gate_toxicity();

        if(gate_toxicity_json_objects.isEmpty()) {
            options.set_toxicity( false );
            return;
        }

        for(Map map: gate_toxicity_json_objects) {

            if(!map.containsKey("gate_name")) {
                continue;
            }

            if(!map.containsKey("input") || !map.containsKey("growth")) {
                continue;
            }

            String gate_name = map.get("gate_name").toString();

            if (gate_library.get_GATES_BY_NAME().containsKey(gate_name)) {

                ArrayList<Double> input = new ArrayList<Double>((Collection<? extends Double>) map.get("input"));
                ArrayList<Double> growth = new ArrayList<Double>((Collection<? extends Double>) map.get("growth"));

                Gate g = gate_library.get_GATES_BY_NAME().get(gate_name);

                ArrayList<Pair> toxtable = new ArrayList<Pair>();
                for (int i = 0; i < input.size(); ++i) {
                    toxtable.add(new Pair(input.get(i), growth.get(i)));
                }

                g.set_toxtable(toxtable);
            }
        }

    }



    public void setGateCytometry(UCF ucf, GateLibrary gate_library, Args options) {

        ArrayList<Map> gate_cytometry_json_objects = ucf.get_gate_cytometry();

        if(gate_cytometry_json_objects.isEmpty()) {
            options.set_histogram(false);
            return;
        }

        for(Map map: gate_cytometry_json_objects) {

            String gate_name = map.get("gate_name").toString();

            if(gate_name.equals(map.get("gate_name").toString())) {

                if (gate_library.get_GATES_BY_NAME().containsKey(gate_name)) {

                    Integer nbins = 0;
                    Double logmin = 0.0;
                    Double logmax = 0.0;

                    ArrayList<double[]> xfer_binned = new ArrayList<double[]>();

                    ArrayList<Double> xfer_titration_inputRPUs = new ArrayList<Double>();

                    ArrayList<JSONObject> titrations = (ArrayList<JSONObject>) map.get("cytometry_data");

                    for (int i = 0; i < titrations.size(); ++i) {
                        //JSONObject titration = JSONObject.fromObject(titrations.get(i));
                        JSONObject titration = titrations.get(i);

                        Double input = Double.valueOf(titration.get("input").toString());

                        ArrayList<Double> output_bins = new ArrayList<Double>((Collection<? extends Double>) titration.get("output_bins"));
                        ArrayList<Double> output_counts = new ArrayList<Double>((Collection<? extends Double>) titration.get("output_counts"));

                        if(i==0) {
                            nbins = output_bins.size();
                            logmin = Math.log10(output_bins.get(0));
                            logmax = Math.log10(output_bins.get(output_bins.size() - 1));
                        }
                        else {
                            double current_logmin = Math.log10(output_bins.get(0));
                            double current_logmax = Math.log10(output_bins.get(output_bins.size() - 1));
                        }

                        xfer_titration_inputRPUs.add(input);

                        double[] xfer_titration_counts = new double[output_counts.size()];
                        for (int b = 0; b < output_counts.size(); ++b) {
                            xfer_titration_counts[b] = output_counts.get(b);
                        }

                        double[] xfer_normalized = HistogramUtil.normalize(xfer_titration_counts);
                        xfer_binned.add(xfer_normalized);

                    }

                    Gate g = gate_library.get_GATES_BY_NAME().get(gate_name);
                    g.get_histogram_bins().init();
                    g.get_histogram_bins().set_NBINS( nbins );
                    g.get_histogram_bins().set_LOGMAX( logmax );
                    g.get_histogram_bins().set_LOGMIN( logmin );

                    g.get_xfer_hist().set_xfer_titration(xfer_titration_inputRPUs);

                    g.get_xfer_hist().set_xfer_binned(xfer_binned);
                }
            }
        }
    }



    //get Eugene gate rules

    public ArrayList<String> getEugeneGateRules(UCF ucf) {

        Map map = ucf.get_eugene_rules();

        if(!map.isEmpty() && map.containsKey("eugene_gate_rules")) {
            ArrayList<String> gate_rules = new ArrayList<String>((Collection<? extends String>) map.get("eugene_gate_rules"));
            return gate_rules;
        }
        else {
            return new ArrayList<String>();
        }
    }


    //get Eugene part rules

    public ArrayList<String> getEugenePartRules(UCF ucf) {

        Map map = ucf.get_eugene_rules();

        if(!map.isEmpty() && map.containsKey("eugene_part_rules")) {
            ArrayList<String> part_rules = new ArrayList<String>((Collection<? extends String>) map.get("eugene_part_rules"));
            return part_rules;
        }
        else {
            return new ArrayList<String>();
        }


    }




    ///////////////////genetic locations

    public String getSensorModuleLocationName(UCF ucf) {
        Map map = ucf.get_genetic_locations();
        if(map.containsKey("sensor_module_location")) {
            JSONArray one_or_more_locations = (JSONArray) map.get("sensor_module_location");
            JSONObject obj = (JSONObject) one_or_more_locations.get(0); //not currently handling cases of more than one location per module
            return obj.get("location_name").toString();
        }
        return null;
    }
    public String getCircuitModuleLocationName(UCF ucf) {
        Map map = ucf.get_genetic_locations();

        if(map.containsKey("circuit_module_location")) {
            JSONArray one_or_more_locations = (JSONArray) map.get("circuit_module_location");
            JSONObject obj = (JSONObject) one_or_more_locations.get(0); //not currently handling cases of more than one location per module
            return obj.get("location_name").toString();
        }
        return null;
    }
    public String getOutputModuleLocationName(UCF ucf) {
        Map map = ucf.get_genetic_locations();
        if(map.containsKey("output_module_location")) {
            JSONArray one_or_more_locations = (JSONArray) map.get("output_module_location");
            JSONObject obj = (JSONObject) one_or_more_locations.get(0); //not currently handling cases of more than one location per module
            return obj.get("location_name").toString();
        }
        return null;
    }

    public Double getUnitConversion(UCF ucf) {

        Double unit_conversion = 1.0;

        Map map = ucf.get_genetic_locations();

        if (!map.containsKey("output_module_location")) {
            return unit_conversion;
        } else {
            ArrayList<JSONObject> module_location = (ArrayList<JSONObject>) map.get("output_module_location");

            for (int i = 0; i < module_location.size(); ++i) {

                JSONObject jsonObject = module_location.get(i);

                if(jsonObject.containsKey("unit_conversion")) {
                    return (Double) jsonObject.get("unit_conversion");
                }
            }

        }

        return unit_conversion;
    }



    public Integer getLocationStartBP(UCF ucf, String module_type) {
        JSONObject genetic_locations = ucf.get_genetic_locations();

        String module_name = module_type + "_module_location";

        if(genetic_locations.containsKey(module_name)) {

            JSONArray module_locations = (JSONArray) genetic_locations.get(module_name);
            JSONObject module_location = (JSONObject) module_locations.get(0); //WARNING: hard-coded!

            Integer bp_start = Integer.valueOf(module_location.get("bp_start").toString());
            return bp_start;
        }

        return 0;
    }

    public int getLocationEndBP(UCF ucf, String module_type) {
        JSONObject genetic_locations = ucf.get_genetic_locations();

        String module_name = module_type + "_module_location";

        if(genetic_locations.containsKey(module_name)) {
            JSONArray module_locations = (JSONArray) genetic_locations.get(module_name);
            JSONObject module_location = (JSONObject) module_locations.get(0); //WARNING: hard-coded!

            Integer bp_end = Integer.valueOf(module_location.get("bp_end").toString());
            return bp_end;
        }

        return 0;
    }

    public String getLocationName(UCF ucf, String module_type) {

        String location_name = "";
        JSONObject genetic_locations = ucf.get_genetic_locations();

        String module_name = module_type + "_module_location";

        if(genetic_locations.containsKey(module_name)) {
            JSONArray module_locations = (JSONArray) genetic_locations.get(module_name);
            JSONObject module_location = (JSONObject) module_locations.get(0); //WARNING: hard-coded!

            location_name = module_location.get("location_name").toString();
        }

        return location_name;
    }

    public ArrayList<String> getLocationGenbankLines(UCF ucf, String module_type) {

        ArrayList<String> genbank_lines = new ArrayList<>();

        JSONObject genetic_locations = ucf.get_genetic_locations();

        String module_name = module_type + "_module_location";

        if(genetic_locations.containsKey(module_name)) {
            JSONArray module_locations = (JSONArray) genetic_locations.get(module_name);
            JSONObject module_location = (JSONObject) module_locations.get(0); //WARNING: hard-coded!

            String location_name = module_location.get("location_name").toString();

            JSONArray locations = (JSONArray) genetic_locations.get("locations");
            for (int i = 0; i < locations.size(); ++i) {
                JSONObject location = (JSONObject) locations.get(i);
                if (location.get("name").equals(location_name)) {
                    genbank_lines = (ArrayList<String>) location.get("file");
                }
            }
        }

        return genbank_lines;
    }



    public void setTandemPromoters(UCF ucf, GateLibrary gate_library, Args options) {

        HistogramBins hbins = new HistogramBins();
        hbins.init();

        if(ucf.get_tandem_promoters().isEmpty()) {
            options.set_tandem_promoter( false );
            return;
        }

        //iterate through tp's

        JSONArray tps = ucf.get_tandem_promoters();

        for(int i=0; i<tps.size(); ++i) {
            Map map = (Map) tps.get(i);

            JSONObject tp = (JSONObject) tps.get(i);

            String tp_name = (String) tp.get("name");
            System.out.println("reading grid for " + tp_name);

            double[][] grid = new double[hbins.get_NBINS()][hbins.get_NBINS()];

            JSONArray outer = (JSONArray) tp.get("grid");
            for(int j = 0; j< outer.size(); ++j) {
                JSONArray inner = (JSONArray) outer.get(j);

                for(int k = 0; k< inner.size(); ++k) {
                    Double val = (Double) inner.get(k);

                    grid[j][k] = val;
                }
            }


            //InterpolateTandemPromoter itp = new InterpolateTandemPromoter();
            //double[][] grid = itp.interpolateTandemPromoter(tp, tp_name);


            /**
             * See also: GateUtil.getVariableValues,
             * tandem promoter data will change the additive assumption for tandem promoter RPU values.
             */
            gate_library.get_TANDEM_PROMOTERS().put(tp_name, grid);
        }

    }


    @Getter
    @Setter
    private String threadDependentLoggername;

    private Logger logger  = Logger.getLogger(getClass());
}
