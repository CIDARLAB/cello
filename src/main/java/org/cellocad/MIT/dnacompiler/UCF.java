/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cellocad.MIT.dnacompiler;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 *
 * @author Bryan Der
 */
public class UCF {

    //The UCF is an array of JSON objects, each belonging to a 'collection'.
    //Each collection is listed below as a JSON object or array.
    //In the class 'UCFReader', the method 'readAllCollections' populates each collection.

    @Getter @Setter private JSONObject _header = new JSONObject();
    @Getter @Setter private JSONObject _measurement_std = new JSONObject();
    @Getter @Setter private JSONObject _logic_constraints = new JSONObject();
    @Getter @Setter private JSONArray _motif_library = new JSONArray();
    @Getter @Setter private JSONArray _gates = new JSONArray();
    @Getter @Setter private JSONArray _gate_parts = new JSONArray();
    @Getter @Setter private JSONArray _parts = new JSONArray();
    @Getter @Setter private JSONArray _response_functions = new JSONArray();
    @Getter @Setter private JSONArray _gate_cytometry = new JSONArray();
    @Getter @Setter private JSONArray _gate_toxicity = new JSONArray();
    @Getter @Setter private JSONObject _eugene_rules = new JSONObject();
    @Getter @Setter private JSONObject _genetic_locations = new JSONObject();
    @Getter @Setter private JSONArray _tandem_promoters = new JSONArray();
}