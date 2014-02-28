package Bio::KBase::KBaseNetworksService::Client;

use JSON::RPC::Client;
use strict;
use Data::Dumper;
use URI;
use Bio::KBase::Exceptions;

# Client version should match Impl version
# This is a Semantic Version number,
# http://semver.org
our $VERSION = "0.1.0";

=head1 NAME

Bio::KBase::KBaseNetworksService::Client

=head1 DESCRIPTION


Module KBaseNetworks version 2.0
This module provides access to various types of network-related datasets across all domains of  in a unified format.

KBaseNetworks are composed of Nodes and Edges. Nodes represent entities from the datasets (e.g., genes, proteins,
biclusters, subystems, etc.), and edges represent relationships (e.g., protein-protein interactions,
gene-subsystem membership, etc.). Networks can contain Nodes and Edges from multiple datasets.

All methods in this module can be classified into two types: 
i. getting general information about datasets and network types currently available via Networks API
   For example: all_DatasetSources(), allnetwork_types(), datasetSource2Datasets()
ii. building various types of Network objects
   For example: buildFirstNeighborNetwork(), buildInternalNetwork()


=cut

sub new
{
    my($class, $url, @args) = @_;
    

    my $self = {
	client => Bio::KBase::KBaseNetworksService::Client::RpcClient->new,
	url => $url,
    };


    my $ua = $self->{client}->ua;	 
    my $timeout = $ENV{CDMI_TIMEOUT} || (30 * 60);	 
    $ua->timeout($timeout);
    bless $self, $class;
    #    $self->_validate_version();
    return $self;
}




=head2 all_datasets

  $datasets = $obj->all_datasets()

=over 4

=item Parameter and return types

=begin html

<pre>
$datasets is a reference to a list where each element is a KBaseNetworks.Dataset
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	network_type has a value which is a KBaseNetworks.network_type
	source_ref has a value which is a KBaseNetworks.dataset_source_ref
	taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
network_type is a string
dataset_source_ref is a string
taxon is a string

</pre>

=end html

=begin text

$datasets is a reference to a list where each element is a KBaseNetworks.Dataset
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	network_type has a value which is a KBaseNetworks.network_type
	source_ref has a value which is a KBaseNetworks.dataset_source_ref
	taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
network_type is a string
dataset_source_ref is a string
taxon is a string


=end text

=item Description

Returns a list of all datasets that can be used to create a network

=back

=cut

sub all_datasets
{
    my($self, @args) = @_;

# Authentication: none

    if ((my $n = @args) != 0)
    {
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error =>
							       "Invalid argument count for function all_datasets (received $n, expecting 0)");
    }

    my $result = $self->{client}->call($self->{url}, {
	method => "KBaseNetworks.all_datasets",
	params => \@args,
    });
    if ($result) {
	if ($result->is_error) {
	    Bio::KBase::Exceptions::JSONRPC->throw(error => $result->error_message,
					       code => $result->content->{error}->{code},
					       method_name => 'all_datasets',
					       data => $result->content->{error}->{error} # JSON::RPC::ReturnObject only supports JSONRPC 1.1 or 1.O
					      );
	} else {
	    return wantarray ? @{$result->result} : $result->result->[0];
	}
    } else {
        Bio::KBase::Exceptions::HTTP->throw(error => "Error invoking method all_datasets",
					    status_line => $self->{client}->status_line,
					    method_name => 'all_datasets',
				       );
    }
}



=head2 all_dataset_sources

  $datasetSources = $obj->all_dataset_sources()

=over 4

=item Parameter and return types

=begin html

<pre>
$datasetSources is a reference to a list where each element is a KBaseNetworks.DatasetSource
DatasetSource is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	reference has a value which is a KBaseNetworks.dataset_source_ref
	description has a value which is a string
	resource_url has a value which is a string
dataset_source_ref is a string

</pre>

=end html

=begin text

$datasetSources is a reference to a list where each element is a KBaseNetworks.DatasetSource
DatasetSource is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	reference has a value which is a KBaseNetworks.dataset_source_ref
	description has a value which is a string
	resource_url has a value which is a string
dataset_source_ref is a string


=end text

=item Description

Returns a list of all dataset sources available in  via Networks API

=back

=cut

sub all_dataset_sources
{
    my($self, @args) = @_;

# Authentication: none

    if ((my $n = @args) != 0)
    {
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error =>
							       "Invalid argument count for function all_dataset_sources (received $n, expecting 0)");
    }

    my $result = $self->{client}->call($self->{url}, {
	method => "KBaseNetworks.all_dataset_sources",
	params => \@args,
    });
    if ($result) {
	if ($result->is_error) {
	    Bio::KBase::Exceptions::JSONRPC->throw(error => $result->error_message,
					       code => $result->content->{error}->{code},
					       method_name => 'all_dataset_sources',
					       data => $result->content->{error}->{error} # JSON::RPC::ReturnObject only supports JSONRPC 1.1 or 1.O
					      );
	} else {
	    return wantarray ? @{$result->result} : $result->result->[0];
	}
    } else {
        Bio::KBase::Exceptions::HTTP->throw(error => "Error invoking method all_dataset_sources",
					    status_line => $self->{client}->status_line,
					    method_name => 'all_dataset_sources',
				       );
    }
}



=head2 all_network_types

  $networkTypes = $obj->all_network_types()

=over 4

=item Parameter and return types

=begin html

<pre>
$networkTypes is a reference to a list where each element is a KBaseNetworks.network_type
network_type is a string

</pre>

=end html

=begin text

$networkTypes is a reference to a list where each element is a KBaseNetworks.network_type
network_type is a string


=end text

=item Description

Returns a list of all types of networks that can be created

=back

=cut

sub all_network_types
{
    my($self, @args) = @_;

# Authentication: none

    if ((my $n = @args) != 0)
    {
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error =>
							       "Invalid argument count for function all_network_types (received $n, expecting 0)");
    }

    my $result = $self->{client}->call($self->{url}, {
	method => "KBaseNetworks.all_network_types",
	params => \@args,
    });
    if ($result) {
	if ($result->is_error) {
	    Bio::KBase::Exceptions::JSONRPC->throw(error => $result->error_message,
					       code => $result->content->{error}->{code},
					       method_name => 'all_network_types',
					       data => $result->content->{error}->{error} # JSON::RPC::ReturnObject only supports JSONRPC 1.1 or 1.O
					      );
	} else {
	    return wantarray ? @{$result->result} : $result->result->[0];
	}
    } else {
        Bio::KBase::Exceptions::HTTP->throw(error => "Error invoking method all_network_types",
					    status_line => $self->{client}->status_line,
					    method_name => 'all_network_types',
				       );
    }
}



