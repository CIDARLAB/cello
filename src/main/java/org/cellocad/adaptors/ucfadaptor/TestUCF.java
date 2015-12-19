package org.cellocad.adaptors.ucfadaptor;


import org.cellocad.MIT.dnacompiler.Args;
import org.cellocad.MIT.dnacompiler.UCF;
import org.json.simple.JSONObject;

public class TestUCF {

    public static void main(String[] args) {

        Args _options = new Args();
        _options.set_UCFfilepath(_options.get_home() + "/resources/UCF/EcoJS4ib.UCF.json");
        //_options.set_UCFfilepath(_options.get_home() + "/resources/UCF/EcoCB1.UCF.json");

        /**
         * read all UCF collections
         */

        //UCFReader reads the JSON text file and creates the UCF object.
        UCFReader ucf_reader = new UCFReader();

        //UCF.  JSON objects organized by 'collection'.
        UCF ucf = ucf_reader.readAllCollections(_options.get_UCFfilepath());

        //UCFValidator. returns 'false' if something is not valid in the UCF.
        //(note: some collections are optional)
        UCFValidator ucf_validator = new UCFValidator();


        JSONObject ucf_validation_map = ucf_validator.validateAllUCFCollections(ucf, _options);

        boolean is_ucf_valid = (boolean) ucf_validation_map.get("is_valid");


        //optional collections
        // toxicity
        // cytometry
        // eugene rules
        // motif_library
        // genetic locations

        //options is passed in order to turn off the toxicity, histogram, plasmid options if that data
        //is missing from the UCF.
        if (!is_ucf_valid) {
            System.out.println("INVALID UCF: " + _options.get_UCFfilepath());
        }
        else {
            System.out.println("Valid UCF: " + _options.get_UCFfilepath());
        }


    }
}
