import java.util.ArrayList;
import org.cellocad.MIT.dnacompiler.Permute;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

public class PermuteTest {

    @Test
    public void testGetLogicPermutationTypeCheck() {
        int[] arr = {0, 0};
        ArrayList<int[]> inputLogicsSet = new ArrayList<int[]>(Arrays.asList(arr));
        int n[] = {0, 0};
        int nR[] = {0, 0};
        int idx = 0;
        Permute.getLogicPermutation(inputLogicsSet, n, nR, idx);
        int[] actual = inputLogicsSet.get(0);
        int[] expected = {0, 0};
        assertArrayEquals(expected, actual);
    }
}

