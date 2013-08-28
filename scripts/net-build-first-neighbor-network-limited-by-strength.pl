use strict;
use Data::Dumper;
use Carp;

=head1 NAME

net_build_first_neighbor_network_limited_by_strength - build a "first-neighbor" network with strength cut-off

=head1 SYNOPSIS

net_build_first_neighbor_network_limited_by_strength [--url=http://kbase.us/services/networks] dataset_ids edge_types cut_off < query_entity_ids

=head1 DESCRIPTION

Build a first-neighbor network given the list of dataset, edge types, and entity ids with the edge strength above cut-off threshold.

=head2 Documentation for underlying call

Returns a "first-neighbor" network constructed from a given list of datasets with the edge strength above cut-off threshold. A first-neighbor network contains a "source" node and all other nodes that have at least one interaction with the "source" node. Only interactions of given edge types are considered.    

list<string> dataset_ids
List of dataset identifiers to be used for building a network

string query_entity_ids
List of entity identifiers of interest for building a network         
                
list<EdgeType> edge_types
List of possible edge types to be considered for building a network

float cut_off
Threshold for edge strength

=head1 OPTIONS

=over 6

=item B<-u> I<[http://kbase.us/services/networks]> B<--url>=I<[http://kbase.us/services/networks]>
the service url

=item B<-h> B<--help>
print help information

=item B<--version>
print version information

=back

=head1 EXAMPLE

 echo "kb|g.3899.locus.2366 kb|g.3899.locus.2366" | net_build_first_neighbor_network_limited_by_strength "kb|netdataset.plant.cn.7,kb|netdataset.plant.fn.25" "GENE_GENE" 0.8
 net_build_first_neighbor_network_limited_by_strength --help
 net_build_first_neighbor_network_limited_by_strength --version

=head1 VERSION

1.0

=cut

use Getopt::Long;
use Bio::KBase::KBaseNetworksService::Client;

my $usage = "Usage: net_build_first_neighbor_network_limited_by_strength [--url=http://kbase.us/services/networks] dataset_ids edge_types cut_off < query_entity_ids\n";

my $url       = "http://kbase.us/services/networks";
my $help       = 0;
my $version    = 0;

GetOptions("help"       => \$help,
           "version"    => \$version,
           "url=s"     => \$url) or die $usage;

if($help)
{
    print "$usage\n";
    print "\n";
    print "General options\n";
    print "\t--url=[http://kbase.us/services/networks]\t\tthe service url\n";
    print "\t--help\t\tprint help information\n";
    print "\t--version\t\tprint version information\n";
    print "\tdataset_ids=[xxx,yyy,zzz,...]\t\tdataset id list(comma separated)\n";
    print "\tedge_types=[xxx,yyy,zzz,...]\t\tinterested edge types(comma separated)\n";
    print "\tcut_off=xx.yy\t\tedge strength cut off threshold\n";
    print "\n";
    print "Examples: \n";
    print "echo 'kb|g.3899.locus.10 kb|g.3899.locus.11' | net_build_first_neighbor_network_limited_by_strength 'kb|netdataset.plant.fn.25,kb|netdataset.plant.cn.6' 'GENE_GENE' 0.8\n";
    print "\n";
    print "net_build_first_neighbor_network_limited_by_strength --help\tprint out help\n";
    print "\n";
    print "net_build_first_neighbor_network_limited_by_strength --version\tprint out version information\n";
    print "\n";
    print "Report bugs to kbase-networks\@lists.kbase.us\n";
    exit(0);
}

if($version)
{
    print "net_build_first_neighbor_network_limited_by_strength version 1.0\n";
    print "Copyright (C) 2012 KBase Network Team\n";
    print "License GPLv3+: GNU GPL version 3 or later <http://gnu.org/licenses/gpl.html>.\n";
    print "This is free software: you are free to change and redistribute it.\n";
    print "There is NO WARRANTY, to the extent permitted by law.\n";
    print "\n";
    print "Written by Shinjae Yoo\n";
    exit(0);
}

die $usage unless @ARGV == 3;
my $dataset_ids = $ARGV[0];
my $edge_types = $ARGV[1];
my $cutOff = $ARGV[2];

my $oc = Bio::KBase::KBaseNetworksService::Client->new($url);
my @input = <STDIN>;                                                                             
my $istr = join(" ", @input);                                                                    
$istr =~ s/[,]/ /g;
@input = split /\s+/, $istr;          
my @datasetIds = split/,/, $dataset_ids;
my @edgeTypes = split/,/, $edge_types;
my $results = $oc->buildFirstNeighborNetworkLimtedByStrength(\@datasetIds, \@input, \@edgeTypes, $cutOff);
my %nodes = ();
foreach my $hr (@{$results->{'nodes'}}) {
  $nodes{$hr->{'id'}} = [$hr->{'entityId'}, $hr->{'name'}];
}
foreach my $hr (@{$results->{'edges'}}) {
  my $id1 = $nodes{$hr->{'nodeId1'}}[0];
  my $id2 = $nodes{$hr->{'nodeId2'}}[0];
  my $nm1 = $nodes{$hr->{'nodeId1'}}[1];
  my $nm2 = $nodes{$hr->{'nodeId2'}}[1];
  my $strength = $hr->{'strength'};
  my $ds  = $hr->{'datasetId'};
  my $enm = $hr->{'name'};
  print "$id1\t$id2\t$strength\t$ds\t$nm1\t$nm2\t$enm\n";
}
