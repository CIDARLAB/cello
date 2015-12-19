function getInputs() {
    return ["in1","in2","in3"];
}
function getOutputs() {
    return ["out"];
}

function getBioGates() {
    var biogates = [];
    var AmtR = {};
    var LitR = {};
    var BM3R1 = {};
    var SrpR = {};
    var PhlF = {};

    AmtR["name"] = "AmtR";
    LitR["name"] = "LitR";
    BM3R1["name"]= "BM3R1";
    SrpR["name"] = "SrpR";
    PhlF["name"] = "PhlF";

    AmtR["color"] = "#FF7F00";
    LitR["color"] = "#6A3D9A";
    BM3R1["color"]= "#33A02C";
    SrpR["color"] = "#1F78B4";
    PhlF["color"] = "#E31A1C";

    //g.Name = "NOR_an1-AmtR";
    //g.Name = "NOR_an2-LitR";
    //g.Name = "NOR_an1-BM3R1";
    //g.Name = "NOR_an0-SrpR";
    //g.Name = "NOR_an1-PhlF";

    AmtR["params"] = {};
    LitR["params"] = {};
    BM3R1["params"]= {};
    SrpR["params"] = {};
    PhlF["params"] = {};

    AmtR["params"]["pmax"] = 13.18696;
    AmtR["params"]["pmin"] = 0.316394;
    AmtR["params"]["k"] = 0.169953;
    AmtR["params"]["n"] = 1.319126;

    LitR["params"]["pmax"] = 10.20081;
    LitR["params"]["pmin"] = 0.263379;
    LitR["params"]["k"] = 0.138481;
    LitR["params"]["n"] = 1.542546;

    BM3R1["params"]["pmax"] = 2.168494;
    BM3R1["params"]["pmin"] = 0.019056;
    BM3R1["params"]["k"] = 0.627955;
    BM3R1["params"]["n"] = 2.945651;

    SrpR["params"]["pmax"] = 8.821696;
    SrpR["params"]["pmin"] = 0.030248;
    SrpR["params"]["k"] = 0.415826;
    SrpR["params"]["n"] = 2.809881;

    PhlF["params"]["pmax"] = 17.22043;
    PhlF["params"]["pmin"] = 0.073805;
    PhlF["params"]["k"] = 0.560868;
    PhlF["params"]["n"] = 3.876935;

    biogates.push(BM3R1);
    biogates.push(AmtR);
    biogates.push(SrpR);
    biogates.push(LitR);
    biogates.push(PhlF);


    return biogates;
}
