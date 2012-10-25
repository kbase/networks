package KBaseNetworkImpl;
use strict;
use Bio::KBase::Exceptions;
# Use Semantic Versioning (2.0.0-rc.1)
# http://semver.org 
our $VERSION = "0.1.0";

=head1 NAME

KBaseNetwork

=head1 DESCRIPTION



=cut

#BEGIN_HEADER
#END_HEADER

sub new
{
    my($class, @args) = @_;
    my $self = {
    };
    bless $self, $class;
    #BEGIN_CONSTRUCTOR
    #END_CONSTRUCTOR

    if ($self->can('_init_instance'))
    {
	$self->_init_instance();
    }
    return $self;
}

=head1 METHODS



=head2 getDatasets

  $datasetList = $obj->getDatasets($ParameterList)

=over 4

=item Parameter and return types

=begin html

<pre>
$ParameterList is a ParameterList
$datasetList is a DatasetList
ParameterList is a reference to a list where each element is a Parameter
Parameter is a reference to a hash where the following keys are defined:
	type has a value which is a Type
	value has a value which is a Value
Type is a string
Value is a string
DatasetList is a reference to a list where each element is a Dataset
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	networkType has a value which is a NetworkType
	datasetSource has a value which is a DatasetSource
	taxons has a value which is a reference to a list where each element is a Taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
NetworkType is a string
DatasetSource is a string
Taxon is a string

</pre>

=end html

=begin text

$ParameterList is a ParameterList
$datasetList is a DatasetList
ParameterList is a reference to a list where each element is a Parameter
Parameter is a reference to a hash where the following keys are defined:
	type has a value which is a Type
	value has a value which is a Value
Type is a string
Value is a string
DatasetList is a reference to a list where each element is a Dataset
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	networkType has a value which is a NetworkType
	datasetSource has a value which is a DatasetSource
	taxons has a value which is a reference to a list where each element is a Taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
NetworkType is a string
DatasetSource is a string
Taxon is a string


=end text



=item Description



=back

=cut

sub getDatasets
{
    my $self = shift;
    my($ParameterList) = @_;

    my @_bad_arguments;
    (ref($ParameterList) eq 'ARRAY') or push(@_bad_arguments, "Invalid type for argument \"ParameterList\" (value was \"$ParameterList\")");
    if (@_bad_arguments) {
	my $msg = "Invalid arguments passed to getDatasets:\n" . join("", map { "\t$_\n" } @_bad_arguments);
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
							       method_name => 'getDatasets');
    }

    my $ctx = $KBaseNetworkServer::CallContext;
    my($datasetList);
    #BEGIN getDatasets
    #END getDatasets
    my @_bad_returns;
    (ref($datasetList) eq 'ARRAY') or push(@_bad_returns, "Invalid type for return variable \"datasetList\" (value was \"$datasetList\")");
    if (@_bad_returns) {
	my $msg = "Invalid returns passed to getDatasets:\n" . join("", map { "\t$_\n" } @_bad_returns);
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
							       method_name => 'getDatasets');
    }
    return($datasetList);
}




=head2 buildNetwork

  $network = $obj->buildNetwork($ParameterList)

=over 4

=item Parameter and return types

=begin html

<pre>
$ParameterList is a ParameterList
$network is a Network
ParameterList is a reference to a list where each element is a Parameter
Parameter is a reference to a hash where the following keys are defined:
	type has a value which is a Type
	value has a value which is a Value
Type is a string
Value is a string
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
	confidence has a value which is a float
	strength has a value which is a float
	datasetId has a value which is a string
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string
Node is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	entity has a value which is a KBaseEntity
	type has a value which is a NodeType
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string
KBaseEntity is a reference to a hash where the following keys are defined:
	id has a value which is a string
NodeType is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	networkType has a value which is a NetworkType
	datasetSource has a value which is a DatasetSource
	taxons has a value which is a reference to a list where each element is a Taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
NetworkType is a string
DatasetSource is a string
Taxon is a string

</pre>

=end html

=begin text

$ParameterList is a ParameterList
$network is a Network
ParameterList is a reference to a list where each element is a Parameter
Parameter is a reference to a hash where the following keys are defined:
	type has a value which is a Type
	value has a value which is a Value
