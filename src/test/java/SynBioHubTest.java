import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cellocad.MIT.dnacompiler.Args;
import org.cellocad.MIT.dnacompiler.Gate;
import org.cellocad.MIT.dnacompiler.GateLibrary;
import org.cellocad.MIT.dnacompiler.Part;
import org.cellocad.MIT.dnacompiler.PartLibrary;
import org.cellocad.MIT.dnacompiler.UCF;
import org.cellocad.adaptors.synbiohubadaptor.SynBioHubAdaptor;
import org.cellocad.adaptors.ucfadaptor.UCFAdaptor;
import org.cellocad.adaptors.ucfadaptor.UCFReader;
import org.junit.Test;

/**
 * Compare the output (part names) of the UCF-generated parts library
 * to the output of the SynBioHub-generated parts library.
 *
 * @author: Tim Jones
 */
public class SynBioHubTest {

    @Test
    public void testPartLibrary() throws Exception {
        UCFAdaptor ucfAdaptor = new UCFAdaptor();
        UCFReader ucfReader = new UCFReader();
        UCF ucf = ucfReader.readAllCollections("resources/UCF/Eco1C1G1T0.UCF.json");
        PartLibrary partLibrary = ucfAdaptor.createPartLibrary(ucf);
        HashMap<String,Part> ucfParts = partLibrary.get_ALL_PARTS();
        Set<String> ucfPartNames = ucfParts.keySet();

        SynBioHubAdaptor sbhAdaptor = new SynBioHubAdaptor();
        HashMap<String,Part> sbhParts = sbhAdaptor.getPartLibrary().get_ALL_PARTS();
        Set<String> sbhPartNames = sbhParts.keySet();

        for (String name : ucfPartNames) {
            assertTrue(name + " in the UCF is missing from SynBioHub", sbhPartNames.contains(name));
        }
        for (String name : sbhPartNames) {
            assertTrue(name + " in SynBioHub is missing from the UCF", ucfPartNames.contains(name));
        }
    }

    @Test
    public void testGateLibrary() throws Exception {
        UCFAdaptor ucfAdaptor = new UCFAdaptor();
        UCFReader ucfReader = new UCFReader();
        UCF ucf = ucfReader.readAllCollections("resources/UCF/Eco1C1G1T0.UCF.json");
        GateLibrary ucfGateLibrary = ucfAdaptor.createGateLibrary(ucf,2,1,new Args());

        SynBioHubAdaptor sbhAdaptor = new SynBioHubAdaptor();
        GateLibrary sbhGateLibrary = sbhAdaptor.getGateLibrary();

        Set<String> ucfGateNames = ucfGateLibrary.get_GATES_BY_NAME().keySet();
        Set<String> sbhGateNames = sbhGateLibrary.get_GATES_BY_NAME().keySet();
        assertTrue(ucfGateNames.equals(sbhGateNames));
    }

	@Test
	public void testSetGateParts() throws Exception {
		UCFAdaptor ucfAdaptor = new UCFAdaptor();
		UCFReader ucfReader = new UCFReader();
		UCF ucf = ucfReader.readAllCollections("resources/UCF/Eco1C1G1T0.UCF.json");
		GateLibrary ucfGateLibrary = ucfAdaptor.createGateLibrary(ucf,2,1,new Args());
		PartLibrary ucfPartLibrary = ucfAdaptor.createPartLibrary(ucf);
		ucfAdaptor.setGateParts(ucf,ucfGateLibrary,ucfPartLibrary);
		Map<String,Gate> ucfGatesMap = ucfGateLibrary.get_GATES_BY_NAME();
		Map<String,String> ucfPromoterMap = new HashMap<>();
		for (String gateName : ucfGatesMap.keySet()) {
			ucfPromoterMap.put(gateName,ucfGatesMap.get(gateName).get_regulable_promoter().get_name());
		}

		SynBioHubAdaptor sbhAdaptor = new SynBioHubAdaptor();
		PartLibrary sbhPartLibrary = sbhAdaptor.getPartLibrary();
		GateLibrary sbhGateLibrary = sbhAdaptor.getGateLibrary();
		sbhAdaptor.setGateParts(sbhGateLibrary, sbhPartLibrary);
		Map<String,Gate> sbhGatesMap = sbhGateLibrary.get_GATES_BY_NAME();
		Map<String,String> sbhPromoterMap = new HashMap<>();
		for (String gateName : sbhGatesMap.keySet()) {
			sbhPromoterMap.put(gateName,sbhGatesMap.get(gateName).get_regulable_promoter().get_name());
		}
		assertTrue(sbhPromoterMap.equals(ucfPromoterMap));
	}

