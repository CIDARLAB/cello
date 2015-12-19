#!/usr/bin/perl


$ENV{'PATH'} = '/bin:/usr/bin:/usr/local/bin';

my $location = $ARGV[0];
chdir($location);
my $jobID = $ARGV[1];
my $scripts_dir = $ARGV[2];
my $grid_txt = $ARGV[3];
my $points_on_txt = $ARGV[4];
my $points_off_txt = $ARGV[5];
my $tp_name = $ARGV[6];

my $file = $grid_txt;
print "$file\n";
my $eps = substr($file, 0, -4) . ".eps";
my $txt = substr($file, 0, -4) . ".txt";
my $gp = "plot_" . $tp_name . "_grid.gp";

my $template_gp = "$scripts_dir/plot_tandem_promoter_grid_template.gp";

open FILE, "<$template_gp";
open NEW, ">$gp";

while(<FILE>) {
    my $line = $_;
    chomp($line);
    $line =~ s/grid.eps/$eps/g;
    $line =~ s/grid.txt/$txt/g;
    $line =~ s/points_on.txt/$points_on_txt/g;
    $line =~ s/points_off.txt/$points_off_txt/g;
    print NEW "$line\n";
}



system("gnuplot $gp");
system("~/epstopdf.pl $eps");
