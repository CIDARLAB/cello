import static org.junit.Assert.assertTrue;

import java.util.HashMap;
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

        Part p = sbhParts.get(sbhPartNames.iterator().next());
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
}
