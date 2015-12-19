#!/bin/sh

FILE=$1;

gnuplot <<EOF
set output "$FILE"
set terminal postscript  eps  enhanced color "Helvetica, 35" size 6,4

reset

unset xtics
unset ytics
unset xlabel
unset ylabel
set lmargin 0
set bmargin 0
set rmargin 0
set tmargin 0


n=100 #number of intervals
max=log(10) #max value
min=log(0.03) #min value
width=(max-min)/n #interval width

hist(x,width)=width*floor(x/width)+width/2.0

set xrange [min:max]
set yrange [0:700]

set style fill solid 0.5 #fillstyle

set multiplot layout 4,1 rowsfirst

plot "out_hist_0.txt" u (hist(\$1,width)):(1.0) smooth freq w boxes lc rgb "blue" notitle
plot "out_hist_1.txt" u (hist(\$1,width)):(1.0) smooth freq w boxes lc rgb "blue" notitle
plot "out_hist_2.txt" u (hist(\$1,width)):(1.0) smooth freq w boxes lc rgb "blue" notitle
plot "out_hist_3.txt" u (hist(\$1,width)):(1.0) smooth freq w boxes lc rgb "blue" notitle


unset multiplot

EOF


