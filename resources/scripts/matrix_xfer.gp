set output 'matrix_xfer.eps'
set terminal postscript eps enhanced color 'Helvetica, 35' size 4,4

unset border

set size square

set xrange[0:250]
set yrange[0:250]

set format x ''
set format y ''
set format cb ''

set lmargin 0
set rmargin 0
set bmargin 0
set tmargin 0

unset xtics
unset ytics

set palette defined (0 'white', 1000 '#hexcolor', 5000 'black')
unset colorbox


plot 'matrix_xfer.txt' matrix with image  notitle