Type is a string
Value is a string
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
	confidence has a value which is a float
	strength has a value which is a float
	datasetId has a value which is a string
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string
Node is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	entity has a value which is a KBaseEntity
	type has a value which is a NodeType
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string
KBaseEntity is a reference to a hash where the following keys are defined:
	id has a value which is a string
NodeType is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	networkType has a value which is a NetworkType
	datasetSource has a value which is a DatasetSource
	taxons has a value which is a reference to a list where each element is a Taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
NetworkType is a string
DatasetSource is a string
Taxon is a string


=end text



=item Description



=back

=cut

sub buildNetwork
{
    my $self = shift;
    my($ParameterList) = @_;

    my @_bad_arguments;
    (ref($ParameterList) eq 'ARRAY') or push(@_bad_arguments, "Invalid type for argument \"ParameterList\" (value was \"$ParameterList\")");
    if (@_bad_arguments) {
	my $msg = "Invalid arguments passed to buildNetwork:\n" . join("", map { "\t$_\n" } @_bad_arguments);
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
							       method_name => 'buildNetwork');
    }

    my $ctx = $KBaseNetworkServer::CallContext;
    my($network);
    #BEGIN buildNetwork
    #END buildNetwork
    my @_bad_returns;
    (ref($network) eq 'HASH') or push(@_bad_returns, "Invalid type for return variable \"network\" (value was \"$network\")");
    if (@_bad_returns) {
	my $msg = "Invalid returns passed to buildNetwork:\n" . join("", map { "\t$_\n" } @_bad_returns);
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
							       method_name => 'buildNetwork');
    }
    return($network);
}




=head2 buildFirstNeighborNetwork

  $network = $obj->buildFirstNeighborNetwork($ParameterList)

=over 4

=item Parameter and return types

=begin html

<pre>
$ParameterList is a ParameterList
$network is a Network
ParameterList is a reference to a list where each element is a Parameter
Parameter is a reference to a hash where the following keys are defined:
	type has a value which is a Type
	value has a value which is a Value
Type is a string
Value is a string
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
	confidence has a value which is a float
	strength has a value which is a float
	datasetId has a value which is a string
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string
Node is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	entity has a value which is a KBaseEntity
	type has a value which is a NodeType
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string
KBaseEntity is a reference to a hash where the following keys are defined:
	id has a value which is a string
NodeType is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	networkType has a value which is a NetworkType
	datasetSource has a value which is a DatasetSource
	taxons has a value which is a reference to a list where each element is a Taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
NetworkType is a string
DatasetSource is a string
Taxon is a string

</pre>

=end html

=begin text

$ParameterList is a ParameterList
$network is a Network
ParameterList is a reference to a list where each element is a Parameter
Parameter is a reference to a hash where the following keys are defined:
	type has a value which is a Type
	value has a value which is a Value
Type is a string
Value is a string
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
	confidence has a value which is a float
	strength has a value which is a float
	datasetId has a value which is a string
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string
Node is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	entity has a value which is a KBaseEntity
	type has a value which is a NodeType
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string
KBaseEntity is a reference to a hash where the following keys are defined:
	id has a value which is a string
NodeType is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	networkType has a value which is a NetworkType
	datasetSource has a value which is a DatasetSource
	taxons has a value which is a reference to a list where each element is a Taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
NetworkType is a string
DatasetSource is a string
Taxon is a string


=end text



=item Description



=back

=cut

sub buildFirstNeighborNetwork
{
    my $self = shift;
    my($ParameterList) = @_;

    my @_bad_arguments;
    (ref($ParameterList) eq 'ARRAY') or push(@_bad_arguments, "Invalid type for argument \"ParameterList\" (value was \"$ParameterList\")");
    if (@_bad_arguments) {
	my $msg = "Invalid arguments passed to buildFirstNeighborNetwork:\n" . join("", map { "\t$_\n" } @_bad_arguments);
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
							       method_name => 'buildFirstNeighborNetwork');
    }

    my $ctx = $KBaseNetworkServer::CallContext;
    my($network);
    #BEGIN buildFirstNeighborNetwork
    #END buildFirstNeighborNetwork
    my @_bad_returns;
    (ref($network) eq 'HASH') or push(@_bad_returns, "Invalid type for return variable \"network\" (value was \"$network\")");
    if (@_bad_returns) {
	my $msg = "Invalid returns passed to buildFirstNeighborNetwork:\n" . join("", map { "\t$_\n" } @_bad_returns);
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
							       method_name => 'buildFirstNeighborNetwork');
    }
    return($network);
}




