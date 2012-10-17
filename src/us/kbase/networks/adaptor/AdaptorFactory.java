package us.kbase.networks.adaptor;

public interface AdaptorFactory {
	public Adaptor buildAdaptor() throws AdaptorException;
}
