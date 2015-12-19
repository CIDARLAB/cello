package org.cellocad.MIT.dnacompiler;

import java.util.ArrayList;

/**
 * Created by Bryan Der on 9/15/14.
 */
public class Permute {

    /**
     * get permuations of index of gate, for example, for 3 logic gates, it will
     * save indexes_set as:
     * [0, 1, 2], [0, 2, 1], [1, 0, 2], [1, 2, 0], [2, 0, 1], [2, 1, 0];
     *
     * Nr is the larger number
     * For example, for 6-choose-4, Nr = 6 and n = 4.
     *
     *
     */
    public static void getIndexProduct(ArrayList<int[]> indexes_set, int[] n, int[] Nr, int idx){
        if (idx == n.length){
            int[] indexes = new int[n.length];
            System.arraycopy(n, 0, indexes, 0, n.length);
            //Print.message(1, java.util.Arrays.toString(indexes));
            indexes_set.add(indexes);
            return;
        }
        for (int i = 0; i<=Nr[idx]; ++i){
            boolean occured_before = false;
            for (int j=0; j<idx; ++j){
                if (n[j] == i){
                    occured_before = true;
                    break;
                }
            }

            if (!occured_before){
                n[idx] = i;
                getIndexProduct(indexes_set, n, Nr, idx+1);
            }
        }
    }


    /**
     * for a two-input circuit, save input_logics_set as
     * [0, 0], [0, 1], [1, 0], [1, 1];
     *
     */
    public static void getLogicPermutation(ArrayList<int[]> input_logics_set, int[] n, int[] Nr, int idx) {
        if (idx == n.length){
            int[] input_logics = new int[n.length];
            System.arraycopy(n, 0, input_logics, 0, n.length);
            input_logics_set.add(input_logics);
            return;
        }

        for(int i=0; i<=Nr[idx]; ++i){
            n[idx] = i;
            getLogicPermutation(input_logics_set, n, Nr, idx+1);
        }
    }


    public static ArrayList<ArrayList<String>> getVariableNamePermutation(ArrayList<String> variables) {

        ArrayList<ArrayList<String>> name_orders = new ArrayList<>();

        ArrayList<int[]> indexes_set = new ArrayList<int[]>();
        int[] n = new int[variables.size()];
        int[] Nr = new int[variables.size()];
        for (int i = 0; i<variables.size(); ++i){
            Nr[i] = variables.size()-1;
        }
        Permute.getIndexProduct(indexes_set, n, Nr, 0);


        for(int[] index_set: indexes_set) {

            ArrayList<String> name_order = new ArrayList<>();
            for(int i=0; i<index_set.length; ++i) {
                name_order.add(variables.get(index_set[i]));
            }
            name_orders.add(name_order);
        }

        return name_orders;
    }
}