	@Test
	public void testSetResponseFunctions() throws Exception {
		UCFAdaptor ucfAdaptor = new UCFAdaptor();
		UCFReader ucfReader = new UCFReader();
		UCF ucf = ucfReader.readAllCollections("resources/UCF/Eco1C1G1T0.UCF.json");
		GateLibrary ucfGateLibrary = ucfAdaptor.createGateLibrary(ucf,2,1,new Args());
		PartLibrary ucfPartLibrary = ucfAdaptor.createPartLibrary(ucf);
		ucfAdaptor.setResponseFunctions(ucf,ucfGateLibrary);
		Map<String,Gate> ucfGatesMap = ucfGateLibrary.get_GATES_BY_NAME();
		Set<Map> ucfParams = new HashSet<>();
		Set<List> ucfVariableNames = new HashSet<>();
		Set<Map> ucfVariableThresholds = new HashSet<>();
		Set<String> ucfEquations = new HashSet<>();
		for (String gateName : ucfGatesMap.keySet()) {
			ucfParams.add(ucfGatesMap.get(gateName).get_params());
			ucfVariableNames.add(ucfGatesMap.get(gateName).get_variable_names());
			ucfVariableThresholds.add(ucfGatesMap.get(gateName).get_variable_thresholds());
			ucfEquations.add(ucfGatesMap.get(gateName).get_equation());
		}

		SynBioHubAdaptor sbhAdaptor = new SynBioHubAdaptor();
		PartLibrary sbhPartLibrary = sbhAdaptor.getPartLibrary();
		GateLibrary sbhGateLibrary = sbhAdaptor.getGateLibrary();
		sbhAdaptor.setResponseFunctions(sbhGateLibrary);
		Map<String,Gate> sbhGatesMap = sbhGateLibrary.get_GATES_BY_NAME();
		Set<Map> sbhParams = new HashSet<>();
		Set<List> sbhVariableNames = new HashSet<>();
		Set<String> sbhEquations = new HashSet<>();
		for (String gateName : sbhGatesMap.keySet()) {
			sbhParams.add(sbhGatesMap.get(gateName).get_params());
			sbhVariableNames.add(sbhGatesMap.get(gateName).get_variable_names());

			Double a = sbhGatesMap.get(gateName).get_variable_thresholds().get("x")[0];
			Double b = ucfGatesMap.get(gateName).get_variable_thresholds().get("x")[0];
			assert(Math.abs(a-b)<1e-6);
			a = sbhGatesMap.get(gateName).get_variable_thresholds().get("x")[1];
			b = ucfGatesMap.get(gateName).get_variable_thresholds().get("x")[1];
			assert(Math.abs(a-b)<1e-6);

			sbhEquations.add(sbhGatesMap.get(gateName).get_equation());
		}
		assertTrue(sbhParams.equals(ucfParams));
		assertTrue(sbhVariableNames.equals(ucfVariableNames));
		assertTrue(sbhEquations.equals(ucfEquations));
	}

    // @Test
    // public void testCytometry() throws Exception {
    //     UCFAdaptor ucfAdaptor = new UCFAdaptor();
    //     UCFReader ucfReader = new UCFReader();
    //     UCF ucf = ucfReader.readAllCollections("resources/UCF/Eco1C1G1T0.UCF.json");
    //     GateLibrary ucfGateLibrary = ucfAdaptor.createGateLibrary(ucf,2,1,new Args());
	// 	ucfAdaptor.setGateCytometry(ucf, ucfGateLibrary, new Args());
		
    //     SynBioHubAdaptor sbhAdaptor = new SynBioHubAdaptor();
    //     GateLibrary sbhGateLibrary = sbhAdaptor.getGateLibrary();

	// 	Map<String,Gate> sbhGatesMap = sbhGateLibrary.get_GATES_BY_NAME();
	// 	Map<String,Gate> ucfGatesMap = ucfGateLibrary.get_GATES_BY_NAME();
    // }

}