=head2 dataset_source2datasets

  $datasets = $obj->dataset_source2datasets($source_ref)

=over 4

=item Parameter and return types

=begin html

<pre>
$source_ref is a KBaseNetworks.dataset_source_ref
$datasets is a reference to a list where each element is a KBaseNetworks.Dataset
dataset_source_ref is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	network_type has a value which is a KBaseNetworks.network_type
	source_ref has a value which is a KBaseNetworks.dataset_source_ref
	taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
network_type is a string
taxon is a string

</pre>

=end html

=begin text

$source_ref is a KBaseNetworks.dataset_source_ref
$datasets is a reference to a list where each element is a KBaseNetworks.Dataset
dataset_source_ref is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	network_type has a value which is a KBaseNetworks.network_type
	source_ref has a value which is a KBaseNetworks.dataset_source_ref
	taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
network_type is a string
taxon is a string


=end text

=item Description

Returns a list of all datasets from a given dataset source                   
           dataset_source_ref datasetSourceRef - A reference to a dataset source

=back

=cut

sub dataset_source2datasets
{
    my($self, @args) = @_;

# Authentication: none

    if ((my $n = @args) != 1)
    {
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error =>
							       "Invalid argument count for function dataset_source2datasets (received $n, expecting 1)");
    }
    {
	my($source_ref) = @args;

	my @_bad_arguments;
        (!ref($source_ref)) or push(@_bad_arguments, "Invalid type for argument 1 \"source_ref\" (value was \"$source_ref\")");
        if (@_bad_arguments) {
	    my $msg = "Invalid arguments passed to dataset_source2datasets:\n" . join("", map { "\t$_\n" } @_bad_arguments);
	    Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
								   method_name => 'dataset_source2datasets');
	}
    }

    my $result = $self->{client}->call($self->{url}, {
	method => "KBaseNetworks.dataset_source2datasets",
	params => \@args,
    });
    if ($result) {
	if ($result->is_error) {
	    Bio::KBase::Exceptions::JSONRPC->throw(error => $result->error_message,
					       code => $result->content->{error}->{code},
					       method_name => 'dataset_source2datasets',
					       data => $result->content->{error}->{error} # JSON::RPC::ReturnObject only supports JSONRPC 1.1 or 1.O
					      );
	} else {
	    return wantarray ? @{$result->result} : $result->result->[0];
	}
    } else {
        Bio::KBase::Exceptions::HTTP->throw(error => "Error invoking method dataset_source2datasets",
					    status_line => $self->{client}->status_line,
					    method_name => 'dataset_source2datasets',
				       );
    }
}



=head2 taxon2datasets

  $datasets = $obj->taxon2datasets($taxid)

=over 4

=item Parameter and return types

=begin html

<pre>
$taxid is a KBaseNetworks.taxon
$datasets is a reference to a list where each element is a KBaseNetworks.Dataset
taxon is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	network_type has a value which is a KBaseNetworks.network_type
	source_ref has a value which is a KBaseNetworks.dataset_source_ref
	taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
network_type is a string
dataset_source_ref is a string

</pre>

=end html

=begin text

$taxid is a KBaseNetworks.taxon
$datasets is a reference to a list where each element is a KBaseNetworks.Dataset
taxon is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	network_type has a value which is a KBaseNetworks.network_type
	source_ref has a value which is a KBaseNetworks.dataset_source_ref
	taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
network_type is a string
dataset_source_ref is a string


=end text

=item Description

Returns a list of all datasets that can be used to build a network for a particular genome represented by NCBI taxonomy id. 
       taxon taxon - NCBI taxonomy id

=back

=cut

sub taxon2datasets
{
    my($self, @args) = @_;

# Authentication: none

    if ((my $n = @args) != 1)
    {
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error =>
							       "Invalid argument count for function taxon2datasets (received $n, expecting 1)");
    }
    {
	my($taxid) = @args;

	my @_bad_arguments;
        (!ref($taxid)) or push(@_bad_arguments, "Invalid type for argument 1 \"taxid\" (value was \"$taxid\")");
        if (@_bad_arguments) {
	    my $msg = "Invalid arguments passed to taxon2datasets:\n" . join("", map { "\t$_\n" } @_bad_arguments);
	    Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
								   method_name => 'taxon2datasets');
	}
    }

    my $result = $self->{client}->call($self->{url}, {
	method => "KBaseNetworks.taxon2datasets",
	params => \@args,
    });
    if ($result) {
	if ($result->is_error) {
	    Bio::KBase::Exceptions::JSONRPC->throw(error => $result->error_message,
					       code => $result->content->{error}->{code},
					       method_name => 'taxon2datasets',
					       data => $result->content->{error}->{error} # JSON::RPC::ReturnObject only supports JSONRPC 1.1 or 1.O
					      );
	} else {
	    return wantarray ? @{$result->result} : $result->result->[0];
	}
    } else {
        Bio::KBase::Exceptions::HTTP->throw(error => "Error invoking method taxon2datasets",
					    status_line => $self->{client}->status_line,
					    method_name => 'taxon2datasets',
				       );
    }
}



=head2 network_type2datasets

  $datasets = $obj->network_type2datasets($net_type)

=over 4

=item Parameter and return types

=begin html

<pre>
$net_type is a KBaseNetworks.network_type
$datasets is a reference to a list where each element is a KBaseNetworks.Dataset
network_type is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	network_type has a value which is a KBaseNetworks.network_type
	source_ref has a value which is a KBaseNetworks.dataset_source_ref
	taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
dataset_source_ref is a string
taxon is a string

</pre>

=end html

=begin text

$net_type is a KBaseNetworks.network_type
$datasets is a reference to a list where each element is a KBaseNetworks.Dataset
network_type is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	network_type has a value which is a KBaseNetworks.network_type
	source_ref has a value which is a KBaseNetworks.dataset_source_ref
	taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
dataset_source_ref is a string
taxon is a string


=end text

