package org.cellocad.MIT.dnacompiler;

import org.cellocad.MIT.dnacompiler.Gate.GateType;

import java.util.ArrayList;

/**
 * Created by Bryan Der on 9/15/14.
 */

public class BooleanLogic {

    /**
     *
     * Compute logic based on gate type and input bits
     *
     * NOT of [0]   = 1
     * NOR of [0,0] = 1
     * AND of [1,1] = 1
     * OUTPUT of [1] = 1
     * OUTPUT_OR of [0,1] = 1
     *
     * @param gate_type
     * @param inputs
     * @return
     */
    public static Integer computeLogic(GateType gate_type, ArrayList<Integer> inputs){
        Integer out = new Integer(-1);
        if (gate_type == GateType.OR || gate_type == GateType.OUTPUT_OR || gate_type == GateType.OUTPUT){
            out = computeOR(inputs);
        }else if (gate_type == GateType.NOR){
            out = computeNOR(inputs);
        }
        else if (gate_type == GateType.NOT) {
            out = computeNOT(inputs.get(0));
        }
        else if (gate_type == GateType.AND) {
            out = computeAND(inputs);
        }
        else if (gate_type == GateType.NAND) {
            out = computeNAND(inputs);
        }
        else if (gate_type == GateType.XOR) {
            out = computeXOR(inputs);
        }
        else if (gate_type == GateType.XNOR) {
            out = computeXNOR(inputs);
        }
        return out;
    }

    /**
     * invert
     *
     * @param in1
     * @return
     */
    public static Integer computeNOT(Integer in1){
        if ( in1 == 0 ) {
            return 1;
        }
        return 0;
    }

    /**
     * NOR: any input with a 1 gives a 0
     *
     * @param inputs
     * @return
     */
    public static Integer computeNOR(ArrayList<Integer> inputs){
        for(Integer i: inputs) {
            if(i == 1) {
                return 0;
            }
        }
        return 1;
    }

    /**
     * OR: any input with a 1 gives a 1
     *
     * @param inputs
     * @return
     */
    public static Integer computeOR(ArrayList<Integer> inputs){
        for(Integer i: inputs) {
            if(i == 1) {
                return 1;
            }
        }
        return 0;
    }


    /**
     * AND: any input with a 0 gives a 0
     *
     * @param inputs
     * @return
     */
    public static Integer computeAND(ArrayList<Integer> inputs){
        for(Integer i: inputs) {
            if(i == 0) {
                return 0;
            }
        }
        return 1;
    }
    
    public static Integer computeNAND(ArrayList<Integer> inputs){
        for(Integer i:inputs){
            if(i == 0) {
                return 1;
            }
        }
        return 0;
    }
    
    public static Integer computeXOR(ArrayList<Integer> inputs){
        int count = 0;
        for(Integer i:inputs){
            count += i;
        }
        if(count%2 == 0){
            return 0;
        }
        return 1;
    }

    public static Integer computeXNOR(ArrayList<Integer> inputs){
        int count = 0;
        for(Integer i:inputs){
            count += i;
        }
        if(count%2 == 0){
            return 1;
        }
        return 0;
    }
    /**
     * Reformat toString() of ArrayList<Integer>.  Remove spaces and brackets.
     *
     * ArrayList toString would give [0, 0, 0, 1] for 2-input AND logic, this function returns "0001"
     *
     * @param logic
     * @return
     */
    public static String logicString(ArrayList<Integer> logic) {
        String l = logic.toString();
        l = l.replaceAll("\\[","");
        l = l.replaceAll("\\]","");
        l = l.replaceAll(",","");
        l = l.replaceAll(" ","");
        l = l.replaceAll("2","-");//dontcare
        return l;
    }
}
