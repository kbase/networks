use strict;
use Data::Dumper;
use Carp;

#
# This is a SAS Component
#

=head1 buildFirstNeighborNetwork

Example:

    buildFirstNeighborNetwork [arguments] < input > output

The standard input should be a tab-separated table (i.e., each line
is a tab-separated set of fields).  Normally, the last field in each
line would contain the identifer. If another column contains the identifier
use

    -c N

where N is the column (from 1) that contains the identifier.

This is a pipe command. The input is taken from the standard input, and the
output is to the standard output.

=head2 Documentation for underlying call

This script is a wrapper for the CDMI-API call buildFirstNeighborNetwork. It is documented as follows:

Returns a "first-neighbor" network constructed basing on a given list of datasets. First-neighbor network contains 
a "source" node and all other nodes that have at least one interaction with the "source" node. Only interactions of given types are 
considered.    

list<string> datasetIds
List of dataset identifiers to be used for building a network

                  string geneId
                  Identifier of a gene to be used as a source node           
                
list<EdgeType> edgeTypes
List of possible edge types to be considered for building a network

=over 4

=item Parameter and return types

=begin html

<pre>
$datasetIds is a reference to a list where each element is a string
$geneId is a string
$edgeTypes is a reference to a list where each element is an EdgeType
$network is a Network
EdgeType is a string
Network is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	edges has a value which is a reference to a list where each element is an Edge
	nodes has a value which is a reference to a list where each element is a Node
	datasets has a value which is a reference to a list where each element is a Dataset
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string
Edge is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	nodeId1 has a value which is a string
	nodeId2 has a value which is a string
	directed has a value which is a Boolean
	confidence has a value which is a float
	strength has a value which is a float
	datasetId has a value which is a string
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string
Boolean is a string
Node is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	entityId has a value which is a string
	type has a value which is a NodeType
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string
NodeType is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	networkType has a value which is a NetworkType
	sourceReference has a value which is a DatasetSourceRef
	taxons has a value which is a reference to a list where each element is a Taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
NetworkType is a string
DatasetSourceRef is a string
Taxon is a string

</pre>

=end html

=begin text

$datasetIds is a reference to a list where each element is a string
$geneId is a string
$edgeTypes is a reference to a list where each element is an EdgeType
$network is a Network
EdgeType is a string
Network is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	edges has a value which is a reference to a list where each element is an Edge
	nodes has a value which is a reference to a list where each element is a Node
	datasets has a value which is a reference to a list where each element is a Dataset
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string
Edge is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	nodeId1 has a value which is a string
	nodeId2 has a value which is a string
	directed has a value which is a Boolean
	confidence has a value which is a float
	strength has a value which is a float
	datasetId has a value which is a string
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string
Boolean is a string
Node is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	entityId has a value which is a string
	type has a value which is a NodeType
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string
NodeType is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	networkType has a value which is a NetworkType
	sourceReference has a value which is a DatasetSourceRef
	taxons has a value which is a reference to a list where each element is a Taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
NetworkType is a string
DatasetSourceRef is a string
Taxon is a string


=end text

=back

=head2 Command-Line Options

=over 4

=item -c Column

This is used only if the column containing the identifier is not the last column.

=item -i InputFile    [ use InputFile, rather than stdin ]

=back

=head2 Output Format

The standard output is a tab-delimited file. It consists of the input
file with extra columns added.

Input lines that cannot be extended are written to stderr.

=cut

use Getopt::Long;
use Bio::KBase::KBaseNetworksService::Client;

my $usage = "Usage: $0 [--host=140.221.92.222:7064] dataset_ids edge_types < queryID\n";

my $host       = "140.221.92.222:7064";
my $help       = 0;
my $version    = 0;

GetOptions("help"       => \$help,
           "version"    => \$version,
           "host=s"     => \$host) or die $usage;

if($help)
{
	print "$usage\n";
	print "\n";
	print "General options\n";
	print "\t--host=[xxx.xxx.xx.xxx:xxxx]\t\thostname of the server\n";
	print "\t--help\t\tprint help information\n";
	print "\t--version\t\tprint version information\n";
	print "\tdataset_ids=[xxx,yyy,zzz,...]\t\tdataset id list(comma separated)\n";
	print "\tedge_types=[xxx,yyy,zzz,...]\t\tinterested edge types(comma separated)\n";
	print "\n";
	print "Examples: \n";
	print "$0 --host=x.x.x.x:x \n";
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

my $oc = Bio::KBase::KBaseNetworksService::Client->new("http://".$host."/KBaseNetworksRPC/networks");
my @input = <STDIN>;                                                                             
my $istr = join(" ", @input);                                                                    
$istr =~ s/[,]/ /g;
@input = split /\s+/, $istr;          
my @datasetIds = split/,/, $dataset_ids;
my @edgeTypes = split/,/, $edge_types;
my $results = $oc->buildFirstNeighborNetwork(\@datasetIds, \@input, \@edgeTypes);
print Dumper($results);
