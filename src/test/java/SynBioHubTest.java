

import static org.junit.Assert.*;
import org.cellocad.adaptors.synbiohubadaptor.*;
import org.cellocad.adaptors.ucfadaptor.*;
import org.cellocad.MIT.dnacompiler.*;
import java.util.HashMap;
import java.util.Set;

import org.junit.Test;

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
	HashMap<String,Part> sbhParts = sbhAdaptor.createPartLibrary().get_ALL_PARTS();
	Set<String> sbhPartNames = sbhParts.keySet();

	for (String name : ucfPartNames) {
	    assertTrue(name + " in the UCF is missing from SynBioHub", sbhPartNames.contains(name));
	}
	for (String name : sbhPartNames) {
	    assertTrue(name + " in SynBioHub is missing from the UCF", ucfPartNames.contains(name));
	}
    }
}
