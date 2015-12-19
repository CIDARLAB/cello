package org.cellocad.MIT.misc;


public class TemperatureLoop {

    public static void main(String[] args) {

        Double MAXTEMP = 100.0;
        Double MINTEMP = 0.001;

        Integer STEPS = 100000;

        Double LOGMAX = Math.log(MAXTEMP);
        Double LOGMIN = Math.log(MINTEMP);

        Double LOGINC = (LOGMAX-LOGMIN)/STEPS;

        String output = "";

        int counter = 0;
        for(int i=0; i<STEPS; ++i) {

            Double log_temperature = LOGMAX - i*LOGINC;
            Double temperature = Math.pow(Math.E, log_temperature);

            Double new_score = 1.4;
            Double old_score = 1.9;

            Double p1 = Math.exp( (-0.1)/temperature ); //e^b
            Double p2 = Math.exp( (-0.2)/temperature ); //e^b
            Double p3 = Math.exp( (-0.5)/temperature ); //e^b
            Double p4 = Math.exp( (-1.0)/temperature ); //e^b
            Double p5 = Math.exp( (-1.5)/temperature ); //e^b
            Double p6 = Math.exp( (-2.0)/temperature ); //e^b

            String s1 = String.format("%-5.4f", p1);
            String s2 = String.format("%-5.4f", p2);
            String s3 = String.format("%-5.4f", p3);
            String s4 = String.format("%-5.4f", p4);
            String s5 = String.format("%-5.4f", p5);
            String s6 = String.format("%-5.4f", p6);

            //System.out.println(counter + " " + temperature + " " + " " + i);
            output += counter + " " + temperature + "\n";
            //System.out.println(counter + " " + s1 + " " + s2 + " " + s3 + " " + s4 + " " + s5 + " " + s6);
            counter++;
            System.out.println(counter);
        }

    }
}
