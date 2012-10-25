package KBaseNetworkClient;

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

KBaseNetworkClient

=head1 DESCRIPTION



=cut

sub new
{
    my($class, $url) = @_;

    my $self = {
	client => KBaseNetworkClient::RpcClient->new,
	url => $url,
    };
    my $ua = $self->{client}->ua;	 
    my $timeout = $ENV{CDMI_TIMEOUT} || (30 * 60);	 
    $ua->timeout($timeout);
    bless $self, $class;
    #    $self->_validate_version();
    return $self;
}




=head2 $result = getDatasets(ParameterList)



=cut

sub getDatasets
{
    my($self, @args) = @_;

    if ((my $n = @args) != 1)
    {
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error =>
							       "Invalid argument count for function getDatasets (received $n, expecting 1)");
    }
    {
	my($ParameterList) = @args;

	my @_bad_arguments;
        (ref($ParameterList) eq 'ARRAY') or push(@_bad_arguments, "Invalid type for argument 1 \"ParameterList\" (value was \"$ParameterList\")");
        if (@_bad_arguments) {
	    my $msg = "Invalid arguments passed to getDatasets:\n" . join("", map { "\t$_\n" } @_bad_arguments);
	    Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
								   method_name => 'getDatasets');
	}
    }

    my $result = $self->{client}->call($self->{url}, {
	method => "KBaseNetwork.getDatasets",
	params => \@args,
    });
    if ($result) {
	if ($result->is_error) {
	    Bio::KBase::Exceptions::JSONRPC->throw(error => $result->error_message,
					       code => $result->content->{code},
					       method_name => 'getDatasets',
					      );
	} else {
	    return wantarray ? @{$result->result} : $result->result->[0];
	}
    } else {
        Bio::KBase::Exceptions::HTTP->throw(error => "Error invoking method getDatasets",
					    status_line => $self->{client}->status_line,
					    method_name => 'getDatasets',
				       );
    }
}



=head2 $result = buildNetwork(ParameterList)



=cut

sub buildNetwork
{
    my($self, @args) = @_;

    if ((my $n = @args) != 1)
    {
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error =>
							       "Invalid argument count for function buildNetwork (received $n, expecting 1)");
    }
    {
	my($ParameterList) = @args;

	my @_bad_arguments;
        (ref($ParameterList) eq 'ARRAY') or push(@_bad_arguments, "Invalid type for argument 1 \"ParameterList\" (value was \"$ParameterList\")");
        if (@_bad_arguments) {
	    my $msg = "Invalid arguments passed to buildNetwork:\n" . join("", map { "\t$_\n" } @_bad_arguments);
	    Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
								   method_name => 'buildNetwork');
	}
    }

    my $result = $self->{client}->call($self->{url}, {
	method => "KBaseNetwork.buildNetwork",
	params => \@args,
    });
    if ($result) {
	if ($result->is_error) {
	    Bio::KBase::Exceptions::JSONRPC->throw(error => $result->error_message,
					       code => $result->content->{code},
					       method_name => 'buildNetwork',
					      );
	} else {
	    return wantarray ? @{$result->result} : $result->result->[0];
	}
    } else {
        Bio::KBase::Exceptions::HTTP->throw(error => "Error invoking method buildNetwork",
					    status_line => $self->{client}->status_line,
					    method_name => 'buildNetwork',
				       );
    }
}



=head2 $result = buildFirstNeighborNetwork(ParameterList)



=cut

sub buildFirstNeighborNetwork
{
    my($self, @args) = @_;

    if ((my $n = @args) != 1)
    {
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error =>
							       "Invalid argument count for function buildFirstNeighborNetwork (received $n, expecting 1)");
    }
    {
	my($ParameterList) = @args;

	my @_bad_arguments;
        (ref($ParameterList) eq 'ARRAY') or push(@_bad_arguments, "Invalid type for argument 1 \"ParameterList\" (value was \"$ParameterList\")");
        if (@_bad_arguments) {
	    my $msg = "Invalid arguments passed to buildFirstNeighborNetwork:\n" . join("", map { "\t$_\n" } @_bad_arguments);
	    Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
								   method_name => 'buildFirstNeighborNetwork');
	}
    }

    my $result = $self->{client}->call($self->{url}, {
	method => "KBaseNetwork.buildFirstNeighborNetwork",
	params => \@args,
    });
    if ($result) {
	if ($result->is_error) {
	    Bio::KBase::Exceptions::JSONRPC->throw(error => $result->error_message,
					       code => $result->content->{code},
					       method_name => 'buildFirstNeighborNetwork',
					      );
	} else {
	    return wantarray ? @{$result->result} : $result->result->[0];
	}
    } else {
        Bio::KBase::Exceptions::HTTP->throw(error => "Error invoking method buildFirstNeighborNetwork",
					    status_line => $self->{client}->status_line,
					    method_name => 'buildFirstNeighborNetwork',
				       );
    }
}



=head2 $result = buildInternalNetwork(ParameterList)



=cut

sub buildInternalNetwork
{
    my($self, @args) = @_;

    if ((my $n = @args) != 1)
    {
	Bio::KBase::Exceptions::ArgumentValidationError->throw(error =>
							       "Invalid argument count for function buildInternalNetwork (received $n, expecting 1)");
    }
    {
	my($ParameterList) = @args;

	my @_bad_arguments;
        (ref($ParameterList) eq 'ARRAY') or push(@_bad_arguments, "Invalid type for argument 1 \"ParameterList\" (value was \"$ParameterList\")");
        if (@_bad_arguments) {
	    my $msg = "Invalid arguments passed to buildInternalNetwork:\n" . join("", map { "\t$_\n" } @_bad_arguments);
	    Bio::KBase::Exceptions::ArgumentValidationError->throw(error => $msg,
								   method_name => 'buildInternalNetwork');
	}
    }

    my $result = $self->{client}->call($self->{url}, {
	method => "KBaseNetwork.buildInternalNetwork",
	params => \@args,
    });
    if ($result) {
	if ($result->is_error) {
	    Bio::KBase::Exceptions::JSONRPC->throw(error => $result->error_message,
					       code => $result->content->{code},
					       method_name => 'buildInternalNetwork',
					      );
	} else {
	    return wantarray ? @{$result->result} : $result->result->[0];
	}
    } else {
        Bio::KBase::Exceptions::HTTP->throw(error => "Error invoking method buildInternalNetwork",
					    status_line => $self->{client}->status_line,
					    method_name => 'buildInternalNetwork',
				       );
    }
}



sub version {
    my ($self) = @_;
    my $result = $self->{client}->call($self->{url}, {
        method => "KBaseNetwork.version",
        params => [],
    });
    if ($result) {
        if ($result->is_error) {
            Bio::KBase::Exceptions::JSONRPC->throw(
                error => $result->error_message,
                code => $result->content->{code},
                method_name => 'buildInternalNetwork',
            );
        } else {
            return wantarray ? @{$result->result} : $result->result->[0];
        }
    } else {
        Bio::KBase::Exceptions::HTTP->throw(
            error => "Error invoking method buildInternalNetwork",
            status_line => $self->{client}->status_line,
            method_name => 'buildInternalNetwork',
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
        warn "New client version available for KBaseNetworkClient\n";
    }
    if ($sMajor == 0) {
        warn "KBaseNetworkClient version is $svr_version. API subject to change.\n";
    }
}

package KBaseNetworkClient::RpcClient;
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

1;
