#!/usr/bin/env python

import csv
import sys
import json



def gates_from_csv(table, header_map):

    gates = []
    for row in table:
        obj = {}
        obj["collection"] = "gates"
        obj["regulator"] = row[header_map["cds"]]
        obj["group_name"] = row[header_map["cds"]]
        obj["gate_name"] = row[header_map["name"]]
        obj["gate_type"] = row[header_map["type"]]
        obj["system"] = "TetR"
        obj["color_hexcode"] = "000000" # for images
        gates.append(obj)

    return gates


def parts_from_csv(table, header_map):

    parts = []
    for row in table:

        ribozyme = {}
        ribozyme["collection"] = "parts"
        ribozyme["type"] = "ribozyme"
        ribozyme["name"] = row[header_map["ribozyme"]]
        ribozyme["dnasequence"] = row[header_map["ribozymeDNA"]]

        rbs = {}
        rbs["collection"] = "parts"
        rbs["type"] = "rbs"
        rbs["name"] = row[header_map["rbs"]]
        rbs["dnasequence"] = row[header_map["rbsDNA"]]

        cds = {}
        cds["collection"] = "parts"
        cds["type"] = "cds"
        cds["name"] = row[header_map["cds"]]
        cds["dnasequence"] = row[header_map["cdsDNA"]]

        terminator = {}
        terminator["collection"] = "parts"
        terminator["type"] = "terminator"
        terminator["name"] = row[header_map["terminator"]]
        terminator["dnasequence"] = row[header_map["terminatorDNA"]]


        promoter = {}
        promoter["collection"] = "parts"
        promoter["type"] = "promoter"
        promoter["name"] = row[header_map["promoter"]]
        promoter["dnasequence"] = row[header_map["promoterDNA"]]

        parts.append(ribozyme)
        parts.append(rbs)
        parts.append(cds)
        parts.append(terminator)
        parts.append(promoter)

    return parts


def gate_parts_from_csv(table, header_map):

    gate_parts = []
    for row in table:

        gate_name = row[header_map["name"]]

        cassette_part_names = []
        cassette_part_names.append(row[header_map["ribozyme"]])
        cassette_part_names.append(row[header_map["rbs"]])
        cassette_part_names.append(row[header_map["cds"]])
        cassette_part_names.append(row[header_map["terminator"]])

        expression_cassettes = []
        cassette = {}
        cassette["maps_to_variable"] = "x"
        cassette["cassette_parts"] = cassette_part_names
        expression_cassettes.append(cassette)

        obj = {}
        obj["collection"] = "gate_parts"
        obj["gate_name"] = gate_name
        obj["expression_cassettes"] = expression_cassettes
        obj["promoter"] = row[header_map["promoter"]]

        gate_parts.append(obj)
    
    return gate_parts


def response_functions_from_csv(table, header_map):
    response_functions = []
    for row in table:

        obj = {}
        obj["collection"] = "response_functions"

        gate_name = row[header_map["name"]]
        equation = row[header_map["equation"]]

        ymax = row[header_map["ymax"]]
        ymin = row[header_map["ymin"]]
        K    = row[header_map["K"]]
        n    = row[header_map["n"]]
        IL   = row[header_map["IL"]]
        IH   = row[header_map["IH"]]

        map_ymax = {}
        map_ymin = {}
        map_K = {}
        map_n = {}

        map_ymax["name"] = "ymax"
        map_ymax["value"] = ymax
        map_ymin["name"] = "ymin"
        map_ymin["value"] = ymin
        map_K["name"] = "K"
        map_K["value"] = K
        map_n["name"] = "n"
        map_n["value"] = n


        parameters = []
        parameters.append(map_ymax)
        parameters.append(map_ymin)
        parameters.append(map_K)
        parameters.append(map_n)


        variables = []
        map_var = {}
        map_var["name"] = "x"
        map_var["off_threshold"] = IL
        map_var["on_threshold"] =  IH
        variables.append(map_var)


        obj["gate_name"] = gate_name
        obj["equation"] = equation
        obj["variables"] = variables
        obj["parameters"] = parameters

        response_functions.append(obj)

    return response_functions


def eugene_rules(roadblock_promoters):
    eugene_rules = {}
    eugene_rules["collection"] = "eugene_rules"
    eugene_rules["eugene_part_rules"] = []
    eugene_rules["eugene_gate_rules"] = []

    for promoter_name in roadblock_promoters:
        eugene_rules["eugene_part_rules"].append("STARTSWITH " + promoter_name)
        

    return eugene_rules