=head2 buildInternalNetwork

  $network = $obj->buildInternalNetwork($ParameterList)

=over 4

=item Parameter and return types

=begin html

<pre>
$ParameterList is a ParameterList
$network is a Network
ParameterList is a reference to a list where each element is a Parameter
Parameter is a reference to a hash where the following keys are defined:
	type has a value which is a Type
	value has a value which is a Value
Type is a string
Value is a string
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
	confidence has a value which is a float
	strength has a value which is a float
	datasetId has a value which is a string
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string
Node is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	entity has a value which is a KBaseEntity
	type has a value which is a NodeType
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string
KBaseEntity is a reference to a hash where the following keys are defined:
	id has a value which is a string
NodeType is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	networkType has a value which is a NetworkType
	datasetSource has a value which is a DatasetSource
	taxons has a value which is a reference to a list where each element is a Taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
NetworkType is a string
DatasetSource is a string
Taxon is a string

</pre>

=end html

=begin text

$ParameterList is a ParameterList
$network is a Network
ParameterList is a reference to a list where each element is a Parameter
Parameter is a reference to a hash where the following keys are defined:
	type has a value which is a Type
	value has a value which is a Value
Type is a string
Value is a string
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
	confidence has a value which is a float
	strength has a value which is a float
	datasetId has a value which is a string
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string
Node is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	entity has a value which is a KBaseEntity
	type has a value which is a NodeType
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string
KBaseEntity is a reference to a hash where the following keys are defined:
	id has a value which is a string
NodeType is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	networkType has a value which is a NetworkType
	datasetSource has a value which is a DatasetSource
	taxons has a value which is a reference to a list where each element is a Taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
NetworkType is a string
DatasetSource is a string
Taxon is a string


=end text



=item Description



=back

=cut

sub buildInternalNetwork
{
    my $self = shift;
    my($ParameterList) = @_;

    my @_bad_arguments;
    (ref($ParameterList) eq 'ARRAY') or push(@_bad_arguments, "Invalid type for argument \"ParameterList\" (value was \"$ParameterList\")");
    if (@_bad_arguments) {
	my $msg = "Invalid arguments passed to buildInternalNetwork:\n" . join("", map { "\t$_\n" } @_bad_arguments);
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
							       method_name => 'buildInternalNetwork');
    }

    my $ctx = $KBaseNetworkServer::CallContext;
    my($network);
    #BEGIN buildInternalNetwork
    #END buildInternalNetwork
    my @_bad_returns;
    (ref($network) eq 'HASH') or push(@_bad_returns, "Invalid type for return variable \"network\" (value was \"$network\")");
    if (@_bad_returns) {
	my $msg = "Invalid returns passed to buildInternalNetwork:\n" . join("", map { "\t$_\n" } @_bad_returns);
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
							       method_name => 'buildInternalNetwork');
    }
    return($network);
}




=head2 version 

  $return = $obj->version()

=over 4

=item Parameter and return types

=begin html

<pre>
$return is a string
</pre>

=end html

=begin text

$return is a string

=end text

=item Description

Return the module version. This is a Semantic Versioning number.

=back

=cut

sub version {
    return $VERSION;
}

=head1 TYPES



=head2 NetworkType

=over 4



=item Definition

=begin html

<pre>
a string
</pre>

=end html

=begin text

a string

=end text

=back



=head2 NodeType

=over 4



=item Definition

=begin html

<pre>
a string
</pre>

=end html

=begin text

a string

=end text

=back



=head2 DatasetSource

=over 4



=item Definition

=begin html

<pre>
a string
</pre>

=end html

=begin text

a string

=end text

=back



=head2 Taxon

=over 4



=item Definition

=begin html

<pre>
a string
</pre>

=end html

=begin text

a string

=end text

=back



=head2 Type

=over 4



=item Definition

=begin html

<pre>
a string
</pre>

