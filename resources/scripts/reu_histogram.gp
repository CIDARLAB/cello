set output "all.eps"
set terminal postscript  eps  enhanced color "Helvetica, 35" size 6,4

reset
unset key
set title ""

plot "all.txt" using 1:2 with lines lc rgb "red",\
     "bin_centers.txt" using 2:1 with points ps 1 pt 1 lc rgb "black"
