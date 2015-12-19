package org.cellocad.MIT.logic_motif_synthesis;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Circuit{
	
	//A quick way to check whether a circuit can be used to build other circuits or not.
	boolean isGood = true;
	
	//Other properties of a circuit
	String name;
	String truthValue;
	String operator = null;
	double circuitCost;
	
	//subcircuits is a HashMap that maps strings of subcircuits to their key operator. There will only be one copy of each unique 
	//subcircuit in HashMap. ((a.b).(a.b)) will only have (a.b) once.
	HashMap<String,String> subcircuits = new HashMap<String,String> ();
	
	//Keeps track of each of the truth values in its subcircuits. We do not want to save a circuit if it gives the same truth value
	//as one of its subcircuits.
	HashSet<String> containedTruthValues= new HashSet<String> ();
	
	//Constructors for the class
	//For building inputs
	public Circuit(String symbols, String values) {
		if (values == null|| symbols==null|| (!(values.contains("1")) && !(values.contains("0")))){
			isGood = false;
			return;
		}
		name = symbols;
		truthValue = values;
		containedTruthValues.add(truthValue);
		circuitCost = 0.0;
	}

	//For building NOT of a circuit
	public Circuit (String op, Circuit alpha) {
		if (!(op.equals("~")) || !(alpha.canBeUsed()) || alpha==null){
			isGood = false;
			return;
		}
		truthValue = NOT(alpha.getTruthValue());
		containedTruthValues.addAll(alpha.getContainedTruthValues());
		if(truthValue == null || truthValue.equals("00000000")||truthValue.equals("11111111")||containedTruthValues.contains(truthValue)){
			isGood = false;
			return;
		}
		containedTruthValues.add(truthValue);
		operator = op;
		name = "(" + operator +alpha.getName() + ")";
		
		subcircuits.putAll(alpha.getSubcircuits());
		if (alpha.getOperator() != null){
			subcircuits.put(alpha.getName(), alpha.getOperator());
		}
		
		
		circuitCost = calcCost();
	}
	
	//For building circuits by combining two smaller circuits with an approved operator
	public Circuit (Circuit alpha,String op ,Circuit beta) {
		if (!(ConstantProperties.approvedOperators.contains(op) && !(op.equals("~")) ) 
				|| !(alpha.canBeUsed()) || !(beta.canBeUsed()) 
				//We want to allow the NAND of a with itself
				|| (beta.getName().equals(alpha.getName()) && !(op.equals("@")))
				|| alpha == null || beta == null){
			isGood = false;
			return;
		}
		//Finds the truth value by using the operator to combine the two subcircuits' truth values.
		if (op.equals("&")){
			truthValue = AND(alpha.getTruthValue(),beta.getTruthValue());
		}
		else if (op.equals("@")){
			truthValue = NAND(alpha.getTruthValue(),beta.getTruthValue());
		}
		else if (op.equals("+")){
			truthValue = OR(alpha.getTruthValue(),beta.getTruthValue());
		}
		else if (op.equals("^")){
			truthValue = XOR(alpha.getTruthValue(),beta.getTruthValue());
		}
		else if (op.equals(".")){
			truthValue = NOR(alpha.getTruthValue(),beta.getTruthValue());
		}
		else if (op.equals("=")){
			truthValue = XNOR(alpha.getTruthValue(),beta.getTruthValue());
		}
		else if (op.equals(">")){
			truthValue = IMPLIES(alpha.getTruthValue(),beta.getTruthValue());
		}
		else if (op.equals("$")){
			truthValue = NIMPLIES(alpha.getTruthValue(),beta.getTruthValue());
		}
		else{
			isGood = false;
			return;
		}
		
		containedTruthValues.addAll(alpha.getContainedTruthValues());
		containedTruthValues.addAll(beta.getContainedTruthValues());
		if(truthValue == null || truthValue.equals("00000000")||truthValue.equals("11111111")||containedTruthValues.contains(truthValue)){
			isGood = false;
			return;
		}
		containedTruthValues.add(truthValue);
		operator = op;
		name = "(" + alpha.getName() + operator + beta.getName() + ")";
		//Makes the new set of subcircuits from what a and b had. This must be done before calculating the cost.
		subcircuits.putAll(alpha.getSubcircuits());
		subcircuits.putAll(beta.getSubcircuits());
		if (alpha.getOperator() != null){
			subcircuits.put(alpha.getName(), alpha.getOperator());
		}
		if (beta.getOperator() != null){
			subcircuits.put(beta.getName(), beta.getOperator());
		}
		circuitCost = calcCost();
	}
	
	//Getter functions
	public boolean canBeUsed(){
		return isGood;
	}
	public String getTruthValue(){
		return truthValue;
	}
	public String getName(){
		return name;
	}
	public HashSet<String> getContainedTruthValues(){
		return containedTruthValues;
	}
	public String getOperator(){
		return operator;
	}
	public HashMap<String, String> getSubcircuits(){
		return subcircuits;
	}
	public String toString(){
		return name;
	}
	public double getCost(){
		return circuitCost;
	}
	
	//Determines the cost of the circuit
	public double calcCost(){
		double totalCost = 0;
		Set<String> keys = subcircuits.keySet();
		for (String key:keys){
			totalCost += ConstantProperties.costPerOp.get(subcircuits.get(key));
		}
		totalCost += ConstantProperties.costPerOp.get(operator);
		totalCost = roundTwoDecimals(totalCost);
		return totalCost;
	}
	
	//Used to round the cost of the circuits to two decimal places.
	double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
    return Double.valueOf(twoDForm.format(d));
	}
	
	//Checks if two circuits are equal by comparing their names.
	public boolean equals(Circuit beta){
		return name.equals(beta.getName());
	}
	
	//Logic Operators
	String NOT(String values){
		if (values==null){
			isGood = false;
			return null;
		}
		String y = "";
		for (char i:values.toCharArray()){
			if (i=='1'){
				y+="0";
			}
			else if (i=='0'){;
				y+="1";
			}
		}

		return y;
	}
	String AND(String alphaVals, String betaVals){
		if (alphaVals==null || betaVals==null || alphaVals.length() != betaVals.length()){
			isGood = false;
			return null;
		}
		int x = alphaVals.length();
		String y = "";
		for (int i=0;i<x;i++){
			if (alphaVals.charAt(i)=='0' || betaVals.charAt(i)=='0'){
				y+="0";
			}
			else if (alphaVals.charAt(i)=='1' && betaVals.charAt(i)=='1'){
				y+="1";
			}
		}
		return y;
		
	}
	String NAND(String alphaVals, String betaVals){
		if (alphaVals==null || betaVals==null || alphaVals.length() != betaVals.length()){
			isGood = false;
			return null;
		}
		int x = alphaVals.length();
		String y = "";
		for (int i=0;i<x;i++){
			if (alphaVals.charAt(i)=='0' || betaVals.charAt(i)=='0'){
				y+="1";
			}
			else if (alphaVals.charAt(i)=='1' && betaVals.charAt(i)=='1'){
				y+="0";
			}
		}
		return y;
		
	}
	String OR(String alphaVals, String betaVals){
		if (alphaVals==null || betaVals==null || alphaVals.length() != betaVals.length()){
			isGood = false;
			return null;
		}
		int x = alphaVals.length();
		String y = "";
		for (int i=0;i<x;i++){
			if (alphaVals.charAt(i)=='1' || betaVals.charAt(i)=='1'){
				y+="1";
			}
			else if (alphaVals.charAt(i)=='0' && betaVals.charAt(i)=='0'){
				y+="0";
			}
		}
		return y;
		
	}
	String XOR(String alphaVals, String betaVals){
		if (alphaVals==null || betaVals==null || alphaVals.length() != betaVals.length()){
			isGood = false;
			return null;
		}
		int x = alphaVals.length();
		String y = "";
		for (int i=0;i<x;i++){
			if (alphaVals.charAt(i)==betaVals.charAt(i)){
				y+="0";
			}
			else if (alphaVals.charAt(i)!=betaVals.charAt(i)){
				y+="1";
			}
		}
		return y;
		
	}
	String NOR(String alphaVals, String betaVals){
		if (alphaVals==null || betaVals==null || alphaVals.length() != betaVals.length()){
			isGood = false;
			return null;
		}
		int x = alphaVals.length();
		String y = "";
		for (int i=0;i<x;i++){
			if (alphaVals.charAt(i)=='1' || betaVals.charAt(i)=='1'){
				y+="0";
			}
			else if (alphaVals.charAt(i)=='0' && betaVals.charAt(i)=='0'){
				y+="1";
			}
		}
		return y;
		
	}
	String XNOR(String alphaVals, String betaVals){
		if (alphaVals==null || betaVals==null || alphaVals.length() != betaVals.length()){
			isGood = false;
			return null;
		}
		int x = alphaVals.length();
		String y = "";
		for (int i=0;i<x;i++){
			if (alphaVals.charAt(i)==betaVals.charAt(i)){
				y+="1";
			}
			else if (alphaVals.charAt(i)!=betaVals.charAt(i)){
				y+="0";
			}
		}
		return y;
		
	}
	String IMPLIES(String alphaVals, String betaVals){
		if (alphaVals==null || betaVals==null || alphaVals.length() != betaVals.length()){
			isGood = false;
			return null;
		}
		int x = alphaVals.length();
		String y = "";
		for (int i=0;i<x;i++){
			if (alphaVals.charAt(i)=='0' || betaVals.charAt(i)=='1'){
				y+="1";
			}
			else if (alphaVals.charAt(i)=='1' && betaVals.charAt(i)=='0'){
				y+="0";
			}
		}
		return y;
		
	}
	String NIMPLIES(String alphaVals, String betaVals){
		if (alphaVals==null || betaVals==null || alphaVals.length() != betaVals.length()){
			isGood = false;
			return null;
		}
		int x = alphaVals.length();
		String y = "";
		for (int i=0;i<x;i++){
			if (alphaVals.charAt(i)=='0' || betaVals.charAt(i)=='1'){
				y+="0";
			}
			else if (alphaVals.charAt(i)=='1' && betaVals.charAt(i)=='0'){
				y+="1";
			}
		}
		return y;
		
	}
	

}