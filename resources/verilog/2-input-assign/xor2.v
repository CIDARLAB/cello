module xor2 (
  A,
  B,
  O
);

input A;
input B;
output O;

assign O = ( ( A & ( ~ ( B ) ) ) | ( ( ~ ( A ) ) & B ) );

endmodule
