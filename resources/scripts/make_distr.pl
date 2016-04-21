#!/usr/bin/perl

my @files = qx{ls rpus*.txt};
foreach my $file (@files) {
    chomp($file);

    my $eps = substr($file, 0, -4) . ".eps";
    my $gp = substr($file, 0, -4) . ".gp";

    open GP, ">$gp";

    print GP "set output \"$eps\" \n";
    print GP "set terminal postscript  eps  enhanced color \"Helvetica, 35\" size 6,4\n";

    print GP "unset xtics\n";
    print GP "unset ytics\n";
    print GP "unset xlabel\n";
    print GP "unset ylabel\n";

    print GP "n=100\n";
    print GP "max=2.5\n";
    print GP "min=-3.5\n";
    print GP "width=(max-min)/n \n";
    
    print GP "hist(x,width)=width*floor(x/width)+width/2.0\n";

    print GP "set xrange [min:max]\n";

    print GP "set style fill solid 0.5\n";

    print GP "plot \"$file\" u (hist(log10(\$1),width)):(1.0) smooth freq w boxes lc rgb \"blue\" notitle\n";


    system("gnuplot $gp");
    system("~/epstopdf.pl $eps");
}
