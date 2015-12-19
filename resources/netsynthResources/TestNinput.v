module 158_3(output out, input inp1,inp2,inp3);
     reg out;
     always@(inp1,inp2,inp3) begin
          case({inp1,inp2,inp3})
               3'b000: out = 1'b1;
               3'b001: out = 1'b0;
               3'b010: out = 1'b0;
               3'b011: out = 1'b1;
               3'b100: out = 1'b1;
               3'b101: out = 1'b1;
               3'b110: out = 1'b1;
               3'b111: out = 1'b0;
               default: out = 1'b0;
          endcase
     end
endmodule
