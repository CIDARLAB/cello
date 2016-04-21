#!/usr/bin/perl

$ENV{'PATH'} = '/bin:/usr/bin:/usr/local/bin';


my $location = $ARGV[0];
chdir($location);
my $dateID = $ARGV[1];
my $input_txt = $ARGV[2];
my $truth     = $ARGV[3];  #0001 is 2-input AND
my $input_truth = $ARGV[4];
my @input_truths =  split(",", $input_truth);

my @logics = split("", $truth);

my $rows = length($truth);

my $eps = substr($input_txt, 0, -4) . ".eps";
my $svg = substr($input_txt, 0, -4) . ".svg";
my $pdf = substr($input_txt, 0, -4) . ".pdf";
my $png = substr($input_txt, 0, -4) . ".png";
my $gp  = substr($input_txt, 0, -4) . ".gp";

my $height = $rows;


open GP, ">$gp";
print GP "set output \"$eps\"\n";
#print GP "set terminal postscript  eps  enhanced color \"Helvetica, 35\" size 4,4\n";
print GP "set terminal svg size 800,800\n";
print GP "reset\n";
print GP "max=log(20) #max value\n";
print GP "min=log(0.05) #min value\n";
print GP "set xrange [min:max]\n";
print GP "set style fill solid 0.5 #fillstyle\n";
print GP "set multiplot layout $rows,1 rowsfirst\n";
for(my $i=1; $i<=$rows; ++$i) {
    my $column = $i+1;
    my $logic = $logics[$i-1];
    my $color = "red";
    if($logic == 1) {
	$color = "blue";
    }
    print GP "set table \"conv$i.dat\"\n";
    print GP "plot \"$input_txt\" u 1:$column w boxes lc rgb \"$color\" notitle\n";
    print GP "unset table\n";

}
print GP "unset multiplot\n";

system("gnuplot $gp");
my $max_y = qx{cat conv*.dat | sort -grk 2 | head -1 | awk '{print \$2}'};
chomp($max_y);
system("rm conv*.dat");


my $nrows_plus2 = $rows + 2;

open GP, ">$gp";
print GP "set output \"$eps\"\n";
print GP "set terminal postscript  eps  enhanced color \"Helvetica, 35\" size 8,8\n";
print GP "reset\n";
print GP "set xtics nomirror\n";
print GP "set tics scale 2\n";
print GP "set logscale x\n";
print GP "set format y \"\" \n";
print GP "set format x \"\" \n";
print GP "set xtics (0.001, 0.01, 0.1, 0, 1, 10, 100)\n";
print GP "set ytics 0.01\n";
print GP "set border 15 lw 2\n";
print GP "set lmargin 5\n";
print GP "set bmargin 0\n";
print GP "set rmargin 0\n";
print GP "set tmargin 0\n";
print GP "max=500 #max value\n";
print GP "min=0.0005 #min value\n";
print GP "set xrange [min:max]\n";
print GP "set yrange [-0.0002:$max_y]\n";
print GP "set style fill solid 0.5 #fillstyle\n";
print GP "set multiplot layout $nrows_plus2,1 rowsfirst\n";
for(my $i=1; $i<=$rows; ++$i) {
    my $column = $i+1;
    my $logic = $logics[$i-1];
    my $color = "dark-gray";
    if($logic == 1) {
	$color = "black";
    }
    my $binary = sprintf("%b", $i-1);
    my $last_binary = sprintf("%b", $rows-1);
    my $n_digits = length($last_binary);
    my $fullbinary = sprintf("%0*d", $n_digits, $binary);


    $fullbinary = $input_truths[$i-1];

    print GP "set ylabel \"$fullbinary\" rotate by 0\n";

    if($i == $rows) {
	print GP "set format x \"10^{%T}\" \n";
	print GP "set xlabel \"Output (RPU)\" \n";
    }
    print GP "plot \"$input_txt\" u (10**\$1):$column w boxes lc rgb \"$color\" notitle\n";
}
print GP "unset multiplot\n";

system("gnuplot $gp");
system("ps2pdf -dPDFSETTINGS=/prepress -dEPSCrop -r100 $eps");
system("gs -dQUIET -dNOPAUSE -dBATCH -sDEVICE=pngalpha -sOutputFile=$png -r80 $pdf");
