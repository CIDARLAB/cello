#!/usr/bin/perl

$ENV{'PATH'} = '/bin:/usr/bin:/usr/local/bin';

my $location = $ARGV[0];
chdir($location);
my $dateID = $ARGV[1];
my $input_txt = $ARGV[2];
my $truth     = $ARGV[3];  #0001 is 2-input AND
my @logics = split("", $truth);

my $rows = length($truth);


my @lines = qx{grep \: $input_txt};

my $counter = 1;

my $base = substr($input_txt, 0, -15);

my $dat_file = substr($input_txt, 0, -4) . ".dat";
my $eps_file = substr($input_txt, 0, -4) . ".eps";
my $pdf_file = substr($input_txt, 0, -4) . ".pdf";
my $png_file = substr($input_txt, 0, -4) . ".png";
my $gp_file  = substr($input_txt, 0, -4) . ".gp";

my @out_colors;
my $lmargin = 0;
my $nrows = @lines;
chomp($nrows);


open DAT, ">$dat_file";

foreach my $line(@lines) {
    chomp($line);

    my @split_brackets = split /\[|\]/, $line;
    my $input_truth = $split_brackets[1];
    $input_truth =~ s/ //g;


    $line =~ s/\[//g;
    $line =~ s/\]//g;
    my @split = split /:/, $line;
    $line =~ s/\://g;
    my $out_expected = qx{echo \"$split[1]\" | awk '{print \$1}'};
    my $out_observed = qx{echo \"$split[2]\" | awk '{print \$1}'};
    chomp($out_expected);
    chomp($out_observed);
    
    $out_colors[$counter-1] = $out_expected;
    
    my $color;
    if($out_expected == 0) {
	$color = "red";
    }
    else {
	$color = "blue";
    }

    print DAT "$counter $input_truth $out_observed  $color\n";

    $counter++;
}

open GP, ">$gp_file";
print GP "set output \"$eps_file\"\n";
print GP "set terminal postscript eps enhanced color \"Helvetica, 35\" size 6,6\n";
print GP "set boxwidth 0.5\n";
print GP "set lmargin 6\n";
#print GP "set lmargin screen 0\n";
#print GP "set rmargin screen 1\n";
#print GP "set tmargin screen 0\n";
#print GP "set bmargin screen 1\n";
print GP "set border linewidth 2\n";
print GP "set tics scale 2\n";
print GP "set mytics 10\n";
print GP "set key off\n";
print GP "set xrange [0.5:$nrows+0.5]\n";
print GP "set yrange [0.001:100.0]\n";
#print GP "set format x \"\"\n";
#print GP "set ylabel \"Output (RPU)\"\n";
#print GP "set xlabel \"Input Logic\"\n";
#print GP "set format y \"\"\n";
#print GP "set xtic rotate by -90\n";
print GP "set logscale y\n";
print GP "set style fill solid\n";

print GP "plot \"< awk '{if(\$4 == \\\"red\\\") print}'  $dat_file\" u 1:3:xtic(2) with boxes ls 1 lc rgb \"dark-gray\", \\
     \"< awk '{if(\$4 == \\\"blue\\\") print}' $dat_file\" u 1:3:xtic(2) with boxes ls 1 lc rgb \"black\" \n";


close(GP);

system("gnuplot $gp_file");         #gp to eps
system("ps2pdf -dPDFSETTINGS=/prepress -dEPSCrop -r100  $eps_file");  #eps to pdf
system("gs -dQUIET -dNOPAUSE -dBATCH -sDEVICE=pngalpha -sOutputFile=$png_file -r300 $pdf_file"); #pdf to png