=item Description

Returns a list of all datasets that can be used to build a network of a given type.
          network_type networkType - The type of network

=back

=cut

sub network_type2datasets
{
    my($self, @args) = @_;

# Authentication: none

    if ((my $n = @args) != 1)
    {
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error =>
							       "Invalid argument count for function network_type2datasets (received $n, expecting 1)");
    }
    {
	my($net_type) = @args;

	my @_bad_arguments;
        (!ref($net_type)) or push(@_bad_arguments, "Invalid type for argument 1 \"net_type\" (value was \"$net_type\")");
        if (@_bad_arguments) {
	    my $msg = "Invalid arguments passed to network_type2datasets:\n" . join("", map { "\t$_\n" } @_bad_arguments);
	    Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
								   method_name => 'network_type2datasets');
	}
    }

    my $result = $self->{client}->call($self->{url}, {
	method => "KBaseNetworks.network_type2datasets",
	params => \@args,
    });
    if ($result) {
	if ($result->is_error) {
	    Bio::KBase::Exceptions::JSONRPC->throw(error => $result->error_message,
					       code => $result->content->{error}->{code},
					       method_name => 'network_type2datasets',
					       data => $result->content->{error}->{error} # JSON::RPC::ReturnObject only supports JSONRPC 1.1 or 1.O
					      );
	} else {
	    return wantarray ? @{$result->result} : $result->result->[0];
	}
    } else {
        Bio::KBase::Exceptions::HTTP->throw(error => "Error invoking method network_type2datasets",
					    status_line => $self->{client}->status_line,
					    method_name => 'network_type2datasets',
				       );
    }
}



=head2 entity2datasets

  $datasets = $obj->entity2datasets($entity_id)

=over 4

=item Parameter and return types

=begin html

<pre>
$entity_id is a string
$datasets is a reference to a list where each element is a KBaseNetworks.Dataset
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	network_type has a value which is a KBaseNetworks.network_type
	source_ref has a value which is a KBaseNetworks.dataset_source_ref
	taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
network_type is a string
dataset_source_ref is a string
taxon is a string

</pre>

=end html

=begin text

$entity_id is a string
$datasets is a reference to a list where each element is a KBaseNetworks.Dataset
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	network_type has a value which is a KBaseNetworks.network_type
	source_ref has a value which is a KBaseNetworks.dataset_source_ref
	taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
network_type is a string
dataset_source_ref is a string
taxon is a string


=end text

=item Description

Returns a list of all datasets that have at least one interaction for a given  entity

=back

=cut

sub entity2datasets
{
    my($self, @args) = @_;

# Authentication: none

    if ((my $n = @args) != 1)
    {
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error =>
							       "Invalid argument count for function entity2datasets (received $n, expecting 1)");
    }
    {
	my($entity_id) = @args;

	my @_bad_arguments;
        (!ref($entity_id)) or push(@_bad_arguments, "Invalid type for argument 1 \"entity_id\" (value was \"$entity_id\")");
        if (@_bad_arguments) {
	    my $msg = "Invalid arguments passed to entity2datasets:\n" . join("", map { "\t$_\n" } @_bad_arguments);
	    Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
								   method_name => 'entity2datasets');
	}
    }

    my $result = $self->{client}->call($self->{url}, {
	method => "KBaseNetworks.entity2datasets",
	params => \@args,
    });
    if ($result) {
	if ($result->is_error) {
	    Bio::KBase::Exceptions::JSONRPC->throw(error => $result->error_message,
					       code => $result->content->{error}->{code},
					       method_name => 'entity2datasets',
					       data => $result->content->{error}->{error} # JSON::RPC::ReturnObject only supports JSONRPC 1.1 or 1.O
					      );
	} else {
	    return wantarray ? @{$result->result} : $result->result->[0];
	}
    } else {
        Bio::KBase::Exceptions::HTTP->throw(error => "Error invoking method entity2datasets",
					    status_line => $self->{client}->status_line,
					    method_name => 'entity2datasets',
				       );
    }
}



=head2 build_first_neighbor_network

  $network = $obj->build_first_neighbor_network($dataset_ids, $entity_ids, $edge_types)

=over 4

=item Parameter and return types

=begin html

<pre>
$dataset_ids is a reference to a list where each element is a string
$entity_ids is a reference to a list where each element is a string
$edge_types is a reference to a list where each element is a KBaseNetworks.edge_type
$network is a KBaseNetworks.Network
edge_type is a string
Network is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	edges has a value which is a reference to a list where each element is a KBaseNetworks.Edge
	nodes has a value which is a reference to a list where each element is a KBaseNetworks.Node
	datasets has a value which is a reference to a list where each element is a KBaseNetworks.Dataset
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
Edge is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	node_id1 has a value which is a string
	node_id2 has a value which is a string
	directed has a value which is a KBaseNetworks.boolean
	confidence has a value which is a float
	strength has a value which is a float
	dataset_id has a value which is a string
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
boolean is a string
Node is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	entity_id has a value which is a string
	type has a value which is a KBaseNetworks.node_type
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
node_type is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	network_type has a value which is a KBaseNetworks.network_type
	source_ref has a value which is a KBaseNetworks.dataset_source_ref
	taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
network_type is a string
dataset_source_ref is a string
taxon is a string

</pre>

=end html

=begin text

$dataset_ids is a reference to a list where each element is a string
$entity_ids is a reference to a list where each element is a string
$edge_types is a reference to a list where each element is a KBaseNetworks.edge_type
$network is a KBaseNetworks.Network
edge_type is a string
Network is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	edges has a value which is a reference to a list where each element is a KBaseNetworks.Edge
	nodes has a value which is a reference to a list where each element is a KBaseNetworks.Node
	datasets has a value which is a reference to a list where each element is a KBaseNetworks.Dataset
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
Edge is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	node_id1 has a value which is a string
	node_id2 has a value which is a string
	directed has a value which is a KBaseNetworks.boolean
	confidence has a value which is a float
	strength has a value which is a float
	dataset_id has a value which is a string
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
boolean is a string
Node is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	entity_id has a value which is a string
	type has a value which is a KBaseNetworks.node_type
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
node_type is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	network_type has a value which is a KBaseNetworks.network_type
	source_ref has a value which is a KBaseNetworks.dataset_source_ref
	taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