=end html

=begin text

a string

=end text

=back



=head2 Value

=over 4



=item Definition

=begin html

<pre>
a string
</pre>

=end html

=begin text

a string

=end text

=back



=head2 Parameter

=over 4



=item Definition

=begin html

<pre>
a reference to a hash where the following keys are defined:
type has a value which is a Type
value has a value which is a Value

</pre>

=end html

=begin text

a reference to a hash where the following keys are defined:
type has a value which is a Type
value has a value which is a Value


=end text

=back



=head2 ParameterList

=over 4



=item Definition

=begin html

<pre>
a reference to a list where each element is a Parameter
</pre>

=end html

=begin text

a reference to a list where each element is a Parameter

=end text

=back



=head2 Dataset

=over 4



=item Definition

=begin html

<pre>
a reference to a hash where the following keys are defined:
id has a value which is a string
name has a value which is a string
description has a value which is a string
networkType has a value which is a NetworkType
datasetSource has a value which is a DatasetSource
taxons has a value which is a reference to a list where each element is a Taxon
properties has a value which is a reference to a hash where the key is a string and the value is a string

</pre>

=end html

=begin text

a reference to a hash where the following keys are defined:
id has a value which is a string
name has a value which is a string
description has a value which is a string
networkType has a value which is a NetworkType
datasetSource has a value which is a DatasetSource
taxons has a value which is a reference to a list where each element is a Taxon
properties has a value which is a reference to a hash where the key is a string and the value is a string


=end text

=back



=head2 KBaseEntity

=over 4



=item Definition

=begin html

<pre>
a reference to a hash where the following keys are defined:
id has a value which is a string

</pre>

=end html

=begin text

a reference to a hash where the following keys are defined:
id has a value which is a string


=end text

=back



=head2 Node

=over 4



=item Definition

=begin html

<pre>
a reference to a hash where the following keys are defined:
id has a value which is a string
name has a value which is a string
entity has a value which is a KBaseEntity
type has a value which is a NodeType
properties has a value which is a reference to a hash where the key is a string and the value is a string
userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string

</pre>

=end html

=begin text

a reference to a hash where the following keys are defined:
id has a value which is a string
name has a value which is a string
entity has a value which is a KBaseEntity
type has a value which is a NodeType
properties has a value which is a reference to a hash where the key is a string and the value is a string
userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string


=end text

=back



=head2 Edge

=over 4



=item Definition

=begin html

<pre>
a reference to a hash where the following keys are defined:
id has a value which is a string
name has a value which is a string
nodeId1 has a value which is a string
nodeId2 has a value which is a string
confidence has a value which is a float
strength has a value which is a float
datasetId has a value which is a string
properties has a value which is a reference to a hash where the key is a string and the value is a string
userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string

</pre>

=end html

=begin text

a reference to a hash where the following keys are defined:
id has a value which is a string
name has a value which is a string
nodeId1 has a value which is a string
nodeId2 has a value which is a string
confidence has a value which is a float
strength has a value which is a float
datasetId has a value which is a string
properties has a value which is a reference to a hash where the key is a string and the value is a string
userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string


=end text

=back



=head2 Network

=over 4



=item Definition

=begin html

<pre>
a reference to a hash where the following keys are defined:
id has a value which is a string
name has a value which is a string
edges has a value which is a reference to a list where each element is an Edge
nodes has a value which is a reference to a list where each element is a Node
datasets has a value which is a reference to a list where each element is a Dataset
properties has a value which is a reference to a hash where the key is a string and the value is a string
userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string

</pre>

=end html

=begin text

a reference to a hash where the following keys are defined:
id has a value which is a string
name has a value which is a string
edges has a value which is a reference to a list where each element is an Edge
nodes has a value which is a reference to a list where each element is a Node
datasets has a value which is a reference to a list where each element is a Dataset
properties has a value which is a reference to a hash where the key is a string and the value is a string
userAnnotations has a value which is a reference to a hash where the key is a string and the value is a string


=end text

=back



=head2 DatasetList

=over 4



=item Definition

=begin html

<pre>
a reference to a list where each element is a Dataset
</pre>

=end html

=begin text

a reference to a list where each element is a Dataset

=end text

=back



=cut

1;