def writeUCF(table, header_map):


    # description only, values not parsed
    header = {}
    header["collection"] = "header"
    header["description"] = "placeholder"
    header["version"] = "placeholder"
    header["date"] = "placeholder"
    header["author"] = ["author1"]
    header["organism"] = "Escherichia coli NEB 10-beta"
    header["genome"] = "placeholder"
    header["media"] = "placeholder"
    header["temperature"] = "37"
    header["growth"] = "placeholder"

    # description only, values not parsed
    measurement_std = {}
    measurement_std["collection"] = "measurement_std"
    measurement_std["signal_carrier_units"] = "REU"
    measurement_std["normalization_instructions"] = "placeholder"
    measurement_std["plasmid_description"] = "placeholder"
    measurement_std["plasmid_sequence"] = "placeholder"

    # Not used
    logic_constraints = {}
    nor = {}
    nor["type"] = "NOR"
    nor["max_instances"] = 10
    outor = {}
    outor["type"] = "OUTPUT_OR"
    outor["max_instances"] = 3
    gate_type_constraints = []
    gate_type_constraints.append(nor)
    gate_type_constraints.append(outor)
    logic_constraints["collection"] = "logic_constraints"
    logic_constraints["available_gates"] = gate_type_constraints


    # For Netsynth motif swapping
    motif_library = []
    output_or = {}
    output_or["collection"] = "motif_library"
    output_or["inputs"] = ["a", "b"]
    output_or["outputs"] = ["y"]
    output_or["netlist"] = []
    output_or["netlist"].append("OUTPUT_OR(y,a,b)")
    motif_library.append(output_or)



    gates = gates_from_csv(table, header_map)

    response_functions = response_functions_from_csv(table, header_map)

    gate_parts = gate_parts_from_csv(table, header_map)

    parts = parts_from_csv(table, header_map)


    roadblock_promoters = []
    roadblock_promoters.append("pTac")    
    roadblock_promoters.append("pBAD")
    roadblock_promoters.append("pPhlF")
    roadblock_promoters.append("pSrpR")
    roadblock_promoters.append("pBM3R1")
    roadblock_promoters.append("pQacR")

    eugene = eugene_rules(roadblock_promoters)



    ucf = []
    ucf.append(header)
    ucf.append(measurement_std)
    ucf.append(logic_constraints)
    ucf.extend(motif_library)
    ucf.extend(gates)
    ucf.extend(response_functions)
    ucf.extend(gate_parts)
    ucf.extend(parts)
    ucf.append(eugene)

    print json.dumps(ucf, indent=2)




if __name__ == '__main__':
    
    if len(sys.argv) < 2:
        print "Example usage:"

        print ''
        print 'Step 1: write UCF'
        print 'python ucf_writer.py  ../../resources/csv_gate_libraries/gates_Eco1C1G1T1.csv > myName.UCF.json'
        
        print '\n\n'
        print 'Step 2: post UCF'
        print 'cello post_ucf --name myName.UCF.json --filepath myName.UCF.json'
        print 'or'
        print 'curl -u "username:password" -X POST http://127.0.0.1:8080/ucf/myName.UCF.json --data-urlencode "filetext@myName.UCF.json"'

        print '\n\n'
        print 'Step 3: validate UCF'
        print 'cello validate_ucf --name myName.UCF.json'
        print 'or'
        print 'curl -u "username:password" -X GET http://127.0.0.1:8080/ucf/myName.UCF.json/validate'

        print '\n\n'
        print 'Optional: delete UCF (invalid UCFs should be deleted)'
        print 'cello delete_ucf --name myName.UCF.json'
        print 'or'
        print 'curl -u "username:password" -X GET http://127.0.0.1:8080/ucf/myName.UCF.json/validate'

        print '\n\n'
        print 'cello submit --jobid j3 --verilog resources/0xFE.v --inputs resources/Inputs.txt --outputs resources/Outputs.txt --options "-UCF myName.UCF.json -plasmid false -eugene false"'

        print '\n\n'
        sys.exit()


    csvpath = sys.argv[1]

        

    with open(csvpath) as csvfile:
        csvreader = csv.reader(csvfile, delimiter=',')

        # Header
        headers = next(csvreader, None)
        header_map = {}
        i = 0
        for col in headers:
            header_map[col] = i
            i = i+1

        # Table
        table = []
        for row in csvreader:
            values = []
            for col in row:
                values.append(col)
            table.append(row)


        writeUCF(table, header_map)

    

