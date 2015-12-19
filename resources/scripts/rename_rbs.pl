#!/usr/bin/perl

my $old_prefix = $ARGV[0];
my $new_prefix = $ARGV[1];

my $query = "*" . $old_prefix . "*";

my @files = qx{ls $query};
foreach my $file(@files) {
    chomp($file);

    my $newfile = $file;
    $newfile =~ s/$old_prefix/$new_prefix/g;

    print "$file   $newfile\n";

    system("mv $file $newfile");
}
