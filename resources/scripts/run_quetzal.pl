#!/usr/bin/perl

#####################################
# Swapnil's Finch version of Eugene
#####################################

my $sets = $ARGV[0];
my $rgx = $ARGV[1];
my $n = $ARGV[2];
my $outfile = $ARGV[3];

system("/Users/peng/cello/source/quetzal generate $sets $n < $rgx >& $outfile");
