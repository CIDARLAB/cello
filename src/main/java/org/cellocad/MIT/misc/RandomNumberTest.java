package org.cellocad.MIT.misc;

import java.util.Random;

public class RandomNumberTest {

    public static void main(String[] args) {
        Random random = new Random(50);

        for(int i=0; i<10; ++i) {

            System.out.println(random.nextInt(10));
        }
    }

}
