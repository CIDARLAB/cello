function getNetlist() {
    var n = "";
    n += "NOT(Wire0,in1)\n";
    n += "NOR(Wire1,in2,in3)\n";
    n += "NOR(Wire2,Wire0,Wire1)\n";
    n += "NOR(Wire3,in1,in3)\n";
    n += "NOR(out,Wire2,Wire3)";

    return n;
}