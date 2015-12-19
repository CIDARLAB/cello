package org.cellocad.MIT.logic_motif_synthesis;

import java.util.*;


public class Optimize{
	public static HashMap<String,ArrayList<Circuit>> optimizer1(){
		
		HashSet<String> allowedOps = ConstantProperties.allowedOps;
		double maxCost = ConstantProperties.maxCost;
		String truthValueToFind = ConstantProperties.truthValueToFind;
		String dir = ConstantProperties.dir;
		System.out.println(allowedOps);
		// Starts the timer
		double startTime = System.currentTimeMillis();
		
		//Stops the program if it is run with anything but 2 or 3 inputs.
		if (ConstantProperties.numInputs!=3 && ConstantProperties.numInputs!=2){
			System.out.println("This only works for two or three inputs.");
			return null;
		}
		
		//Makes sure that you included a at least the minimum number of gates to be able to find all the truth values.
		boolean isPoss = true;
		if (allowedOps.size()<4){
			isPoss = false;
			//The allowed operations must include each operator from at least one of the HashSets in combo.
			HashSet<HashSet<String>> combos = new HashSet<HashSet<String>>();
			HashSet<String> allowed = new HashSet<String>(Arrays.asList("@"));
			combos.add(allowed);
			allowed = new HashSet<String>(Arrays.asList("."));
			combos.add(allowed);
			allowed = new HashSet<String>(Arrays.asList("+","~"));
			combos.add(allowed);
			allowed = new HashSet<String>(Arrays.asList("&","~"));
			combos.add(allowed);
			allowed = new HashSet<String>(Arrays.asList(">"));
			combos.add(allowed);
			allowed = new HashSet<String>(Arrays.asList("$"));
			combos.add(allowed);
			allowed = new HashSet<String>(Arrays.asList("+","="));
			combos.add(allowed);
			allowed = new HashSet<String>(Arrays.asList("+","^"));
			combos.add(allowed);
			allowed = new HashSet<String>(Arrays.asList("&","="));
			combos.add(allowed);
			allowed = new HashSet<String>(Arrays.asList("&","^"));
			combos.add(allowed);
			//Determines that there is a problem if it goes through each HashSet in combos and cannot find a Set for which each of
			//its operators are contained in the allowed operators.
			for (HashSet<String> acc:combos){
				boolean problem = false;
				for(String s : acc){
					if (!allowedOps.contains(s)){
						problem = true;
						break;
					}
				}
				if (!problem){
					isPoss = true;
					break;
				}
			}
		}
		//Stops the program if there it is not possible to find all truth values with the given operations.
		if (!isPoss){
			System.out.println("This combination of allowed circuits will not work.");
			return null;
		}
		
		//Adjusts the name of the output file if it is not a text file.
		Date d = new Date();
		boolean alterName = false;
		if (dir.endsWith("/") || !dir.contains(".json")){
			alterName = true;
			if (!dir.endsWith("/")){
				dir += "/";
			}
			String fileName = "MaxCost"+maxCost;
			for (String op:allowedOps){
				fileName += op;
			}
			fileName = fileName + d.toString().replace(' ', '_').replace(':', '_');
			dir = dir + fileName + ".json";
		}
		System.out.println(dir);
		
		//Calculates the expected number of truth values based on the number of inputs.
		int val = 1;
		for (int i=0;i<ConstantProperties.numInputs;i++){
			val *= 2;
		}
		int expectedNumTruths = 1;
		for (int i=0;i<val;i++){
			expectedNumTruths *= 2;
		}
		System.out.println("We expect to see "+expectedNumTruths+" possible truth values");
		
		//Sorts the allowed operators into three categories: symmetric (a OR b == b OR a), asymmetric (a IMPLIES b != b IMPLIES a), 
		//and special (NOT a).
		HashSet<String> symmetricOps = new HashSet<String>  ();
		HashSet<String> asymmetricOps = new HashSet<String>  ();
		HashSet<String> specialOps = new HashSet<String>  ();
		for (String operator:allowedOps){
			if (operator.equals("&")||operator.equals("@")
					||operator.equals("+")||operator.equals("^")
					||operator.equals(".")||operator.equals("=")){
				symmetricOps.add(operator);
			}
			else if(operator.equals(">")||operator.equals("$")){
				asymmetricOps.add(operator);
			}
			else if (operator.equals("~")){
				specialOps.add(operator);
			}
		}
		
		//Make sure there are no negative costs and defaults any unspecified truth value to 1.
		//Also changes any operator with a cost of 0 that is not NOT to 1.
		ConstantProperties.setCostPerOp();
		for (String op:ConstantProperties.approvedOperators){
			if (!(ConstantProperties.costPerOp.containsKey(op))|| (ConstantProperties.costPerOp.get(op)<0) || (!op.equalsIgnoreCase("~") && ConstantProperties.costPerOp.get(op)==0)){
				ConstantProperties.costPerOp.put(op,1.0);
				System.out.println("Changing the cost of "+op+" to 1.");
			}
		}
		
		//Removes unrecognized operators
		ArrayList<String> inAllowed = new ArrayList<String> ();
		ArrayList<String> inCost = new ArrayList<String> ();
		for (String operator:ConstantProperties.costPerOp.keySet()){
			if (!ConstantProperties.approvedOperators.contains(operator)){
				inCost.add(operator);
			}
		}
		for (String operator:inCost){
			ConstantProperties.costPerOp.remove(operator);
		}
		for (String operator:allowedOps){
			if (!ConstantProperties.approvedOperators.contains(operator)){
				inAllowed.add(operator);
			}
		}
		for (String operator:inAllowed){
			allowedOps.remove(operator);
		}
		
		//Operators not used
		for (String operator:ConstantProperties.approvedOperators){
			if (!allowedOps.contains(operator) && ConstantProperties.costPerOp.containsKey(operator)){
				ConstantProperties.costPerOp.remove(operator);
			}
		}
		
		//Initialize the object that stores all the found truth values and the circuits associated with them.
		HashMap<String,ArrayList<Circuit>> foundTruthValues = new HashMap<String,ArrayList<Circuit>> ();
		//Adds the inputs, one, and zero to foundTruth values, adjusting their truth values based on the number of inputs.
		ArrayList<Circuit> temp = new ArrayList<Circuit> ();
		Circuit a = new Circuit ("","");
		Circuit b = new Circuit ("","");
		Circuit c = new Circuit ("","");
		Circuit one = new Circuit ("","");
		Circuit zero = new Circuit ("","");
		if (ConstantProperties.numInputs==2){
			one = new Circuit("1","1111");
			temp.add(one);
			foundTruthValues.put("1111",temp);
			temp = new ArrayList<Circuit> ();
			zero = new Circuit("0","0000");
			temp.add(zero);
			foundTruthValues.put("0000",temp);
			temp = new ArrayList<Circuit> ();
			a = new Circuit("a","0011");
			temp.add(a);
			foundTruthValues.put("0011",temp);
			temp = new ArrayList<Circuit> ();
			b = new Circuit("b","0101");
			temp.add(b);
			foundTruthValues.put("0101",temp);
		}
		else if (ConstantProperties.numInputs==3){
			one = new Circuit("1","11111111");
			temp.add(one);
			foundTruthValues.put("11111111",temp);
			temp = new ArrayList<Circuit> ();
			zero = new Circuit("0","00000000");
			temp.add(zero);
			foundTruthValues.put("00000000",temp);
			temp = new ArrayList<Circuit> ();
			a = new Circuit("a","00001111");
			temp.add(a);
			foundTruthValues.put("00001111",temp);
			temp = new ArrayList<Circuit> ();
			b = new Circuit("b","00110011");
			temp.add(b);
			foundTruthValues.put("00110011",temp);
			temp = new ArrayList<Circuit> ();
			c = new Circuit("c","01010101");
			temp.add(c);
			foundTruthValues.put("01010101",temp);
		}
		temp = new ArrayList<Circuit> ();
		
		//Creates a HashMap that maps cost to all the circuits with that cost and adds the inputs to cost 0.
		HashMap<Double, ArrayList<Circuit>> sortedByCost = new HashMap<Double,ArrayList<Circuit>> ();
		if (ConstantProperties.numInputs==2 || ConstantProperties.numInputs==3){
			temp.add(a);
			temp.add(b);
		}
		if (ConstantProperties.numInputs==3){
			temp.add(c);
		}
		sortedByCost.put(0.0, temp);
		
		//If there is an operation that with a cost of 0, we must check the found truth values at the end 
		//because we might build a circuit that would have the same cost as the ones we are iterating through.
		boolean has0Cost = ConstantProperties.costPerOp.containsValue(0.0);
		
		//initialize the cost of the first set of circuits.
		double alphaCost = 0;
		
		//This is a filtering mechanism to speed up the program. We do not want to save circuits 
		//that contain truth values that are in this list. Circuits with 0 and the minimum non-zero 
		//gate cost have their truth values added to the list.
		HashSet<String> notAllowed = new HashSet<String> ();
		int count = 0;
		
		while(alphaCost <= maxCost && foundTruthValues.size()<expectedNumTruths){
			//Go through each cost in sortedByCost from 0 to the max. Set the group of alpha circuits.
			ArrayList<Circuit> alphaCircuits = sortedByCost.get(alphaCost);
			//If there is no operator with a cost of 0 we can check the alpha circuits for truth values now. Otherwise, we must 
			//check at the end
			if (!has0Cost){
				//Add to the list of not allowed truth values things that have a 0 smallest non-zero cost.
				if(count<=1){
					for (Circuit circ:alphaCircuits){
						notAllowed.add(circ.getTruthValue());
					}
					count++;
				}
				//Keeps track of the truth values found this round. We want to save all circuits who give these truth values (because
				//these will have the same cost as the minimum circuit found for that truth value) or whose truth values we have not
				//yet found a circuit for.
				ArrayList<String> foundThisRound = new ArrayList<String> ();
				for (Circuit circ: alphaCircuits){
					String tv = circ.getTruthValue();
					if (!foundTruthValues.containsKey(tv)){
						ArrayList<Circuit> unique = new ArrayList<Circuit> ();
						unique.add(circ);
						foundTruthValues.put(tv, unique);
						foundThisRound.add(tv);
					}
					else if(foundTruthValues.containsKey(tv) && foundThisRound.contains(tv)){
						foundTruthValues.get(tv).add(circ);
					}
				}
				double currTime = System.currentTimeMillis();
				double timeSoFar = (currTime - startTime)/1000;
				
				//Give some status updates.
				System.out.println(timeSoFar+" seconds");
				System.out.println("Number of truth values found so far: "+foundTruthValues.size());
				System.out.println("Dealing with circuits that cost "+alphaCost);
				System.out.println(alphaCircuits.size()+" circuits in this level");
				//System.out.println(foundTruthValues);
				
				//Write the contents to a file.
				System.out.println("Saving to a file. Please do not cancel at this point.");
				String tempdir = dir;
				if (alterName){
					tempdir = tempdir.replace(Double.toString(maxCost), Double.toString(alphaCost));
				}
				JsonWriter.writeToJson(foundTruthValues, tempdir, timeSoFar, d, alphaCost, allowedOps);
				System.out.println("Done Saving.");
				
				if (truthValueToFind != null){
					if(foundTruthValues.containsKey(truthValueToFind)){
						System.out.println("\nFound "+truthValueToFind+":");
						System.out.println(foundTruthValues.get(truthValueToFind));
						System.out.println("---------------------------------------");
						return foundTruthValues;
					}
				}
				if (foundTruthValues.size() == expectedNumTruths || alphaCost == maxCost){
					System.out.println("\nFinished");
					System.out.println("---------------------------------------");
					return foundTruthValues;
				}
				System.out.println("---------------------------------------");
			}
			//In case anything has the same cost as the current level

			//Create the special circuits that only require 1 input and give the not of the input. (e.g. (~a), (a^1), (a.0))
			int indexAlpha=0;
			while (true){
				Circuit alpha = alphaCircuits.get(indexAlpha);
				for (String operator:specialOps){
					//Create the circuit
					Circuit newCircuit = new Circuit(operator,alpha);
					double newCircuitCost = newCircuit.getCost();
					//If it is an acceptable circuits
					if(newCircuit.canBeUsed() && !notAllowed.contains(newCircuit.getTruthValue()) && newCircuitCost<=maxCost){
						//if we have circuits with that cost already, add this to the list at that cost
						 if(sortedByCost.containsKey(newCircuitCost) && !sortedByCost.get(newCircuitCost).contains(newCircuit)){
							 sortedByCost.get(newCircuitCost).add(newCircuit);
						 }
						 //If we have not seen this cost before, make a new list with this circuit.
						 else if(!sortedByCost.containsKey(newCircuitCost)){
							 ArrayList<Circuit> tempArray = new ArrayList<Circuit>();
							 tempArray.add(newCircuit);
							 sortedByCost.put(newCircuitCost,tempArray);
						 }
					}
				}
				
			
				for (String operator:symmetricOps){
					if (operator.equals(".") || operator.equals("=")){
						Circuit newCircuit = new Circuit(alpha,operator,zero);
						double newCircuitCost = newCircuit.getCost();
						
						if(newCircuit.canBeUsed() && !notAllowed.contains(newCircuit.getTruthValue()) && newCircuitCost<=maxCost){
							 if(sortedByCost.containsKey(newCircuitCost) && !sortedByCost.get(newCircuitCost).contains(newCircuit)){
								 sortedByCost.get(newCircuitCost).add(newCircuit);
							 }
							 else if(!sortedByCost.containsKey(newCircuitCost)){
								 ArrayList<Circuit> tempArray = new ArrayList<Circuit>();
								 tempArray.add(newCircuit);
								 sortedByCost.put(newCircuitCost,tempArray);
							 }
						}
					}
					else if (operator.equals("@")){
						Circuit newCircuit = new Circuit(alpha,operator,alpha);
						double newCircuitCost = newCircuit.getCost();
						if(newCircuit.canBeUsed() && !notAllowed.contains(newCircuit.getTruthValue()) && newCircuitCost<=maxCost){
							 if(sortedByCost.containsKey(newCircuitCost) && !sortedByCost.get(newCircuitCost).contains(newCircuit)){
								 sortedByCost.get(newCircuitCost).add(newCircuit);
							 }
							 else if(!sortedByCost.containsKey(newCircuitCost)){
								 ArrayList<Circuit> tempArray = new ArrayList<Circuit>();
								 tempArray.add(newCircuit);
								 sortedByCost.put(newCircuitCost,tempArray);
							 }
						}
					}
					else if (operator.equals("^")){
						Circuit newCircuit = new Circuit(alpha,operator,one);
						double newCircuitCost = newCircuit.getCost();
						
						if(newCircuit.canBeUsed() && !notAllowed.contains(newCircuit.getTruthValue()) && newCircuitCost<=maxCost){
							 if(sortedByCost.containsKey(newCircuitCost) && !sortedByCost.get(newCircuitCost).contains(newCircuit)){
								 sortedByCost.get(newCircuitCost).add(newCircuit);
							 }
							 else if(!sortedByCost.containsKey(newCircuitCost)){
								 ArrayList<Circuit> tempArray = new ArrayList<Circuit>();
								 tempArray.add(newCircuit);
								 sortedByCost.put(newCircuitCost,tempArray);
							 }
						}
					}
					
				}
			
				for (String operator:asymmetricOps){
					if (operator.equals(">")){
						Circuit newCircuit = new Circuit(alpha,operator,zero);
						double newCircuitCost = newCircuit.getCost();
						
						if(newCircuit.canBeUsed() && !notAllowed.contains(newCircuit.getTruthValue()) && newCircuitCost<=maxCost){
							 if(sortedByCost.containsKey(newCircuitCost) && !sortedByCost.get(newCircuitCost).contains(newCircuit)){
								 sortedByCost.get(newCircuitCost).add(newCircuit);
							 }
							 else if(!sortedByCost.containsKey(newCircuitCost)){
								 ArrayList<Circuit> tempArray = new ArrayList<Circuit>();
								 tempArray.add(newCircuit);
								 sortedByCost.put(newCircuitCost,tempArray);
							 }
						}
					}
					else if (operator.equals("$")){
						Circuit newCircuit = new Circuit(one,operator,alpha);
						double newCircuitCost = newCircuit.getCost();
						
						if(newCircuit.canBeUsed() && !notAllowed.contains(newCircuit.getTruthValue()) && newCircuitCost<=maxCost){
							 if(sortedByCost.containsKey(newCircuitCost) && !sortedByCost.get(newCircuitCost).contains(newCircuit)){
								 sortedByCost.get(newCircuitCost).add(newCircuit);
							 }
							 else if(!sortedByCost.containsKey(newCircuitCost)){
								 ArrayList<Circuit> tempArray = new ArrayList<Circuit>();
								 tempArray.add(newCircuit);
								 sortedByCost.put(newCircuitCost,tempArray);
							 }
						}
					}
				}
				//Perform these operations for each circuit in alpha. Note we iterate through alphaCircuits as we potentially change it,
				//but since we only get not of alpha and circuits are designed to reject itself if it contains an equal subcircuit,
				//we will not get stuck in an infinite loop like (~(~a)) inverted again.
				indexAlpha++;
				if (indexAlpha==alphaCircuits.size()){
					break;
				}
			}
			
			//Now we want to use the operators that involve another circuit to create more circuits and put them in their
			//appropriate place in sortedByCost.
			indexAlpha=0;
			while (true){
				//Go through each circuit in a given cost
				Circuit alpha = alphaCircuits.get(indexAlpha);
				//Compare it to all circuits with costs that are less than or equal to it
				double betaCost = 0;
				while(betaCost<=alphaCost){
					ArrayList<Circuit> betaCircuits = sortedByCost.get(betaCost);
					//if betaCost is still less than alphaCost, we want to combine alpha cost with everything in the circuits 
					//with betaCost using the allowable operations
					if (betaCost != alphaCost){
						for (Circuit beta : betaCircuits){
							for (String operator:symmetricOps){
								Circuit newCircuit = new Circuit(alpha,operator,beta);
								double newCircuitCost = newCircuit.getCost();
								if(newCircuit.canBeUsed() && !notAllowed.contains(newCircuit.getTruthValue()) && newCircuitCost<=maxCost){
									 if(sortedByCost.containsKey(newCircuitCost) && !sortedByCost.get(newCircuitCost).contains(newCircuit)){
										 sortedByCost.get(newCircuitCost).add(newCircuit);
									 }
									 else if(!sortedByCost.containsKey(newCircuitCost)){
										 ArrayList<Circuit> tempArray = new ArrayList<Circuit>();
										 tempArray.add(newCircuit);
										 sortedByCost.put(newCircuitCost,tempArray);
									 }
								}
							}
							//Covers the asymmetric operators. (e.g. does both a>b and b>a)
							for (String operator:asymmetricOps){
								Circuit newCircuit1 = new Circuit(alpha,operator,beta);
								Circuit newCircuit2 = new Circuit(beta,operator,alpha);
								double newCircuitCost1 = newCircuit1.getCost();
								double newCircuitCost2 = newCircuit2.getCost();
								if(newCircuit1.canBeUsed() && !notAllowed.contains(newCircuit1.getTruthValue()) && newCircuitCost1<=maxCost){
									 if(sortedByCost.containsKey(newCircuitCost1) && !sortedByCost.get(newCircuitCost1).contains(newCircuit1)){
										 sortedByCost.get(newCircuitCost1).add(newCircuit1);
									 }
									 else if(!sortedByCost.containsKey(newCircuitCost1)){
										 ArrayList<Circuit> tempArray = new ArrayList<Circuit>();
										 tempArray.add(newCircuit1);
										 sortedByCost.put(newCircuitCost1,tempArray);
									 }
								}
								if(newCircuit2.canBeUsed() && !notAllowed.contains(newCircuit2.getTruthValue()) && newCircuitCost2<=maxCost){
									 if(sortedByCost.containsKey(newCircuitCost2) && !sortedByCost.get(newCircuitCost2).contains(newCircuit2)){
										 sortedByCost.get(newCircuitCost2).add(newCircuit2);
									 }
									 else if(!sortedByCost.containsKey(newCircuitCost2)){
										 ArrayList<Circuit> tempArray = new ArrayList<Circuit>();
										 tempArray.add(newCircuit2);
										 sortedByCost.put(newCircuitCost2,tempArray);
									 }
								}
							}
						}
					}
					//if betaCost == alphaCost then we are combining alpha circuits with other alpha circuits from left to right. We 
					// want to stop when we reach the alpha circuit.
					else if (betaCost == alphaCost){
						int indexBeta = 0;
						while (true){
							Circuit beta = betaCircuits.get(indexBeta);
							if(beta.equals(alpha)){
								break;
							}
							for (String operator:symmetricOps){
								//Note beta and alpha are switched here to make (a&b) appear rather than (b&a)
								Circuit newCircuit = new Circuit(beta,operator,alpha);
								double newCircuitCost = newCircuit.getCost();
								if(newCircuit.canBeUsed() && !notAllowed.contains(newCircuit.getTruthValue()) && newCircuitCost<=maxCost){
									 if(sortedByCost.containsKey(newCircuitCost) && !sortedByCost.get(newCircuitCost).contains(newCircuit)){
										 sortedByCost.get(newCircuitCost).add(newCircuit);
									 }
									 else if(!sortedByCost.containsKey(newCircuitCost)){
										 ArrayList<Circuit> tempArray = new ArrayList<Circuit>();
										 tempArray.add(newCircuit);
										 sortedByCost.put(newCircuitCost,tempArray);
									 }
								}
							}
							for (String operator:asymmetricOps){
								Circuit newCircuit1 = new Circuit(alpha,operator,beta);
								Circuit newCircuit2 = new Circuit(beta,operator,alpha);
								double newCircuitCost1 = newCircuit1.getCost();
								double newCircuitCost2 = newCircuit2.getCost();
								if(newCircuit1.canBeUsed() && !notAllowed.contains(newCircuit1.getTruthValue()) && newCircuitCost1<=maxCost){
									 if(sortedByCost.containsKey(newCircuitCost1) && !sortedByCost.get(newCircuitCost1).contains(newCircuit1)){
										 sortedByCost.get(newCircuitCost1).add(newCircuit1);
									 }
									 else if(!sortedByCost.containsKey(newCircuitCost1)){
										 ArrayList<Circuit> tempArray = new ArrayList<Circuit>();
										 tempArray.add(newCircuit1);
										 sortedByCost.put(newCircuitCost1,tempArray);
									 }
								}
								if(newCircuit2.canBeUsed() && !notAllowed.contains(newCircuit2.getTruthValue()) && newCircuitCost2<=maxCost){
									 if(sortedByCost.containsKey(newCircuitCost2) && !sortedByCost.get(newCircuitCost2).contains(newCircuit2)){
										 sortedByCost.get(newCircuitCost2).add(newCircuit2);
									 }
									 else if(!sortedByCost.containsKey(newCircuitCost2)){
										 ArrayList<Circuit> tempArray = new ArrayList<Circuit>();
										 tempArray.add(newCircuit2);
										 sortedByCost.put(newCircuitCost2,tempArray);
									 }
								}
							}
							indexBeta++;
							if (indexBeta==betaCircuits.size()){
								break;
							}
						}
					}
					//Finds the next cost of the beta circuits
					ArrayList<Double> allCosts2 =new ArrayList<Double> (sortedByCost.keySet());
					Collections.sort(allCosts2);
					if (betaCost != alphaCost){
						betaCost = allCosts2.get(allCosts2.indexOf(betaCost)+1);
					}
					else if (betaCost == alphaCost){
						break;
					}
				}
				indexAlpha++;
				if (indexAlpha==alphaCircuits.size()){
					break;
				}
			}
			if (has0Cost){
				//Add to the list of not allowed truth values things that have a 0 smallest non-zero cost.
				if(count<=1){
					for (Circuit circ:alphaCircuits){
						notAllowed.add(circ.getTruthValue());
					}
					count++;
				}
				//Keeps track of the truth values found this round. We want to save all circuits who give these truth values (because
				//these will have the same cost as the minimum circuit found for that truth value) or whose truth values we have not
				//yet found a circuit for.
				ArrayList<String> foundThisRound = new ArrayList<String> ();
				for (Circuit circ: alphaCircuits){
					String tv = circ.getTruthValue();
					if (!foundTruthValues.containsKey(tv)){
						ArrayList<Circuit> unique = new ArrayList<Circuit> ();
						unique.add(circ);
						foundTruthValues.put(tv, unique);
						foundThisRound.add(tv);
					}
					else if(foundTruthValues.containsKey(tv) && foundThisRound.contains(tv)){
						foundTruthValues.get(tv).add(circ);
					}
				}
				double currTime = System.currentTimeMillis();
				double timeSoFar = (currTime - startTime)/1000;
				//Give some status updates.
				System.out.println(timeSoFar+" seconds");
				System.out.println("Number of truth values found so far: "+foundTruthValues.size());
				System.out.println("Dealing with circuits that cost "+alphaCost);
				System.out.println(alphaCircuits.size()+" circuits in this level");
				//System.out.println(foundTruthValues);
				System.out.println("Saving to a file. Please do not cancel at this point.");
				String tempdir = dir;
				if (alterName){
					tempdir = tempdir.replace(Double.toString(maxCost), Double.toString(alphaCost));
				}
				JsonWriter.writeToJson(foundTruthValues, tempdir, timeSoFar, d, alphaCost, allowedOps);
				System.out.println("Done Saving.");
				
				if (truthValueToFind != null){
					if(foundTruthValues.containsKey(truthValueToFind)){
						System.out.println("\nFound "+truthValueToFind+":");
						System.out.println(foundTruthValues.get(truthValueToFind));
						System.out.println("---------------------------------------");
						return foundTruthValues;
					}
				}
				if (foundTruthValues.size() == expectedNumTruths || alphaCost == maxCost){
					System.out.println("\nFinished");
					System.out.println("---------------------------------------");
					return foundTruthValues;
				}
				System.out.println("---------------------------------------");
			}
			//Gets the next cost for the alpha circuits.
			ArrayList<Double> allCosts =new ArrayList<Double> (sortedByCost.keySet());
			Collections.sort(allCosts);
			alphaCost = allCosts.get(allCosts.indexOf(alphaCost)+1);
		}
		
		System.out.println("Finished");
		return foundTruthValues;
	}




}