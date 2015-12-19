module EspToVerilog (
in1, in2, in3, in4,
out );
input in1, in2, in3, in4;
output out;
wire w0, w1;
assign w0 = in2 | in4;
assign w1 = ~in3 | in4;
assign out =  w0 & w1;
endmodule