network_type is a string
dataset_source_ref is a string
taxon is a string


=end text

=item Description

Returns a "first-neighbor" network constructed based on a given list of datasets. A first-neighbor network contains 
"source" nodes and all other nodes that have at least one interaction with the "source" nodes. 
Only interactions of given types are considered.    
          list<string> dataset_ids - List of dataset identifiers to be used for building a network
       list<string> entity_ids - List of entity identifiers to be used as source nodes
          list<edge_type> edge_types - List of possible edge types to be considered for building a network

=back

=cut

sub build_first_neighbor_network
{
    my($self, @args) = @_;

# Authentication: none

    if ((my $n = @args) != 3)
    {
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error =>
							       "Invalid argument count for function build_first_neighbor_network (received $n, expecting 3)");
    }
    {
	my($dataset_ids, $entity_ids, $edge_types) = @args;

	my @_bad_arguments;
        (ref($dataset_ids) eq 'ARRAY') or push(@_bad_arguments, "Invalid type for argument 1 \"dataset_ids\" (value was \"$dataset_ids\")");
        (ref($entity_ids) eq 'ARRAY') or push(@_bad_arguments, "Invalid type for argument 2 \"entity_ids\" (value was \"$entity_ids\")");
        (ref($edge_types) eq 'ARRAY') or push(@_bad_arguments, "Invalid type for argument 3 \"edge_types\" (value was \"$edge_types\")");
        if (@_bad_arguments) {
	    my $msg = "Invalid arguments passed to build_first_neighbor_network:\n" . join("", map { "\t$_\n" } @_bad_arguments);
	    Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
								   method_name => 'build_first_neighbor_network');
	}
    }

    my $result = $self->{client}->call($self->{url}, {
	method => "KBaseNetworks.build_first_neighbor_network",
	params => \@args,
    });
    if ($result) {
	if ($result->is_error) {
	    Bio::KBase::Exceptions::JSONRPC->throw(error => $result->error_message,
					       code => $result->content->{error}->{code},
					       method_name => 'build_first_neighbor_network',
					       data => $result->content->{error}->{error} # JSON::RPC::ReturnObject only supports JSONRPC 1.1 or 1.O
					      );
	} else {
	    return wantarray ? @{$result->result} : $result->result->[0];
	}
    } else {
        Bio::KBase::Exceptions::HTTP->throw(error => "Error invoking method build_first_neighbor_network",
					    status_line => $self->{client}->status_line,
					    method_name => 'build_first_neighbor_network',
				       );
    }
}



=head2 build_first_neighbor_network_limted_by_strength

  $network = $obj->build_first_neighbor_network_limted_by_strength($dataset_ids, $entity_ids, $edge_types, $cutOff)

=over 4

=item Parameter and return types

=begin html

<pre>
$dataset_ids is a reference to a list where each element is a string
$entity_ids is a reference to a list where each element is a string
$edge_types is a reference to a list where each element is a KBaseNetworks.edge_type
$cutOff is a float
$network is a KBaseNetworks.Network
edge_type is a string
Network is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	edges has a value which is a reference to a list where each element is a KBaseNetworks.Edge
	nodes has a value which is a reference to a list where each element is a KBaseNetworks.Node
	datasets has a value which is a reference to a list where each element is a KBaseNetworks.Dataset
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
Edge is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	node_id1 has a value which is a string
	node_id2 has a value which is a string
	directed has a value which is a KBaseNetworks.boolean
	confidence has a value which is a float
	strength has a value which is a float
	dataset_id has a value which is a string
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
boolean is a string
Node is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	entity_id has a value which is a string
	type has a value which is a KBaseNetworks.node_type
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
node_type is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	network_type has a value which is a KBaseNetworks.network_type
	source_ref has a value which is a KBaseNetworks.dataset_source_ref
	taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
network_type is a string
dataset_source_ref is a string
taxon is a string

</pre>

=end html

=begin text

$dataset_ids is a reference to a list where each element is a string
$entity_ids is a reference to a list where each element is a string
$edge_types is a reference to a list where each element is a KBaseNetworks.edge_type
$cutOff is a float
$network is a KBaseNetworks.Network
edge_type is a string
Network is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	edges has a value which is a reference to a list where each element is a KBaseNetworks.Edge
	nodes has a value which is a reference to a list where each element is a KBaseNetworks.Node
	datasets has a value which is a reference to a list where each element is a KBaseNetworks.Dataset
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
Edge is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	node_id1 has a value which is a string
	node_id2 has a value which is a string
	directed has a value which is a KBaseNetworks.boolean
	confidence has a value which is a float
	strength has a value which is a float
	dataset_id has a value which is a string
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
boolean is a string
Node is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	entity_id has a value which is a string
	type has a value which is a KBaseNetworks.node_type
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
node_type is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	network_type has a value which is a KBaseNetworks.network_type
	source_ref has a value which is a KBaseNetworks.dataset_source_ref
	taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
network_type is a string
dataset_source_ref is a string
taxon is a string


=end text

=item Description

Returns a "first-neighbor" network constructed basing on a given list of datasets. First-neighbor network contains 
"source" nodes and all other nodes that have at least one interaction with the "source" nodes. 
Only interactions of given types are considered. Additional cutOff parameter allows setting a threshold
on the strength of edges to be considered.   
          list<string> dataset_ids - List of dataset identifiers to be used for building a network
       list<string> entity_ids - List of entity identifiers to be used as source nodes
          list<edge_type> edge_types - List of possible edge types to be considered for building a network
          float cutOff - The threshold on the strength of edges to be considered for building a network

=back

=cut

