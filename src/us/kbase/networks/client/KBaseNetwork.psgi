use KBaseNetworkImpl;

use KBaseNetworkServer;



my @dispatch;

{
    my $obj = KBaseNetworkImpl->new;
    push(@dispatch, 'KBaseNetwork' => $obj);
}


my $server = KBaseNetworkServer->new(instance_dispatch => { @dispatch },
				allow_get => 0,
			       );

my $handler = sub { $server->handle_input(@_) };

$handler;
