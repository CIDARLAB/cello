package org.cellocad.MIT.misc;

/**
 * Created by Bryan Der on 6/16/15.
 */

import java.util.ArrayList;

public class LogicMotifEnum {

    public ArrayList<String> level1 = new ArrayList<>();
    public ArrayList<String> level2 = new ArrayList<>();
    public ArrayList<String> level3 = new ArrayList<>();
    public ArrayList<String> level4 = new ArrayList<>();
    public ArrayList<String> level5 = new ArrayList<>();
    public ArrayList<String> level6 = new ArrayList<>();

    public static void main(String[] args) {

        LogicMotifEnum lme = new LogicMotifEnum();

        ArrayList<String> prev = new ArrayList<String>();

        lme.initializeLevel1();
        System.out.println("Level 1 size: " + lme.level1.size());

        lme.makeNextLevel(lme.level2, lme.level1, prev);
        System.out.println("Level 2 size: " + lme.level2.size());

        prev.addAll(lme.level1);
        lme.makeNextLevel(lme.level3, lme.level2, prev);
        System.out.println("Level 3 size: " + lme.level3.size());

        prev.addAll(lme.level2);
        lme.makeNextLevel(lme.level4, lme.level3, prev);
        System.out.println("Level 4 size: " + lme.level4.size());

        prev.addAll(lme.level3);
        lme.makeNextLevel(lme.level5, lme.level4, prev);
        System.out.println("Level 5 size: " + lme.level5.size());


    }

    public void initializeLevel1() {
        level1.add("a");
        level1.add("b");
        level1.add("c");
        level1.add("0");
    }

    private void makeNextLevel(ArrayList<String> next, ArrayList<String> curr, ArrayList<String> prev) {

        for(int i=0; i<curr.size(); i++) {
            for(int j=0; j<prev.size(); ++j) {
                String x = "(" + curr.get(i) + "." + prev.get(j) + ")";
                next.add(x);
            }
        }

        for(int i=0; i<curr.size()-1; ++i) {
            for(int j=i+1; j<curr.size(); ++j) {
                String x = "(" + curr.get(i) + "." + curr.get(j) + ")";
                next.add(x);
            }
        }
    }


}

