sub build_first_neighbor_network_limted_by_strength
{
    my($self, @args) = @_;

# Authentication: none

    if ((my $n = @args) != 4)
    {
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error =>
							       "Invalid argument count for function build_first_neighbor_network_limted_by_strength (received $n, expecting 4)");
    }
    {
	my($dataset_ids, $entity_ids, $edge_types, $cutOff) = @args;

	my @_bad_arguments;
        (ref($dataset_ids) eq 'ARRAY') or push(@_bad_arguments, "Invalid type for argument 1 \"dataset_ids\" (value was \"$dataset_ids\")");
        (ref($entity_ids) eq 'ARRAY') or push(@_bad_arguments, "Invalid type for argument 2 \"entity_ids\" (value was \"$entity_ids\")");
        (ref($edge_types) eq 'ARRAY') or push(@_bad_arguments, "Invalid type for argument 3 \"edge_types\" (value was \"$edge_types\")");
        (!ref($cutOff)) or push(@_bad_arguments, "Invalid type for argument 4 \"cutOff\" (value was \"$cutOff\")");
        if (@_bad_arguments) {
	    my $msg = "Invalid arguments passed to build_first_neighbor_network_limted_by_strength:\n" . join("", map { "\t$_\n" } @_bad_arguments);
	    Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
								   method_name => 'build_first_neighbor_network_limted_by_strength');
	}
    }

    my $result = $self->{client}->call($self->{url}, {
	method => "KBaseNetworks.build_first_neighbor_network_limted_by_strength",
	params => \@args,
    });
    if ($result) {
	if ($result->is_error) {
	    Bio::KBase::Exceptions::JSONRPC->throw(error => $result->error_message,
					       code => $result->content->{error}->{code},
					       method_name => 'build_first_neighbor_network_limted_by_strength',
					       data => $result->content->{error}->{error} # JSON::RPC::ReturnObject only supports JSONRPC 1.1 or 1.O
					      );
	} else {
	    return wantarray ? @{$result->result} : $result->result->[0];
	}
    } else {
        Bio::KBase::Exceptions::HTTP->throw(error => "Error invoking method build_first_neighbor_network_limted_by_strength",
					    status_line => $self->{client}->status_line,
					    method_name => 'build_first_neighbor_network_limted_by_strength',
				       );
    }
}



=head2 build_internal_network

  $network = $obj->build_internal_network($dataset_ids, $gene_ids, $edge_types)

=over 4

=item Parameter and return types

=begin html

<pre>
$dataset_ids is a reference to a list where each element is a string
$gene_ids is a reference to a list where each element is a string
$edge_types is a reference to a list where each element is a KBaseNetworks.edge_type
$network is a KBaseNetworks.Network
edge_type is a string
Network is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	edges has a value which is a reference to a list where each element is a KBaseNetworks.Edge
	nodes has a value which is a reference to a list where each element is a KBaseNetworks.Node
	datasets has a value which is a reference to a list where each element is a KBaseNetworks.Dataset
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
Edge is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	node_id1 has a value which is a string
	node_id2 has a value which is a string
	directed has a value which is a KBaseNetworks.boolean
	confidence has a value which is a float
	strength has a value which is a float
	dataset_id has a value which is a string
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
boolean is a string
Node is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	entity_id has a value which is a string
	type has a value which is a KBaseNetworks.node_type
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
node_type is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	network_type has a value which is a KBaseNetworks.network_type
	source_ref has a value which is a KBaseNetworks.dataset_source_ref
	taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
network_type is a string
dataset_source_ref is a string
taxon is a string

</pre>

=end html

=begin text

$dataset_ids is a reference to a list where each element is a string
$gene_ids is a reference to a list where each element is a string
$edge_types is a reference to a list where each element is a KBaseNetworks.edge_type
$network is a KBaseNetworks.Network
edge_type is a string
Network is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	edges has a value which is a reference to a list where each element is a KBaseNetworks.Edge
	nodes has a value which is a reference to a list where each element is a KBaseNetworks.Node
	datasets has a value which is a reference to a list where each element is a KBaseNetworks.Dataset
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
Edge is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	node_id1 has a value which is a string
	node_id2 has a value which is a string
	directed has a value which is a KBaseNetworks.boolean
	confidence has a value which is a float
	strength has a value which is a float
	dataset_id has a value which is a string
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
boolean is a string
Node is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	entity_id has a value which is a string
	type has a value which is a KBaseNetworks.node_type
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
node_type is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	network_type has a value which is a KBaseNetworks.network_type
	source_ref has a value which is a KBaseNetworks.dataset_source_ref
	taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
network_type is a string
dataset_source_ref is a string
taxon is a string


=end text

=item Description

Returns an "internal" network constructed based on a given list of datasets. 
Internal network contains only the nodes defined by the gene_ids parameter, 
and edges representing interactions between these nodes.  Only interactions of given types are considered.    
          list<string> dataset_ids - List of dataset identifiers to be used for building a network
       list<string> gene_ids - Identifiers of genes of interest for building a network         
          list<edge_type> edge_types - List of possible edge types to be considered for building a network

=back

=cut

sub build_internal_network
{
    my($self, @args) = @_;

# Authentication: none

    if ((my $n = @args) != 3)
    {
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error =>
							       "Invalid argument count for function build_internal_network (received $n, expecting 3)");
    }
    {
	my($dataset_ids, $gene_ids, $edge_types) = @args;

	my @_bad_arguments;
        (ref($dataset_ids) eq 'ARRAY') or push(@_bad_arguments, "Invalid type for argument 1 \"dataset_ids\" (value was \"$dataset_ids\")");
        (ref($gene_ids) eq 'ARRAY') or push(@_bad_arguments, "Invalid type for argument 2 \"gene_ids\" (value was \"$gene_ids\")");
        (ref($edge_types) eq 'ARRAY') or push(@_bad_arguments, "Invalid type for argument 3 \"edge_types\" (value was \"$edge_types\")");
        if (@_bad_arguments) {
	    my $msg = "Invalid arguments passed to build_internal_network:\n" . join("", map { "\t$_\n" } @_bad_arguments);
	    Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
								   method_name => 'build_internal_network');
	}
    }

    my $result = $self->{client}->call($self->{url}, {
	method => "KBaseNetworks.build_internal_network",
	params => \@args,
    });
    if ($result) {
	if ($result->is_error) {
	    Bio::KBase::Exceptions::JSONRPC->throw(error => $result->error_message,
					       code => $result->content->{error}->{code},
					       method_name => 'build_internal_network',
					       data => $result->content->{error}->{error} # JSON::RPC::ReturnObject only supports JSONRPC 1.1 or 1.O
					      );
	} else {
	    return wantarray ? @{$result->result} : $result->result->[0];
	}
    } else {
        Bio::KBase::Exceptions::HTTP->throw(error => "Error invoking method build_internal_network",
					    status_line => $self->{client}->status_line,
					    method_name => 'build_internal_network',
				       );
    }
}



=head2 build_internal_network_limited_by_strength

  $network = $obj->build_internal_network_limited_by_strength($dataset_ids, $gene_ids, $edge_types, $cutOff)

