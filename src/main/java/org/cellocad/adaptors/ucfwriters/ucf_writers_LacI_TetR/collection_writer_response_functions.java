package org.cellocad.adaptors.ucfwriters.ucf_writers_LacI_TetR;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_response_functions extends collection_writer {

    @Override
    public ArrayList<Map> getObjects() {

        ArrayList<Map> objects = new ArrayList<>();


        Map laci_obj = new LinkedHashMap();
        laci_obj.put("collection", "response_functions");

        Map tetr_obj = new LinkedHashMap();
        tetr_obj.put("collection", "response_functions");


        String equation = "ymin+(ymax-ymin)/(1.0+(x/K)^n)";

        LinkedHashMap map_laci_ymax = new LinkedHashMap();
        LinkedHashMap map_laci_ymin = new LinkedHashMap();
        LinkedHashMap map_laci_K = new LinkedHashMap();
        LinkedHashMap map_laci_n = new LinkedHashMap();

        LinkedHashMap map_tetr_ymax = new LinkedHashMap();
        LinkedHashMap map_tetr_ymin = new LinkedHashMap();
        LinkedHashMap map_tetr_K = new LinkedHashMap();
        LinkedHashMap map_tetr_n = new LinkedHashMap();



        map_laci_ymax.put("name", "ymax");
        map_laci_ymax.put("value", 5.27540867);
        map_laci_ymin.put("name", "ymin");
        map_laci_ymin.put("value", 0.0116915423);
        map_laci_K.put("name", "K");
        map_laci_K.put("value", 0.213145414);
        map_laci_n.put("name", "n");
        map_laci_n.put("value", 1.661943472);


        map_tetr_ymax.put("name", "ymax");
        map_tetr_ymax.put("value", 8.9257285);
        map_tetr_ymin.put("name", "ymin");
        map_tetr_ymin.put("value", 0.008315565);
        map_tetr_K.put("name", "K");
        map_tetr_K.put("value", 0.960097246);
        map_tetr_n.put("name", "n");
        map_tetr_n.put("value", 5.418426561);



        ArrayList<Map> laci_parameters = new ArrayList<Map>();
        laci_parameters.add(map_laci_ymax);
        laci_parameters.add(map_laci_ymin);
        laci_parameters.add(map_laci_K);
        laci_parameters.add(map_laci_n);


        ArrayList<Map> tetr_parameters = new ArrayList<Map>();
        tetr_parameters.add(map_tetr_ymax);
        tetr_parameters.add(map_tetr_ymin);
        tetr_parameters.add(map_tetr_K);
        tetr_parameters.add(map_tetr_n);


        ArrayList<Map> laci_variables = new ArrayList<Map>();
        LinkedHashMap laci_map_var = new LinkedHashMap();
        laci_map_var.put("name", "x");
        //laci_map_var.put("off_threshold", 0.2);
        //laci_map_var.put("on_threshold",  0.9);
        laci_variables.add(laci_map_var);

        ArrayList<Map> tetr_variables = new ArrayList<Map>();
        LinkedHashMap tetr_map_var = new LinkedHashMap();
        tetr_map_var.put("name", "x");
        //tetr_map_var.put("off_threshold", 0.9);
        //tetr_map_var.put("on_threshold",  5.0);
        tetr_variables.add(tetr_map_var);

        laci_obj.put("gate_name", "const_LacI");
        laci_obj.put("equation", equation);
        laci_obj.put("variables", laci_variables);
        laci_obj.put("parameters", laci_parameters);

        tetr_obj.put("gate_name", "const_TetR");
        tetr_obj.put("equation", equation);
        tetr_obj.put("variables", tetr_variables);
        tetr_obj.put("parameters", tetr_parameters);

        objects.add(laci_obj);
        objects.add(tetr_obj);


        return objects;

    }
}
