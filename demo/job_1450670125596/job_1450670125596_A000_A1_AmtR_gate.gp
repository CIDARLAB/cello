set output "job_1450670125596_A000_A1_AmtR_gate.eps"
set terminal postscript  eps  enhanced color "Helvetica, 35" size 8,8
reset
set xtics nomirror
set tics scale 2
set logscale x
set format y "" 
set format x "" 
set xtics (0.001, 0.01, 0.1, 0, 1, 10, 100)
set ytics 0.01
set border 15 lw 2
set lmargin 5
set bmargin 0
set rmargin 0
set tmargin 0
max=500 #max value
min=0.0005 #min value
set xrange [min:max]
set yrange [-0.0002:0.0674105]
set style fill solid 0.5 #fillstyle
set multiplot layout 6,1 rowsfirst
set ylabel "00" rotate by 0
plot "job_1450670125596_A000_A1_AmtR_gate.txt" u (10**$1):2 w boxes lc rgb "black" notitle
set ylabel "01" rotate by 0
plot "job_1450670125596_A000_A1_AmtR_gate.txt" u (10**$1):3 w boxes lc rgb "dark-gray" notitle
set ylabel "10" rotate by 0
plot "job_1450670125596_A000_A1_AmtR_gate.txt" u (10**$1):4 w boxes lc rgb "black" notitle
set ylabel "11" rotate by 0
set format x "10^{%T}" 
set xlabel "Output (REU)" 
plot "job_1450670125596_A000_A1_AmtR_gate.txt" u (10**$1):5 w boxes lc rgb "dark-gray" notitle
unset multiplot
