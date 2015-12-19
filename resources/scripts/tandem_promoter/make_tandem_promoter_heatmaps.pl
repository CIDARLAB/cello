
#!/usr/bin/perl

my @files = qx{ls -1 grid*.txt };
foreach my $file(@files) {
    chomp($file);
    print "$file\n";
    my $eps = substr($file, 0, -4) . ".eps";
    my $txt = substr($file, 0, -4) . ".txt";


    my $template_gp = "plot_OR_grid_template.gp";
    if ($file =~ /diff/) {
	$template_gp = "plot_OR_grid_template_diff.gp";
    }

    open FILE, "<$template_gp";
    open NEW, ">plot_OR_grid.gp";

    while(<FILE>) {
	my $line = $_;
	chomp($line);
	$line =~ s/grid_avg.eps/$eps/g;
	$line =~ s/grid_avg.txt/$txt/g;
	print NEW "$line\n";
    }

    system("gnuplot plot_OR_grid.gp");
    system("~/cello/resources/scripts/epstopdf.pl $eps");
    
}
