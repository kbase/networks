use strict;
use Data::Dumper;
use Carp;

#
# This is a SAS Component
#

=head1 entity2Datasets

Example:

    entity2Datasets [arguments] < input > output

The standard input should be a tab-separated table (i.e., each line
is a tab-separated set of fields).  Normally, the last field in each
line would contain the identifer. If another column contains the identifier
use

    -c N

where N is the column (from 1) that contains the identifier.

This is a pipe command. The input is taken from the standard input, and the
output is to the standard output.

=head2 Documentation for underlying call

This script is a wrapper for the CDMI-API call entity2Datasets. It is documented as follows:

Returns a list of all datasets that have at least one interection for a given KBase entity (gene, protein, molecule, genome, etc)

=over 4

=item Parameter and return types

=begin html

<pre>
$entityId is a string
$datasets is a reference to a list where each element is a Dataset
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

$entityId is a string
$datasets is a reference to a list where each element is a Dataset
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

my $usage = "Usage: $0 [--host=140.221.92.222:7064] < enityID\n";

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

die $usage unless @ARGV == 0;

my $oc = Bio::KBase::KBaseNetworksService::Client->new("http://".$host."/KBaseNetworksRPC/networks");
my $input = <STDIN>;
$input =~ s/\s+//g;
my $results = $oc->datasetSource2Datasets($input);
foreach my $rh (@{$results}) {
  print $rh->{"id"}."\t".$rh->{'networkType'}."\t".$rh->{"sourceReference"}."\t".$rh->{"name"}."\t".$rh->{"description"}."\n";
}
