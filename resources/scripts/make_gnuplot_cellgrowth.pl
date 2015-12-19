#!/usr/bin/perl

$ENV{'PATH'} = '/bin:/usr/bin:/usr/local/bin';


my $location = $ARGV[0];
chdir($location);
my $dateID = $ARGV[1];
my $input_txt = $ARGV[2];

my $rows = length($truth);


my @lines = qx{grep \: $input_txt};

my $counter = 1;

my $base = substr($input_txt, 0, -15);

my $dat_file = substr($input_txt, 0, -4) . ".dat";
my $eps_file = substr($input_txt, 0, -4) . ".eps";
my $pdf_file = substr($input_txt, 0, -4) . ".pdf";
my $png_file = substr($input_txt, 0, -4) . ".png";
my $gp_file  = substr($input_txt, 0, -4) . ".gp";

my $lmargin = 0;
my $nrows = @lines;
chomp($nrows);


open DAT, ">$dat_file";

foreach my $line(@lines) {
    chomp($line);
    $line =~ s/\[//g;
    $line =~ s/\]//g;
    my @split = split /tox:\ /, $line;
    my $tox = $split[1];
    chomp($tox);
    
    print DAT "$counter $tox \n";

    $counter++;
}

open GP, ">$gp_file";
print GP "set output \"$eps_file\"\n";
print GP "set terminal postscript eps enhanced color \"Helvetica, 35\" size 4,4\n";
print GP "set boxwidth 0.5\n";
print GP "set lmargin screen 0\n";
print GP "set rmargin screen 1\n";
print GP "set tmargin screen 0\n";
print GP "set bmargin screen 1\n";
print GP "set border linewidth 5\n";
print GP "set tics scale 2\n";
print GP "set key off\n";
print GP "set xrange [0.5:$nrows+0.5]\n";
print GP "set yrange [0:1]\n";
print GP "set format x \"\"\n";
print GP "set format y \"\"\n";
print GP "set xtic rotate by -90\n";
print GP "set style fill solid\n";

print GP "plot \"$dat_file\" u 1:(1-\$2) with boxes ls 1 lc rgb \"red\" \n";

close(GP);

system("gnuplot $gp_file");         #gp to eps
system("ps2pdf -dPDFSETTINGS=/prepress -dEPSCrop -r100  $eps_file");  #eps to pdf
system("gs -dQUIET -dNOPAUSE -dBATCH -sDEVICE=pngalpha -sOutputFile=$png_file -r300 $pdf_file"); #pdf to png

