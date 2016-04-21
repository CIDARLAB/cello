package org.cellocad.MIT.dnacompiler;
/**
 * Created by Bryan Der on 3/26/14.
 */

import java.util.HashMap;

/**
 *
 * Compute out_rpu from in_rpu given the 4 transfer function parameters
 *
 */
public class ResponseFunction {

    /**
     *
     * Given an equation, parameter values, and variable values, compute output RPU
     *
     * 'variables' maps a variable name (e.g. "x") to a summed value if there are tandem promoters
     *
     * 'equation' would be a String "ymin+(ymax-ymin)/(1.0+(x/K)^n)", for example.
     */
    public static double computeOutput(HashMap<String, Double> variables, HashMap<String, Double> params, String equation){

        MathEval math = new MathEval();

        if(params != null) {
            for (String param_name : params.keySet()) {
                math.setVariable(param_name, params.get(param_name));
            }
        }

        for(String variable_name: variables.keySet()) {
            math.setVariable(variable_name, variables.get(variable_name));

        }

        //math.setVariable("x", input);

        //double outputRPU = math.evaluate("ymin+(ymax-ymin)/(1.0+(x/K)^n)"); //hill function

        double outputRPU = math.evaluate(equation);

        return outputRPU;
    }


};
