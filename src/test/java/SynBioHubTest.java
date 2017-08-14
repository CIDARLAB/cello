import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Set;

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
	public void test() {
		UCFAdaptor ucfAdaptor = new UCFAdaptor();
		UCFReader ucfReader = new UCFReader();
		UCF ucf = ucfReader.readAllCollections("resources/UCF/Eco1C1G1T0.UCF.json");
		PartLibrary partLib = ucfAdaptor.createPartLibrary(ucf);
		HashMap<String,Part> ucfParts = partLib.get_ALL_PARTS();
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
}
