package org.cellocad.MIT.misc;

public class POJO1 {

    public int i = 1;
    public String s = "abc";
    public POJOB pb = new POJOB(true);

    @Override
    public String toString() {
        return i + s + pb.getB();
    }
}
