import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import java.util.Arrays;
import org.cellocad.MIT.dnacompiler.LogicCircuit;
import org.cellocad.MIT.dnacompiler.Gate;
import java.util.ArrayList;
import org.cellocad.MIT.dnacompiler.*;
import org.cellocad.adaptors.ucfadaptor.*;

public class Rule30Test {

    @Test
    public void testRule30() {
        LogicCircuit abstractLc = new LogicCircuit();
        ArrayList<Gate> gates = new ArrayList<>();
        Args options = new Args();
        
        UCFReader ucfReader = new UCFReader();
        options.set_UCFfilepath(options.get_home() + "/resources/UCF/Eco1C1G1T1.UCF.json");
        UCF ucf = ucfReader.readAllCollections(options.get_UCFfilepath());
        UCFAdaptor ucfAdaptor = new UCFAdaptor();

        PartLibrary partLibrary = ucfAdaptor.createPartLibrary(ucf);
        GateLibrary gateLibrary = ucfAdaptor.createGateLibrary(ucf, 3, 1, options);
		String inputPromotersPath = options.get_home() + "/resources/test/rule30_inputs.txt";
		String outputGenesPath = options.get_home() + "/resources/test/rule30_outputs.txt";
		InputOutputGateReader.readInputsFromFile(inputPromotersPath, gateLibrary);
        InputOutputGateReader.readOutputsFromFile(outputGenesPath, gateLibrary);
		ucfAdaptor.setGateParts(ucf, gateLibrary, partLibrary);

        ucfAdaptor.setResponseFunctions(ucf, gateLibrary);
        ucfAdaptor.setGateToxicity(ucf, gateLibrary, options);
        ucfAdaptor.setGateCytometry(ucf, gateLibrary, options);

		Gate out = new Gate();
		out.name = "out";
        out.index = 0;
        out.type = Gate.GateType.OUTPUT_OR;
        out.set_logics(new ArrayList<Integer>(Arrays.asList(0,1,1,1,1,0,0,0)));

		// PhlF
        Gate g1 = new Gate();
		g1.name = "g1";
        g1.index = 1;
        g1.type = Gate.GateType.NOR;
		g1.set_distance_to_input(1);
        g1.set_logics(new ArrayList<Integer>(Arrays.asList(0,0,0,0,1,0,0,0)));

		// AmtR
        Gate g2 = new Gate();
		g2.name = "g2";
        g2.index = 2;
        g2.type = Gate.GateType.NOR;
		g2.set_distance_to_input(2);
        g2.set_logics(new ArrayList<Integer>(Arrays.asList(0,1,1,1,0,0,0,0)));

		// BetI
        Gate g3 = new Gate();
		g3.name = "g3";
        g3.index = 3;
        g3.type = Gate.GateType.NOT;
		g3.set_distance_to_input(2);
        g3.set_logics(new ArrayList<Integer>(Arrays.asList(0,1,1,1,0,1,1,1)));

		// HlyIIR
        Gate g4 = new Gate();
		g4.name = "g4";
        g4.index = 4;
        g4.type = Gate.GateType.NOR;
		g4.set_distance_to_input(1);
        g4.set_logics(new ArrayList<Integer>(Arrays.asList(1,0,0,0,1,0,0,0)));

		// SrpR
        Gate g5 = new Gate();
		g5.name = "g5";
        g5.index = 5;
        g5.type = Gate.GateType.NOT;
		g5.set_distance_to_input(1);
        g5.set_logics(new ArrayList<Integer>(Arrays.asList(1,1,1,1,0,0,0,0)));

		// pBAD
        Gate in1 = new Gate();
        in1.name = "in1";
        in1.index = 6;
        in1.type = Gate.GateType.INPUT;
		in1.set_distance_to_input(0);
        in1.set_logics(new ArrayList<Integer>(Arrays.asList(0,0,0,0,1,1,1,1)));

		// pTac
        Gate in2 = new Gate();
        in2.name = "in2";
        in2.index = 7;
        in2.type = Gate.GateType.INPUT;
		in2.set_distance_to_input(0);
        in2.set_logics(new ArrayList<Integer>(Arrays.asList(0,0,1,1,0,0,1,1)));

		// pTet
        Gate in3 = new Gate();
        in3.name = "in3";
        in3.index = 8;
        in3.type = Gate.GateType.INPUT;
		in3.set_distance_to_input(0);
        in3.set_logics(new ArrayList<Integer>(Arrays.asList(0,1,0,1,0,1,0,1)));

        abstractLc.set_logic_gates(new ArrayList<>(Arrays.asList(g1,g2,g3,g4,g5)));
        abstractLc.set_input_gates(new ArrayList<>(Arrays.asList(in1,in2,in3)));
        abstractLc.set_output_gates(new ArrayList<>(Arrays.asList(out)));
        abstractLc.set_Gates(new ArrayList<>(Arrays.asList(out,g1,g2,g3,g4,g5,in1,in2,in3)));
		
        out.outgoing = new Wire();
		out.outgoing.index = 1;
        out.outgoing.from = out;
        out.outgoing.from_index = out.index;
        out.outgoing.to = g2;
        out.outgoing.to_index = g2.index;
        out.outgoing.next = new Wire();
		out.outgoing.next.index = 2;
        out.outgoing.next.from = out;
        out.outgoing.next.from_index = out.index;
        out.outgoing.next.to = g1;
        out.outgoing.next.to_index = g1.index;
        
        g1.outgoing = new Wire();
		g1.outgoing.index = 3;
        g1.outgoing.from = g1;
        g1.outgoing.from_index = g1.index;
        g1.outgoing.to = g5;
        g1.outgoing.to_index = g5.index;
        g1.outgoing.next = new Wire();
		g1.outgoing.next.index = 4;
        g1.outgoing.next.from = g1;
        g1.outgoing.next.from_index = g1.index;
        g1.outgoing.next.to = g3;
        g1.outgoing.next.to_index = g3.index;

        g2.outgoing = new Wire();
		g2.outgoing.index = 5;
        g2.outgoing.from = g2;
        g2.outgoing.from_index = g2.index;
        g2.outgoing.to = in1;
        g2.outgoing.to_index = in1.index;
        g2.outgoing.next = new Wire();
		g2.outgoing.next.index = 6;
        g2.outgoing.next.from = g2;
        g2.outgoing.next.from_index = g2.index;
        g2.outgoing.next.to = g4;
        g2.outgoing.next.to_index = g4.index;

        g3.outgoing = new Wire();
		g3.outgoing.index = 7;
        g3.outgoing.from = g3;
        g3.outgoing.from_index = g3.index;
        g3.outgoing.to = g4;
        g3.outgoing.to_index = g4.index;

        g4.outgoing = new Wire();
		g4.outgoing.index = 8;
        g4.outgoing.from = g4;
        g4.outgoing.from_index = g4.index;
        g4.outgoing.to = in2;
        g4.outgoing.to_index = in2.index;
        g4.outgoing.next = new Wire();
		g4.outgoing.next.index = 9;
        g4.outgoing.next.from = g4;
        g4.outgoing.next.from_index = g4.index;
        g4.outgoing.next.to = in3;
        g4.outgoing.next.to_index = in3.index;

        g5.outgoing = new Wire();
		g5.outgoing.index = 10;
        g5.outgoing.from = g5;
        g5.outgoing.from_index = g5.index;
        g5.outgoing.to = in1;
        g5.outgoing.to_index = in1.index;

		abstractLc.set_Wires(new ArrayList<>(Arrays.asList(out.outgoing,out.outgoing.next,g1.outgoing,g1.outgoing.next,g2.outgoing,g2.outgoing.next,g3.outgoing,g4.outgoing,g4.outgoing.next,g5.outgoing)));
		
		LogicCircuit lc = LogicCircuitUtil.getInputAssignments(abstractLc, gateLibrary, options.is_permute_inputs()).get(0);
		LogicCircuitUtil.setInputOutputGroups(lc);
		LogicCircuitUtil.sortGatesByStage(lc);
		for(Gate g: lc.get_output_gates()) {
            Evaluate.refreshGateAttributes(g, gateLibrary);
        }
		lc.get_logic_gates().get(0).name = "S2_SrpR";
		lc.get_logic_gates().get(1).name = "H1_HlyIIR";
		lc.get_logic_gates().get(2).name = "E1_BetI";
		lc.get_logic_gates().get(3).name = "A1_AmtR";
		lc.get_logic_gates().get(4).name = "P3_PhlF";
		for (Gate g : lc.get_logic_gates()) {
			Evaluate.refreshGateAttributes(g, gateLibrary);
			g.set_unvisited(true);
			Evaluate.simulateRPU(g, gateLibrary, options);
			Evaluate.evaluateGate(g, options);
			if(options.is_toxicity()) {
				g.set_toxtable(gateLibrary.get_GATES_BY_NAME().get(g.name).get_toxtable());
				Toxicity.evaluateGateToxicity(g);
			}
		}
		LogicCircuitUtil.sortGatesByStage(lc);
		Evaluate.evaluateCircuit(lc, gateLibrary, options);
		Toxicity.evaluateCircuitToxicity(lc, gateLibrary);
		LogicCircuitUtil.sortGatesByIndex(lc);

		System.out.println(lc.toString());
    }
}

