module structuralXNOR(output out, input inA, inB, inC);

wire w2,w3,w4,w6,w7,w8,w9;

nor (w9, inB, inC);
nor (w8, w9, inC);
nor (w7, w9, inB);
nor (w6, w7, w8);
nor (w4, inA, w6);
nor (w3, w4, w6);
nor (w2, w4, inA);
or (out, w2, w3);
   
endmodule
