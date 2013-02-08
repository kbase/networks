use strict;
use Data::Dumper;
use Carp;

=head1 NAME

net_build_first_neighbor_network - build a "first-neighbor" network 

=head1 SYNOPSIS

net_build_first_neighbor_network [--url=http://kbase.us/services/networks] dataset_ids edge_types < query_entity_ids

=head1 DESCRIPTION

Build a first-neighbor network given the list of dataset, edge types, and entity ids.

=head2 Documentation for underlying call

Returns a "first-neighbor" network constructed basing on a given list of datasets. First-neighbor network contains a "source" node and all other nodes that have at least one interaction with the "source" node. Only interactions of given edge types are considered.    

list<string> datasetIds
List of dataset identifiers to be used for building a network

string geneId
Identifier of a gene to be used as a source node           
                
list<EdgeType> edgeTypes
List of possible edge types to be considered for building a network

=head1 OPTIONS

=over 6

=item B<-u> I<[http://kbase.us/services/ontology_service]> B<--url>=I<[http://kbase.us/services/ontology_service]>
the service url

=item B<-h> B<--help>
prints help information

=item B<--version>
print version information

=back

=head1 EXAMPLE

 echo "kb|g.3899.locus.2366 kb|g.3899.locus.2366" | net_build_first_neighbor_network "kb|netdataset.plant.cn.7,kb|netdataset.plant.fn.25" "GENE_GENE"
 net_build_first_neighbor_network --help
 net_build_first_neighbor_network --version

=head1 VERSION

1.0

=cut

use Getopt::Long;
use Bio::KBase::KBaseNetworksService::Client;

my $usage = "Usage: net_build_first_neighbor_network [--url=http://kbase.us/services/networks] dataset_ids edge_types < query_entity_ids\n";

my $url       = "140.221.92.222:7064";
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
	print "\t--url=[xxx.xxx.xx.xxx:xxxx]\t\tthe service url\n";
	print "\t--help\t\tprint help information\n";
	print "\t--version\t\tprint version information\n";
	print "\tdataset_ids=[xxx,yyy,zzz,...]\t\tdataset id list(comma separated)\n";
	print "\tedge_types=[xxx,yyy,zzz,...]\t\tinterested edge types(comma separated)\n";
	print "\n";
	print "Examples: \n";
	print "$0 --url=x.x.x.x:x \n";
	print "\n";
	print "$0 --help\tprint out help\n";
	print "\n";
	print "$0 --version\tprint out version information\n";
	print "\n";
	print "Report bugs to kbase-networks\@lists.kbase.us\n";
	exit(1);
}

if($version)
{
	print "$0 version 1.0\n";
	print "Copyright (C) 2012 KBase Network Team\n";
	print "License GPLv3+: GNU GPL version 3 or later <http://gnu.org/licenses/gpl.html>.\n";
	print "This is free software: you are free to change and redistribute it.\n";
	print "There is NO WARRANTY, to the extent permitted by law.\n";
	print "\n";
	print "Written by Shinjae Yoo\n";
	exit(1);
}

die $usage unless @ARGV == 2;
my $dataset_ids = $ARGV[0];
my $edge_types = $ARGV[1];

my $oc = Bio::KBase::KBaseNetworksService::Client->new("http://".$url."/KBaseNetworksRPC/networks");
my @input = <STDIN>;                                                                             
my $istr = join(" ", @input);                                                                    
$istr =~ s/[,]/ /g;
@input = split /\s+/, $istr;          
my @datasetIds = split/,/, $dataset_ids;
my @edgeTypes = split/,/, $edge_types;
my $results = $oc->buildFirstNeighborNetwork(\@datasetIds, \@input, \@edgeTypes);
print Dumper($results);
