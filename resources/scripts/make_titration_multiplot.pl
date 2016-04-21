#!/usr/bin/perl

$ENV{'PATH'} = '/bin:/usr/bin:/usr/local/bin';


my %Hash;

$Hash{"SrpR"}=   "#1F78B4";
$Hash{"BM3R1"}=  "#33A02C";
$Hash{"PhlF"}=   "#E31A1C";
$Hash{"AmtR"}=   "#FF7F00";
$Hash{"HlyIIR"}= "#6A3D9A";
$Hash{"BetI"}=   "#A6CEE3";
$Hash{"IcaRA"}=  "#B2DF8A";
$Hash{"QacR"}=   "#FB9A99";
$Hash{"LitR"}=   "#FDBF6F";
$Hash{"AmeR"}=   "#CAB2D6";


my @dirs = qx{ls -1p | grep / | grep NOR | grep -v YFP | grep rbs};
foreach my $dir(@dirs) {
    chomp($dir);
    chop($dir);
    my @split = split /-/, $dir;
    my $cds = $split[1];
    my $color = $Hash{$cds};

    chdir($dir);

    my $eps = $dir . "_titr" . ".eps";
    my $pdf = $dir . "_titr" . ".pdf";
    my $png = $dir . "_titr" . ".png";
    my $gp  = $dir . "_titr" . ".gp";


    open GP, ">$gp";
    print GP "set output \"$eps\"\n";
    print GP "set terminal postscript  eps  enhanced color \"Helvetica, 35\" size 12,12\n";
    print GP "reset\n";
    print GP "unset xtics\n";
    print GP "unset ytics\n";
    print GP "unset xlabel\n";
    print GP "unset ylabel\n";
    print GP "set lmargin 0\n";
    print GP "set bmargin 0\n";
    print GP "set rmargin 0\n";
    print GP "set tmargin 0\n";
    print GP "n=100 #number of intervals\n";
    print GP "max=log10(100) #max value\n";
    print GP "min=log10(0.01) #min value\n";
    print GP "width=(max-min)/n #interval width\n";
    print GP "hist(x,width)=width*floor(x/width)+width/2.0\n";
    print GP "set xrange [min:max]\n";
    print GP "set yrange [0:$max_y]\n";
    print GP "set style fill solid 0.75 #fillstyle\n";
    print GP "set multiplot layout 12,1 rowsfirst\n";

    my @titrs = qx{ls rpus_*_??.txt};
    foreach my $titr(@titrs) {
	chomp($titr);
	print GP "plot \"$titr\" u (hist(log10(\$1),width)):(1.0) smooth freq w boxes lc rgb \"$color\" notitle\n";
    }
    print GP "unset multiplot\n";
	
    
    system("gnuplot $gp");
    system("ps2pdf -dPDFSETTINGS=/prepress -dEPSCrop -r100  $eps");
    system("gs -dQUIET -dNOPAUSE -dBATCH -sDEVICE=pngalpha -sOutputFile=$png -r300 $pdf");

    chdir("..");
}


