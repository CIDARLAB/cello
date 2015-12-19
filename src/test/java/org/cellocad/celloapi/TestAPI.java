package org.cellocad.celloapi;

import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by peng on 10/21/15.
 */
public class TestAPI {

    @Test
    public void testInputJson() {


        JSONArray inputs = new JSONArray();
        JSONObject pTac = new JSONObject();
        JSONObject pTet = new JSONObject();
        JSONObject pBAD = new JSONObject();

        pTac.put("name", "pTac");
        pTac.put("low_reu", 0.0034);
        pTac.put("high_reu", 2.8);
        pTac.put("dna_seq", "AACGATCGTTGGCTGTGTTGACAATTAATCATCGGCTCGTATAATGTGTGGAATTGTGAGCGCTCACAATT");

        pTet.put("name", "pTet");
        pTet.put("low_reu", 0.0013);
        pTet.put("high_reu", 4.4);
        pTet.put("dna_seq", "TACTCCACCGTTGGCTTTTTTCCCTATCAGTGATAGAGATTGACATCCCTATCAGTGATAGAGATAATGAGCAC");

        pBAD.put("name", "pBAD");
        pBAD.put("low_reu", 0.0082);
        pBAD.put("high_reu", 2.4);
        pBAD.put("dna_seq", "ACTTTTCATACTCCCGCCATTCAGAGAAGAAACCAATTGTCCATATTGCATCAGACATTGCCGTCACTGCGTCTTTTACTGGCTCTTCTCGCTAACCAAACCGGTAACCCCGCTTATTAAAAGCATTCTGTAACAAAGCGGGACCAAAGCCATGACAAAAACGCGTAACAAAAGTGTCTATAATCACGGCAGAAAAGTCCACATTGATTATTTGCACGGCGTCACACTTTGCTATGCCATAGCATTTTTATCCATAAGATTAGCGGATCCTACCTGACGCTTTTTATCGCAACTCTCTACTGTTTCTCCATACCCGTTTTTTTGGGCTAGC");

        inputs.add(pTac);
        inputs.add(pTet);
        inputs.add(pBAD);

        JSONObject inputsObj = new JSONObject();
        inputsObj.put("inputs", inputs);

//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String json = gson.toJson(inputsObj);
//        System.out.println(inputsObj.toString());
//        System.out.println(pTac.toString());

        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        ResponseEntity<String> response = new ResponseEntity<String>(headers, HttpStatus.OK);
        rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        Gson gson = new Gson();
        System.out.println(gson.toJson(pTac));

        HashMap<String, String> testMap = new HashMap<>();
        testMap.put("params", "ABC");

        String url = "http://localhost:8080/test/test7";
        try {
//            response = rest.getForEntity(url, String.class);
//            HttpEntity<String> entity = new HttpEntity<String>(pTac.toString());
//            HttpEntity<HashMap<String,String>> entity = new HttpEntity<HashMap<String,String>>(testMap);
            HttpEntity<String> entity = new HttpEntity<String>(pTac.toString());
            response = rest.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error in post entity");
        }




//        TestController tc = new TestController();
//        ResponseEntity<String> response = tc.test4(new HashMap<String, String>());
//        System.out.println(response.getStatusCode());
//        System.out.println(response.getBody());


//        String url = "http://localhost:8080/test/inputs?inputString="+"test";
//        String url = "http://localhost:8080/test/inputs/";

//        HttpEntity<String> entity = new HttpEntity<String>(pTac.toJSONString());
//
//        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
//        System.out.println(response.getStatusCode());
//        System.out.println(response.getBody().toString());

    }

}