=over 4

=item Parameter and return types

=begin html

<pre>
$dataset_ids is a reference to a list where each element is a string
$gene_ids is a reference to a list where each element is a string
$edge_types is a reference to a list where each element is a KBaseNetworks.edge_type
$cutOff is a float
$network is a KBaseNetworks.Network
edge_type is a string
Network is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	edges has a value which is a reference to a list where each element is a KBaseNetworks.Edge
	nodes has a value which is a reference to a list where each element is a KBaseNetworks.Node
	datasets has a value which is a reference to a list where each element is a KBaseNetworks.Dataset
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
Edge is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	node_id1 has a value which is a string
	node_id2 has a value which is a string
	directed has a value which is a KBaseNetworks.boolean
	confidence has a value which is a float
	strength has a value which is a float
	dataset_id has a value which is a string
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
boolean is a string
Node is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	entity_id has a value which is a string
	type has a value which is a KBaseNetworks.node_type
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
node_type is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	network_type has a value which is a KBaseNetworks.network_type
	source_ref has a value which is a KBaseNetworks.dataset_source_ref
	taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
network_type is a string
dataset_source_ref is a string
taxon is a string

</pre>

=end html

=begin text

$dataset_ids is a reference to a list where each element is a string
$gene_ids is a reference to a list where each element is a string
$edge_types is a reference to a list where each element is a KBaseNetworks.edge_type
$cutOff is a float
$network is a KBaseNetworks.Network
edge_type is a string
Network is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	edges has a value which is a reference to a list where each element is a KBaseNetworks.Edge
	nodes has a value which is a reference to a list where each element is a KBaseNetworks.Node
	datasets has a value which is a reference to a list where each element is a KBaseNetworks.Dataset
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
Edge is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	node_id1 has a value which is a string
	node_id2 has a value which is a string
	directed has a value which is a KBaseNetworks.boolean
	confidence has a value which is a float
	strength has a value which is a float
	dataset_id has a value which is a string
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
boolean is a string
Node is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	entity_id has a value which is a string
	type has a value which is a KBaseNetworks.node_type
	properties has a value which is a reference to a hash where the key is a string and the value is a string
	user_annotations has a value which is a reference to a hash where the key is a string and the value is a string
node_type is a string
Dataset is a reference to a hash where the following keys are defined:
	id has a value which is a string
	name has a value which is a string
	description has a value which is a string
	network_type has a value which is a KBaseNetworks.network_type
	source_ref has a value which is a KBaseNetworks.dataset_source_ref
	taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
	properties has a value which is a reference to a hash where the key is a string and the value is a string
network_type is a string
dataset_source_ref is a string
taxon is a string


=end text

=item Description

Returns an "internal" network constructed based on a given list of datasets. 
Internal network contains the only nodes defined by the gene_ids parameter, 
and edges representing interactions between these nodes.  Only interactions of given types are considered. 
Additional cutOff parameter allows to set a threshold on the strength of edges to be considered.     
          list<string> dataset_ids - List of dataset identifiers to be used for building a network
       list<string> gene_ids - Identifiers of genes of interest for building a network         
          list<edge_type> edge_types - List of possible edge types to be considered for building a network
         float cutOff - The threshold on the strength of edges to be considered for building a network

=back

=cut

sub build_internal_network_limited_by_strength
{
    my($self, @args) = @_;

# Authentication: none

    if ((my $n = @args) != 4)
    {
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error =>
							       "Invalid argument count for function build_internal_network_limited_by_strength (received $n, expecting 4)");
    }
    {
	my($dataset_ids, $gene_ids, $edge_types, $cutOff) = @args;

	my @_bad_arguments;
        (ref($dataset_ids) eq 'ARRAY') or push(@_bad_arguments, "Invalid type for argument 1 \"dataset_ids\" (value was \"$dataset_ids\")");
        (ref($gene_ids) eq 'ARRAY') or push(@_bad_arguments, "Invalid type for argument 2 \"gene_ids\" (value was \"$gene_ids\")");
        (ref($edge_types) eq 'ARRAY') or push(@_bad_arguments, "Invalid type for argument 3 \"edge_types\" (value was \"$edge_types\")");
        (!ref($cutOff)) or push(@_bad_arguments, "Invalid type for argument 4 \"cutOff\" (value was \"$cutOff\")");
        if (@_bad_arguments) {
	    my $msg = "Invalid arguments passed to build_internal_network_limited_by_strength:\n" . join("", map { "\t$_\n" } @_bad_arguments);
	    Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
								   method_name => 'build_internal_network_limited_by_strength');
	}
    }

    my $result = $self->{client}->call($self->{url}, {
	method => "KBaseNetworks.build_internal_network_limited_by_strength",
	params => \@args,
    });
    if ($result) {
	if ($result->is_error) {
	    Bio::KBase::Exceptions::JSONRPC->throw(error => $result->error_message,
					       code => $result->content->{error}->{code},
					       method_name => 'build_internal_network_limited_by_strength',
					       data => $result->content->{error}->{error} # JSON::RPC::ReturnObject only supports JSONRPC 1.1 or 1.O
					      );
	} else {
	    return wantarray ? @{$result->result} : $result->result->[0];
	}
    } else {
        Bio::KBase::Exceptions::HTTP->throw(error => "Error invoking method build_internal_network_limited_by_strength",
					    status_line => $self->{client}->status_line,
					    method_name => 'build_internal_network_limited_by_strength',
				       );
    }
}



sub version {
    my ($self) = @_;
    my $result = $self->{client}->call($self->{url}, {
        method => "KBaseNetworks.version",
        params => [],
    });
    if ($result) {
        if ($result->is_error) {
            Bio::KBase::Exceptions::JSONRPC->throw(
                error => $result->error_message,
                code => $result->content->{code},
                method_name => 'build_internal_network_limited_by_strength',
            );
        } else {
            return wantarray ? @{$result->result} : $result->result->[0];
        }
    } else {
        Bio::KBase::Exceptions::HTTP->throw(
            error => "Error invoking method build_internal_network_limited_by_strength",
            status_line => $self->{client}->status_line,
            method_name => 'build_internal_network_limited_by_strength',
        );
    }
}

