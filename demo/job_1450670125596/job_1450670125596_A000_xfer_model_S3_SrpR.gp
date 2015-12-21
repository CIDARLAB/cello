
set output "job_1450670125596_A000_xfer_model_S3_SrpR.eps"
set terminal postscript eps enhanced color "Helvetica, 35" size 2,2
set logscale x
set logscale y
set lmargin screen 0.0
set rmargin screen 1.0
set tmargin screen 1.0
set bmargin screen 0.0
set size ratio 1.0
set border linewidth 2
set tics scale 2
set mxtics 10
set mytics 10
set key bottom left
set key samplen -1
set xrange [0.001:1000.0]
set yrange [0.001:1000.0]
set format y "10^{%L}"    
set format x "10^{%L}"    
set format x ""    
set xlabel '1100'
set arrow from 0.0082,0.001 to 0.0082,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
set arrow from 2.5,0.001 to 2.5,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
ymin = 0.004
ymax = 2.1
n = 2.8
K = 0.06
set dummy x

plot ymin+(ymax-ymin)/(1.0+(x/K)**n) lw 25 lc rgb '#006838' title 'S3SrpR',\
 "<echo '1 2'" using (0.060081844):(1.0500000075678904)  with points pt 7 ps 4 lc rgb 'black' notitle,\
 "<echo '1 2'" using (0.561105739):(0.007999999999874437)  with points pt 7 ps 4 lc rgb 'black' notitle