sub _validate_version {
    my ($self) = @_;
    my $svr_version = $self->version();
    my $client_version = $VERSION;
    my ($cMajor, $cMinor) = split(/\./, $client_version);
    my ($sMajor, $sMinor) = split(/\./, $svr_version);
    if ($sMajor != $cMajor) {
        Bio::KBase::Exceptions::ClientServerIncompatible->throw(
            error => "Major version numbers differ.",
            server_version => $svr_version,
            client_version => $client_version
        );
    }
    if ($sMinor < $cMinor) {
        Bio::KBase::Exceptions::ClientServerIncompatible->throw(
            error => "Client minor version greater than Server minor version.",
            server_version => $svr_version,
            client_version => $client_version
        );
    }
    if ($sMinor > $cMinor) {
        warn "New client version available for Bio::KBase::KBaseNetworksService::Client\n";
    }
    if ($sMajor == 0) {
        warn "Bio::KBase::KBaseNetworksService::Client version is $svr_version. API subject to change.\n";
    }
}

=head1 TYPES



=head2 boolean

=over 4



=item Description

A boolean. 0 = false, other = true.


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



=head2 dataset_source_ref

=over 4



=item Description

The name of a dataset that can be accessed as a source for creating a network


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



=head2 network_type

=over 4



=item Description

Type of network that can be created from a dataset


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



=head2 node_type

=over 4



=item Description

Type of node in a network


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



=head2 edge_type

=over 4



=item Description

Type of edge in a network


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



=head2 taxon

=over 4



=item Description

NCBI taxonomy id


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



=item Description

Provides detailed information about the source of a dataset.
string id - A unique  identifier of a dataset source
string name - A name of a dataset source
        dataset_source_ref reference - Reference to a dataset source
        string description - General description of a dataset source
        string resourceURL - URL of the public web resource hosting the data represented by this dataset source


=item Definition

=begin html

<pre>
a reference to a hash where the following keys are defined:
id has a value which is a string
name has a value which is a string
reference has a value which is a KBaseNetworks.dataset_source_ref
description has a value which is a string
resource_url has a value which is a string

</pre>

=end html

=begin text

a reference to a hash where the following keys are defined:
id has a value which is a string
name has a value which is a string
reference has a value which is a KBaseNetworks.dataset_source_ref
description has a value which is a string
resource_url has a value which is a string


=end text

=back



=head2 Dataset

=over 4



=item Description

Represents a particular dataset.
string id - A unique  identifier of a dataset 
        string name - The name of a dataset
        string description - Description of a dataset
        network_type networkType - Type of network that can be generated from a given dataset
dataset_source_ref sourceReference - Reference to a dataset source
list<taxon> taxons - A list of NCBI taxonomy ids of all organisms for which genomic features (genes, proteins, etc) are used in a given dataset 
        mapping<string,string> properties - Other properties


=item Definition

=begin html

<pre>
a reference to a hash where the following keys are defined:
id has a value which is a string
name has a value which is a string
description has a value which is a string
network_type has a value which is a KBaseNetworks.network_type
source_ref has a value which is a KBaseNetworks.dataset_source_ref
taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
properties has a value which is a reference to a hash where the key is a string and the value is a string

</pre>

=end html

=begin text

a reference to a hash where the following keys are defined:
id has a value which is a string
name has a value which is a string
description has a value which is a string
network_type has a value which is a KBaseNetworks.network_type
source_ref has a value which is a KBaseNetworks.dataset_source_ref
taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
properties has a value which is a reference to a hash where the key is a string and the value is a string


=end text

=back



=head2 Node

=over 4



=item Description

Represents a node in a network.
string id - A unique  identifier of a node 
                string name - String representation of a node. It should be a concise but informative representation that is easy for a person to read.
     string entity_id - The identifier of a  entity represented by a given node 
                node_type type - The type of a node
     mapping<string,string> properties - Other properties of a node
     mapping<string,string> user_annotations - User annotations of a node


=item Definition

=begin html

<pre>
a reference to a hash where the following keys are defined:
id has a value which is a string
name has a value which is a string
entity_id has a value which is a string
type has a value which is a KBaseNetworks.node_type
properties has a value which is a reference to a hash where the key is a string and the value is a string
user_annotations has a value which is a reference to a hash where the key is a string and the value is a string

</pre>

=end html

=begin text

a reference to a hash where the following keys are defined:
id has a value which is a string
name has a value which is a string
entity_id has a value which is a string
type has a value which is a KBaseNetworks.node_type
properties has a value which is a reference to a hash where the key is a string and the value is a string
user_annotations has a value which is a reference to a hash where the key is a string and the value is a string


=end text

=back



=head2 Edge

=over 4



=item Description

Represents an edge in a network.
string id - A unique  identifier of an edge 
     string name - String representation of an edge. It should be a concise but informative representation that is easy for a person to read.
     string node_id1 - Identifier of the first node (source node, if the edge is directed) connected by a given edge 
     string node_id2 - Identifier of the second node (target node, if the edge is directed) connected by a given edge
     boolean        directed - Specify whether the edge is directed or not. 1 if it is directed, 0 if it is not directed
     float confidence - Value from 0 to 1 representing a probability that the interaction represented by a given edge is a true interaction
     float strength - Value from 0 to 1 representing a strength of an interaction represented by a given edge
     string dataset_id - The identifier of a dataset that provided an interaction represented by a given edge
                mapping<string,string> properties - Other edge properties
     mapping<string,string> user_annotations - User annotations of an edge


=item Definition

=begin html

<pre>
a reference to a hash where the following keys are defined:
id has a value which is a string
name has a value which is a string
node_id1 has a value which is a string
node_id2 has a value which is a string
directed has a value which is a KBaseNetworks.boolean
confidence has a value which is a float
strength has a value which is a float
dataset_id has a value which is a string
properties has a value which is a reference to a hash where the key is a string and the value is a string
user_annotations has a value which is a reference to a hash where the key is a string and the value is a string

</pre>

=end html

=begin text

a reference to a hash where the following keys are defined:
id has a value which is a string
name has a value which is a string
node_id1 has a value which is a string
node_id2 has a value which is a string
directed has a value which is a KBaseNetworks.boolean
confidence has a value which is a float
strength has a value which is a float
dataset_id has a value which is a string
properties has a value which is a reference to a hash where the key is a string and the value is a string
user_annotations has a value which is a reference to a hash where the key is a string and the value is a string


=end text

=back



=head2 Network

=over 4



=item Description

Represents a network
string id - A unique  identifier of a network 
        string name - String representation of a network. It should be a concise but informative representation that is easy for a person to read.
list<Edge> edges - A list of all edges in a network
list<Node> nodes - A list of all nodes in a network
list<Dataset> datasets - A list of all datasets used to build a network
mapping<string,string> properties - Other properties of a network
mapping<string,string> user_annotations - User annotations of a network


=item Definition

=begin html

<pre>
a reference to a hash where the following keys are defined:
id has a value which is a string
name has a value which is a string
edges has a value which is a reference to a list where each element is a KBaseNetworks.Edge
nodes has a value which is a reference to a list where each element is a KBaseNetworks.Node
datasets has a value which is a reference to a list where each element is a KBaseNetworks.Dataset
properties has a value which is a reference to a hash where the key is a string and the value is a string
user_annotations has a value which is a reference to a hash where the key is a string and the value is a string

</pre>

=end html

=begin text

a reference to a hash where the following keys are defined:
id has a value which is a string
name has a value which is a string
edges has a value which is a reference to a list where each element is a KBaseNetworks.Edge
nodes has a value which is a reference to a list where each element is a KBaseNetworks.Node
datasets has a value which is a reference to a list where each element is a KBaseNetworks.Dataset
properties has a value which is a reference to a hash where the key is a string and the value is a string
user_annotations has a value which is a reference to a hash where the key is a string and the value is a string


=end text

=back



=head2 Interaction

=over 4



=item Description

Represents a single entity-entity interaction
string id - id of interaction
               string entity1_id - entity1 identifier
               string entity2_id - entity2 identifier
string type          - type of interaction
float strength          - strength of interaction
float confidence  - confidence of interaction

mapping<string,float> scores - various types of scores. 
        Known score types: 

                GENE_DISTANCE - distance between genes on a chromosome 
                CONSERVATION_SCORE - conservation, ranging from 0 (not conserved) to 1 (100% conserved)
                GO_SCORE - Smallest shared GO category, as a fraction of the genome, or missing if one of the genes is not characterized
                STRING_SCORE - STRING score
                COG_SIM        - whether the genes share (1) a COG category or not (0)
                EXPR_SIM - correlation of expression patterns
                SAME_OPERON - whether the pair is predicted to lie (1) in the same operon or not (0)
                SAME_OPERON_PROB - estimated probability that the pair is in the same operon. Values near 1 or 0 are confident predictions of being in the same operon or not, while values near 0.5 are low-confidence predictions.



               @optional id type strength confidence scores


=item Definition

=begin html

<pre>
a reference to a hash where the following keys are defined:
id has a value which is a string
entity1_id has a value which is a string
entity2_id has a value which is a string
type has a value which is a string
strength has a value which is a float
confidence has a value which is a float
scores has a value which is a reference to a hash where the key is a string and the value is a float

</pre>

=end html

=begin text

a reference to a hash where the following keys are defined:
id has a value which is a string
entity1_id has a value which is a string
entity2_id has a value which is a string
type has a value which is a string
strength has a value which is a float
confidence has a value which is a float
scores has a value which is a reference to a hash where the key is a string and the value is a float


=end text

=back



=head2 InteractionSet

=over 4



=item Description

Represents a set of interactions
string id - interaction set identifier
string name - interaction set name
                string type - interaction set type. If specified, all interactions are expected to be of the same type.
\                string description - interaction set description
                DatasetSource source - source
                list<taxon> taxons - taxons
              list<Interaction> interactions - list of interactions

               @optional description type taxons


=item Definition

=begin html

<pre>
a reference to a hash where the following keys are defined:
id has a value which is a string
name has a value which is a string
description has a value which is a string
type has a value which is a string
source has a value which is a KBaseNetworks.DatasetSource
taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
interactions has a value which is a reference to a list where each element is a KBaseNetworks.Interaction

</pre>

=end html

=begin text

a reference to a hash where the following keys are defined:
id has a value which is a string
name has a value which is a string
description has a value which is a string
type has a value which is a string
source has a value which is a KBaseNetworks.DatasetSource
taxons has a value which is a reference to a list where each element is a KBaseNetworks.taxon
interactions has a value which is a reference to a list where each element is a KBaseNetworks.Interaction


=end text

=back



=cut

package Bio::KBase::KBaseNetworksService::Client::RpcClient;
use base 'JSON::RPC::Client';

#
# Override JSON::RPC::Client::call because it doesn't handle error returns properly.
#

sub call {
    my ($self, $uri, $obj) = @_;
    my $result;

    if ($uri =~ /\?/) {
       $result = $self->_get($uri);
    }
    else {
        Carp::croak "not hashref." unless (ref $obj eq 'HASH');
        $result = $self->_post($uri, $obj);
    }

    my $service = $obj->{method} =~ /^system\./ if ( $obj );

    $self->status_line($result->status_line);

    if ($result->is_success) {

        return unless($result->content); # notification?

        if ($service) {
            return JSON::RPC::ServiceObject->new($result, $self->json);
        }

        return JSON::RPC::ReturnObject->new($result, $self->json);
    }
    elsif ($result->content_type eq 'application/json')
    {
        return JSON::RPC::ReturnObject->new($result, $self->json);
    }
    else {
        return;
    }
}


sub _post {
    my ($self, $uri, $obj) = @_;
    my $json = $self->json;

    $obj->{version} ||= $self->{version} || '1.1';

    if ($obj->{version} eq '1.0') {
        delete $obj->{version};
        if (exists $obj->{id}) {
            $self->id($obj->{id}) if ($obj->{id}); # if undef, it is notification.
        }
        else {
            $obj->{id} = $self->id || ($self->id('JSON::RPC::Client'));
        }
    }
    else {
        # $obj->{id} = $self->id if (defined $self->id);
	# Assign a random number to the id if one hasn't been set
	$obj->{id} = (defined $self->id) ? $self->id : substr(rand(),2);
    }

    my $content = $json->encode($obj);

    $self->ua->post(
        $uri,
        Content_Type   => $self->{content_type},
        Content        => $content,
        Accept         => 'application/json',
	($self->{token} ? (Authorization => $self->{token}) : ()),
    );
}



1;
